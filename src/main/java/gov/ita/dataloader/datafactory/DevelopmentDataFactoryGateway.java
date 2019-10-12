package gov.ita.dataloader.datafactory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("development")
public class DevelopmentDataFactoryGateway implements DataFactoryGateway {
  @Override
  public byte[] getPipelineStatus(String pipelineName) {
    String pipelineStatus = "{" +
      "\"piplineName\": \"hello\", " +
      "\"lastUpdated\": \"2019-10-12T12:00:02.321045\", " +
      "\"durationInMs\": 123, " +
      "\"status\": \"Succeeded\", " +
      "\"message\": \"\" " +
      "}";
    return pipelineStatus.getBytes();
  }
}
