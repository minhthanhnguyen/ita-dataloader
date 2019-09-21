package gov.ita.susastatsdataloader.ingest.configuration;

import lombok.Data;

import java.util.List;

@Data
public class DataSetConfig {
  String url;
  String destinationFileName;
  List<ReplaceValue> replaceValues;
  List<ZipFileConfig> zipFileConfigs;
}
