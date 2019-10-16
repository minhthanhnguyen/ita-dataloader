package gov.ita.dataloader.ingest.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReplaceValue {
  String replaceThis;
  String withThis;
}
