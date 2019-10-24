package gov.ita.dataloader.ingest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ita.dataloader.ingest.configuration.BusinessUnit;
import gov.ita.dataloader.ingest.configuration.BusinessUnitConfigResponse;
import gov.ita.dataloader.ingest.configuration.DataloaderConfig;
import gov.ita.dataloader.ingest.translators.Translator;
import gov.ita.dataloader.ingest.translators.TranslatorFactory;
import gov.ita.dataloader.security.AuthenticationFacade;
import gov.ita.dataloader.storage.BlobMetaData;
import gov.ita.dataloader.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class IngestController {

  private Storage storage;
  private IngestProcessor ingestProcessor;
  private AuthenticationFacade authenticationFacade;
  private ObjectMapper objectMapper;
  private TranslatorFactory translatorFactory;

  public IngestController(Storage storage,
                          IngestProcessor ingestProcessor,
                          AuthenticationFacade authenticationFacade,
                          ObjectMapper objectMapper,
                          TranslatorFactory translatorFactory) {
    this.storage = storage;
    this.ingestProcessor = ingestProcessor;
    this.authenticationFacade = authenticationFacade;
    this.objectMapper = objectMapper;
    this.translatorFactory = translatorFactory;
  }

  @GetMapping(value = "/api/configuration", produces = MediaType.APPLICATION_JSON_VALUE)
  private DataloaderConfig getDataSetConfigs(@RequestParam("containerName") String containerName) {
    return getDataloaderConfig(containerName);
  }

  @PreAuthorize("hasRole('ROLE_EDSP')")
  @GetMapping("/api/ingest")
  public String startIngestProcess(@RequestParam("containerName") String containerName) {
    IngestProcessorStatus status = ingestProcessor.getStatus(containerName);
    if (status == null || !status.isIngesting())
      ingestProcessor.process(
        getDataloaderConfig(containerName).getDataSetConfigs(),
        containerName,
        authenticationFacade.getUserName(),
        5000);
    return "done";
  }

  @GetMapping(value = "/api/ingest/status", produces = MediaType.APPLICATION_JSON_VALUE)
  public IngestProcessorStatus getIngestProcessorStatus(@RequestParam("containerName") String containerName) {
    return ingestProcessor.getStatus(containerName);
  }

  @PreAuthorize("hasRole('ROLE_EDSP')")
  @PutMapping("/api/save/file")
  public void saveFile(@RequestParam("file") MultipartFile file,
                       @RequestParam("containerName") String containerName) throws IOException {
    storage.save(
      file.getOriginalFilename(),
      file.getBytes(),
      authenticationFacade.getUserName(),
      containerName,
      true);

    Translator translator = translatorFactory.getTranslator(containerName + "#" + file.getOriginalFilename());
    if (translator != null) {
      byte[] translatedFile = translator.translate(file.getBytes());
      storage.save(
        "translated/" + file.getOriginalFilename(),
        translatedFile,
        authenticationFacade.getUserName(),
        containerName,
        true);
    }
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
      .filter(blobMetaData -> !blobMetaData.getName().equals("configuration.json"))
      .collect(Collectors.toList());
  }

  @GetMapping(value = "/api/business-units", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<BusinessUnit> getBusinessUnits() throws Exception {
    byte[] dataloaderConfig = storage.getBlob("dataloader", "configuration.json");
    BusinessUnitConfigResponse buc = objectMapper.readValue(dataloaderConfig, BusinessUnitConfigResponse.class);
    return buc.getBusinessUnits();
  }

  @GetMapping(value = "/api/storage-containers", produces = MediaType.APPLICATION_JSON_VALUE)
  public Set<String> getStorageContainers() {
    return storage.getContainerNames();
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
