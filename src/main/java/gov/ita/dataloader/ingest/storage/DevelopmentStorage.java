package gov.ita.dataloader.ingest.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Profile("development")
public class DevelopmentStorage implements Storage {

  private Map<String, byte[]> storageContent = new HashMap<>();

  @Override
  public void initContainer(String containerName) {
    log.info("Initializing container: {}", containerName);
  }

  @Override
  public void save(String fileName, byte[] fileContent, String user, String containerName) {
    log.info("Saving blob: {}, {}, {}", fileName, containerName, user);
    storageContent.put(fileName, fileContent);
  }

  @Override
  public String getListBlobsUrl(String containerName) {
    return "http://cool.io";
  }

  @Override
  public List<BlobMetaData> getBlobMetadata(String containerName) {
    List<BlobMetaData> blobMetaDataList = new ArrayList<>();

    for (String fileName : storageContent.keySet()) {
      byte[] fileContent = storageContent.get(fileName);
      BlobMetaData blobMetaData = new BlobMetaData(fileName, "http://" + fileName, 123L, null);
      blobMetaDataList.add(blobMetaData);
    }

    return blobMetaDataList;
  }
}
