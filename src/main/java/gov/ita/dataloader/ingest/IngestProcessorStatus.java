package gov.ita.dataloader.ingest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
class IngestProcessorStatus {
  Integer totalUrlCallsQueued;
  Integer processedUrlCalls;
  boolean processing;
  List<String> log;
}
