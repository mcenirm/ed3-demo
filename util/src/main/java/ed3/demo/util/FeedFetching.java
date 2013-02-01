package ed3.demo.util;

import java.io.File;
import java.io.IOException;
import java.util.TimeZone;
import org.rometools.fetcher.FeedFetcher;
import org.rometools.fetcher.impl.DiskFeedInfoCache;
import org.rometools.fetcher.impl.HttpURLFeedFetcher;

public class FeedFetching {

    public static File ensureDirectoryExists(String dirName, String dirDescription) throws IOException {
        return ensureDirectoryExists(new File("."), dirName, dirDescription);
    }

    public static File ensureDirectoryExists(File parentDir, String dirName, String dirDescription) throws IOException {
        File dir = new File(parentDir, dirName);
        if (!dir.isDirectory() && !dir.mkdir()) {
            throw new IOException(String.format("could not create %3$s directory \"%2$s\" under parent \"%1$s\"", parentDir, dirName, dirDescription));
        }
        return dir;
    }

    public static FeedFetcher newCachingFeedFetcher() throws IOException {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        File feedCacheDir = FeedFetching.ensureDirectoryExists("feedcache", "feed cache");
        String cachePath = feedCacheDir.getPath();
        DiskFeedInfoCache feedInfoCache = new DiskFeedInfoCache(cachePath);
        FeedFetcher fetcher = new HttpURLFeedFetcher(feedInfoCache);
        return fetcher;
    }
}
