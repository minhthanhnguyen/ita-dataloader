package gov.ita.susastatsdataloader;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ita.susastatsdataloader.ingest.configuration.DataLoaderConfigResponse;
import gov.ita.susastatsdataloader.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static gov.ita.susastatsdataloader.ResourceHelper.getResourceAsString;

@Slf4j
@Component
public class SusaStatsDataloaderInitializer implements ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  private Storage storage;

  @Autowired
  private DatabaseInitializer databaseInitializer;

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    log.info("Initializing database");
    databaseInitializer.init();

    try {
      String configJson = getResourceAsString("/fixtures/configuration.json");
      DataLoaderConfigResponse dataLoaderConfigResponse = objectMapper.readValue(configJson, DataLoaderConfigResponse.class);
      databaseInitializer.saveConfiguration(dataLoaderConfigResponse.getDataSetConfigs());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
