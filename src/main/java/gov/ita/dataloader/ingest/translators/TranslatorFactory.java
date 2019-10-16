package gov.ita.dataloader.ingest.translators;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TranslatorFactory {

  private static final Map<String, Translator> translators = new HashMap<>();

  static {
    translators.put("otexa#OTEXA_DATA_SET_CAT.csv", new OtexaCatTranslator());
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
