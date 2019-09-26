package gov.ita.susastatsdataloader.datawarehouse;

import gov.ita.susastatsdataloader.configuration.DataSetConfig;
import gov.ita.susastatsdataloader.configuration.DataSetConfigRepository;
import gov.ita.susastatsdataloader.configuration.ReplaceValueRepository;
import gov.ita.susastatsdataloader.configuration.ZipFileConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class DataWarehouseInitializer {

  @Autowired
  private DataSetConfigRepository dataSetConfigRepository;

  @Autowired
  private ReplaceValueRepository replaceValueRepository;

  @Autowired
  private ZipFileConfigRepository zipFileConfigRepository;

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

}
