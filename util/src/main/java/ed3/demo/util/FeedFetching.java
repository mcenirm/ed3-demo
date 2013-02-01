package ed3.demo.util;

import java.io.File;
import java.io.IOException;
import org.rometools.fetcher.FeedFetcher;
import org.rometools.fetcher.impl.DiskFeedInfoCache;
import org.rometools.fetcher.impl.HttpURLFeedFetcher;

public class FeedFetching {

  public static FeedFetcher newCachingFeedFetcher() throws IOException {
    Misc.switchToGMT();
    File feedCacheDir = Misc.ensureDirectoryExists("feedcache", "feed cache");
    String cachePath = feedCacheDir.getPath();
    DiskFeedInfoCache feedInfoCache = new DiskFeedInfoCache(cachePath);
    FeedFetcher fetcher = new HttpURLFeedFetcher(feedInfoCache);
    return fetcher;
  }
}
