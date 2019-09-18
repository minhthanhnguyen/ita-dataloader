package gov.ita.susastatsdataloader.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlobMetaData {
  String name;
  String url;
  Long size;
}
