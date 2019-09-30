package gov.ita.dataloader.datawarehouse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

import static gov.ita.dataloader.ResourceHelper.getResourceAsString;
import static gov.ita.dataloader.ResourceHelper.getResources;

@Slf4j
@Service
@Profile({"staging", "production"})
public class ProductionDataWarehouseInitializer extends DataWarehouseInitializer {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void init() {
    Map<String, String> sqlScriptPaths = getResources("/db/migration");
    for (String fileName : sqlScriptPaths.keySet()) {
      String filePath = sqlScriptPaths.get(fileName);
      log.info("Executing {}", filePath);
      jdbcTemplate.execute(getResourceAsString(filePath));
    }
  }
}
