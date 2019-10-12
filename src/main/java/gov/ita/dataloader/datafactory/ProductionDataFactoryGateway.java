package gov.ita.dataloader.datafactory;

import gov.ita.dataloader.HttpHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"production", "staging"})
public class ProductionDataFactoryGateway implements DataFactoryGateway {

  @Value("${datafactory-status-url}")
  private String dataFactoryLogUrl;

  @Autowired
  private HttpHelper httpHelper;

  @Override
  public byte[] getPipelineStatus(String pipelineName) {
    try {
      return httpHelper.getBytes(dataFactoryLogUrl + "?pipelineName=" + pipelineName);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
