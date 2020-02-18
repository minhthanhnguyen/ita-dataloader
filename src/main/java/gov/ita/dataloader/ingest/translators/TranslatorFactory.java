package gov.ita.dataloader.ingest.translators;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TranslatorFactory {

  private static final Map<String, Translator> translators = new HashMap<>();

  static {
    translators.put("otexa#OTEXA_DATA_SET_CAT.csv", new OtexaCatCsvTranslator());
    translators.put("otexa#OTEXA_EXE_HTS.csv", new OtexaHtsCsvTranslator());
    translators.put("select-usa#WORLDBANK_EASE_COUNTRY_INDEX.csv", new WorldBankEaseIndexCsvTranslator());
    translators.put("sima#census", new SimaCensusCsvTranslator());

  }

  public Translator getTranslator(String containerFileCompositeKey) {
    for (String key : translators.keySet()) {
      if (containerFileCompositeKey.contains(key)) {
        return translators.get(key);
      }
    }

    return null;
  }
}
