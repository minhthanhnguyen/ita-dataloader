package gov.ita.susastatsdataloader.configuration;

import lombok.Data;

import java.util.List;

@Data
public class DataLoaderConfigResponse {
  public List<DataSetConfig> dataSetConfigs;
}
