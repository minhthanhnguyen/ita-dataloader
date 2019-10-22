package gov.ita.dataloader.extract.fta.tariff;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TariffCsvTranslatorTest {
  private List<Tariff> tariffs;
  private TariffCsvTranslator tariffCsvTranslator;

  @Before
  public void set_up() {
    tariffCsvTranslator = new TariffCsvTranslator();
  }

  @Test
  public void translates_csv_data() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals(100, tariffs.size());
  }

  @Test
  public void translates_ID_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals(439058L, tariffs.get(0).getId(), 0);
  }

  @Test
  public void translates_TariffLine_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals("01011000", tariffs.get(0).getTariffLine());
  }

  @Test
  public void translates_Description_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals("Live purebred breeding horses and asses", tariffs.get(0).getDescription());
  }

  @Test
  public void translates_HS6_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals("010110", tariffs.get(0).getHs6());
    assertEquals("PUREBRED BREEDING ANIMAL", tariffs.get(0).getHs6Description());
  }

  @Test
  public void translates_SectorCode_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals("15", tariffs.get(0).getSectorCode());
  }

  @Test
  public void translates_BaseRate_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals(2.2, tariffs.get(0).getBaseRate(), 0);
  }

  @Test
  public void translates_BaseRateAlt_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals("2.8 cents/kg", tariffs.get(0).getBaseRateAlt());
  }

  @Test
  public void translates_FinalYear_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals(2012, tariffs.get(0).getFinalYear(), 0);
  }

  @Test
  public void translates_StagingBasket_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals("Immediate", tariffs.get(0).getStagingBasket());
  }

  @Test
  public void translates_TariffRateQuota_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals(3, tariffs.get(0).getTariffRateQuota(), 0);
  }

  @Test
  public void translates_TariffRateQuotaNotes_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals("For more information, please see the U.S. Department of Agriculture", tariffs.get(0).getTariffRateQuotaNote());
  }

  @Test
  public void translates_TariffEliminated_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertTrue(tariffs.get(0).getTariffEliminated());
  }

  @Test
  public void translates_Partner_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals("Korea", tariffs.get(0).getPartnerName());
  }

  @Test
  public void translates_Reporter_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals("United States", tariffs.get(0).getReporterName());
  }

  @Test
  public void translates_PartnerStartYear_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals(2012, tariffs.get(0).getPartnerStartYear(), 0);
  }

  @Test
  public void translates_ReporterStartYear_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals(2014, tariffs.get(0).getReporterStartYear(), 0);
  }

  @Test
  public void translates_QuotaName_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals("awesome", tariffs.get(0).getQuotaName());
  }

  @Test
  public void translates_ProductType_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals("Agricultural", tariffs.get(0).getProductType());
  }

  @Test
  public void translates_RuleText_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
    assertEquals("A change to heading 01.01 through 01.06 from any other chapter.", tariffs.get(0).getRuleText());
  }

  @Test
  public void translates_LinkUrl_field() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));
  }

  @Test
  public void translates_line_Rates_values_for_year_2004_to_2041() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("korea.csv"));

    assertEquals(7, tariffs.get(0).getRates().size(), 0);

    Rate rate2004 = tariffs.get(0).getRates().stream().filter(r -> r.getYear().equals(2004)).findFirst().get();
    assertEquals(2, rate2004.getValue(), 0);

    Rate rate2011 = tariffs.get(0).getRates().stream().filter(r -> r.getYear().equals(2011)).findFirst().get();
    assertEquals(4.4, rate2011.getValue(), 0);
  }

  @Test
  public void translates_line_Rates_values_from_year_1_to_x() throws InvalidCsvFileException {
    tariffs = tariffCsvTranslator.translate(getFileAsReader("canada-usmca.csv"));
    Tariff tariff = tariffs.get(0);
    assertEquals(2, tariff.getRates().size(), 0);
    assertEquals(2, tariff.getRateAlts().size(), 0);

    Rate year1rate = tariff.getRates().stream().filter(r -> r.getYear().equals(1)).findFirst().get();
    assertEquals(100, year1rate.getValue(), 0);

    RateAlt year1rateAlt = tariff.getRateAlts().stream().filter(r -> r.getYear().equals(1)).findFirst().get();
    assertEquals("$100 per kilo", year1rateAlt.getValue());

    Rate year2rate = tariff.getRates().stream().filter(r -> r.getYear().equals(2)).findFirst().get();
    assertEquals(50, year2rate.getValue(), 0);

    RateAlt year2rateAlt = tariff.getRateAlts().stream().filter(r -> r.getYear().equals(2)).findFirst().get();
    assertEquals("$50 per kilo", year2rateAlt.getValue());
  }

  @Test(expected = InvalidCsvFileException.class)
  public void throws_error_when_number_field_is_invalid() throws InvalidCsvFileException {
    tariffCsvTranslator.translate(getFileAsReader("korea_invalid_number.csv"));
  }

  private Reader getFileAsReader(String file) {
    return new StringReader(new String(getSampleData(file)));
  }

  private byte[] getSampleData(String file) {
    try {
      return IOUtils.toString(
        this.getClass().getResourceAsStream("/fixtures/fta/" + file)
      ).getBytes();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}