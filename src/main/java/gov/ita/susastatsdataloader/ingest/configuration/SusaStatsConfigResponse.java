package gov.ita.susastatsdataloader.ingest.configuration;

import lombok.Data;

import java.util.List;

@Data
public class SusaStatsConfigResponse {
  public List<DataSetConfig> dataSetConfigs;
}
