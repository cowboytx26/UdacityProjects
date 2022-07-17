package com.udacity.webcrawler.main;

import com.google.inject.Guice;
import com.udacity.webcrawler.WebCrawler;
import com.udacity.webcrawler.WebCrawlerModule;
import com.udacity.webcrawler.json.ConfigurationLoader;
import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.json.CrawlResultWriter;
import com.udacity.webcrawler.json.CrawlerConfiguration;
import com.udacity.webcrawler.profiler.Profiler;
import com.udacity.webcrawler.profiler.ProfilerModule;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Objects;

public final class WebCrawlerMain {

  private final CrawlerConfiguration config;

  private WebCrawlerMain(CrawlerConfiguration config) {
    this.config = Objects.requireNonNull(config);
  }

  @Inject
  private WebCrawler crawler;

  @Inject
  private Profiler profiler;

  private void run() throws Exception {
    Guice.createInjector(new WebCrawlerModule(config), new ProfilerModule()).injectMembers(this);

    CrawlResult result = crawler.crawl(config.getStartPages());
    CrawlResultWriter resultWriter = new CrawlResultWriter(result);
    // TODO: Write the crawl results to a JSON file (or System.out if the file name is empty)

    //Pull the path out of the config which is passed into the constructor in the config variable
    //It is stored in the resultPath key value which can be accessed from config.resultPath
    //Since resultPath returns a string, convert it to a path variable
    //Call resultWriter.write(new path variable)
    //System.out.println("Result Path: " + config.getResultPath());
    //System.out.println("Profile Output Path: " + config.getProfileOutputPath());
    System.out.println(System.lineSeparator());

    if (config.getResultPath() != null & config.getResultPath().length() > 0) {
      Path resultPath = Path.of(config.getResultPath());
      resultWriter.write(resultPath);
    } else {
      //Don't close system out here.  You won't be able to print anything to output after that if you do
      try {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        resultWriter.write(writer);
        System.out.println(System.lineSeparator());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    // TODO: Write the profile data to a text file (or System.out if the file name is empty)
    if (config.getProfileOutputPath() != null & config.getProfileOutputPath().length() > 0) {
      Path profilePath = Path.of(config.getProfileOutputPath());
      profiler.writeData(profilePath);
    } else {
      try {
        BufferedWriter profileWriter = new BufferedWriter(new OutputStreamWriter(System.out));
        profiler.writeData(profileWriter);
        System.out.println(System.lineSeparator());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("Usage: WebCrawlerMain [starting-url]");
      return;
    }

    CrawlerConfiguration config = new ConfigurationLoader(Path.of(args[0])).load();
    new WebCrawlerMain(config).run();
  }
}
