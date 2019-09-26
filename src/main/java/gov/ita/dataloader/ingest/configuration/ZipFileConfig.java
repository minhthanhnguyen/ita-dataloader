package gov.ita.dataloader.ingest.configuration;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class ZipFileConfig {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;
  String originalFileName;
  String destinationFileName;
}
