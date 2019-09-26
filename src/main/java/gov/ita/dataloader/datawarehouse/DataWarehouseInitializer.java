package gov.ita.dataloader.datawarehouse;

import gov.ita.dataloader.ingest.configuration.DataSetConfig;
import gov.ita.dataloader.ingest.configuration.DataSetConfigRepository;
import gov.ita.dataloader.ingest.configuration.ReplaceValueRepository;
import gov.ita.dataloader.ingest.configuration.ZipFileConfigRepository;
import gov.ita.dataloader.ingest.configuration.business.BusinessUnit;
import gov.ita.dataloader.ingest.configuration.business.BusinessUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class DataWarehouseInitializer {

  @Autowired
  private DataSetConfigRepository dataSetConfigRepository;

  @Autowired
  private ReplaceValueRepository replaceValueRepository;

  @Autowired
  private ZipFileConfigRepository zipFileConfigRepository;

  @Autowired
  private BusinessUnitRepository businessUnitRepository;

  public abstract void init();

  public void saveConfiguration(List<DataSetConfig> dataSetConfigs) {
    dataSetConfigs.stream().map(DataSetConfig::getReplaceValues).forEach(replaceValues -> {
      if (replaceValues != null)
        replaceValueRepository.saveAll(replaceValues);
    });
    dataSetConfigs.stream().map(DataSetConfig::getZipFileConfigs).forEach(zipFileConfigs -> {
      if (zipFileConfigs != null)
        zipFileConfigRepository.saveAll(zipFileConfigs);
    });
    dataSetConfigRepository.saveAll(dataSetConfigs);
  }

  public void saveBusinessUnits(List<BusinessUnit> businessUnits) {
    businessUnitRepository.saveAll(businessUnits);
  }
}
