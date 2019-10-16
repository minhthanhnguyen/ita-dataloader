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
        String path = configurationFiles.get(configFile);
        storage.createContainer(containerName);
        storage.save("configuration.json", getResourceAsString(path).getBytes(), null, containerName, false);
      }
    }
  }
}
