package gov.ita.dataloader.extract.fta;

import gov.ita.dataloader.extract.fta.tariff.InvalidCsvFileException;
import gov.ita.dataloader.extract.fta.tariff.Tariff;
import gov.ita.dataloader.extract.fta.tariff.TariffCsvTranslator;
import gov.ita.dataloader.extract.fta.tariff.TariffDocsMetadata;
import gov.ita.dataloader.storage.Storage;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;

@RestController
public class FtaTariffRatesExtractController {

  private Storage storage;
  private TariffCsvTranslator tariffCsvTranslator;
  private TariffDocsMetaDataGateway tariffDocsMetaDataGateway;
  private RulesOfOriginProcessor rulesOfOriginProcessor;

  public FtaTariffRatesExtractController(Storage storage,
                                         TariffCsvTranslator tariffCsvTranslator,
                                         TariffDocsMetaDataGateway tariffDocsMetaDataGateway,
                                         RulesOfOriginProcessor rulesOfOriginProcessor) {
    this.storage = storage;
    this.tariffCsvTranslator = tariffCsvTranslator;
    this.tariffDocsMetaDataGateway = tariffDocsMetaDataGateway;
    this.rulesOfOriginProcessor = rulesOfOriginProcessor;
  }

  @GetMapping(value = "/api/extract/fta/tariff-rates", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Tariff> getTariffRates(@RequestParam String fileName) throws InvalidCsvFileException {
    byte[] blob = storage.getBlob("fta-tariff-rates", fileName);
    List<Tariff> tariffs = tariffCsvTranslator.translate(new InputStreamReader(new ByteArrayInputStream(blob)));
    List<TariffDocsMetadata> metadata = tariffDocsMetaDataGateway.getMetadata();
    rulesOfOriginProcessor.process(tariffs, metadata);
    return tariffs;
  }

}
