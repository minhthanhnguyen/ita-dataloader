package gov.ita.dataloader.ingest;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ita.dataloader.ingest.configuration.DataloaderConfig;
import gov.ita.dataloader.ingest.storage.Storage;
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

  @Before
  public void setUp() throws IOException {
    IngestProcessorStatus ingestProcessorStatus = new IngestProcessorStatus(0, 0, false, null);
    when(ingestProcessor.getStatus("some-container")).thenReturn(ingestProcessorStatus);
    when(authenticationFacade.getUserName()).thenReturn("TestUser@gmail.com");
    when(storage.getBlob("some-container", "configuration.json")).thenReturn("dataloader blob".getBytes());
    DataloaderConfig dataloaderConfig = new DataloaderConfig();
    dataloaderConfig.setDataSetConfigs(Collections.emptyList());
    when(objectMapper.readValue("dataloader blob".getBytes(), DataloaderConfig.class)).thenReturn(dataloaderConfig);
  }

  @Test
  public void startIngestProcess() {
    IngestProcessorStatus ingestProcessorStatus = new IngestProcessorStatus(0, 0, false, null);
    when(ingestProcessor.getStatus("some-container")).thenReturn(ingestProcessorStatus);

    IngestController ingestController = new IngestController(storage, ingestProcessor, authenticationFacade, objectMapper);
    ingestController.startIngestProcess("some-container");

    verify(ingestProcessor, times(1))
      .process(Collections.emptyList(), "some-container", "TestUser@gmail.com", 5000L);
  }

  @Test
  public void attemptsToStartIngestProcessWhenAlreadyIngesting() {
    IngestProcessorStatus ingestProcessorStatus = new IngestProcessorStatus(0, 0, true, null);
    when(ingestProcessor.getStatus("some-container")).thenReturn(ingestProcessorStatus);

    IngestController ingestController = new IngestController(storage, ingestProcessor, authenticationFacade, objectMapper);
    ingestController.startIngestProcess("some-container");

    verify(ingestProcessor, times(0))
      .process(Collections.emptyList(), "some-container", "TestUser@gmail.com", 5000L);
  }

  @Test
  public void getIngestProcessorStatus() {
    IngestProcessorStatus ingestProcessorStatus = new IngestProcessorStatus(333, 123, true, null);
    when(ingestProcessor.getStatus("another-container")).thenReturn(ingestProcessorStatus);

    IngestController ingestController = new IngestController(storage, ingestProcessor, authenticationFacade, objectMapper);

    IngestProcessorStatus result = ingestController.getIngestProcessorStatus("another-container");
    assertTrue(result.isIngesting());
    assertEquals(123, result.getDatasetsCompleted(), 0);
    assertEquals(333, result.getDatasetsQueued(), 0);
  }

  @Test
  public void saveFile() throws IOException {
    IngestController ingestController = new IngestController(storage, ingestProcessor, authenticationFacade, objectMapper);
    MultipartFile multipartFile = mock(MultipartFile.class);
    when(multipartFile.getBytes()).thenReturn("some bytes".getBytes());
    when(multipartFile.getOriginalFilename()).thenReturn("OG File Name");
    ingestController.saveFile(multipartFile, "cool-container");
    verify(storage, times(1))
      .save("OG File Name", "some bytes".getBytes(), "TestUser@gmail.com", "cool-container", true);
  }
}