package gov.ita.dataloader.ingest.translators;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.StringWriter;

public class SimaCensusCsvTranslator implements Translator {

  /**
   * Used the following document as a reference: //https://www.census.gov/foreign-trade/structures/stlstruc.txt
   */

  @Override
  public byte[] translate(byte[] bytes) {
    StringWriter stringWriter = new StringWriter();
    CSVPrinter csvPrinter;

    try {
      csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT
        .withHeader(
          "COUNTRY",
          "CTRY_DESC",
          "COMM_DESC",
          "STEEL_TYPE",
          "VALUE",
          "NETTON",
          "AVGNETPR",
          "METRICTON",
          "AVGMETPR",
          "STAT_MO",
          "STAT_YEAR"
        )
      );

      String[] lines = new String(bytes).split(System.getProperty("line.separator"));

      for (String line : lines) {
        String countryId = line.substring(0, 4);
        String countryDescription = line.substring(5, 36);
        String commodityDescription = line.substring(36, 96);
        String value = line.substring(98, 108);
        String netton = line.substring(109, 123);
        String avgnetpr = line.substring(124, 136);
        String metricton = line.substring(137, 151);
        String avgmetpr = line.substring(152, 164);
        String month = line.substring(165, 167);
        String year = line.substring(168, 172);
        String steelType = "";

        if (commodityDescription.charAt(0)=='C'){
          steelType="Carbon and Alloy";
        } else {
          steelType = "Stainless";
        }

        csvPrinter.printRecord(
          countryId,
          countryDescription,
          commodityDescription,
          steelType,
          value,
          netton,
          avgnetpr,
          metricton,
          avgmetpr,
          month,
          year
        );
      }

      csvPrinter.flush();

      return csvPrinter.getOut().toString().getBytes();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  public int pageSize() {
    return -1;
  }

  @Override
  public TranslatorType type() {
    return TranslatorType.CSV;
  }
}
