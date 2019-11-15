package gov.ita.dataloader.ingest;

import gov.ita.dataloader.HttpHelper;
import gov.ita.dataloader.ingest.configuration.DataSetConfig;
import gov.ita.dataloader.ingest.configuration.ReplaceValue;
import gov.ita.dataloader.ingest.configuration.ZipFileConfig;
import gov.ita.dataloader.storage.Storage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.util.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IngestProcessorTest {

  @Mock
  private ZipFileExtractor zipFileExtractor;
  @Mock
  private Storage storage;
  @Mock
  private HttpHelper httpHelper;
  @Mock
  private IngestTranslationProcessor ingestTranslationProcessor;
  @Mock
  private ProcessorStatusService processorStatusService;

  private List<DataSetConfig> dataSetConfigs;

  private byte[] RAD_BYTES = "rad bytes".getBytes();
  private byte[] VERY_RAD_BYTES = "very rad bytes".getBytes();
  private byte[] REALLY_RAD_BYTES = "really rad bytes".getBytes();
  private byte[] ZIP_FILE_BYTES = "zip file bytes".getBytes();

  @Before
  public void setUp() throws Exception {
    when(httpHelper.getBytes("http://cool.io")).thenReturn(RAD_BYTES);
    when(httpHelper.getBytes("http://very-cool.io")).thenReturn(VERY_RAD_BYTES);
    when(httpHelper.getBytes("http://really-cool.io")).thenReturn(REALLY_RAD_BYTES);
    when(httpHelper.getBytes("http://vangos-zip.io")).thenReturn(ZIP_FILE_BYTES);
    setUpProcessorStatus(false);
    dataSetConfigs = new ArrayList<>();
  }

  private void setUpProcessorStatus(Boolean ingesting) {
    Map<String, IngestProcessorStatus> ingestProcessorStatus = new HashMap<>();
    ingestProcessorStatus.put("a-container", new IngestProcessorStatus(5, 3, ingesting, Collections.emptyList()));
    when(processorStatusService.getIngestProcessorStatusMap()).thenReturn(ingestProcessorStatus);
  }

  @Test
  public void processDataSetConfig() {
    dataSetConfigs.add(new DataSetConfig("http://cool.io", true, "rad.csv", null, null));
    dataSetConfigs.add(new DataSetConfig("http://very-cool.io", true, "very-rad.csv", null, null));
    dataSetConfigs.add(new DataSetConfig("http://really-cool.io", true, "really-rad.csv", null, null));

    IngestProcessor ingestProcessor = new IngestProcessor(null, storage, httpHelper, ingestTranslationProcessor, processorStatusService);
    ingestProcessor.process(dataSetConfigs, "a-container", "TestUser@gmail.com", 0);

    verify(storage, times(1))
      .save("rad.csv", RAD_BYTES, "TestUser@gmail.com", "a-container", false);
    verify(storage, times(1))
      .save("very-rad.csv", VERY_RAD_BYTES, "TestUser@gmail.com", "a-container", false);
    verify(storage, times(1))
      .save("really-rad.csv", REALLY_RAD_BYTES, "TestUser@gmail.com", "a-container", false);
  }

  @Test
  public void skipsIngestProcessWhenAlreadyInProgress() {
    setUpProcessorStatus(true);
    dataSetConfigs.add(new DataSetConfig("http://cool.io", true, "rad.csv", null, null));

    IngestProcessor ingestProcessor = new IngestProcessor(null, storage, httpHelper, ingestTranslationProcessor, processorStatusService);
    ingestProcessor.process(dataSetConfigs, "a-container", "TestUser@gmail.com", 0);

    verify(storage, times(0))
      .save("rad.csv", RAD_BYTES, "TestUser@gmail.com", "a-container", false);
  }

  @Test
  public void processDataSetConfigWithReplaceValues() throws Exception {
    List<ReplaceValue> replaceValues = new ArrayList<>();
    replaceValues.add(new ReplaceValue("baseball", "football"));
    dataSetConfigs.add(new DataSetConfig("http://vango.io", true, "vangos.csv", replaceValues, null));

    when(httpHelper.getBytes("http://vango.io")).thenReturn("The best sport is baseball!".getBytes());

    IngestProcessor ingestProcessor = new IngestProcessor(null, storage, httpHelper, ingestTranslationProcessor, processorStatusService);
    ingestProcessor.process(dataSetConfigs, "a-container", "TestUser@gmail.com", 0);

    verify(storage, times(1))
      .save("vangos.csv", "The best sport is football!".getBytes(), "TestUser@gmail.com", "a-container", false);
  }

  @Test
  public void processDataSetConfigWithZipConfig() throws Exception {
    List<ReplaceValue> replaceValues = new ArrayList<>();
    replaceValues.add(new ReplaceValue("swimming", "hiking"));
    replaceValues.add(new ReplaceValue("biking", "climbing"));

    List<ZipFileConfig> zipFileConfigs = new ArrayList<>();
    zipFileConfigs.add(new ZipFileConfig("Hobbies_1342.csv", "Hobbies_A.csv", null, replaceValues));
    zipFileConfigs.add(new ZipFileConfig("Hobbies_5674.csv", "Hobbies_B.csv", null, replaceValues));

    dataSetConfigs.add(new DataSetConfig("http://vangos-zip.io", true, "vangos.zip", null, zipFileConfigs));

    Map<String, ByteArrayOutputStream> zipFileContents = new HashMap<>();
    zipFileContents.put("Hobbies_1342.csv", convert("My favorite hobby is swimming!"));
    zipFileContents.put("Hobbies_5674.csv", convert("My favorite hobby is biking!"));

    when(zipFileExtractor.extract(ZIP_FILE_BYTES)).thenReturn(zipFileContents);

    IngestProcessor ingestProcessor = new IngestProcessor(zipFileExtractor, storage, httpHelper, ingestTranslationProcessor, processorStatusService);
    ingestProcessor.process(dataSetConfigs, "a-container", "TestUser@gmail.com", 0);

    verify(storage, times(1))
      .save("vangos.zip", ZIP_FILE_BYTES, "TestUser@gmail.com", "a-container", false);
    verify(storage, times(1))
      .save("Hobbies_A.csv", "My favorite hobby is hiking!".getBytes(), "TestUser@gmail.com", "a-container", false);
    verify(storage, times(1))
      .save("Hobbies_B.csv", "My favorite hobby is climbing!".getBytes(), "TestUser@gmail.com", "a-container", false);
  }

  @Test
  public void processDataSetConfigWithSkipLinesInZipConfig() throws Exception {
    Integer skipLineCount = 2;

    List<ZipFileConfig> zipFileConfigs = new ArrayList<>();
    zipFileConfigs.add(new ZipFileConfig("Hobbies_skip.csv", "Skipped_Hobbies.csv", skipLineCount, null));
    dataSetConfigs.add(new DataSetConfig("http://vangos-zip.io", true, "vangos.zip", null, zipFileConfigs));

    when(httpHelper.getBytes("http://vangos-zip.io")).thenReturn(ZIP_FILE_BYTES);

    Map<String, ByteArrayOutputStream> zipFileContents = new HashMap<>();
    zipFileContents.put("Hobbies_skip.csv", convert("zip file bytes \n more bytes \n and more \n never ending bytes"));

    when(zipFileExtractor.extract(ZIP_FILE_BYTES)).thenReturn(zipFileContents);

    IngestProcessor ingestProcessor = new IngestProcessor(zipFileExtractor, storage, httpHelper, ingestTranslationProcessor, processorStatusService);
    ingestProcessor.process(dataSetConfigs, "a-container", "TestUser@gmail.com", 0);

    verify(storage, times(1))
      .save("vangos.zip", ZIP_FILE_BYTES, "TestUser@gmail.com", "a-container", false);
    verify(storage, times(1))
      .save("Skipped_Hobbies.csv", " and more \n never ending bytes\n".getBytes(), "TestUser@gmail.com", "a-container", false);
  }

  @Test
  public void sendFilesThroughIngestTranslationProcessor() {
    dataSetConfigs.add(new DataSetConfig("http://cool.io", true, "rad.csv", null, null));

    IngestProcessor ingestProcessor = new IngestProcessor(zipFileExtractor, storage, httpHelper, ingestTranslationProcessor, processorStatusService);
    ingestProcessor.process(dataSetConfigs, "a-container", "TestUser@gmail.com", 0);

    verify(storage, times(1))
      .save("rad.csv", RAD_BYTES, "TestUser@gmail.com", "a-container", false);
    verify(ingestTranslationProcessor, times(1))
      .process("a-container", "rad.csv", RAD_BYTES, "TestUser@gmail.com");
  }

  private ByteArrayOutputStream convert(String s) {
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    for (int i = 0; i < s.length(); ++i)
      outStream.write(s.charAt(i));

    return outStream;
  }
}