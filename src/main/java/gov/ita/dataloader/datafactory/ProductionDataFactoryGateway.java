package gov.ita.dataloader.datafactory;

import gov.ita.dataloader.azure_auth.AccessTokenGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@Profile({"production", "staging"})
public class ProductionDataFactoryGateway implements DataFactoryGateway {

  @Value("${datafactory.client-id}")
  private String clientId;

  @Value("${datafactory.client-secret}")
  private String clientSecret;

  @Value("${datafactory.name}")
  private String datafactoryName;

  @Value("${datafactory.resource-group}")
  private String resourceGroup;

  @Value("${azure-subscription-id}")
  private String subscriptionId;

  private final AccessTokenGateway accessTokenGateway;
  private final RestTemplate restTemplate;

  public ProductionDataFactoryGateway(AccessTokenGateway accessTokenGateway, RestTemplate restTemplate) {
    this.accessTokenGateway = accessTokenGateway;
    this.restTemplate = restTemplate;
  }

  @Override
  public PipelineRun getPipelineStatus(String pipelineName) {
    String apiUrl = getBaseApiUrl() + "/queryPipelineRuns?api-version=2018-06-01";
    HttpHeaders headers = buildHttpHeaders();

    String requestBody = "{ \"lastUpdatedAfter\": \"1999-03-25T00:00:00.000Z\", \"lastUpdatedBefore\": " + today() + " }";
    HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
    DataFactoryPipelineResponse responseBody = restTemplate.exchange(
      apiUrl,
      HttpMethod.POST,
      request,
      DataFactoryPipelineResponse.class
    ).getBody();

    List<PipelineRun> pipelineRuns = responseBody.getValue();
    PipelineRun latestPipelineRun = null;
    for (PipelineRun run : pipelineRuns) {
      if (run.pipelineName.equals(pipelineName)
        && ((latestPipelineRun == null) || (run.isLatest && run.lastUpdated.isAfter(latestPipelineRun.lastUpdated)))) {
        latestPipelineRun = run;
      }
    }

    return latestPipelineRun;
  }

  @Override
  public void runPipeline(String pipelineName) {
    log.info("Starting pipeline run: {}", pipelineName);
    String apiUrl = getBaseApiUrl() + String.format("/pipelines/%s/createRun?api-version=2018-06-01", pipelineName);
    HttpEntity<String> request = new HttpEntity<>("", buildHttpHeaders());
    restTemplate.postForLocation(apiUrl, request);
  }

  private HttpHeaders buildHttpHeaders() {
    String accessToken = accessTokenGateway.getAccessToken(clientId, clientSecret, "https://management.azure.com");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(accessToken);
    return headers;
  }

  private String today() {
    return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
  }

  private String getBaseApiUrl() {
    return String.format(
      "https://management.azure.com/subscriptions/%s/resourceGroups/%s/providers/Microsoft.DataFactory/factories/%s",
      subscriptionId, resourceGroup, datafactoryName);
  }
}
