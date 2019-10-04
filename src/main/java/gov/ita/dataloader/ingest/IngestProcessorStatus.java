package gov.ita.dataloader.ingest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
class IngestProcessorStatus {
  Integer datasetsQueued;
  Integer datasetsCompleted;
  boolean ingesting;
  List<LogItem> log;
}
