package gov.ita.dataloader;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceHelper {
  public static String getResourceAsString(String path) {
    InputStream in = DataloaderInitializer.class.getResourceAsStream(path);
    try {
      return IOUtils.toString(new InputStreamReader(in));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
