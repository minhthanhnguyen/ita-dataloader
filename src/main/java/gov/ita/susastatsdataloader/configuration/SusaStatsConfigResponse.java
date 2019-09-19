package gov.ita.susastatsdataloader.configuration;

import lombok.Data;

import java.util.List;

@Data
public class SusaStatsConfigResponse {
  public List<DataSourceConfig> dataSourceConfigs;
}
