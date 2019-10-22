package gov.ita.dataloader;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ita.dataloader.storage.StorageInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataloaderInitializer implements ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private StorageInitializer storageInitializer;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    log.info("Initializing storage");
    storageInitializer.init();
  }
}
