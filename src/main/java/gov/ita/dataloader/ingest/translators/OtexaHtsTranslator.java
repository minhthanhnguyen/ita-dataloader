package gov.ita.dataloader.ingest.translators;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.CharSequenceReader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OtexaHtsTranslator implements Translator {

  @Override
  public byte[] translate(byte[] bytes, int offset, int size) {
    StringWriter stringWriter = new StringWriter();
    CSVPrinter csvPrinter;

    try {
      csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT
        .withHeader("CTRY_ID", "CAT_ID", "HTS", "SYEF", "HEADER_ID", "VAL"));

      Reader reader = new CharSequenceReader(new String(bytes));
      CSVParser csvParser;
      csvParser = new CSVParser(
        reader,
        CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim().withNullString("").withIgnoreHeaderCase());

      Map<String, Integer> headers = csvParser.getHeaderMap();

      List<String> valueFields = headers.keySet().stream()
        .filter(header -> header.startsWith("D") || header.startsWith("QTY") || header.startsWith("VAL"))
        .collect(Collectors.toList());

      List<CSVRecord> records = csvParser.getRecords();
      for (int i = 0; i < size && i + offset < records.size(); i++) {
        CSVRecord csvRecord = records.get(i + offset);
        String ctryNum = csvRecord.get("CTRYNUM");
        String catId = csvRecord.get("CAT");
        String syef = csvRecord.get("SYEF");
        String hts = csvRecord.get("HTS");

        for (String header : valueFields) {
          String val = csvRecord.get(header);
          if (val != null)
            csvPrinter.printRecord(
              ctryNum, catId, hts, syef, header, val
            );
        }
      }

      reader.close();
      csvPrinter.flush();

      return csvPrinter.getOut().toString().getBytes();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  public int pageSize() {
    return 25000;
  }

}
