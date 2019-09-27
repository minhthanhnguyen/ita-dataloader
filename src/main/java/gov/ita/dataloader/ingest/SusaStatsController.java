package gov.ita.dataloader.ingest;

import gov.ita.dataloader.ingest.configuration.DataSetConfig;
import gov.ita.dataloader.ingest.configuration.DataSetConfigRepository;
import gov.ita.dataloader.ingest.configuration.ReplaceValue;
import gov.ita.dataloader.ingest.configuration.ZipFileConfig;
import gov.ita.dataloader.ingest.storage.BlobMetaData;
import gov.ita.dataloader.ingest.storage.Storage;
import gov.ita.dataloader.security.AuthenticationFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class SusaStatsController {

  private Storage storage;
  private RestTemplate restTemplate;
  private ZipFileExtractor zipFileExtractor;
  private DataSetConfigRepository dataSetConfigRepository;
  private AuthenticationFacade authenticationFacade;

  public SusaStatsController(Storage storage,
                             RestTemplate restTemplate,
                             ZipFileExtractor zipFileExtractor,
                             DataSetConfigRepository dataSetConfigRepository,
                             AuthenticationFacade authenticationFacade) {
    this.storage = storage;
    this.restTemplate = restTemplate;
    this.zipFileExtractor = zipFileExtractor;
    this.dataSetConfigRepository = dataSetConfigRepository;
    this.authenticationFacade = authenticationFacade;
  }

  @PreAuthorize("hasRole('ROLE_EDSP')")
  @GetMapping("/api/configuration")
  private List<DataSetConfig> getSusaStatsCongig(@RequestParam("container-name") String containerName) {
    return dataSetConfigRepository.findByContainerName(containerName);
  }

  @PreAuthorize("hasRole('ROLE_EDSP')")
  @GetMapping("/api/ingest")
  public String startIngestProcess(@RequestParam("container-name") String containerName) throws IOException, InterruptedException {
    storage.initContainer(containerName);

    List<DataSetConfig> dataSourceConfigs = getSusaStatsCongig(containerName);

    byte[] fileBytes;
    for (DataSetConfig dsc : dataSourceConfigs) {
      if (dsc.isEnabled()) {
        log.info("Importing file {} from {}", dsc.getFileName(), dsc.getUrl());
        fileBytes = httpGetBytes(dsc.getUrl());
        processAndSaveDataSource(dsc.getFileName(), fileBytes, dsc.getReplaceValues(), dsc.getContainerName());

        if (dsc.getZipFileConfigs() != null) {
          Map<String, ByteArrayOutputStream> fileMap = zipFileExtractor.extract(fileBytes);
          for (String fileName : fileMap.keySet()) {
            ZipFileConfig zfc = getZipFileConfig(dsc, fileName);
            processAndSaveDataSource(
              zfc.getDestinationFileName(),
              fileMap.get(fileName).toByteArray(),
              dsc.getReplaceValues(),
              dsc.getContainerName());
          }
        }
        Thread.sleep(3000);
      }
    }

    log.info("Ingest process complete for container: {}", containerName);
    return "done";
  }

  @PreAuthorize("hasRole('ROLE_EDSP')")
  @PutMapping("/api/save/file")
  public String saveFile(@RequestParam("file") MultipartFile file, @RequestParam("containerName") String containerName) throws IOException {
    storage.save(
      file.getOriginalFilename(),
      file.getBytes(),
      authenticationFacade.getUserName(),
      containerName);
    return "success";
  }

  @PreAuthorize("hasRole('ROLE_EDSP')")
  @PutMapping("/api/save/configuration")
  public String saveConfiguration(@RequestBody List<DataSetConfig> dataSetConfigs) {
    dataSetConfigRepository.saveAll(dataSetConfigs);
    return "success";
  }

  private void processAndSaveDataSource(String fileName, byte[] fileBytes, List<ReplaceValue> replaceValues, String containerName) {
    if (replaceValues != null) {
      for (ReplaceValue rv : replaceValues) {
        fileBytes = replace(fileBytes, rv.getReplaceThis(), rv.getWithThis());
      }
    }
    storage.save(fileName, fileBytes, null, containerName);
  }

  private byte[] replace(byte[] fileBytes, String replace, String with) {
    return new String(fileBytes).replaceAll(replace, with).getBytes();
  }

  private ZipFileConfig getZipFileConfig(DataSetConfig dsc, String fileName) {
    return dsc.getZipFileConfigs().stream()
      .filter(zipFileContent -> zipFileContent.getOriginalFileName().equals(fileName))
      .collect(Collectors.toList())
      .get(0);
  }

  @GetMapping("/api/storage-content-url")
  public String getStorageContentUrl(@RequestParam("container-name") String containerName) {
    return storage.getListBlobsUrl(containerName);
  }

  @GetMapping("/api/storage-content")
  public List<BlobMetaData> getStorageMetadata(@RequestParam("container-name") String containerName) {
    return storage.getBlobMetadata(containerName);
  }

  private byte[] httpGetBytes(String url) {
    return restTemplate.getForEntity(url, byte[].class).getBody();
  }
}
