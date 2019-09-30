package gov.ita.dataloader.ingest.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

import static gov.ita.dataloader.ResourceHelper.getResourceAsString;
import static gov.ita.dataloader.ResourceHelper.getResources;

@Service
public class StorageInitializer {

  @Autowired
  private Storage storage;

  public void init() {
    Map<String, String> configurationFiles = getResources("/fixtures");
    Set<String> containers = storage.getContainerNames();
    for (String configFile : configurationFiles.keySet()) {
      String containerName = configFile.replace(".json", "");
      if (!containers.contains(containerName)) {
        storage.createContainer(containerName);
        storage.save("configuration.json", getResourceAsString(configurationFiles.get(configFile)).getBytes(), null, containerName);
      }
    }
  }
}
