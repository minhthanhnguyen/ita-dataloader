package gov.ita.susastatsdataloader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile({"development"})
public class DevelopmentDatabaseInitializer extends DatabaseInitializer {

  @Override
  public void init() {
  }

}
