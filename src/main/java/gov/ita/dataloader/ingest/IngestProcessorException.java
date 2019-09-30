package gov.ita.dataloader.ingest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IngestProcessorException extends Exception {

  public IngestProcessorException(String message) {
    super(message);
    log.error(message);
  }
}
