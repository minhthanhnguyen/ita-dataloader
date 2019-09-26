package gov.ita.susastatsdataloader;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ita.susastatsdataloader.configuration.DataLoaderConfigResponse;
import gov.ita.susastatsdataloader.datawarehouse.DataWarehouseInitializer;
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
  private DataWarehouseInitializer dataWarehouseInitializer;

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    log.info("Initializing database");
    dataWarehouseInitializer.init();

    try {
      String configJson = getResourceAsString("/fixtures/configuration.json");
      DataLoaderConfigResponse dataLoaderConfigResponse = objectMapper.readValue(configJson, DataLoaderConfigResponse.class);
      dataWarehouseInitializer.saveConfiguration(dataLoaderConfigResponse.getDataSetConfigs());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
