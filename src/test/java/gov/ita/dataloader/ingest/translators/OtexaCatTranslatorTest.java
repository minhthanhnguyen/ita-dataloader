package gov.ita.dataloader.ingest.translators;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CharSequenceReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("development")
public class OtexaCatTranslatorTest {

  private List<CSVRecord> records;

  @Before
  public void setUp() throws IOException {
    OtexaCatTranslator otexaCatTranslator = new OtexaCatTranslator();
    String csv = IOUtils.toString(
      this.getClass().getResourceAsStream("/fixtures/OTEXA_DATA_SET_CAT.csv")
    );

    byte[] translatedBytes = otexaCatTranslator.translate(csv.getBytes());
    Reader reader = new CharSequenceReader(new String(translatedBytes));
    CSVParser csvParser;
    csvParser = new CSVParser(
      reader,
      CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim().withNullString("").withIgnoreHeaderCase());

    records = csvParser.getRecords();
  }

  @Test
  public void translates_CTRYNUM() {
    assertEquals("0", records.get(0).get("CTRYNUM"));
  }

  @Test
  public void translates_CAT() {
    assertEquals("0", records.get(0).get("CAT_ID"));
  }

  @Test
  public void translates_CNAME() {
    assertEquals("VANGO", records.get(0).get("CNAME"));
  }

  @Test
  public void translates_SYEF() {
    assertEquals("1", records.get(0).get("SYEF"));
  }

  @Test
  public void translates_D_VAL_QTY() {
    Map<String, String> expected = new HashMap<>();
    expected.put("D1", "64780210952");
    expected.put("D2", "68594522565");
    expected.put("D3", "46836824838");
    expected.put("D4", "42488171797");
    expected.put("D5", "44424107649");
    expected.put("DG28", "6635940481");
    expected.put("DG29", "10911436956");
    expected.put("QTY1989", "12144215483");
    expected.put("QTY1990", "12195002622");
    expected.put("QTY1991", "12800352859");
    expected.put("QTY1992", "14520641062");
    expected.put("QTY1993", "15847506702");
    expected.put("VAL1989", "26748794877");
    expected.put("VAL1990", "27935777436");
    expected.put("VAL1991", "29040385897");
    expected.put("VAL1992", "34110437547");
    expected.put("VAL1993", "36078870889");

    //Only checking the first record
    List<CSVRecord> otexaCatRecords =
      records.stream().filter(r -> r.get("CNAME").equals("VANGO")).collect(Collectors.toList());

    for (CSVRecord r : otexaCatRecords) {
      String header = r.get("header");
      String val = r.get("val");
      assertEquals("Expected " + header + " to have value of " + expected.get(val), expected.get(header), val);
    }
  }
}
