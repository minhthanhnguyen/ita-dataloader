package gov.ita.dataloader.ingest.storage;

import com.microsoft.azure.storage.blob.*;
import com.microsoft.azure.storage.blob.models.BlobHTTPHeaders;
import com.microsoft.azure.storage.blob.models.ContainerItem;
import com.microsoft.azure.storage.blob.models.PublicAccessType;
import com.microsoft.rest.v2.http.HttpPipeline;
import com.microsoft.rest.v2.util.FlowableUtil;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Profile({"production", "staging"})
public class ProductionStorage implements Storage {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${storage-params.azure-storage-account}")
  private String accountName;

  @Value("${storage-params.azure-storage-account-key}")
  private String accountKey;

  @Override
  public void save(String fileName, byte[] fileContent, String user, String containerName) {
    if (user == null) user = accountName;
    ContainerURL containerURL = makeContainerUrl(containerName);
    BlockBlobURL blobURL = containerURL.createBlockBlobURL(fileName);
    blobURL.upload(Flowable.just(ByteBuffer.wrap(fileContent)), fileContent.length,
      makeHeader(fileName), makeMetaData(user), null, null)
      .flatMap(blobsDownloadResponse ->
        blobURL.download())
      .flatMap(blobsDownloadResponse ->
        FlowableUtil.collectBytesInBuffer(blobsDownloadResponse.body(null))
          .doOnSuccess(byteBuffer -> {
            if (byteBuffer.compareTo(ByteBuffer.wrap(fileContent)) != 0) {
              throw new Exception("The downloaded data does not match the uploaded data.");
            }
          }))
      .blockingGet();
  }

  private boolean containerExists(String containerName) {
    ServiceURL serviceURL = makeServiceURL();
    assert serviceURL != null;
    List<ContainerItem> containerItems = serviceURL.listContainersSegment(null, null)
      .blockingGet().body().containerItems();
    return containerItems.stream().anyMatch(containerItem -> containerItem.name().equals(containerName));
  }

  @Override
  public void initContainer(String containerName) {
    if (!containerExists(containerName)) {
      log.info("Initializing container: {}", containerName);
      makeContainerUrl(containerName)
        .create(makeMetaData(accountName), PublicAccessType.CONTAINER, null)
        .blockingGet();
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
    return makeContainerUrl(containerName)
      .listBlobsFlatSegment(null, listBlobsOptions, null).blockingGet().body().segment()
      .blobItems()
      .stream().map(
        x -> new BlobMetaData(
          x.name(),
          buildUrlForBlob(x.name(), containerName),
          x.properties().contentLength(),
          x.properties().lastModified()
        )).filter(item -> !item.name.startsWith("adfpolybaserejectedrows"))
      .collect(Collectors.toList());
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

  private Metadata makeMetaData(String user) {
    Metadata metadata = new Metadata();
    metadata.put("uploaded_by", user);
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