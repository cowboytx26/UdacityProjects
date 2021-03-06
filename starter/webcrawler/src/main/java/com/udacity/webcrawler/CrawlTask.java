package com.udacity.webcrawler;

import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ConcurrentSkipListSet;

//Check on what the implementation of a task class should look like in the lesson on forkjoin
//I need to have a method called compute for the thread task and possibly another method that does some type of work
//I think this second method is optional - actually the second  method is the constructor and I have to have it
//The compute method should return a CrawlResult
//I will need to call a new CrawlTask within the compute method of this class to do the recursion
//The initial call to CrawlTask and compute should be from the ParallelWebCrawler
public class CrawlTask extends RecursiveAction {

    private final String url;
    private final Clock clock;
    private final PageParserFactory parserFactory;
    private final Instant deadline;
    private final int popularWordCount;
    private final int maxDepth;
    private final List<Pattern> ignoredUrls;
    private final Map<String, Integer> counts;
    private final Set<String> visitedUrls;
    private final ForkJoinPool pool;

    public CrawlTask(String url, Clock clock, PageParserFactory parserFactory, Instant deadline, int popularWordCount,
                     int maxDepth, List<Pattern> ignoredUrls, Map<String, Integer> counts,
                     Set<String> visitedUrls, ForkJoinPool pool) {
        this.url = url;
        this.clock = clock;
        this.parserFactory = parserFactory;
        this.deadline = deadline;
        this.popularWordCount = popularWordCount;
        this.maxDepth = maxDepth;
        this.ignoredUrls = ignoredUrls;
        this.counts = counts;
        this.visitedUrls = visitedUrls;
        this.pool = pool;
    }

    //Compute doesn't need to return anything so long as it updates the counts and visitedUrls collections
    @Override
    protected void compute() {

        if (maxDepth == 0) {
            return;
        }

        if (clock.instant().isAfter(deadline)) {
            return;
        }

        for (Pattern pattern : ignoredUrls) {
            if (pattern.matcher(url).matches()) {
                return;
            }
        }

        //If the URL is not in the set, add returns true which is false when negated and the code continues
        //If the URL is in the set, add returns false which is true when negated and the code returns
        //and does not continue
        if (!visitedUrls.add(url)) {
            return;
        }

        PageParser.Result result = parserFactory.get(url).parse();
        List<String> sublinks =result.getLinks();

        for (ConcurrentMap.Entry<String, Integer> e : result.getWordCounts().entrySet()) {
            counts.compute(e.getKey(), (k,v)->(v==null)?e.getValue():e.getValue()+v);
        }

        List<CrawlTask> crawlTasks = result.getLinks().stream().map(link -> new CrawlTask(link, clock, parserFactory, deadline, popularWordCount,
                maxDepth - 1, ignoredUrls, counts, visitedUrls, pool)).collect(Collectors.toList());
        invokeAll(crawlTasks);
        return;
    }
}
