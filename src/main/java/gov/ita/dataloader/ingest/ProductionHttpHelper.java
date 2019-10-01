package gov.ita.dataloader.ingest;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

@Service
@Profile({"staging", "production"})
public class ProductionHttpHelper implements HttpHelper {
  public byte[] getBytes(String url) throws Exception {
    try {
      HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
      return IOUtils.toByteArray(con.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
      throw new Exception(LocalDateTime.now() + " Failed to retrieve data from URL: " + url);
    }
  }
}
