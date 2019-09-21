package gov.ita.susastatsdataloader.configuration;

import lombok.Data;

import java.util.List;

@Data
public class DataSourceConfig {
  String url;
  String destinationFileName;
  List<ReplaceValue> replaceValues;
  List<ZipFileConfig> zipFileConfigs;
}
