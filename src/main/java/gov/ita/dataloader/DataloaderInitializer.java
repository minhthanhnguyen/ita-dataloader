package gov.ita.dataloader;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ita.dataloader.configuration.DataLoaderConfigResponse;
import gov.ita.dataloader.datawarehouse.DataWarehouseInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static gov.ita.dataloader.ResourceHelper.getResourceAsString;

@Slf4j
@Component
public class DataloaderInitializer implements ApplicationListener<ContextRefreshedEvent> {

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
      dataWarehouseInitializer.saveConfiguration(dataLoaderConfigResponse.getDataSetConfigs()); //TODO: Remove after initial deployment
      dataWarehouseInitializer.saveBusinessUnits(dataLoaderConfigResponse.getBusinessUnits());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
