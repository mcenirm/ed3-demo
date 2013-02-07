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

  public static void main(String[] args) throws IOException, MalformedURLException, IllegalArgumentException, FetcherException, FeedException {
    Misc.switchToGMT();
    final String workflowEndpoint = "http://ed3test.itsc.uah.edu:80/ed3/dataservices/doworkflow.php";
    final String echoEndpoint = "https://api.echo.nasa.gov/echo-esip/search/granule.atom";
    Dataset dataset = new Dataset();
    dataset.ed3id = "MODIS";
    dataset.title = "MODIS";
    dataset.echoShortName = "MOD02QKM";
    dataset.echoVersionId = "5";
    dataset.echoDataCenter = "LAADS";
    Datasets datasets = new Datasets();
    datasets.datasetList.add(dataset);
    ReadyToWork rtw = new ReadyToWork();
    rtw.datasets = datasets;
    rtw.workflowEndpoint = workflowEndpoint;
    rtw.echoEndpoint = echoEndpoint;
    rtw.workDataset(dataset.ed3id);
    Work work = rtw.fetchWork(dataset.ed3id);
    System.out.println(work);
    System.out.println("-----------------------");
    final String myClientId = "mmceniry@itsc.uah.edu";
    List<String> urls = rtw.askEcho(rtw.echoEndpoint, myClientId, dataset.echoShortName, dataset.echoVersionId, dataset.echoDataCenter, work);
    System.out.println(urls);
    System.out.println("-----------------------");
    String message = rtw.updateWork(workflowEndpoint, dataset.ed3id, work, urls);
    System.out.println(message);
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
  }

  public List<String> search() throws MalformedURLException, IOException, IllegalArgumentException, FetcherException, FeedException {
    URL feedUrl = getUrl();
    System.out.println(feedUrl);
    FeedFetcher fetcher = new HttpURLFeedFetcher();
    SyndFeed feed = fetcher.retrieveFeed(feedUrl);
    List<SyndEntry> entries = feed.getEntries();
    List<String> urls = new ArrayList<>();
    for (SyndEntry entry : entries) {
      System.out.println(String.format("%s [%s]", entry.getTitle(), entry.getUri()));
      List<SyndLink> links = entry.getLinks();
      for (SyndLink link : links) {
        System.out.println(link);
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
