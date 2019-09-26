package gov.ita.susastatsdataloader.ingest.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlobMetaData {
  String name;
  String url;
  Long size;
  OffsetDateTime modified;
}
