package gov.ita.dataloader.ingest.configuration;

import lombok.Data;

@Data
public class ReplaceValue {
  String replaceThis;
  String withThis;
}
