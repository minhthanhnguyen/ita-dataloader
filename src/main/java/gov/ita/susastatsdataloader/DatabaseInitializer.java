package gov.ita.susastatsdataloader;

import gov.ita.susastatsdataloader.ingest.configuration.DataSetConfig;
import gov.ita.susastatsdataloader.ingest.configuration.DataSetConfigRepository;
import gov.ita.susastatsdataloader.ingest.configuration.ReplaceValueRepository;
import gov.ita.susastatsdataloader.ingest.configuration.ZipFileConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

abstract class DatabaseInitializer {

  @Autowired
  private DataSetConfigRepository dataSetConfigRepository;

  @Autowired
  private ReplaceValueRepository replaceValueRepository;

  @Autowired
  private ZipFileConfigRepository zipFileConfigRepository;

  abstract void init();

  void saveConfiguration(List<DataSetConfig> dataSetConfigs) {
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
