package gov.ita.dataloader.ingest;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Data
@Service
public class ProcessorStatusService {
  private Map<String, IngestProcessorStatus> ingestProcessorStatusMap = new HashMap<>();
  private Map<String, Map<String, IngestTranslationStatus>> ingestTranslationStatusMap = new HashMap<>();

  public void updateIngestProcessorStatus(String containerName, IngestProcessorStatus ingestProcessorStatus) {
    ingestProcessorStatusMap.put(containerName, ingestProcessorStatus);
  }

  public void updateTranslationProcessorStatus(String containerName, String fileName, IngestTranslationStatus ingestProcessorStatus) {
    Map<String, IngestTranslationStatus> ingestTranslationStatusFileMap = ingestTranslationStatusMap.computeIfAbsent(containerName, k -> new HashMap<>());
    ingestTranslationStatusFileMap.put(fileName, ingestProcessorStatus);
  }

  public boolean isIngesting(String containerName) {
    return ingestProcessorStatusMap.get(containerName).ingesting;
  }

  public boolean isProcessing(String containerName, String fileName) {
    Map<String, IngestTranslationStatus> stringIngestTranslationStatusMap = ingestTranslationStatusMap.get(containerName);
    if (stringIngestTranslationStatusMap != null) {
      IngestTranslationStatus ingestTranslationStatus = stringIngestTranslationStatusMap.get(fileName);
      if (ingestTranslationStatus != null) {
        return ingestTranslationStatus.isProcessing();
      } else {
        return false;
      }
    } else {
      return false;
    }
  }
}
