package gov.ita.susastatsdataloader.ingest.storage;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {
  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.additionalMessageConverters(new ByteArrayHttpMessageConverter()).build();
  }
}
