package gov.ita.dataloader;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ResourceHelper {
  public static String getResourceAsString(String path) {
    InputStream in = DataloaderApplication.class.getResourceAsStream(path);
    try {
      return IOUtils.toString(new InputStreamReader(in));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Map<String, String> getResources(String root) {
    Map<String, String> fileContentsList = new HashMap<>();
    try (
      InputStream in = DataloaderApplication.class.getResourceAsStream(root);
      BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
      String resource;

      while ((resource = br.readLine()) != null) {
        fileContentsList.put(resource, root + "/" + resource);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return fileContentsList;
  }
}
