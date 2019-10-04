package gov.ita.dataloader.ingest;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class LogItem {
  private String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  private String message;

  public LogItem(String message) {
    this.message = message;
  }
}
