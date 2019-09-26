package gov.ita.dataloader.ingest.configuration;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class DataSetConfig {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 1000)
  private String url;
  private boolean enabled;
  private String fileName;
  private String containerName;

  @OneToMany
  private List<ReplaceValue> replaceValues;

  @OneToMany
  private List<ZipFileConfig> zipFileConfigs;
}
