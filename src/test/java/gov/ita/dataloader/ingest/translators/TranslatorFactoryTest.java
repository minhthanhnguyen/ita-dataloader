package gov.ita.dataloader.ingest.translators;

import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

public class TranslatorFactoryTest {

  @Test
  public void returnsOtexaCatTranslator() {
    TranslatorFactory translatorFactory = new TranslatorFactory();
    Translator translator = translatorFactory.getTranslator("otexa#OTEXA_DATA_SET_CAT.csv");
    assertThat(translator, instanceOf(OtexaCatTranslator.class));
  }

}
