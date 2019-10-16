package gov.ita.dataloader.ingest;

import gov.ita.dataloader.HttpHelper;
import gov.ita.dataloader.ingest.configuration.DataSetConfig;
import gov.ita.dataloader.ingest.configuration.ReplaceValue;
import gov.ita.dataloader.ingest.configuration.ZipFileConfig;
import gov.ita.dataloader.ingest.storage.Storage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IngestProcessorTest {

  @Mock
  private ZipFileExtractor zipFileExtractor;
  @Mock
  private Storage storage;
  @Mock
  private HttpHelper httpHelper;

  private List<DataSetConfig> dataSetConfigs;

  @Test
  public void processDataSetConfig() throws Exception {
    byte[] radBytes = "rad bytes".getBytes();
    byte[] veryRadBytes = "very rad bytes".getBytes();
    byte[] reallyRadBytes = "really rad bytes".getBytes();

    dataSetConfigs = new ArrayList<>();
    dataSetConfigs.add(new DataSetConfig("http://cool.io", true, "rad.csv", null, null));
    dataSetConfigs.add(new DataSetConfig("http://very-cool.io", true, "very-rad.csv", null, null));
    dataSetConfigs.add(new DataSetConfig("http://really-cool.io", true, "really-rad.csv", null, null));

    when(httpHelper.getBytes("http://cool.io")).thenReturn(radBytes);
    when(httpHelper.getBytes("http://very-cool.io")).thenReturn(veryRadBytes);
    when(httpHelper.getBytes("http://really-cool.io")).thenReturn(reallyRadBytes);

    IngestProcessor ingestProcessor = new IngestProcessor(zipFileExtractor, storage, httpHelper);
    ingestProcessor.process(dataSetConfigs, "a-container", "TestUser@gmail.com", 0);

    verify(storage, times(1))
      .save("rad.csv", radBytes, "TestUser@gmail.com", "a-container", false);
    verify(storage, times(1))
      .save("very-rad.csv", veryRadBytes, "TestUser@gmail.com", "a-container", false);
    verify(storage, times(1))
      .save("really-rad.csv", reallyRadBytes, "TestUser@gmail.com", "a-container", false);
  }

  @Test
  public void processDataSetConfigWithReplaceValues() throws Exception {
    List<ReplaceValue> replaceValues = new ArrayList<>();
    replaceValues.add(new ReplaceValue("baseball", "football"));

    dataSetConfigs = new ArrayList<>();
    dataSetConfigs.add(new DataSetConfig("http://vango.io", true, "vangos.csv", replaceValues, null));

    when(httpHelper.getBytes("http://vango.io")).thenReturn("The best sport is baseball!".getBytes());

    IngestProcessor ingestProcessor = new IngestProcessor(zipFileExtractor, storage, httpHelper);
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
    zipFileConfigs.add(new ZipFileConfig("Hobbies_1342.csv", "Hobbies_A.csv", replaceValues));
    zipFileConfigs.add(new ZipFileConfig("Hobbies_5674.csv", "Hobbies_B.csv", replaceValues));

    dataSetConfigs = new ArrayList<>();
    dataSetConfigs.add(new DataSetConfig("http://vangos-zip.io", true, "vangos.zip", null, zipFileConfigs));

    byte[] zipFileBytes = "zip file bytes".getBytes();
    when(httpHelper.getBytes("http://vangos-zip.io")).thenReturn(zipFileBytes);

    Map<String, ByteArrayOutputStream> zipFileContents = new HashMap<>();
    zipFileContents.put("Hobbies_1342.csv", convert("My favorite hobby is swimming!"));
    zipFileContents.put("Hobbies_5674.csv", convert("My favorite hobby is biking!"));

    when(zipFileExtractor.extract(zipFileBytes)).thenReturn(zipFileContents);

    IngestProcessor ingestProcessor = new IngestProcessor(zipFileExtractor, storage, httpHelper);
    ingestProcessor.process(dataSetConfigs, "a-container", "TestUser@gmail.com", 0);

    verify(storage, times(1))
      .save("vangos.zip", zipFileBytes, "TestUser@gmail.com", "a-container", false);
    verify(storage, times(1))
      .save("Hobbies_A.csv", "My favorite hobby is hiking!".getBytes(), "TestUser@gmail.com", "a-container", false);
    verify(storage, times(1))
      .save("Hobbies_B.csv", "My favorite hobby is climbing!".getBytes(), "TestUser@gmail.com", "a-container", false);
  }

  private ByteArrayOutputStream convert(String s) {
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    for (int i = 0; i < s.length(); ++i)
      outStream.write(s.charAt(i));

    return outStream;
  }
}