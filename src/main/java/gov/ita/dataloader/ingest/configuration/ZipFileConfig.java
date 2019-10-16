package gov.ita.dataloader.ingest.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ZipFileConfig {
  String originalFileName;
  String destinationFileName;
  List<ReplaceValue> replaceValues;
}
