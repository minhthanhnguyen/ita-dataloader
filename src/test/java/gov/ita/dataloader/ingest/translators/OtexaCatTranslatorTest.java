package gov.ita.dataloader.ingest.translators;

import gov.ita.dataloader.TestHelpers;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static gov.ita.dataloader.TestHelpers.formattedResults;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("development")
public class OtexaCatTranslatorTest {
  private TestHelpers h = new TestHelpers();
  private List<CSVRecord> results;

  @Before
  public void setUp() {
    OtexaCatTranslator otexaCatTranslator = new OtexaCatTranslator();
    byte[] translatedBytes = otexaCatTranslator.translate(h.get("/fixtures/otexa/OTEXA_DATA_SET_CAT.csv"));
    results = formattedResults(translatedBytes);
  }

  @Test
  public void translates_CTRYNUM() {
    assertEquals("8", results.get(0).get("CTRY_ID"));
  }

  @Test
  public void translates_CAT() {
    assertEquals("0", results.get(0).get("CAT_ID"));
  }

  @Test
  public void translates_SYEF() {
    assertEquals("1", results.get(0).get("SYEF"));
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
      results.stream().filter(r -> r.get("CTRY_ID").equals("8")).collect(Collectors.toList());

    for (CSVRecord r : otexaCatRecords) {
      String header = r.get("HEADER_ID");
      String val = r.get("VAL");
      assertEquals("Expected " + header + " to have value of " + expected.get(val), expected.get(header), val);
    }
  }

}
