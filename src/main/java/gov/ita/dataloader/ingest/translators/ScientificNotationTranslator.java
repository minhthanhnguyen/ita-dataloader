package gov.ita.dataloader.ingest.translators;

public class ScientificNotationTranslator {
  public boolean isScientificNotation(String val) {
    return val.matches("[+-]?\\d(\\.\\d+)?[Ee][+-]?\\d+");
  }

  String translate(String val) {
    return String.format("%f", Double.parseDouble(val));
  }
}
