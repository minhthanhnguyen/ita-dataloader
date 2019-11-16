package gov.ita.dataloader.ingest;

import gov.ita.dataloader.ingest.translators.Translator;
import gov.ita.dataloader.ingest.translators.TranslatorFactory;
import gov.ita.dataloader.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static gov.ita.dataloader.ingest.Phase.*;

@Slf4j
@Service
public class IngestTranslationProcessor {

  private Storage storage;
  private TranslatorFactory translatorFactory;
  private ProcessorStatusService processorStatusService;

  public IngestTranslationProcessor(Storage storage, TranslatorFactory translatorFactory, ProcessorStatusService processorStatusService) {
    this.storage = storage;
    this.translatorFactory = translatorFactory;
    this.processorStatusService = processorStatusService;
  }

  @Async
  public CompletableFuture<Void> process(String containerName, String fileName, byte[] fileBytes, String userName) {
    String containerFileCompositeKey = containerName + "#" + fileName;
    Translator translator = translatorFactory.getTranslator(containerFileCompositeKey);
    String fileRootName = "translated/" + fileName;

    if (!processorStatusService.isProcessing(containerName, fileName)) {
      IngestTranslationStatus ingestProcessorStatus = new IngestTranslationStatus(fileName, -1, 0, SAVING_NEW_FILE);
      processorStatusService.updateTranslationProcessorStatus(containerName, fileName, ingestProcessorStatus);

      storage.save(fileName, fileBytes, userName, containerName, true);
      storage.makeSnapshot(containerName, fileName);

      if (translator != null) {
        log.info("Processing {}", containerFileCompositeKey);
        ingestProcessorStatus.setPhase(DELETING_OLD_TRANSLATIONS);

        storage.delete(containerName, fileRootName);

        int HEADER_ROW = 1;
        int lineCount = countLines(fileBytes);
        int pageSize = translator.pageSize();
        if (pageSize == -1) pageSize = lineCount;
        int totalPages = (lineCount + pageSize - 1) / pageSize;
        int currentPage = 0;

        ingestProcessorStatus.setTotalPages(totalPages);
        ingestProcessorStatus.setCurrentPage(currentPage);
        ingestProcessorStatus.setPhase(CREATING_NEW_TRANSLATIONS);

        while (currentPage < totalPages) {
          log.info("Translating page {} of {} for {}", currentPage + 1, totalPages, containerFileCompositeKey);
          ingestProcessorStatus.setCurrentPage(currentPage + 1);
          int offset = currentPage * pageSize;
          byte[] partitionedFile = getFilePartition(fileBytes, offset, pageSize);
          byte[] translatedFile = translator.translate(partitionedFile);
          storage.save(fileRootName + "/" + UUID.randomUUID(), translatedFile, userName, containerName, true);
          currentPage++;
        }
      }
      ingestProcessorStatus.setPhase(DONE);
    }
    return new CompletableFuture<>();
  }

  private byte[] getFilePartition(byte[] bytes, int offset, int size) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
    StringWriter writer = new StringWriter();
    String temp = null;
    int i = 0;
    while (true) {
      try {
        if ((temp = reader.readLine()) == null) break;
      } catch (IOException e) {
        e.printStackTrace();
      }

      if ((i == 0) || (i > offset && (i - offset <= size))) {
        writer.write(temp);
        writer.append(System.getProperty("line.separator"));
      }
      i++;
    }

    return writer.toString().getBytes();
  }

  private int countLines(byte[] bytes) {
    InputStream is = new ByteArrayInputStream(bytes);
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
