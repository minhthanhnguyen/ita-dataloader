package gov.ita.dataloader.extract.fta;

import gov.ita.dataloader.extract.fta.tariff.AccessTokenResponse;
import gov.ita.dataloader.extract.fta.tariff.TariffDocsMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TariffDocsMetaDataGateway {

  @Value("${tariff-docs.access-token-url}")
  private String tariffDocsAccessTokenUrl;

  @Value("${tariff-docs.client-id}")
  private String clientId;

  @Value("${tariff-docs.client-secret}")
  private String clientSecret;

  @Value("${tariff-docs.metadata-url}")
  private String tariffDocsMetadataUrl;

  private final RestTemplate restTemplate;

  public TariffDocsMetaDataGateway(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<TariffDocsMetadata> getMetadata() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(getAccessToken());

    String body = "{\"query\": \"$filter=Publication eq 'FTA Publication'\"}";
    
    HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
    return restTemplate.exchange(
      tariffDocsMetadataUrl,
      HttpMethod.POST,
      requestEntity,
      new ParameterizedTypeReference<List<TariffDocsMetadata>>() {
      }).getBody();
  }

  private String getAccessToken() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> tokenBody = new LinkedMultiValueMap<>();
    tokenBody.add("grant_type", "client_credentials");
    tokenBody.add("Content-Type", "application/x-www-form-urlencoded");
    tokenBody.add("cache-control", "no-cache");
    tokenBody.add("client_id", clientId);
    tokenBody.add("client_secret", clientSecret);
    tokenBody.add("Resource", clientId);

    HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenBody, headers);
    ResponseEntity<AccessTokenResponse> accessTokenResponseEntity =
      restTemplate.exchange(tariffDocsAccessTokenUrl, HttpMethod.POST, tokenRequest, AccessTokenResponse.class);
    return accessTokenResponseEntity.getBody().getAccessToken();
  }
}
