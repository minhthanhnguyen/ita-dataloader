package gov.ita.susastatsdataloader;

import gov.ita.susastatsdataloader.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@Profile({"production", "staging"})
public class SusaStatsDataloaderInitializer implements ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  private Storage storage;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    if (!storage.containerExists()) {
      log.info("Initializing storage");
      storage.createContainer();
    }

    log.info("Saving configuration.json to storage");
    byte[] configBytes = Objects.requireNonNull(getResourceAsString("/fixtures/configuration.json")).getBytes();
    storage.save("configuration.json", configBytes, null);

    for (String sqlScriptPath : getSqlScriptPaths()) {
      log.info("Executing {}", sqlScriptPath);
      jdbcTemplate.execute(getResourceAsString(sqlScriptPath));
    }
  }

  private String getResourceAsString(String path) {
    InputStream in = SusaStatsDataloaderInitializer.class.getResourceAsStream(path);
    try {
      return IOUtils.toString(new InputStreamReader(in));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private List<String> getSqlScriptPaths() {
    List<String> fileContentsList = new ArrayList<>();
    String sqlScriptRootDirectory = "/db/migration";
    try (
      InputStream in = SusaStatsDataloaderInitializer.class.getResourceAsStream(sqlScriptRootDirectory);
      BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
      String resource;

      while ((resource = br.readLine()) != null) {
        fileContentsList.add(sqlScriptRootDirectory + "/" + resource);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return fileContentsList;
  }
}
