package gov.ita.dataloader.ingest.translators;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.StringWriter;

public class SimaCensusCsvTranslator implements Translator {

  StringWriter stringWriter = new StringWriter();
  CSVPrinter csvPrinter;

  @Override
  public byte[] translate(byte[] bytes) {
    try {
      csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT
        .withHeader(
          "CTRY_ID",
          "CTRY_NAME",
          "UNKNOWN_FIELD_1",
          "UNKNOWN_FIELD_2",
          "UNKNOWN_FIELD_3",
          "UNKNOWN_FIELD_4",
          "UNKNOWN_FIELD_5",
          "UNKNOWN_FIELD_6",
          "UNKNOWN_FIELD_7",
          "MONTH",
          "YEAR")
      );

      String[] lines = new String(bytes).split(System.getProperty("line.separator"));

      for (String line : lines) {
        String countryId = line.substring(0, 4);
        String countryName = line.substring(5, 35);
        String unknownField1 = line.substring(36, 76);
        String unknownField2 = line.substring(77, 78);
        String unknownField3 = line.substring(79, 89);
        String unknownField4 = line.substring(90, 104);
        String unknownField5 = line.substring(105, 117);
        String unknownField6 = line.substring(118, 132);
        String unknownField7 = line.substring(133, 145);
        String month = line.substring(146, 148);
        String year = line.substring(149, 153);
        csvPrinter.printRecord(
          countryId,
          countryName,
          unknownField1,
          unknownField2,
          unknownField3,
          unknownField4,
          unknownField5,
          unknownField6,
          unknownField7,
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
