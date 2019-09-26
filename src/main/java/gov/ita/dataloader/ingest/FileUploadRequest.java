package gov.ita.dataloader.ingest;

import lombok.Data;

import java.util.Base64;

@Data
public class FileUploadRequest {
  String base64FileContent;
  String destinationFileName;
  String containerName;

  public byte[] getFileBytes() {
    return  Base64.getDecoder().decode(base64FileContent);
  }
}
