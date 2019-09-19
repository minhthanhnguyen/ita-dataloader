package gov.ita.susastatsdataloader.storage;

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
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Profile({"production", "staging"})
public class ProductionStorage implements Storage {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${tarifftooldataloader.azure-storage-account}")
  private String accountName;

  @Value("${tarifftooldataloader.azure-storage-account-key}")
  private String accountKey;

  @Value("${tarifftooldataloader.azure-storage-container}")
  private String containerName;

  @Override
  public void save(String fileName, byte[] fileContent, String user) {
    if (user == null) user = accountName;
    ContainerURL containerURL = makeContainerUrl();
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

  @Override
  public boolean containerExists() {
    ServiceURL serviceURL = makeServiceURL();
    assert serviceURL != null;
    List<ContainerItem> containerItems = serviceURL.listContainersSegment(null, null)
      .blockingGet().body().containerItems();
    return containerItems.stream().anyMatch(containerItem -> containerItem.name().equals(containerName));
  }

  @Override
  public void createContainer() {
    makeContainerUrl()
      .create(makeMetaData(accountName), PublicAccessType.BLOB, null)
      .blockingGet();
  }

  @Override
  public String getBlobAsString(String blobName) {
    String url = makeBaseStorageUrl() + "/" + blobName;
    return Objects.requireNonNull(restTemplate.getForObject(url, String.class));
  }

  @Override
  public String getListBlobsUrl() {
    return makeBaseStorageUrl() + "?restype=container&comp=list";
  }

  private String makeBaseStorageUrl() {
    return String.format("https://%s.blob.core.windows.net/%s", accountName, containerName);
  }

  @Override
  public List<BlobMetaData> getBlobMetadata() {
    ListBlobsOptions listBlobsOptions = new ListBlobsOptions();
    BlobListDetails details = new BlobListDetails();
    details.withMetadata(true);
    listBlobsOptions.withDetails(details);
    return makeContainerUrl()
      .listBlobsFlatSegment(null, listBlobsOptions, null).blockingGet().body().segment()
      .blobItems()
      .stream().map(
        x -> new BlobMetaData(
          x.name(),
          buildUrlForBlob(x.name()),
          x.properties().contentLength(),
          x.properties().lastModified()
        ))
      .collect(Collectors.toList());
  }

  private String buildUrlForBlob(String name) {
    return String.format("https://%s.blob.core.windows.net/%s/%s", accountName, containerName, name);
  }

  private ContainerURL makeContainerUrl() {
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

    BlobHTTPHeaders headers = new BlobHTTPHeaders();
    headers.withBlobContentType(contentType);
    return headers;
  }
}
