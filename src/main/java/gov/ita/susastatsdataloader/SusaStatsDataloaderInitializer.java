package gov.ita.susastatsdataloader;

import gov.ita.susastatsdataloader.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile({"production", "staging"})
public class SusaStatsDataloaderInitializer implements ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  private Storage storage;

  @Autowired
  private DatabaseInitializer databaseInitializer;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    storage.init();
    databaseInitializer.init();
  }
}
