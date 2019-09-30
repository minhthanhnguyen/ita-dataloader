package gov.ita.dataloader.ingest;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ita.dataloader.ingest.configuration.BusinessUnit;
import gov.ita.dataloader.ingest.configuration.BusinessUnitConfigResponse;
import gov.ita.dataloader.ingest.configuration.DataLoaderConfigResponse;
import gov.ita.dataloader.ingest.configuration.DataSetConfig;
import gov.ita.dataloader.ingest.storage.BlobMetaData;
import gov.ita.dataloader.ingest.storage.Storage;
import gov.ita.dataloader.security.AuthenticationFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class IngestController {

  private Storage storage;
  private IngestProcessor ingestProcessor;
  private AuthenticationFacade authenticationFacade;
  private ObjectMapper objectMapper;

  public IngestController(Storage storage,
                          IngestProcessor ingestProcessor,
                          AuthenticationFacade authenticationFacade,
                          ObjectMapper objectMapper) {
    this.storage = storage;
    this.ingestProcessor = ingestProcessor;
    this.authenticationFacade = authenticationFacade;
    this.objectMapper = objectMapper;
  }

  @PreAuthorize("hasRole('ROLE_EDSP')")
  @GetMapping("/api/configuration")
  private List<DataSetConfig> getDataSetConfigs(@RequestParam("containerName") String containerName) {
    DataLoaderConfigResponse dataloaderConfig = getDataloaderConfig(containerName);
    if (dataloaderConfig != null)
      return dataloaderConfig.getDataSetConfigs();
    return Collections.emptyList();
  }

  @PreAuthorize("hasRole('ROLE_EDSP')")
  @GetMapping("/api/ingest")
  public String startIngestProcess(@RequestParam("containerName") String containerName) {
    ingestProcessor.process(
      Objects.requireNonNull(getDataloaderConfig(containerName)).getDataSetConfigs(),
      containerName,
      authenticationFacade.getUserName());
    return "done";
  }

  @GetMapping("/api/ingest/status")
  public IngestProcessorStatus getIngestProcessorStatus(@RequestParam("containerName") String containerName) {
    return ingestProcessor.getStatus(containerName);
  }

  @PreAuthorize("hasRole('ROLE_EDSP')")
  @PutMapping("/api/save/file")
  public String saveFile(@RequestParam("file") MultipartFile file,
                         @RequestParam("containerName") String containerName) throws IOException {
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
    return "success";
  }

  @GetMapping("/api/storage-content-url")
  public String getStorageContentUrl(@RequestParam("containerName") String containerName) {
    return storage.getListBlobsUrl(containerName);
  }

  @GetMapping("/api/storage-content")
  public List<BlobMetaData> getStorageMetadata(@RequestParam("containerName") String containerName) {
    return storage.getBlobMetadata(containerName).stream()
      .filter(blobMetaData -> !blobMetaData.getName().equals("configuration.json"))
      .collect(Collectors.toList());
  }

  @GetMapping("/api/business-units")
  public List<BusinessUnit> getBusinessUnits() throws IOException {
    byte[] dataloaderConfig = storage.getBlob("dataloader", "configuration.json");
    BusinessUnitConfigResponse buc = objectMapper.readValue(dataloaderConfig, BusinessUnitConfigResponse.class);
    return buc.getBusinessUnits();
  }

  private DataLoaderConfigResponse getDataloaderConfig(String containerName) {
    byte[] blob = storage.getBlob(containerName, "configuration.json");
    if (blob != null) {
      try {
        return objectMapper.readValue(blob, DataLoaderConfigResponse.class);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }
}
