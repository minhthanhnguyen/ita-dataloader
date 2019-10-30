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

    Tariff aus = new Tariff();
    aus.setPartnerName("Australia");
    aus.setHs6("33123");
    tariffs.add(aus);

    Tariff gr = new Tariff();
    gr.setPartnerName("Greece");
    gr.setHs6("88321");
    tariffs.add(gr);

    TariffDocsMetadata ausMeta = new TariffDocsMetadata();
    ausMeta.setCountry("Australia;Bahrain");
    ausMeta.setFtaPublicationHsCode("33 - Essential oils");
    ausMeta.setMetadataStoragePath("http://aus.file.1.pdf");
    tariffDocsMetadata.add(ausMeta);

    TariffDocsMetadata ausMeta2 = new TariffDocsMetadata();
    ausMeta2.setCountry("Bahrain;Australia");
    ausMeta2.setFtaPublicationHsCode("33 - Essential oils");
    ausMeta2.setMetadataStoragePath("http://aus.file.2.pdf");
    tariffDocsMetadata.add(ausMeta2);

    TariffDocsMetadata grMeta = new TariffDocsMetadata();
    grMeta.setCountry("Mexico;Greece");
    grMeta.setFtaPublicationHsCode("88 - Something else");
    grMeta.setMetadataStoragePath("http://gr.file.1.pdf");
    tariffDocsMetadata.add(grMeta);

    RulesOfOriginProcessor rulesOfOriginProcessor = new RulesOfOriginProcessor();
    rulesOfOriginProcessor.process(tariffs, tariffDocsMetadata);

    Tariff australia = getTariff(tariffs, "Australia");
    Tariff greece = getTariff(tariffs, "Greece");

    assertTrue(containsLink(australia, "http://aus.file.1.pdf"));
    assertTrue(containsLink(australia, "http://aus.file.2.pdf"));
    assertTrue(containsLink(greece, "http://gr.file.1.pdf"));
  }

  private boolean containsLink(Tariff t, String linkUrl) {
    return t.getLinks().stream().anyMatch(l -> l.getLinkUrl().equals(linkUrl));
  }

  private Tariff getTariff(List<Tariff> tariffs, String country) {
    return tariffs.stream().filter(t -> t.getPartnerName().equals(country)).findFirst().get();
  }

}
