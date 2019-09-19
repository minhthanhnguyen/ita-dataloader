package gov.ita.susastatsdataloader.ingest;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ita.susastatsdataloader.configuration.DataSourceConfig;
import gov.ita.susastatsdataloader.configuration.SusaStatsConfigResponse;
import gov.ita.susastatsdataloader.configuration.ZipFileContent;
import gov.ita.susastatsdataloader.storage.BlobMetaData;
import gov.ita.susastatsdataloader.storage.Storage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class SusaStatsController {

  private Storage storage;
  private ObjectMapper objectMapper;
  private RestTemplate restTemplate;
  private ZipFileExtractor zipFileExtractor;

  public SusaStatsController(Storage storage,
                             ObjectMapper objectMapper,
                             RestTemplate restTemplate,
                             ZipFileExtractor zipFileExtractor) {
    this.storage = storage;
    this.objectMapper = objectMapper;
    this.restTemplate = restTemplate;
    this.zipFileExtractor = zipFileExtractor;
  }

  @GetMapping("/api/config")
  private List<DataSourceConfig> getSusaStatsCongig() throws IOException {
    String blobAsString = storage.getBlobAsString("configuration.json");
    SusaStatsConfigResponse susaStatsConfigResponse = objectMapper.readValue(blobAsString, SusaStatsConfigResponse.class);
    return susaStatsConfigResponse.getDataSourceConfigs();
  }

  @GetMapping("/api/save-datasets")
  public String saveDatasets() throws IOException {
    List<DataSourceConfig> dataSourceConfigs = getSusaStatsCongig();
    for (DataSourceConfig dsc : dataSourceConfigs) {
      byte[] fileContent = httpGetBytes(dsc.getUrl());
      storage.save(dsc.getDestinationFileName(), fileContent, null);
      if (dsc.getZipFileContent() != null) {
        Map<String, ByteArrayOutputStream> fileMap = zipFileExtractor.extract(fileContent);
        for (String fileName : fileMap.keySet()) {
          String destinationFileName = dsc.getZipFileContent().stream()
            .filter(zipFileContent -> zipFileContent.getOriginalFileName().equals(fileName))
            .collect(Collectors.toList())
            .get(0).getDestinationFileName();
          storage.save(destinationFileName, fileMap.get(fileName).toByteArray(), null);
        }
      }
    }

    return "done";
  }

  @GetMapping("/api/storage-content-url")
  public String getStorageContentUrl() {
    return storage.getListBlobsUrl();
  }

  @GetMapping("/api/storage-content")
  public List<BlobMetaData> getStorageMetadata() {
    return storage.getBlobMetadata();
  }

  private byte[] httpGetBytes(String url) {
    return restTemplate.getForEntity(url, byte[].class).getBody();
  }
}
