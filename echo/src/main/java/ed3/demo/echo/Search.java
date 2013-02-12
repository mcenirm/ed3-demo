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
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import org.rometools.fetcher.FeedFetcher;
import org.rometools.fetcher.FetcherException;
import org.rometools.fetcher.impl.HttpURLFeedFetcher;

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
    work.id = dataset.ed3id;
    work.latitude = 34.73;
    work.longitude = -86.585;
    work.spatial_buffer = 100;
    work.temporal_post = 1;
    work.temporal_pre = 1;
    work.ts = "2013-02-01 12:00:00";
    System.out.println(work);
    System.out.println("-----------------------");
    Search search = new Search();
    search.apply(config).apply(dataset).apply(work);
    List<String> urls = search.search();
    for (String url : urls) {
      System.out.println(url);
    }
    System.out.println("-----------------------");
  }
  public String base;
  public String shortName;
  public String versionId;
  public String dataCenter;
  private Envelope boundingBox;
  private Polygon polygon;
  private LineString line;
  public Point point;
  public Calendar startTime;
  public Calendar endTime;
  public int cursor;
  public int numberOfResults;
  public String clientId;
  private String spatialType;

  public Search() {
    _objBean = new ObjectBean(Search.class, this);
    cursor = 0;
    numberOfResults = 10;
  }

  public Search apply(WorkConfiguration config) {
    base = config.echoEndpoint;
    clientId = config.echoClientId;
    return this;
  }

  public Search apply(Dataset dataset) {
    shortName = dataset.echoShortName;
    versionId = dataset.echoVersionId;
    dataCenter = dataset.echoDataCenter;
    return this;
  }

  public Search apply(Work work) {
    point = new Point(new Position(work.latitude, work.longitude));
    startTime = work.getStartTime();
    endTime = work.getEndTime();
    return this;
  }

  public List<String> search() throws MalformedURLException, IllegalArgumentException, IOException, FeedException, FetcherException {
    URL feedUrl = getUrl();
    FeedFetcher fetcher = new HttpURLFeedFetcher();
    SyndFeed feed = fetcher.retrieveFeed(feedUrl);
    List<SyndEntry> entries = feed.getEntries();
    List<String> urls = new ArrayList<>();
    for (SyndEntry entry : entries) {
      List<SyndLink> links = entry.getLinks();
      for (SyndLink link : links) {
        String href = link.getHref();
        if (href != null) {
          urls.add(href);
        }
      }
    }
    return urls;
  }

  @Override
  public String toString() {
    return _objBean.toString();
  }

  public String getBase() {
    return base;
  }

  public String getShortName() {
    return shortName;
  }

  public String getVersionId() {
    return versionId;
  }

  public String getDataCenter() {
    return dataCenter;
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
    return point;
  }

  public Calendar getStartTime() {
    return startTime;
  }

  public Calendar getEndTime() {
    return endTime;
  }

  public int getCursor() {
    return cursor;
  }

  public int getNumberOfResults() {
    return numberOfResults;
  }

  public String getClientId() {
    return clientId;
  }

  public String getSpatialType() {
    return spatialType;
  }

  private URL getUrl() throws MalformedURLException {
    StringBuilder sb = new StringBuilder();
    sb.append(base);
    sb.append(formatQueryParameter("shortName", shortName));
    sb.append(formatQueryParameter("versionId", versionId));
    sb.append(formatQueryParameter("dataCenter", dataCenter));
    sb.append(formatQueryParameter("boundingBox", boundingBox));
    sb.append(formatQueryParameter("polygon", polygon));
    sb.append(formatQueryParameter("line", line));
    sb.append(formatQueryParameter("point", point));
    sb.append(formatQueryParameter("startTime", startTime));
    sb.append(formatQueryParameter("endTime", endTime));
    sb.append(formatQueryParameter("cursor", cursor));
    sb.append(formatQueryParameter("numberOfResults", numberOfResults));
    sb.append(formatQueryParameter("clientId", clientId));
    sb.append(formatQueryParameter("spatialType", spatialType));
    sb.setCharAt(base.length(), '?');
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
