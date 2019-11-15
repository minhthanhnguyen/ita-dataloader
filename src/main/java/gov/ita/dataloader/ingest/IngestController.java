package gov.ita.dataloader.ingest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ita.dataloader.ingest.configuration.BusinessUnit;
import gov.ita.dataloader.ingest.configuration.BusinessUnitConfigResponse;
import gov.ita.dataloader.ingest.configuration.DataloaderConfig;
import gov.ita.dataloader.security.AuthenticationFacade;
import gov.ita.dataloader.storage.BlobMetaData;
import gov.ita.dataloader.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class IngestController {

  private Storage storage;
  private IngestProcessor ingestProcessor;
  private AuthenticationFacade authenticationFacade;
  private ObjectMapper objectMapper;
  private IngestTranslationProcessor ingestTranslationProcessor;

  public IngestController(Storage storage,
                          IngestProcessor ingestProcessor,
                          AuthenticationFacade authenticationFacade,
                          ObjectMapper objectMapper,
                          IngestTranslationProcessor ingestTranslationProcessor) {
    this.storage = storage;
    this.ingestProcessor = ingestProcessor;
    this.authenticationFacade = authenticationFacade;
    this.objectMapper = objectMapper;
    this.ingestTranslationProcessor = ingestTranslationProcessor;
  }

  @GetMapping(value = "/api/configuration", produces = MediaType.APPLICATION_JSON_VALUE)
  private DataloaderConfig getDataSetConfigs(@RequestParam("containerName") String containerName) {
    return getDataloaderConfig(containerName);
  }

  @PreAuthorize("hasRole('ROLE_EDSP')")
  @GetMapping("/api/ingest")
  public void startIngestProcess(@RequestParam("containerName") String containerName) {
    ingestProcessor.process(
      getDataloaderConfig(containerName).getDataSetConfigs(),
      containerName,
      authenticationFacade.getUserName(),
      5000);
  }

  @PreAuthorize("hasRole('ROLE_EDSP')")
  @PutMapping("/api/save/file")
  public void saveFile(@RequestParam("file") MultipartFile file,
                       @RequestParam("containerName") String containerName) throws IOException {
    ingestTranslationProcessor.process(containerName, file.getOriginalFilename(), file.getBytes(), authenticationFacade.getUserName());
  }

  @PreAuthorize("hasRole('ROLE_EDSP')")
  @PutMapping("/api/save/configuration")
  public void saveConfiguration(@RequestBody DataloaderConfig dataloaderConfig,
                                @RequestParam("containerName") String containerName) throws JsonProcessingException {
    byte[] dataSetConfigsJsonBytes = objectMapper.writeValueAsString(dataloaderConfig).getBytes();
    storage.save("configuration.json", dataSetConfigsJsonBytes, authenticationFacade.getUserName(), containerName, true);
  }

  @GetMapping("/api/storage-content-url")
  public String getStorageContentUrl(@RequestParam("containerName") String containerName) {
    return storage.getListBlobsUrl(containerName);
  }

  @GetMapping(value = "/api/container-metadata", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<BlobMetaData> getStorageMetadata(@RequestParam("containerName") String containerName) {
    return storage.getBlobMetadata(containerName).stream()
      .filter(blobMetaData -> !blobMetaData.getFileName().equals("configuration.json"))
      .map(blobMetaData -> {
        Map<String, String> metadata = blobMetaData.getMetadata();
        if (metadata == null) {
          metadata = new HashMap<>();
          metadata.put("uploaded_by", "---");
          metadata.put("user_upload", "true");
        }

        if (!metadata.containsKey("uploaded_by")) {
          metadata.put("uploaded_by", "---");
        }

        if (!metadata.containsKey("user_upload")) {
          metadata.put("user_upload", "true");
        }

        blobMetaData.setMetadata(metadata);
        return blobMetaData;
      })
      .collect(Collectors.toList());
  }

  @GetMapping(value = "/api/business-units", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<BusinessUnit> getBusinessUnits() throws Exception {
    byte[] dataloaderConfig = storage.getBlob("dataloader", "configuration.json");
    BusinessUnitConfigResponse buc = objectMapper.readValue(dataloaderConfig, BusinessUnitConfigResponse.class);
    return buc.getBusinessUnits();
  }

  @GetMapping(value = "/api/storage-containers", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<String> getStorageContainers() throws Exception {
    return getBusinessUnits().stream().map(BusinessUnit::getContainerName).collect(Collectors.toList());
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
