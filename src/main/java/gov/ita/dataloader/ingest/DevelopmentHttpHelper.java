package gov.ita.dataloader.ingest;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("development")
public class DevelopmentHttpHelper implements HttpHelper {
  public byte[] getBytes(String url) throws Exception {
    return "someBytes".getBytes();
  }
}
