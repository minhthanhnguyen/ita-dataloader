package gov.ita.dataloader.ingest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ita.dataloader.ingest.configuration.BusinessUnit;
import gov.ita.dataloader.ingest.configuration.BusinessUnitConfigResponse;
import gov.ita.dataloader.ingest.configuration.DataloaderConfig;
import gov.ita.dataloader.ingest.storage.BlobMetaData;
import gov.ita.dataloader.ingest.storage.Storage;
import gov.ita.dataloader.security.AuthenticationFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
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
  @GetMapping(value = "/api/configuration", produces = MediaType.APPLICATION_JSON_VALUE)
  private DataloaderConfig getDataSetConfigs(@RequestParam("containerName") String containerName) {
    return getDataloaderConfig(containerName);
  }

  @PreAuthorize("hasRole('ROLE_EDSP')")
  @GetMapping("/api/ingest")
  public String startIngestProcess(@RequestParam("containerName") String containerName) {
    IngestProcessorStatus status = ingestProcessor.getStatus(containerName);
    if (status == null || !status.isProcessing())
      ingestProcessor.process(
        getDataloaderConfig(containerName).getDataSetConfigs(),
        containerName,
        authenticationFacade.getUserName());
    return "done";
  }

  @GetMapping(value = "/api/ingest/status", produces = MediaType.APPLICATION_JSON_VALUE)
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
  public void saveConfiguration(@RequestBody DataloaderConfig dataloaderConfig,
                                @RequestParam("containerName") String containerName) throws JsonProcessingException {
    byte[] dataSetConfigsJsonBytes = objectMapper.writeValueAsString(dataloaderConfig).getBytes();
    storage.save("configuration.json", dataSetConfigsJsonBytes, authenticationFacade.getUserName(), containerName);
  }

  @GetMapping("/api/storage-content-url")
  public String getStorageContentUrl(@RequestParam("containerName") String containerName) {
    return storage.getListBlobsUrl(containerName);
  }

  @GetMapping(value = "/api/storage-content", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<BlobMetaData> getStorageMetadata(@RequestParam("containerName") String containerName) {
    return storage.getBlobMetadata(containerName).stream()
      .filter(blobMetaData -> !blobMetaData.getName().equals("configuration.json"))
      .collect(Collectors.toList());
  }

  @GetMapping(value = "/api/business-units", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<BusinessUnit> getBusinessUnits() throws Exception {
    byte[] dataloaderConfig = storage.getBlob("dataloader", "configuration.json");
    BusinessUnitConfigResponse buc = objectMapper.readValue(dataloaderConfig, BusinessUnitConfigResponse.class);
    return buc.getBusinessUnits();
  }

  private DataloaderConfig getDataloaderConfig(String containerName) {
    try {
      return objectMapper.readValue(storage.getBlob(containerName, "configuration.json"), DataloaderConfig.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }
}
