package gov.ita.dataloader.ingest.translators;

import com.fasterxml.jackson.databind.ObjectMapper;
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

public class OtexaCatTranslator implements Translator {

  private static final ObjectMapper mapper = new ObjectMapper();

  @Override
  public byte[] translate(byte[] bytes) {
    StringWriter stringWriter = new StringWriter();
    CSVPrinter csvPrinter;

    try {
      csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT
        .withHeader("CTRYNUM", "CAT", "CNAME", "SYEF", "YR", "Mon", "header", "val"));

      Reader reader = new CharSequenceReader(new String(bytes));
      CSVParser csvParser;
      csvParser = new CSVParser(
        reader,
        CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim().withNullString("").withIgnoreHeaderCase());

      Map<String, Integer> headers = csvParser.getHeaderMap();

      List<String> valueFields = headers.keySet().stream()
        .filter(header -> header.startsWith("D") || header.startsWith("QTY") || header.startsWith("VAL"))
        .collect(Collectors.toList());

      for (CSVRecord csvRecord : csvParser) {
        String ctrynum = csvRecord.get("CTRYNUM");
        String cat = csvRecord.get("CAT");
        String cname = csvRecord.get("CNAME");
        String syef = csvRecord.get("SYEF");
        String yr = csvRecord.get("YR");
        String mon = csvRecord.get("Mon");

        for (String header : valueFields) {
          csvPrinter.printRecord(
            ctrynum, cat, cname, syef, yr, mon, header, csvRecord.get(header)
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
}
