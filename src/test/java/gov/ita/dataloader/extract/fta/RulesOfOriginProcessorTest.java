package gov.ita.dataloader.extract.fta;

import gov.ita.dataloader.extract.fta.tariff.Tariff;
import gov.ita.dataloader.extract.fta.tariff.TariffDocsMetadata;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class RulesOfOriginProcessorTest {

  @Test
  public void rulesOfOriginTest() {
    List<Tariff> tariffs = new ArrayList<>();
    List<TariffDocsMetadata> tariffDocsMetadata = new ArrayList<>();

    tariffs.add(makeTariff("Australia", "United States", "33123"));
    tariffs.add(makeTariff("United States", "Greece", "88321"));

    tariffDocsMetadata.add(makeMeta("Australia;Bahrain", "33 - Essential oils", "http://file.1.pdf"));
    tariffDocsMetadata.add(makeMeta("Bahrain;Australia", "33 - Essential oils", "http://file.2.pdf"));
    tariffDocsMetadata.add(makeMeta("Mexico;Greece", "88 - Something else", "http://file.3.pdf"));
    
    RulesOfOriginProcessor rulesOfOriginProcessor = new RulesOfOriginProcessor();
    rulesOfOriginProcessor.process(tariffs, tariffDocsMetadata);

    Tariff australia = getTariff(tariffs, "Australia");
    Tariff greece = getTariff(tariffs, "Greece");

    assertTrue(containsLink(australia, "http://file.1.pdf"));
    assertTrue(containsLink(australia, "http://file.2.pdf"));
    assertTrue(containsLink(greece, "http://file.3.pdf"));
  }

  private Tariff makeTariff(String partner, String reporter, String hs6) {
    Tariff t = new Tariff();
    t.setPartnerName(partner);
    t.setReporterName(reporter);
    t.setHs6(hs6);
    return t;
  }

  private TariffDocsMetadata makeMeta(String countryArray, String hs6Description, String url) {
    TariffDocsMetadata metadata = new TariffDocsMetadata();
    metadata.setCountry(countryArray);
    metadata.setFtaPublicationHsCode(hs6Description);
    metadata.setMetadataStoragePath(url);
    return metadata;
  }

  private boolean containsLink(Tariff t, String linkUrl) {
    return t.getLinks().stream().anyMatch(l -> l.getLinkUrl().equals(linkUrl));
  }

  private Tariff getTariff(List<Tariff> tariffs, String country) {
    return tariffs.stream().filter(t -> t.getCountry().equals(country)).findFirst().get();
  }

}
