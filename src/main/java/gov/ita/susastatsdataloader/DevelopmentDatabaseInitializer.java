package gov.ita.susastatsdataloader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile({"staging", "development"})
public class DevelopmentDatabaseInitializer implements DatabaseInitializer {
  @Override
  public void init() {
    log.info("Skipping database initialization");
  }
}
