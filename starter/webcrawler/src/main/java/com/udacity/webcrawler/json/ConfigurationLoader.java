package com.udacity.webcrawler.json;

import java.io.Reader;
import java.nio.file.Path;
import java.util.Objects;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.BufferedReader;
import java.nio.file.Files;

/**
 * A static utility class that loads a JSON configuration file.
 */
public final class ConfigurationLoader {

  private final Path path;

  /**
   * Create a {@link ConfigurationLoader} that loads configuration from the given {@link Path}.
   */
  public ConfigurationLoader(Path path) {
    this.path = Objects.requireNonNull(path);
  }

  /**
   * Loads configuration from this {@link ConfigurationLoader}'s path
   *
   * @return the loaded {@link CrawlerConfiguration}.
   */
  public CrawlerConfiguration load() {
    // TODO: Fill in this method.
    // create a reader to pass to read()
    // the reader should be based on a JSON file in the "path" path
    // make sure to close the file

    //Should I be doing a buffered reader here? <QUESTION>
    try (BufferedReader reader = Files.newBufferedReader(path)) {
      return read(reader);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Loads crawler configuration from the given reader.
   *
   * @param reader a Reader pointing to a JSON string that contains crawler configuration.
   * @return a crawler configuration
   */
  public static CrawlerConfiguration read(Reader reader) throws IOException {
    // TODO: Fill in this method
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.disable(com.fasterxml.jackson.core.JsonParser.Feature.AUTO_CLOSE_SOURCE);
    try {
      CrawlerConfiguration config = objectMapper.readValue(reader, CrawlerConfiguration.class);
      return config;
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    }
  }
}
