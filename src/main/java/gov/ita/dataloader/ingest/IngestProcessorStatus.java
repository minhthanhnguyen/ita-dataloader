package gov.ita.dataloader.ingest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class IngestProcessorStatus {
  Integer totalApiCalls;
  Integer processedApiCalls;
}
