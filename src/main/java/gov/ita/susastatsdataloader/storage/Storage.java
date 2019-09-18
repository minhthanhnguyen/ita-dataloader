package gov.ita.susastatsdataloader.storage;

public interface Storage {
  void save(String fileName, String fileContent, String user);

  boolean containerExists();

  void createContainer();

  String getBlobAsString(String blobName);

}
