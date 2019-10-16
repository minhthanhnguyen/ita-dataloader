package gov.ita.dataloader.ingest.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DataSetConfig {
  private String url;
  private boolean enabled;
  private String fileName;
  private List<ReplaceValue> replaceValues;
  private List<ZipFileConfig> zipFileConfigs;
}
