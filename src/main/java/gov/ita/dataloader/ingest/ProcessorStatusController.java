package gov.ita.dataloader.ingest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ProcessorStatusController {

  @Autowired
  private ProcessorStatusService processorStatusService;

  @GetMapping(value = "/api/automated-ingest/status", produces = MediaType.APPLICATION_JSON_VALUE)
  public ManualIngestProcessorStatus getIngestProcessorStatus(@RequestParam("containerName") String containerName) {
    return processorStatusService.getIngestProcessorStatusMap().get(containerName);
  }

  @GetMapping(value = "/api/manual-ingest/status", produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, ManualIngestTranslationStatus> getTranslationStatus(@RequestParam("containerName") String containerName) {
    return processorStatusService.getIngestTranslationStatusMap().get(containerName);
  }

}
