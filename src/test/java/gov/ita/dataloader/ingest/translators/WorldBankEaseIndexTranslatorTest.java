package gov.ita.dataloader.ingest.translators;

import gov.ita.dataloader.TestHelpers;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static gov.ita.dataloader.TestHelpers.formattedResults;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class WorldBankEaseIndexTranslatorTest {
  private TestHelpers h = new TestHelpers();

  private List<CSVRecord> results;

  @Before
  public void setUp() {
    WorldBankEaseIndexTranslator worldBankEaseIndexTranslator = new WorldBankEaseIndexTranslator();
    byte[] translatedBytes = worldBankEaseIndexTranslator.translate(h.get("/fixtures/select-usa/WORLDBANK_EASE_COUNTRY_INDEX.csv"), -1, -1);
    results = formattedResults(translatedBytes);
  }

  @Test
  public void translates_CountryName() {
    assertEquals("Aruba", results.get(0).get("Country_Name"));
  }

  @Test
  public void translates_CountryCode() {
    assertEquals("ABW", results.get(0).get("Country_Code"));
  }

  @Test
  public void translates_IndicatorName() {
    assertEquals("Ease of doing business index (1=most business-friendly regulations)", results.get(0).get("Indicator_Name"));
  }

  @Test
  public void translates_IndicatorCode() {
    assertEquals("IC.BUS.EASE.XQ", results.get(0).get("Indicator_Code"));
  }

  @Test
  public void translates_Year() {
    assertEquals("1960", results.get(0).get("Year"));
  }

  @Test
  public void translates_Val() {
    assertNull(results.get(0).get("Val"));
  }

  @Test
  public void translates_Greece_2019() {
    CSVRecord csvRecord = results.stream().filter(r -> r.get("Country_Name").equals("Greece") && r.get("Year").equals("2019")).findFirst().get();
    assertEquals("79", csvRecord.get("Val"));
  }
}