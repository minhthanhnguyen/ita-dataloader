package gov.ita.dataloader.ingest.translators;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

public class TranslatorFactoryTest {

  @Test
  public void returnsOtexaCatTranslator() {
    TranslatorFactory translatorFactory = new TranslatorFactory();
    Translator translator = translatorFactory.getTranslator("otexa#OTEXA_DATA_SET_CAT.csv");
    assertThat(translator, instanceOf(OtexaCatTranslator.class));
    assertEquals(-1, translator.pageSize());
  }

  @Test
  public void returnsOtexaHtsTranslator() {
    TranslatorFactory translatorFactory = new TranslatorFactory();
    Translator translator = translatorFactory.getTranslator("otexa#OTEXA_EXE_HTS.csv");
    assertThat(translator, instanceOf(OtexaHtsTranslator.class));
    assertEquals(25000, translator.pageSize());
  }

  @Test
  public void returnsWorldBankEaseIndexTranslator() {
    TranslatorFactory translatorFactory = new TranslatorFactory();
    Translator translator = translatorFactory.getTranslator("select-usa#WORLDBANK_EASE_COUNTRY_INDEX.csv");
    assertThat(translator, instanceOf(WorldBankEaseIndexTranslator.class));
    assertEquals(-1, translator.pageSize());
  }

}
