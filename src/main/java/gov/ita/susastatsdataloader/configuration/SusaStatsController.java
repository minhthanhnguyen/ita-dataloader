package gov.ita.susastatsdataloader.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ita.susastatsdataloader.storage.Storage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RestController
public class SusaStatsController {

  private Storage storage;
  private ObjectMapper objectMapper;
  private RestTemplate restTemplate;

  public SusaStatsController(Storage storage, ObjectMapper objectMapper, RestTemplate restTemplate) {
    this.storage = storage;
    this.objectMapper = objectMapper;
    this.restTemplate = restTemplate;
  }

  @GetMapping("/api/dataset/imf-weodata")
  public ResponseEntity<byte[]> getImfWeoData() throws IOException {
    String blobAsString = storage.getBlobAsString("configuration.json");
    SusaStatsConfigResponse susaStatsConfigResponse = objectMapper.readValue(blobAsString, SusaStatsConfigResponse.class);
    return restTemplate.getForEntity(susaStatsConfigResponse.susaStatsConfig.imfWeoUrl, byte[].class);
  }
}
