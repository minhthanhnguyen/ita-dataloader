package gov.ita.susastatsdataloader.ingest.configuration;

import lombok.Data;

@Data
public class ZipFileConfig {
  String originalFileName;
  String destinationFileName;
}
