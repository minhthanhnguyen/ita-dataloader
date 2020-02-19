package gov.ita.dataloader.storage;

import com.microsoft.azure.storage.blob.*;
import com.microsoft.azure.storage.blob.models.BlobFlatListSegment;
import com.microsoft.azure.storage.blob.models.BlobHTTPHeaders;
import com.microsoft.azure.storage.blob.models.ContainerItem;
import com.microsoft.rest.v2.http.HttpPipeline;
import gov.ita.dataloader.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.microsoft.azure.storage.blob.models.DeleteSnapshotsOptionType.INCLUDE;

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
      .create(null, null, null)
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
  public List<BlobMetaData> getBlobMetadata(String containerName, Boolean withSnapshots) {
    ListBlobsOptions listBlobsOptions = new ListBlobsOptions();
    BlobListDetails details = new BlobListDetails();
    details.withMetadata(true);
    details.withSnapshots(withSnapshots);
    listBlobsOptions.withDetails(details);
    BlobFlatListSegment segment = makeContainerUrl(containerName)
      .listBlobsFlatSegment(null, listBlobsOptions, null).blockingGet().body().segment();
    if (segment != null && segment.blobItems() != null) {
      return segment.blobItems()
        .stream().map(
          x -> new BlobMetaData(
            x.name(),
            buildUrlForBlob(containerName, x.name(), x.snapshot()),
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
  public List<BlobMetaData> getBlobMetadata(String containerName) {
    return getBlobMetadata(containerName, true);
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
    return makeContainerUrl(containerName).createBlobURL(blobName)
      .download()
      .blockingGet()
      .body(new ReliableDownloadOptions())
      .blockingFirst().array();
  }

  @Override
  public byte[] getBlob(String containerName, String blobName, String snapshot) {
    BlobURL blobURL = null;
    try {
      blobURL = (snapshot == null) ?
        makeContainerUrl(containerName).createBlobURL(blobName) :
        makeContainerUrl(containerName).createBlobURL(blobName).withSnapshot(snapshot);
    } catch (MalformedURLException | UnknownHostException e) {
      e.printStackTrace();
    }

    return blobURL
      .download()
      .blockingGet()
      .body(new ReliableDownloadOptions())
      .blockingFirst().array();
  }

  @Override
  public void makeSnapshot(String containerName, String blobName) {
    makeServiceURL().createContainerURL(containerName).createBlobURL(blobName).createSnapshot().blockingGet();
  }

  @Override
  public void delete(String containerName, String blobName) {
    List<BlobMetaData> blobMetadata = getBlobMetadata(containerName, false);
    for (BlobMetaData b : blobMetadata) {
      if (b.getFileName().equals(blobName)) {
        log.info("Deleting blob: {}", b.getFileName());
        makeServiceURL().createContainerURL(containerName).
          createBlobURL(b.getFileName())
          .delete(INCLUDE, null, null).blockingGet();
      }
    }
  }

  private String buildUrlForBlob(String containerName, String blobName, String snapshot) {
    if (snapshot != null)
      return String.format("/api/%s/%s?snapshot=%s", containerName, blobName, snapshot);
    return String.format("/api/%s/%s", containerName, blobName);
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
