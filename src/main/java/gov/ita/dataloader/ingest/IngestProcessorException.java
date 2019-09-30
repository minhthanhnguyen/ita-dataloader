package gov.ita.dataloader.ingest;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
class IngestProcessorException extends Exception {

  IngestProcessorException(String message) {
    super(LocalDateTime.now() + " " + message);
    log.error(message);
  }

}
