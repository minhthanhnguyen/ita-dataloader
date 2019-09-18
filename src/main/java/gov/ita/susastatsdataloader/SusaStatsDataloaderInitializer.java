package gov.ita.susastatsdataloader;

import gov.ita.susastatsdataloader.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
@Component
public class SusaStatsDataloaderInitializer implements ApplicationListener<ContextRefreshedEvent> {

  private Storage storage;

  public SusaStatsDataloaderInitializer(Storage storage) {
    this.storage = storage;
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    if (!storage.containerExists()) {
      log.info("Initializing storage");
      storage.createContainer();
    }
    storage.save("configuration.json", getResourceAsString("/fixtures/configuration.json").getBytes(), null);
  }

  private String getResourceAsString(String resource) {
    InputStream inputStream = SusaStatsDataloaderInitializer.class.getResourceAsStream(resource);
    try {
      return IOUtils.toString(new InputStreamReader(inputStream));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }
}
