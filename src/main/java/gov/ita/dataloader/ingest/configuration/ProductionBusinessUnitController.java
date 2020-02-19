package gov.ita.dataloader.ingest.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ita.dataloader.security.AuthenticationFacade;
import gov.ita.dataloader.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Profile("production")
public class ProductionBusinessUnitController {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private AuthenticationFacade authenticationFacade;

  @Autowired
  private Storage storage;

  @GetMapping(value = "/api/business-units", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<BusinessUnit> getBusinessUnits() throws Exception {
    byte[] dataloaderConfig = storage.getBlob("dataloader", "configuration.json");
    BusinessUnitConfigResponse buc = objectMapper.readValue(dataloaderConfig, BusinessUnitConfigResponse.class);
    return buc.getBusinessUnits().stream()
      .filter(businessUnit -> businessUnit.getUsers().contains(authenticationFacade.getUserName().toLowerCase()))
      .collect(Collectors.toList());
  }

  @GetMapping(value = "/api/storage-containers", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<String> getStorageContainers() throws Exception {
    byte[] dataloaderConfig = storage.getBlob("dataloader", "configuration.json");
    BusinessUnitConfigResponse buc = objectMapper.readValue(dataloaderConfig, BusinessUnitConfigResponse.class);
    return buc.getBusinessUnits().stream().map(BusinessUnit::getContainerName).collect(Collectors.toList());
  }

}
