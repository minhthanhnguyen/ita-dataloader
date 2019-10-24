package gov.ita.dataloader.storage;

import com.microsoft.azure.storage.blob.*;
import com.microsoft.azure.storage.blob.models.BlobFlatListSegment;
import com.microsoft.azure.storage.blob.models.BlobHTTPHeaders;
import com.microsoft.azure.storage.blob.models.ContainerItem;
import com.microsoft.azure.storage.blob.models.PublicAccessType;
import com.microsoft.rest.v2.http.HttpPipeline;
import gov.ita.dataloader.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Profile({"production", "staging"})
public class ProductionStorage implements Storage {

  @Value("${storage-params.azure-storage-account}")
  private String accountName;

  @Value("${storage-params.azure-storage-account-key}")
  private String accountKey;

  @Autowired
  private HttpHelper httpHelper;

  @Override
  public void createContainer(String containerName) {
    log.info("Initializing container: {}", containerName);
    makeContainerUrl(containerName)
      .create(null, PublicAccessType.CONTAINER, null)
      .blockingGet();
  }

  @Override
  public void save(String fileName, byte[] fileContent, String user, String containerName, Boolean userUpload) {
    try {
      if (user == null) user = accountName;

      ContainerURL containerURL = makeContainerUrl(containerName);
      BlockBlobURL blobURL = containerURL.createBlockBlobURL(fileName);

      File tmpFile = File.createTempFile("tmpFile", ".tmp");
      OutputStream os = new FileOutputStream(tmpFile);
      os.write(fileContent);
      os.close();

      AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(Paths.get(tmpFile.getAbsolutePath()));
      TransferManagerUploadToBlockBlobOptions options =
        new TransferManagerUploadToBlockBlobOptions(
          null,
          makeHeader(fileName),
          makeMetaData(user, userUpload),
          null,
          10);
      TransferManager.uploadFileToBlockBlob(fileChannel, blobURL, 1000000, 20000000, options).blockingGet();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getListBlobsUrl(String containerName) {
    return makeBaseStorageUrl(containerName) + "?restype=container&comp=list";
  }

  private String makeBaseStorageUrl(String containerName) {
    return String.format("https://%s.blob.core.windows.net/%s", accountName, containerName);
  }

  @Override
  public List<BlobMetaData> getBlobMetadata(String containerName) {
    ListBlobsOptions listBlobsOptions = new ListBlobsOptions();
    BlobListDetails details = new BlobListDetails();
    details.withMetadata(true);
    listBlobsOptions.withDetails(details);
    BlobFlatListSegment segment = makeContainerUrl(containerName)
      .listBlobsFlatSegment(null, listBlobsOptions, null).blockingGet().body().segment();
    if (segment != null && segment.blobItems() != null) {
      return segment.blobItems()
        .stream().map(
          x -> new BlobMetaData(
            x.name(),
            buildUrlForBlob(x.name(), containerName),
            x.properties().contentLength(),
            containerName,
            x.properties().lastModified(),
            x.metadata()
          )).filter(item -> !item.fileName.startsWith("adfpolybaserejectedrows"))
        .collect(Collectors.toList());
    }

    return Collections.emptyList();
  }

  @Override
  public Set<String> getContainerNames() {
    return makeServiceURL().listContainersSegment(null, null)
      .blockingGet().body()
      .containerItems().stream().map(ContainerItem::name)
      .collect(Collectors.toSet());
  }

  @Override
  public byte[] getBlob(String containerName, String blobName) {
    Optional<BlobMetaData> blobMetaData = getBlobMetadata(containerName).stream().filter(b -> b.getFileName().equals(blobName)).findFirst();
    if (blobMetaData.isPresent()) {
      try {
        return httpHelper.getBytes(blobMetaData.get().getUrl());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  private String buildUrlForBlob(String name, String containerName) {
    return String.format("https://%s.blob.core.windows.net/%s/%s", accountName, containerName, name);
  }

  private ContainerURL makeContainerUrl(String containerName) {
    ServiceURL serviceURL = makeServiceURL();
    assert serviceURL != null;
    return serviceURL.createContainerURL(containerName);
  }

  private ServiceURL makeServiceURL() {
    try {
      SharedKeyCredentials credential = new SharedKeyCredentials(accountName, accountKey);
      HttpPipeline pipeline = StorageURL.createPipeline(credential, new PipelineOptions());
      URL url = new URL(String.format("https://%s.blob.core.windows.net/", accountName));
      return new ServiceURL(url, pipeline);
    } catch (InvalidKeyException | MalformedURLException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Metadata makeMetaData(String uploadedBy, Boolean userUpload) {
    Metadata metadata = new Metadata();
    metadata.put("uploaded_by", uploadedBy);
    metadata.put("user_upload", userUpload.toString());
    return metadata;
  }

  private BlobHTTPHeaders makeHeader(String fileName) {
    String contentType = null;

    if (fileName.endsWith(".zip"))
      contentType = "application/zip";
    if (fileName.endsWith(".json"))
      contentType = "application/json";
    if (fileName.endsWith(".csv"))
      contentType = "text/csv";
    if (fileName.endsWith(".xml"))
      contentType = "application/xml";
    if (fileName.endsWith(".txt"))
      contentType = "text/plain";

    BlobHTTPHeaders headers = new BlobHTTPHeaders();
    headers.withBlobContentType(contentType);
    return headers;
  }
}
