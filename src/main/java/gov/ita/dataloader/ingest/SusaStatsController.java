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
  private IngestProcessor ingestProcessor;
  private DataSetConfigRepository dataSetConfigRepository;
  private AuthenticationFacade authenticationFacade;

  public SusaStatsController(Storage storage,
                             IngestProcessor ingestProcessor,
                             DataSetConfigRepository dataSetConfigRepository,
                             AuthenticationFacade authenticationFacade) {
    this.storage = storage;
    this.ingestProcessor = ingestProcessor;
    this.dataSetConfigRepository = dataSetConfigRepository;
    this.authenticationFacade = authenticationFacade;
  }

  @PreAuthorize("hasRole('ROLE_EDSP')")
  @GetMapping("/api/configuration")
  private List<DataSetConfig> getDataSetCongigs(@RequestParam("containerName") String containerName) {
    return dataSetConfigRepository.findByContainerName(containerName);
  }

  @PreAuthorize("hasRole('ROLE_EDSP')")
  @GetMapping("/api/ingest")
  public String startIngestProcess(@RequestParam("containerName") String containerName) {
    storage.initContainer(containerName);
    List<DataSetConfig> dataSourceConfigs = dataSetConfigRepository.findByContainerName(containerName);
    ingestProcessor.process(dataSourceConfigs, containerName, authenticationFacade.getUserName());
    return "done";
  }

  @PreAuthorize("hasRole('ROLE_EDSP')")
  @PutMapping("/api/save/file")
  public String saveFile(@RequestParam("file") MultipartFile file,
                         @RequestParam("containerName") String containerName) throws IOException {
    storage.initContainer(containerName);
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

  @GetMapping("/api/storage-content-url")
  public String getStorageContentUrl(@RequestParam("containerName") String containerName) {
    return storage.getListBlobsUrl(containerName);
  }

  @GetMapping("/api/storage-content")
  public List<BlobMetaData> getStorageMetadata(@RequestParam("containerName") String containerName) {
    return storage.getBlobMetadata(containerName);
  }

}
