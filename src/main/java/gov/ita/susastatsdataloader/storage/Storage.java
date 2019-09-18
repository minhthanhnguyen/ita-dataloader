package gov.ita.susastatsdataloader.storage;

import java.util.List;

public interface Storage {
  void save(String fileName, byte[] fileContent, String user);

  boolean containerExists();

  void createContainer();

  String getBlobAsString(String blobName);

  String getListBlobsUrl();

  List<BlobMetaData> getBlobMetadata();
}
