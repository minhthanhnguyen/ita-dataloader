package gov.ita.dataloader.ingest;

import gov.ita.dataloader.TestHelpers;
import gov.ita.dataloader.ingest.translators.Translator;
import gov.ita.dataloader.ingest.translators.TranslatorFactory;
import gov.ita.dataloader.storage.Storage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IngestTranslationProcessorTest {

  @Mock
  Storage storage;

  @Mock
  TranslatorFactory translatorFactory;

  @Mock
  Translator translator;

  private ProcessorStatusService processorStatusService = new ProcessorStatusService();

  private TestHelpers h = new TestHelpers();
  private byte[] FIRST_BYTES = "first bytes".getBytes();
  private byte[] SECOND_BYTES = "second bytes".getBytes();

  @Test
  public void usesPageSizeToPartitionFile() {
    byte[] bytes = h.get("/fixtures/otexa/OTEXA_EXE_HTS.csv");
    when(translatorFactory.getTranslator("some-container#some-file-name.csv")).thenReturn(translator);
    when(translator.pageSize()).thenReturn(5);
    when(translator.translate(bytes, 0, 5)).thenReturn(FIRST_BYTES);
    when(translator.translate(bytes, 5, 5)).thenReturn(SECOND_BYTES);

    IngestTranslationProcessor ingestTranslationProcessor = new IngestTranslationProcessor(storage, translatorFactory, processorStatusService);

    ingestTranslationProcessor.process("some-container", "some-file-name.csv", bytes, "TestUser@gmail.com");

    verify(storage).delete("some-container", "translated/some-file-name.csv");
    verify(translator).translate(bytes, 0, 5);
    verify(translator).translate(bytes, 5, 5);
    verify(storage).save(anyString(), eq(FIRST_BYTES), eq("TestUser@gmail.com"), eq("some-container"), eq(true));
    verify(storage).save(anyString(), eq(SECOND_BYTES), eq("TestUser@gmail.com"), eq("some-container"), eq(true));
  }

  @Test
  public void skipsTranslationProcessWhenAlreadyInProgress() {
    IngestTranslationStatus status = new IngestTranslationStatus("some-file-name.csv", 2, 1, Phase.CREATING_NEW_TRANSLATIONS);
    processorStatusService.updateTranslationProcessorStatus("some-container", "some-file-name.csv", status);
    byte[] bytes = h.get("/fixtures/otexa/OTEXA_EXE_HTS.csv");

    IngestTranslationProcessor ingestTranslationProcessor = new IngestTranslationProcessor(storage, translatorFactory, processorStatusService);
    ingestTranslationProcessor.process("some-container", "some-file-name.csv", bytes, "TestUser@gmail.com");

    verify(storage, times(0)).delete("some-container", "translated/some-file-name.csv");
    verify(translator, times(0)).translate(bytes, 0, 5);
    verify(translator, times(0)).translate(bytes, 5, 5);
    verify(storage, times(0)).save(anyString(), eq(FIRST_BYTES), eq("TestUser@gmail.com"), eq("some-container"), eq(true));
    verify(storage, times(0)).save(anyString(), eq(SECOND_BYTES), eq("TestUser@gmail.com"), eq("some-container"), eq(true));
  }

  @Test
  public void savesUploadedFileAndMakesSnapshot() throws IOException {
    byte[] bytes = h.get("/fixtures/otexa/OTEXA_EXE_HTS.csv");
    when(translatorFactory.getTranslator("some-container#some-file-name.csv")).thenReturn(translator);
    when(translator.pageSize()).thenReturn(5);

    IngestTranslationProcessor ingestTranslationProcessor = new IngestTranslationProcessor(storage, translatorFactory, processorStatusService);

    ingestTranslationProcessor.process("some-container", "some-file-name.csv", bytes, "TestUser@gmail.com");

    verify(storage, times(1)).save("some-file-name.csv", bytes, "TestUser@gmail.com", "some-container", true);
    verify(storage, times(1)).makeSnapshot("some-container", "some-file-name.csv");
  }

}
