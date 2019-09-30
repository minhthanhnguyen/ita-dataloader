package gov.ita.dataloader.ingest;

import gov.ita.dataloader.ingest.configuration.DataSetConfig;
import gov.ita.dataloader.ingest.configuration.ReplaceValue;
import gov.ita.dataloader.ingest.configuration.ZipFileConfig;
import gov.ita.dataloader.ingest.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IngestProcessor {

  private ZipFileExtractor zipFileExtractor;
  private RestTemplate restTemplate;
  private Storage storage;
  private Map<String, IngestProcessorStatus> status;

  public IngestProcessor(ZipFileExtractor zipFileExtractor, RestTemplate restTemplate, Storage storage) {
    this.zipFileExtractor = zipFileExtractor;
    this.restTemplate = restTemplate;
    this.storage = storage;
    status = new HashMap<>();
  }

  public Map<String, String> process(List<DataSetConfig> dataSourceConfigs, String containerName, String userName) {
    Map<String, String> results = new HashMap<>();

    log.info("Starting ingest process for container: {}", containerName);
    List<DataSetConfig> enabledConfigs = dataSourceConfigs.stream().filter(DataSetConfig::isEnabled).collect(Collectors.toList());
    initializeStatus(containerName, enabledConfigs.size());

    byte[] fileBytes;

    try {
      for (DataSetConfig dsc : enabledConfigs) {
        log.info("Importing file {} from {} to container {}", dsc.getFileName(), dsc.getUrl(), dsc.getContainerName());
        fileBytes = httpGetBytes(dsc.getUrl());
        processAndSaveDataSource(dsc.getFileName(), fileBytes, dsc.getReplaceValues(), dsc.getContainerName(), userName);

        if (dsc.getZipFileConfigs() != null) {
          Map<String, ByteArrayOutputStream> fileMap;
          try {
            fileMap = zipFileExtractor.extract(fileBytes);
          } catch (IOException e) {
            e.printStackTrace();
            String message = String.format("Could not extract zip file from %s", dsc.getUrl());
            throw new IngestProcessorException(message);
          }
          assert fileMap != null;
          for (String fileName : fileMap.keySet()) {
            ZipFileConfig zfc = getZipFileConfig(dsc, fileName);
            processAndSaveDataSource(
              zfc.getDestinationFileName(),
              fileMap.get(fileName).toByteArray(),
              dsc.getReplaceValues(),
              dsc.getContainerName(),
              userName);
          }

          updateStatus(containerName);

          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }

      }
    } catch (IngestProcessorException e) {
      results.put("error", e.getMessage());
    }

    log.info("Ingest process complete for container: {}", containerName);
    if (results.get("error") != null) return results;

    results.put("success", "Ingest process succeeded");
    return results;
  }

  private void initializeStatus(String containerName, int totalApiCalls) {
    status.put(containerName, new IngestProcessorStatus(totalApiCalls, 0));
  }

  private void updateStatus(String containerName) {
    IngestProcessorStatus ips = status.get(containerName);
    ips.setProcessedApiCalls(ips.getProcessedApiCalls() + 1);
  }

  IngestProcessorStatus getStatus(String containerName) {
    return status.get(containerName);
  }

  private byte[] replace(byte[] fileBytes, String replace, String with) {
    return new String(fileBytes).replaceAll(replace, with).getBytes();
  }

  private void processAndSaveDataSource(String fileName,
                                        byte[] fileBytes,
                                        List<ReplaceValue> replaceValues,
                                        String containerName,
                                        String userName) {
    if (replaceValues != null) {
      for (ReplaceValue rv : replaceValues) {
        fileBytes = replace(fileBytes, rv.getReplaceThis(), rv.getWithThis());
      }
    }
    storage.save(fileName, fileBytes, userName, containerName);
  }

  private ZipFileConfig getZipFileConfig(DataSetConfig dsc, String fileName) throws IngestProcessorException {
    try {
      return dsc.getZipFileConfigs().stream()
        .filter(zipFileContent -> zipFileContent.getOriginalFileName().equals(fileName))
        .collect(Collectors.toList())
        .get(0);
    } catch (IndexOutOfBoundsException e) {
      String message = String.format("The file %s could not be found in the zip file from from: %s", fileName, dsc.getUrl());
      throw new IngestProcessorException(message);
    }
  }

  private byte[] httpGetBytes(String url) {
    return restTemplate.getForEntity(url, byte[].class).getBody();
  }
}
