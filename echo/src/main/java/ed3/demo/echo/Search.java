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
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.ws.rs.MessageProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientException;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXB;
import javax.xml.datatype.DatatypeConfigurationException;
import org.rometools.fetcher.FeedFetcher;
import org.rometools.fetcher.FetcherException;
import org.rometools.fetcher.impl.HttpURLFeedFetcher;

public class Search {

  private ObjectBean _objBean;

  public static void main(String[] args) throws DatatypeConfigurationException, MalformedURLException, IOException, IllegalArgumentException, FeedException, FetcherException {
    Misc.switchToGMT();
    final String datasetId = "MODIS";
    final String workflowEndpoint = "http://ed3test.itsc.uah.edu:80/ed3/dataservices/doworkflow.php";
    Work work = fetchWork(workflowEndpoint, datasetId);
    System.out.println(work);
    System.out.println("-----------------------");
    final String echoEndpoint = "https://api.echo.nasa.gov/echo-esip/search/granule.atom";
    final String myClientId = "mmceniry@itsc.uah.edu";
    final String dsShortName = "MOD02QKM";
    final String dsVersionId = "5";
    final String dsDataCenter = "LAADS";
    List<String> urls = askEcho(echoEndpoint, myClientId, dsShortName, dsVersionId, dsDataCenter, work);
    System.out.println(urls);
    System.out.println("-----------------------");
    String message = updateWork(workflowEndpoint, datasetId, work, urls);
    System.out.println(message);
  }

  public static Work fetchWork(String workflowEndpoint, String datasetId) throws ClientException, MessageProcessingException, IllegalArgumentException, NullPointerException, IllegalStateException {
    Client client = ClientFactory.newClient();
    WebTarget target = client.target(workflowEndpoint);
    Builder request = target.queryParam("ds", datasetId).request();
    Response response = request.get();
    System.out.println(String.format("%3d %s", response.getStatus(), response.getStatusInfo()));
    String entity = response.readEntity(String.class);
    response.close();
    System.out.println(entity);
    System.out.println("-----------------------");
    Work work = JAXB.unmarshal(new StringReader(entity), Work.class);
    return work;
  }

  public static List<String> askEcho(final String echoEndpoint, final String myClientId, final String dsShortName, final String dsVersionId, final String dsDataCenter, Work work) throws IllegalArgumentException, MalformedURLException, FeedException, FetcherException, IOException {
    Search search = new Search();
    search.base = echoEndpoint;
    search.clientId = myClientId;
    search.shortName = dsShortName;
    search.versionId = dsVersionId;
    search.dataCenter = dsDataCenter;
    search.point = new Point(new Position(work.latitude, work.longitude));
    search.startTime = work.getStartTime();
    search.endTime = work.getEndTime();
    search.numberOfResults = 10;
    search.cursor = 0;
    System.out.println(search);
    URL feedUrl = search.getUrl();
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

  public static String updateWork(final String workflowEndpoint, final String datasetId, Work work, List<String> urls) throws MessageProcessingException, IllegalStateException, NullPointerException, IllegalArgumentException, ClientException {
    Client client = ClientFactory.newClient();
    WebTarget target = client.target(workflowEndpoint);
    Builder request = target.request(MediaType.TEXT_PLAIN_TYPE);
    Form form = new Form()
            .param("ds", datasetId)
            .param("action", "update")
            .param("id", work.id)
            .param("status", "groovy");
    int n = 0;
    for (String url : urls) {
      form.param("url" + n, url);
      n++;
    }
    System.out.println(form);
    Entity<Form> entity = Entity.form(form);
    Response response = request.post(entity);
    System.out.println(String.format("%3d %s", response.getStatus(), response.getStatusInfo()));
    String message = response.readEntity(String.class);
    return message;
  }
  private String base;
  private String shortName;
  private String versionId;
  private String dataCenter;
  private Envelope boundingBox;
  private Polygon polygon;
  private LineString line;
  private Point point;
  private Calendar startTime;
  private Calendar endTime;
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
