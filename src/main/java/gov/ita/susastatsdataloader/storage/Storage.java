package gov.ita.susastatsdataloader.storage;

import java.util.List;

public interface Storage {
  void init();

  void save(String fileName, byte[] fileContent, String user);

  String getBlobAsString(String blobName);

  String getListBlobsUrl();

  List<BlobMetaData> getBlobMetadata();
}
