package gov.ita.dataloader.ingest.configuration;

import lombok.Data;

import java.util.List;

@Data
public class DataSetConfig {
  private String url;
  private boolean enabled;
  private String fileName;
  private String containerName;
  private List<ReplaceValue> replaceValues;
  private List<ZipFileConfig> zipFileConfigs;
}
