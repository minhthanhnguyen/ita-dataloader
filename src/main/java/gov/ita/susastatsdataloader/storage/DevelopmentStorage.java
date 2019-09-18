package gov.ita.susastatsdataloader.storage;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Profile("development")
public class DevelopmentStorage implements Storage {

  @Override
  public void save(String fileName, byte[] fileContent, String user) {
    System.out.println(user);
    System.out.println(fileName);
    System.out.println(Arrays.toString(fileContent));
  }

  @Override
  public boolean containerExists() {
    return false;
  }

  @Override
  public void createContainer() {

  }

  @Override
  public String getBlobAsString(String blobName) {
    if (blobName.equals("countries.json"))
      return getResourceAsString("/fixtures/countries.json");
    else
      return getResourceAsString("/fixtures/open-data-catalog.json");
  }

  @Override
  public String getListBlobsUrl() {
    return "blobs url";
  }

  @Override
  public List<BlobMetaData> getBlobMetadata() {
    return Collections.emptyList();
  }

  private String getResourceAsString(String resource) {
    InputStream inputStream = DevelopmentStorage.class.getResourceAsStream(resource);
    try {
      return IOUtils.toString(new InputStreamReader(inputStream));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }
}
