package gov.ita.dataloader.datafactory;

public interface DataFactoryGateway {
  byte[] getPipelineStatus(String pipelineName);
}
