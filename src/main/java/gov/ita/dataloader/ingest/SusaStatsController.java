package gov.ita.dataloader.ingest;

import gov.ita.dataloader.configuration.DataSetConfig;
import gov.ita.dataloader.configuration.DataSetConfigRepository;
import gov.ita.dataloader.configuration.ReplaceValue;
import gov.ita.dataloader.configuration.ZipFileConfig;
import gov.ita.dataloader.ingest.storage.BlobMetaData;
import gov.ita.dataloader.ingest.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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

  public SusaStatsController(Storage storage,
                             RestTemplate restTemplate,
                             ZipFileExtractor zipFileExtractor,
                             DataSetConfigRepository dataSetConfigRepository) {
    this.storage = storage;
    this.restTemplate = restTemplate;
    this.zipFileExtractor = zipFileExtractor;
    this.dataSetConfigRepository = dataSetConfigRepository;
  }

  @GetMapping("/api/configuration")
  private List<DataSetConfig> getSusaStatsCongig() {
    return dataSetConfigRepository.findAll();
  }

  @GetMapping("/api/ingest")
  public String startIngestProcess(@RequestParam("container-name") String containerName) throws IOException, InterruptedException {
    storage.initContainer(containerName);

    List<DataSetConfig> dataSourceConfigs = getSusaStatsCongig()
      .stream().filter(dsc -> dsc.getContainerName().equals(containerName)).collect(Collectors.toList());

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
