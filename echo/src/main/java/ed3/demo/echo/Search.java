package ed3.demo.echo;

import com.sun.syndication.feed.impl.ObjectBean;
import com.sun.syndication.feed.module.georss.geometries.Envelope;
import com.sun.syndication.feed.module.georss.geometries.LineString;
import com.sun.syndication.feed.module.georss.geometries.Point;
import com.sun.syndication.feed.module.georss.geometries.Polygon;
import com.sun.syndication.feed.module.georss.geometries.Position;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.io.FeedException;
import ed3.demo.util.Misc;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import org.rometools.fetcher.FeedFetcher;
import org.rometools.fetcher.FetcherException;
import org.rometools.fetcher.impl.HttpClientFeedFetcher;

public class Search {

  private ObjectBean _objBean;

  public static void main(String[] args) throws MalformedURLException, IllegalArgumentException, IOException, FeedException, FetcherException {
    Misc.switchToGMT();
    WorkConfiguration config = new WorkConfiguration();
    Datasets datasets = Datasets.loadDatasets();
    String datasetId = "MOD02QKM";
    Dataset dataset = datasets.findDataset(datasetId);
    if (dataset == null) {
      System.out.println("could not find dataset \"" + datasetId + "\"");
      return;
    }
    Work work = new Work();
    work.id = "example";
    work.dataset = dataset;
    work.latitude = 34.73;
    work.longitude = -86.585;
    work.spatial_buffer = 100;
    work.temporal_post = 1;
    work.temporal_pre = 1;
    work.ts = "2013-02-01 12:00:00";
    System.out.println(work);
    System.out.println("-----------------------");
    Search search = new Search();
    search.config = config;
    search.setWork(work);
    System.out.println(search.getUrl());
    search.search();
    for (String url : work.urls) {
      System.out.println(url);
    }
    System.out.println(work.status);
    System.out.println("-----------------------");
  }
  public WorkConfiguration config;
  private Work work;
  private Envelope boundingBox;
  private Polygon polygon;
  private LineString line;
  private int cursor;
  private int numberOfResults;
  private String spatialType;

  public Search() {
    _objBean = new ObjectBean(Search.class, this);
    cursor = 1;
    numberOfResults = 10;
  }

  public Work getWork() {
    return work;
  }

  public void setWork(Work work) {
    this.work = work;
  }

  public void search() throws MalformedURLException, IllegalArgumentException, IOException, FeedException, FetcherException {
    Calendar startTime = work.getStartTime();
    Calendar granuleCutoff = new GregorianCalendar(startTime.getTimeZone());
    work.dataset.latency.negate().addTo(granuleCutoff);
    if (granuleCutoff.after(startTime)) {
      boolean keepSearching = true;
      while (keepSearching) {
        URL feedUrl = getUrl();
        FeedFetcher fetcher = new HttpClientFeedFetcher();
        SyndFeed feed = fetcher.retrieveFeed(feedUrl);
        List<SyndEntry> entries = feed.getEntries();
        if (work.urls == null) {
          work.urls = new ArrayList<>();
        }
        int count = 0;
        for (SyndEntry entry : entries) {
          count++;
          List<SyndLink> links = entry.getLinks();
          for (SyndLink link : links) {
            String href = link.getHref();
            if (href != null) {
              work.urls.add(href);
            }
          }
        }
        keepSearching = count == numberOfResults;
        cursor += numberOfResults;
      }
    }
    Calendar endTime = work.getEndTime();
    if (granuleCutoff.after(endTime)) {
      work.status = Work.CLOSED;
    }
  }

  @Override
  public String toString() {
    return _objBean.toString();
  }

  public String getBase() {
    return config.echoEndpoint;
  }

  public String getShortName() {
    return work.dataset.echoShortName;
  }

  public String getVersionId() {
    return work.dataset.echoVersionId;
  }

  public String getDataCenter() {
    return work.dataset.echoDataCenter;
  }

  public Envelope getBoundingBox() {
    return boundingBox;
  }

  public Polygon getPolygon() {
    return polygon;
  }

  public LineString getLine() {
    return line;
  }

  public Point getPoint() {
    return new Point(new Position(work.latitude, work.longitude));
  }

  public Calendar getStartTime() {
    return work.getStartTime();
  }

  public Calendar getEndTime() {
    return work.getEndTime();
  }

  public int getCursor() {
    return cursor;
  }

  public int getNumberOfResults() {
    return numberOfResults;
  }

  public String getClientId() {
    return config.echoClientId;
  }

  public String getSpatialType() {
    return spatialType;
  }

  private URL getUrl() throws MalformedURLException {
    StringBuilder sb = new StringBuilder();
    sb.append(config.echoEndpoint);
    sb.append(formatQueryParameter("shortName", work.dataset.echoShortName));
    sb.append(formatQueryParameter("versionId", work.dataset.echoVersionId));
    sb.append(formatQueryParameter("dataCenter", work.dataset.echoDataCenter));
    sb.append(formatQueryParameter("boundingBox", boundingBox));
    sb.append(formatQueryParameter("polygon", polygon));
    sb.append(formatQueryParameter("line", line));
    sb.append(formatQueryParameter("point", getPoint()));
    sb.append(formatQueryParameter("startTime", work.getStartTime()));
    sb.append(formatQueryParameter("endTime", work.getEndTime()));
    sb.append(formatQueryParameter("cursor", cursor));
    sb.append(formatQueryParameter("numberOfResults", numberOfResults));
    sb.append(formatQueryParameter("clientId", config.echoClientId));
    sb.append(formatQueryParameter("spatialType", spatialType));
    sb.setCharAt(config.echoEndpoint.length(), '?');
    return new URL(sb.toString());
  }

  private String join(double a, double b) {
    return a + "," + b;
  }

  private String join(Position position) {
    return join(position.getLatitude(), position.getLongitude());
  }

  private String formatQueryParameter(String queryParameterName, String value) {
    String s = "";
    if (value != null) {
      s = "&" + queryParameterName + "=" + URLEncoder.encode(value);
    }
    return s;
  }

  private String formatQueryParameter(String queryParameterName, Envelope value) {
    String s = "";
    if (value != null) {
      double minLongitude = value.getMinLongitude();
      double minLatitude = value.getMinLatitude();
      double maxLongitude = value.getMaxLongitude();
      double maxLatitude = value.getMaxLatitude();
      s = join(minLongitude, minLatitude) + "," + join(maxLongitude, maxLatitude);
      s = formatQueryParameter(queryParameterName, s);
    }
    return s;
  }

  private String formatQueryParameter(String queryParameterName, Polygon value) {
    return value == null ? "" : formatQueryParameter(queryParameterName, value.toString());
  }

  private String formatQueryParameter(String queryParameterName, LineString value) {
    return value == null ? "" : formatQueryParameter(queryParameterName, value.toString());
  }

  private String formatQueryParameter(String queryParameterName, Point value) {
    String s = "";
    if (value != null) {
      Position position = value.getPosition();
      if (position != null) {
        s = formatQueryParameter(queryParameterName, join(position));
      }
    }
    return s;
  }

  private String formatQueryParameter(String queryParameterName, Calendar value) {
    String s = "";
    if (value != null) {
      s = formatQueryParameter(queryParameterName, DatatypeConverter.printDateTime(value));
    }
    return s;
  }

  private String formatQueryParameter(String queryParameterName, int value) {
    return formatQueryParameter(queryParameterName, "" + value);
  }
}
