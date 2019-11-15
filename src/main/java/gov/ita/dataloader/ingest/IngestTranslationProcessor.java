package gov.ita.dataloader.ingest;

import gov.ita.dataloader.ingest.translators.Translator;
import gov.ita.dataloader.ingest.translators.TranslatorFactory;
import gov.ita.dataloader.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
public class IngestTranslationProcessor {

  private Storage storage;
  private TranslatorFactory translatorFactory;

  public IngestTranslationProcessor(Storage storage, TranslatorFactory translatorFactory) {
    this.storage = storage;
    this.translatorFactory = translatorFactory;
  }

  public void process(String containerName, String fileName, byte[] fileBytes, String userName) {
    String fileRootName = "translated/" + fileName;

    storage.delete(containerName, fileRootName);

    String containerFileCompositeKey = containerName + "#" + fileName;
    Translator translator = translatorFactory.getTranslator(containerFileCompositeKey);
    if (translator != null) {
      log.info("Processing {}", containerFileCompositeKey);

      int HEADER_ROW = 1;
      int lineCount = countLines(new ByteArrayInputStream(fileBytes)) - HEADER_ROW;
      int pageSize = translator.pageSize();
      if (pageSize == -1) pageSize = lineCount;
      int pages = (lineCount + pageSize - 1) / pageSize;

      int currentPage = 0;
      while (currentPage < pages) {
        log.info("Translating page {} of {} for {}", currentPage, pages, containerFileCompositeKey);
        
        int offset = currentPage * pageSize;
        byte[] translatedFile = translator.translate(fileBytes, offset, pageSize);
        storage.save(
          fileRootName + "/" + UUID.randomUUID(),
          translatedFile,
          userName,
          containerName,
          true);
        currentPage++;
      }
    }
  }

  private static int countLines(InputStream is) {
    byte[] c = new byte[1024];

    try {
      int readChars = is.read(c);
      if (readChars == -1) {
        return 0;
      }

      int count = 0;
      while (readChars == 1024) {
        for (int i = 0; i < 1024; ) {
          if (c[i++] == '\n') {
            ++count;
          }
        }
        readChars = is.read(c);
      }

      while (readChars != -1) {
        for (int i = 0; i < readChars; ++i) {
          if (c[i] == '\n') {
            ++count;
          }
        }
        readChars = is.read(c);
      }

      return count == 0 ? 1 : count;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return -1;
  }

}
