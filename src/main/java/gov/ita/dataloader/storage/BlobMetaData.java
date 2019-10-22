package gov.ita.dataloader.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlobMetaData {
  String name;
  String url;
  Long size;
  String containerName;
  OffsetDateTime uploadedAt;
  Map<String, String> metadata;
}
