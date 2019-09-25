package gov.ita.susastatsdataloader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static gov.ita.susastatsdataloader.ResourceHelper.getResourceAsString;

@Slf4j
@Service
@Profile("production")
public class ProductionDatabaseInitializer implements DatabaseInitializer {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void init() {
    log.info("Initializing database");
    for (String sqlScriptPath : getSqlScriptPaths()) {
      log.info("Executing {}", sqlScriptPath);
      jdbcTemplate.execute(getResourceAsString(sqlScriptPath));
    }
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

  @Bean
  public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
