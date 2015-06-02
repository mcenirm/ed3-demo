package ed3.demo.quakes;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import static ed3.demo.quakes.Alert.APPLICATION_CAP_XML;
import static ed3.demo.quakes.SAMECodes.EARTHQUAKE_WARNING;
import static ed3.demo.quakes.SAMECodes.SAME;
import ed3.demo.util.FeedFetching;
import ed3.demo.util.Misc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import javax.xml.bind.JAXB;
import org.rometools.fetcher.FeedFetcher;
import org.rometools.fetcher.FetcherException;

public class FetchAndGenerate {

  public static final String CAP_CONSUMER_LOCATION = "http://ed3test.itsc.uah.edu/ed3/events/new.php";
  public static final String FEED_LOCATION = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/1.0_hour.atom";

  public static void main(String[] args) throws MalformedURLException, IllegalArgumentException, IOException, FetcherException, FeedException {
    Pattern magnitudePattern = Pattern.compile("M\\s*(\\d+\\.\\d+)");
    FeedFetcher fetcher = FeedFetching.newCachingFeedFetcher();
    SyndFeed feed = fetcher.retrieveFeed(new URL(FEED_LOCATION));
    List<SyndEntry> entries = feed.getEntries();
    Client client = ClientFactory.newClient();
    WebTarget target = client.target(CAP_CONSUMER_LOCATION);
    AlertBuilder builder = new AlertBuilder();
    File alertsDir = Misc.ensureDirectoryExists("alerts", "alerts");
    for (SyndEntry entry : entries) {
      Alert alert = builder.build(entry);
      alert.setSender("USGS");
      alert.setStatus("Actual");
      alert.setMsgType("Alert");
      alert.setScope("Public");
      alert.setInfoCategory("Geo");
      alert.setInfoEvent("earthquake");
      alert.setInfoUrgency("Past");
      alert.setInfoSeverity("Minor");
      alert.setInfoCertainty("Observed");
      alert.setInfoEventCode(SAME, EARTHQUAKE_WARNING);
      String title = entry.getTitle();
      Matcher magnitudeMatcher = magnitudePattern.matcher(title);
      if (magnitudeMatcher.find()) {
        String possibleMagnitude = magnitudeMatcher.group(1);
        alert.addInfoParameter("magnitude", possibleMagnitude);
      }
      String link = alert.getInfo().getWeb();
      int lastIndexOf = link.lastIndexOf('/');
      String substring = link.substring(lastIndexOf + 1);
      File alertFile = new File(alertsDir, substring + ".xml");
      File responseFile = new File(alertsDir, substring + ".response");
      JAXB.marshal(alert, alertFile);
      Builder request = target.request();
      Entity<Alert> entity = Entity.entity(alert, APPLICATION_CAP_XML);
      Response response = request.post(entity);
      final int status = response.getStatus();
      final StatusType statusInfo = response.getStatusInfo();
      final String message = response.readEntity(String.class);
      saveResponse(response, message, responseFile);
      response.close();
      if (status != 200 || !("success".equalsIgnoreCase(message) || "error - no matching subscriptions".equalsIgnoreCase(message))) {
        System.out.println(substring + " " + alert.getIdentifier());
        System.out.println(status + " " + statusInfo);
        System.out.println(message);
        System.out.println("---------------------");
      }
    }
  }

  static void saveResponse(Response response, String message, File responseFile) throws FileNotFoundException {
    try (PrintWriter out = new PrintWriter(responseFile)) {
      int status = response.getStatus();
      StatusType statusInfo = response.getStatusInfo();
      out.println(status + " " + statusInfo);
      MultivaluedMap<String, Object> headers = response.getHeaders();
      out.println(headers);
      out.println();
      out.println(message);
    }
  }
}
