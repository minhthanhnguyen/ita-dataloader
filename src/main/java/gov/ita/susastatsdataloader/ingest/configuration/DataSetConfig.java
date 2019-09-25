package gov.ita.susastatsdataloader.ingest.configuration;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class DataSetConfig {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String url;
  private boolean enabled;
  private String fileName;
  private String containerName;

  @OneToMany
  private List<ReplaceValue> replaceValues;

  @OneToMany
  private List<ZipFileConfig> zipFileConfigs;
}
