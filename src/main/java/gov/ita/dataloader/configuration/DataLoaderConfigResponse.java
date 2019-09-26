package gov.ita.dataloader.configuration;

import gov.ita.dataloader.configuration.business.BusinessUnit;
import lombok.Data;

import java.util.List;

@Data
public class DataLoaderConfigResponse {
  public List<DataSetConfig> dataSetConfigs;
  public List<BusinessUnit> businessUnits;
}
