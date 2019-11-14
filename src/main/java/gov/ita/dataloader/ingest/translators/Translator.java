package gov.ita.dataloader.ingest.translators;

public interface Translator {
  byte[] translate(byte[] bytes, int offset, int size);
  int pageSize();
}
