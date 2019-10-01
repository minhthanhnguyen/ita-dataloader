package gov.ita.dataloader.ingest.configuration;

import lombok.Data;

import java.util.List;

@Data
public class DataloaderConfig {
  public List<DataSetConfig> dataSetConfigs;
}
