package gov.ita.dataloader.ingest.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Profile("development")
public class DevelopmentStorage implements Storage {

  private Map<String, Map<String, String>> storageContent = new HashMap<>();

  @Override
  public void initContainer(String containerName) {
    log.info("Initializing container: {}", containerName);
  }

  @Override
  public void save(String fileName, byte[] fileContent, String user, String containerName) {
    log.info("Saving blob: {}, {}, {}", fileName, containerName, user);

    Map<String, String> containerContent = storageContent.get(containerName);
    if (containerContent == null)
      containerContent = new HashMap<>();

    containerContent.put(fileName, user);
    storageContent.put(containerName, containerContent);
  }

  @Override
  public String getListBlobsUrl(String containerName) {
    return "http://cool.io";
  }

  @Override
  public List<BlobMetaData> getBlobMetadata(String containerName) {
    List<BlobMetaData> blobMetaDataList = new ArrayList<>();
    for (String container : storageContent.keySet()) {
      if (container.equals(containerName)) {
        Map<String, String> containerContent = storageContent.get(container);
        for (String fileName : containerContent.keySet()) {
          BlobMetaData blobMetaData = new BlobMetaData(
            fileName,
            String.format("http://some-cloud-strage-url.com/%s/%s", containerName, fileName),
            123L, OffsetDateTime.now(),
            containerContent.get(fileName));
          blobMetaDataList.add(blobMetaData);
        }
      }
    }

    return blobMetaDataList;
  }
}
