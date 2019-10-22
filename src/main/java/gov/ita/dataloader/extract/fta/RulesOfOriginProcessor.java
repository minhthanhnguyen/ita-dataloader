package gov.ita.dataloader.extract.fta;

import gov.ita.dataloader.extract.fta.tariff.Tariff;
import gov.ita.dataloader.extract.fta.tariff.TariffDocsMetadata;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RulesOfOriginProcessor {
  public void process(List<Tariff> tariffs, List<TariffDocsMetadata> metadata) {
    //TODO: append document link urls using hs6 and country variables
    metadata.forEach(m -> {
      System.out.println(m.getMetadataStoragePath());
    });
  }
}
