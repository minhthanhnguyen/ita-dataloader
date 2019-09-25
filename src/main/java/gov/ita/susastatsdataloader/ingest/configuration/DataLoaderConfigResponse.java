package gov.ita.susastatsdataloader.ingest.configuration;

import lombok.Data;

import java.util.List;

@Data
public class DataLoaderConfigResponse {
  public List<DataSetConfig> dataSetConfigs;
}
