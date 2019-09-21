package gov.ita.susastatsdataloader;

import gov.ita.susastatsdataloader.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    byte[] configBytes = Objects.requireNonNull(getResourceAsString("/fixtures/configuration.json")).getBytes();
    storage.save("configuration.json", configBytes, null);
    jdbcTemplate.execute(getResourceAsString("/db/migration/V1.0__Initial_Schema.sql"));
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

  @Bean
  public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
