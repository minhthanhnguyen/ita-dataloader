package gov.ita.dataloader.ingest;

import gov.ita.dataloader.ingest.configuration.DataSetConfig;
import gov.ita.dataloader.ingest.configuration.ReplaceValue;
import gov.ita.dataloader.ingest.configuration.ZipFileConfig;
import gov.ita.dataloader.ingest.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class IngestProcessor {

  private ZipFileExtractor zipFileExtractor;
  private Storage storage;
  private HttpHelper httpHelper;
  private Map<String, IngestProcessorStatus> status;

  public IngestProcessor(ZipFileExtractor zipFileExtractor, Storage storage, HttpHelper httpHelper) {
    this.zipFileExtractor = zipFileExtractor;
    this.storage = storage;
    this.httpHelper = httpHelper;
    status = new HashMap<>();
  }

  public void process(List<DataSetConfig> dataSourceConfigs, String containerName, String userName) {
    log.info("Starting ingest process for container: {}", containerName);
    List<DataSetConfig> enabledConfigs = dataSourceConfigs.stream().filter(DataSetConfig::isEnabled).collect(Collectors.toList());
    IngestProcessorStatus ingestProcessorStatus = initializeStatus(containerName, enabledConfigs.size());

    byte[] fileBytes;
    try {
      for (DataSetConfig dsc : enabledConfigs) {
        log.info("Importing file {} from {} to container {}", dsc.getFileName(), dsc.getUrl(), containerName);

        fileBytes = httpHelper.getBytes(dsc.getUrl());

        processAndSaveDataSource(dsc.getFileName(), fileBytes, dsc.getReplaceValues(), containerName, userName);

        if (dsc.getZipFileConfigs() != null) {
          Map<String, ByteArrayOutputStream> fileMap;
          try {
            fileMap = zipFileExtractor.extract(fileBytes);
          } catch (IOException e) {
            e.printStackTrace();
            throw new IngestProcessorException(
              String.format("%s Could not extract zip file from %s", LocalDateTime.now(), dsc.getUrl()));
          }
          assert fileMap != null;
          for (String fileName : fileMap.keySet()) {
            ZipFileConfig zfc = getZipFileConfig(dsc, fileName);
            processAndSaveDataSource(
              zfc.getDestinationFileName(),
              fileMap.get(fileName).toByteArray(),
              dsc.getReplaceValues(),
              containerName,
              userName);
          }
        }

        updateStatus(dsc, ingestProcessorStatus);

        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
      ingestProcessorStatus.getLog().add(new LogItem(e.getMessage()));
    } finally {
      ingestProcessorStatus.getLog().add(new LogItem("Ingest process complete"));
      ingestProcessorStatus.setIngesting(false);
      log.info("Ingest process complete for container: {}", containerName);
    }
  }

  private IngestProcessorStatus initializeStatus(String containerName, int totalApiCalls) {
    IngestProcessorStatus ips = new IngestProcessorStatus(totalApiCalls, 0, true, new ArrayList<>());
    status.put(containerName, ips);
    return ips;
  }

  private void updateStatus(DataSetConfig dataSetConfig, IngestProcessorStatus ingestProcessorStatus) {
    ingestProcessorStatus.setDatasetsCompleted(ingestProcessorStatus.getDatasetsCompleted() + 1);
    ingestProcessorStatus.getLog().add(new LogItem(String.format("Completed ingest of URL: %s", dataSetConfig.getUrl())));
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
      String message = String.format("Error: The file %s could not be found in the zip file from from: %s", fileName, dsc.getUrl());
      throw new IngestProcessorException(message);
    }
  }

}
