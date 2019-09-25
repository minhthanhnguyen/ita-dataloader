package gov.ita.susastatsdataloader.storage;

import java.util.List;

public interface Storage {
  void initContainer(String containerName);

  void save(String fileName, byte[] fileContent, String user, String containerName);

  String getListBlobsUrl(String containerName);

  List<BlobMetaData> getBlobMetadata(String containerName);
}
