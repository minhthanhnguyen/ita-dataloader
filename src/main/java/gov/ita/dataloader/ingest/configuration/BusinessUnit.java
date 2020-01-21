package gov.ita.dataloader.ingest.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

import java.util.List;

@Data
@AllArgsConstructor
public class BusinessUnit {
  String businessName;
  String containerName;
  @JsonIgnore
  List<String> users;
}
