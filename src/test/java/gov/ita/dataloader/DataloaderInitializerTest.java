package gov.ita.dataloader;

import gov.ita.dataloader.storage.StorageInitializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("development")
public class DataloaderInitializerTest {

  @MockBean
  private StorageInitializer storageInitializer;

  @Test
  public void onApplicationEvent() {
    verify(storageInitializer, times(1)).init();
  }
}