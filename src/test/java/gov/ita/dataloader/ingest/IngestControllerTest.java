package gov.ita.dataloader.ingest;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ita.dataloader.ingest.configuration.DataloaderConfig;
import gov.ita.dataloader.storage.Storage;
import gov.ita.dataloader.ingest.translators.Translator;
import gov.ita.dataloader.ingest.translators.TranslatorFactory;
import gov.ita.dataloader.security.AuthenticationFacade;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IngestControllerTest {

  @Mock
  private Storage storage;

  @Mock
  private IngestProcessor ingestProcessor;

  @Mock
  private AuthenticationFacade authenticationFacade;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private TranslatorFactory translatorFactory;

  @Mock
  private Translator translator;

  private static byte[] OTHER_BYTES = "other bytes".getBytes();
  private static byte[] DATALOADER_CONFIG = "dataloader blob".getBytes();
  private static byte[] SOME_BYTES = "some bytes".getBytes();

  @Before
  public void setUp() throws IOException {
    IngestProcessorStatus ingestProcessorStatus = new IngestProcessorStatus(0, 0, false, null);
    when(ingestProcessor.getStatus("some-container")).thenReturn(ingestProcessorStatus);
    when(authenticationFacade.getUserName()).thenReturn("TestUser@gmail.com");
    when(storage.getBlob("some-container", "configuration.json")).thenReturn(DATALOADER_CONFIG);
    DataloaderConfig dataloaderConfig = new DataloaderConfig();
    dataloaderConfig.setDataSetConfigs(Collections.emptyList());
    when(objectMapper.readValue(DATALOADER_CONFIG, DataloaderConfig.class)).thenReturn(dataloaderConfig);
  }

  @Test
  public void startIngestProcess() {
    IngestProcessorStatus ingestProcessorStatus = new IngestProcessorStatus(0, 0, false, null);
    when(ingestProcessor.getStatus("some-container")).thenReturn(ingestProcessorStatus);

    IngestController ingestController = new IngestController(storage, ingestProcessor, authenticationFacade, objectMapper, null);
    ingestController.startIngestProcess("some-container");

    verify(ingestProcessor, times(1))
      .process(Collections.emptyList(), "some-container", "TestUser@gmail.com", 5000L);
  }

  @Test
  public void attemptsToStartIngestProcessWhenAlreadyIngesting() {
    IngestProcessorStatus ingestProcessorStatus = new IngestProcessorStatus(0, 0, true, null);
    when(ingestProcessor.getStatus("some-container")).thenReturn(ingestProcessorStatus);

    IngestController ingestController = new IngestController(storage, ingestProcessor, authenticationFacade, objectMapper, null);
    ingestController.startIngestProcess("some-container");

    verify(ingestProcessor, times(0))
      .process(Collections.emptyList(), "some-container", "TestUser@gmail.com", 5000L);
  }

  @Test
  public void getIngestProcessorStatus() {
    IngestProcessorStatus ingestProcessorStatus = new IngestProcessorStatus(333, 123, true, null);
    when(ingestProcessor.getStatus("another-container")).thenReturn(ingestProcessorStatus);

    IngestController ingestController = new IngestController(null, ingestProcessor, null, null, null);

    IngestProcessorStatus result = ingestController.getIngestProcessorStatus("another-container");

    assertTrue(result.isIngesting());
    assertEquals(123, result.getDatasetsCompleted(), 0);
    assertEquals(333, result.getDatasetsQueued(), 0);
  }

  @Test
  public void saveFile() throws IOException {
    when(translatorFactory.getTranslator("cool-container#OG File Name.csv")).thenReturn(null);

    IngestController ingestController = new IngestController(storage, ingestProcessor, authenticationFacade, null, translatorFactory);
    MultipartFile multipartFile = mock(MultipartFile.class);
    when(multipartFile.getBytes()).thenReturn(SOME_BYTES);
    when(multipartFile.getOriginalFilename()).thenReturn("OG File Name.csv");

    ingestController.saveFile(multipartFile, "cool-container");

    verify(storage, times(1))
      .save("OG File Name.csv", SOME_BYTES, "TestUser@gmail.com", "cool-container", true);

    verify(storage, times(1)).makeSnapshot("cool-container", "OG File Name.csv");
  }

  @Test
  public void savedFilePassedThroughTranslator() throws IOException {
    when(translatorFactory.getTranslator("cool-container#OG File Name.csv")).thenReturn(translator);
    when(translator.translate(SOME_BYTES)).thenReturn(OTHER_BYTES);

    IngestController ingestController = new IngestController(storage, null, authenticationFacade, null, translatorFactory);
    MultipartFile multipartFile = mock(MultipartFile.class);
    when(multipartFile.getBytes()).thenReturn(SOME_BYTES);
    when(multipartFile.getOriginalFilename()).thenReturn("OG File Name.csv");

    ingestController.saveFile(multipartFile, "cool-container");

    verify(storage, times(1))
      .save("translated/OG File Name.csv", OTHER_BYTES, "TestUser@gmail.com", "cool-container", true);
  }

  @Test
  public void savedFileDoesNotPassedThroughTranslator() throws IOException {
    when(translatorFactory.getTranslator("cool-container#OG File Name.csv")).thenReturn(null);

    IngestController ingestController = new IngestController(storage, null, authenticationFacade, null, translatorFactory);
    MultipartFile multipartFile = mock(MultipartFile.class);
    when(multipartFile.getBytes()).thenReturn(SOME_BYTES);
    when(multipartFile.getOriginalFilename()).thenReturn("OG File Name.csv");

    ingestController.saveFile(multipartFile, "cool-container");

    verify(storage, times(0))
      .save("translated/OG File Name.csv.json", OTHER_BYTES, "TestUser@gmail.com", "cool-container", true);
  }
}
