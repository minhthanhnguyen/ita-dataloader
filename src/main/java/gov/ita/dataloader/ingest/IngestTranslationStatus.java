package gov.ita.dataloader.ingest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IngestTranslationStatus {
  String fileName;
  Integer totalPages;
  Integer currentPage;
  Phase phase;

  public boolean isProcessing() {
    return !phase.equals(Phase.DONE);
  }
}
