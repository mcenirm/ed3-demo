package ed3.demo.echo;

import com.sun.syndication.feed.impl.ObjectBean;
import com.sun.syndication.feed.module.georss.geometries.Envelope;
import com.sun.syndication.feed.module.georss.geometries.LineString;
import com.sun.syndication.feed.module.georss.geometries.Point;
import com.sun.syndication.feed.module.georss.geometries.Polygon;
import com.sun.syndication.feed.module.georss.geometries.Position;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import ed3.demo.util.FeedFetching;
import ed3.demo.util.Misc;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.rometools.fetcher.FeedFetcher;
import org.rometools.fetcher.FetcherException;

public class Search {

  private ObjectBean _objBean;

  public static void main(String[] args) throws MalformedURLException, IllegalArgumentException, IOException, FeedException, FetcherException, DatatypeConfigurationException {
    Misc.switchToGMT();
    Search search = new Search();
    search.base = "https://api.echo.nasa.gov/echo-esip/search/granule.atom";
    search.clientId = "mmceniry@itsc.uah.edu";
    search.shortName = "MOD02QKM";
    search.versionId = "5";
    search.dataCenter = "LAADS";
    search.point = new Point(new Position(36.311, -120.8555));
    search.startTime = DatatypeFactory.newInstance().newXMLGregorianCalendar("2013-01-28");
    search.numberOfResults = 10;
    search.cursor = 0;
    System.out.println(search);
    URL feedUrl = search.getUrl();
    System.out.println(feedUrl);
    FeedFetcher fetcher = FeedFetching.newCachingFeedFetcher();
    SyndFeed feed = fetcher.retrieveFeed(feedUrl);
    System.out.println(feed);
  }
  private String base;
  private String shortName;
  private String versionId;
  private String dataCenter;
  private Envelope boundingBox;
  private Polygon polygon;
  private LineString line;
  private Point point;
  private XMLGregorianCalendar startTime;
  private XMLGregorianCalendar endTime;
  private int cursor;
  private int numberOfResults;
  private String clientId;
  private String spatialType;

  public Search() {
    _objBean = new ObjectBean(Search.class, this);
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

  public XMLGregorianCalendar getStartTime() {
    return startTime;
  }

  public XMLGregorianCalendar getEndTime() {
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
    return "" + a + "," + b;
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

  private String formatQueryParameter(String queryParameterName, XMLGregorianCalendar value) {
    String s = "";
    if (value != null) {
      s = formatQueryParameter(queryParameterName, value.toXMLFormat());
    }
    return s;
  }

  private String formatQueryParameter(String queryParameterName, int value) {
    return formatQueryParameter(queryParameterName, "" + value);
  }
}
