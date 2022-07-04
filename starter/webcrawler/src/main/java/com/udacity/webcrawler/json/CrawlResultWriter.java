package com.udacity.webcrawler.json;

import java.io.Writer;
import java.nio.file.Path;
import java.util.Objects;
import java.io.BufferedWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Utility class to write a {@link CrawlResult} to file.
 */
public final class CrawlResultWriter {
  private final CrawlResult result;

  /**
   * Creates a new {@link CrawlResultWriter} that will write the given {@link CrawlResult}.
   */
  public CrawlResultWriter(CrawlResult result) {
    this.result = Objects.requireNonNull(result);
  }

  /**
   * Formats the {@link CrawlResult} as JSON and writes it to the given {@link Path}.
   *
   * <p>If a file already exists at the path, the existing file should not be deleted; new data
   * should be appended to it.
   *
   * @param path the file path where the crawl result data should be written.
   */
  public void write(Path path) {
    //should I be doing a buffered writer here? <QUESTION>
    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      write(writer);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Formats the {@link CrawlResult} as JSON and writes it to the given {@link Writer}.
   *
   * @param writer the destination where the crawl result data should be written.
   */
  public void write(Writer writer) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    try {
      objectMapper.writeValue(writer, result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
