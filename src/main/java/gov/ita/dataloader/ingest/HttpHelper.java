package gov.ita.dataloader.ingest;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelper {
  public static byte[] getBytes(String url) throws Exception {
    try {
      HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
      int responseCode = con.getResponseCode();
      if (responseCode != 200) throw new Exception("Failed to retrieve data from url: " + url);
      return IOUtils.toByteArray(con.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
      throw new Exception("Failed to retrieve data from url: " + url);
    }
  }
}
