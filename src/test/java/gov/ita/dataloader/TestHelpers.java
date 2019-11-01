package gov.ita.dataloader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CharSequenceReader;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class TestHelpers {

  public byte[] get(String path) {
    try {
      return IOUtils.toString(
        this.getClass().getResourceAsStream(path)
      ).getBytes();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static List<CSVRecord> formattedResults(byte[] translatedBytes) {
    Reader reader = new CharSequenceReader(new String(translatedBytes));
    CSVParser csvParser;
    try {
      csvParser = new CSVParser(
        reader,
        CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim().withNullString("").withIgnoreHeaderCase());
      return csvParser.getRecords();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
