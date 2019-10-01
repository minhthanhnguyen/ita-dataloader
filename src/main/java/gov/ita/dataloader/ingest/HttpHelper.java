package gov.ita.dataloader.ingest;

public interface HttpHelper {
  byte[] getBytes(String url) throws Exception;
}
