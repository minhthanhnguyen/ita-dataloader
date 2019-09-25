package gov.ita.susastatsdataloader.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Profile("development")
public class DevelopmentStorage implements Storage {

  @Override
  public void init() {
    log.info("Skipping storage initialization");
  }

  @Override
  public void save(String fileName, byte[] fileContent, String user) {
    System.out.println(user);
    System.out.println(fileName);
    System.out.println(Arrays.toString(fileContent));
  }

  public boolean containerExists() {
    return false;
  }

  public void createContainer() {

  }

  @Override
  public String getBlobAsString(String blobName) {
    return "Blob as string";
  }

  @Override
  public String getListBlobsUrl() {
    return "blobs url";
  }

  @Override
  public List<BlobMetaData> getBlobMetadata() {
    return Collections.emptyList();
  }

}
