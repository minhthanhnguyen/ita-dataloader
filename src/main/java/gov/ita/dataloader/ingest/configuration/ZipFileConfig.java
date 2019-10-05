package gov.ita.dataloader.ingest.configuration;

import lombok.Data;

import java.util.List;

@Data
public class ZipFileConfig {
  String originalFileName;
  String destinationFileName;
  List<ReplaceValue> replaceValues;
}
