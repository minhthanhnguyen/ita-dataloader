package gov.ita.dataloader.ingest;

import gov.ita.dataloader.ingest.translators.Translator;
import gov.ita.dataloader.ingest.translators.TranslatorFactory;
import gov.ita.dataloader.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class IngestTranslationProcessor {

  private Storage storage;
  private TranslatorFactory translatorFactory;

  public IngestTranslationProcessor(Storage storage, TranslatorFactory translatorFactory) {
    this.storage = storage;
    this.translatorFactory = translatorFactory;
  }

  public void process(String containerName, MultipartFile file, String userName) throws IOException {
    String containerFileCompositeKey = containerName + "#" + file.getOriginalFilename();
    Translator translator = translatorFactory.getTranslator(containerFileCompositeKey);
    if (translator == null) System.exit(0);

    log.info("Translating {}", containerFileCompositeKey);

    int lineCount = countLines(file.getInputStream());
    int currentPage = 1;
    int pageSize = translator.pageSize();
    if (pageSize == -1) pageSize = lineCount;
    int pages = (lineCount + pageSize - 1) / pageSize;

    while (currentPage <= pages) {
      int offset = currentPage * pageSize;
      byte[] translatedFile = translator.translate(file.getBytes(), offset, pageSize);
      storage.save(
        "translated/" + file.getOriginalFilename() + "_" + currentPage,
        translatedFile,
        userName,
        containerName,
        true);
      currentPage++;
    }
  }

  static int countLines(InputStream is) throws IOException {
    byte[] c = new byte[1024];

    int readChars = is.read(c);
    if (readChars == -1) {
      // bail out if nothing to read
      return 0;
    }

    // make it easy for the optimizer to tune this loop
    int count = 0;
    while (readChars == 1024) {
      for (int i = 0; i < 1024; ) {
        if (c[i++] == '\n') {
          ++count;
        }
      }
      readChars = is.read(c);
    }

    // count remaining characters
    while (readChars != -1) {
      System.out.println(readChars);
      for (int i = 0; i < readChars; ++i) {
        if (c[i] == '\n') {
          ++count;
        }
      }
      readChars = is.read(c);
    }

    return count == 0 ? 1 : count;
  }
}
