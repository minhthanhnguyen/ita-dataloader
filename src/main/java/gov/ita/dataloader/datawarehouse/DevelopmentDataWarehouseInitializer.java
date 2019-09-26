package gov.ita.dataloader.datawarehouse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile({"development"})
public class DevelopmentDataWarehouseInitializer extends DataWarehouseInitializer {

  @Override
  public void init() {
  }

}
