package gov.ita.dataloader.ingest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
class ZipFileExtractor {

  private final static int BUFFER_SIZE = 2048;

  Map<String, ByteArrayOutputStream> extract(byte[] zipFile) throws IOException {
    ByteArrayInputStream bis = new ByteArrayInputStream(Objects.requireNonNull(zipFile));
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    BufferedOutputStream bos = new BufferedOutputStream(baos);

    byte[] buffer = new byte[BUFFER_SIZE];
    while (bis.read(buffer, 0, BUFFER_SIZE) != -1) {
      bos.write(buffer);
    }
    bos.flush();
    bos.close();
    bis.close();
    return unzip(baos);
  }

  private static Map<String, ByteArrayOutputStream> unzip(ByteArrayOutputStream zippedFileOS) {
    try {
      ZipInputStream inputStream = new ZipInputStream(
        new BufferedInputStream(new ByteArrayInputStream(
          zippedFileOS.toByteArray())));
      ZipEntry entry;

      Map<String, ByteArrayOutputStream> result = new HashMap<>();
      while ((entry = inputStream.getNextEntry()) != null) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        log.info("Extracting entry: " + entry);
        int count;
        byte[] data = new byte[BUFFER_SIZE];
        BufferedOutputStream out = new BufferedOutputStream(
          outputStream, BUFFER_SIZE);
        while ((count = inputStream.read(data, 0, BUFFER_SIZE)) != -1) {
          out.write(data, 0, count);
        }
        out.flush();
        out.close();
        result.put(entry.getName(), outputStream);
      }
      inputStream.close();
      return result;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
