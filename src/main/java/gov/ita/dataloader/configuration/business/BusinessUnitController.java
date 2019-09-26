package gov.ita.dataloader.configuration.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BusinessUnitController {

  @Autowired
  private BusinessUnitRepository businessUnitRepository;

  @GetMapping("/api/business-units")
  public List<BusinessUnit> getBusinessUnits() {
    return businessUnitRepository.findAll();
  }
}
