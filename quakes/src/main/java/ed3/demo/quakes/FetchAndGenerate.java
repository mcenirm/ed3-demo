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
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import javax.xml.bind.JAXB;
import org.rometools.fetcher.FeedFetcher;
import org.rometools.fetcher.FetcherException;

public class FetchAndGenerate {

  public static final String CAP_CONSUMER_LOCATION = "http://ed3test.itsc.uah.edu/ed3/events/new.php";
  public static final String FEED_LOCATION = "http://earthquake.usgs.gov/earthquakes/feed/atom/1.0/hour";

  public static void main(String[] args) throws MalformedURLException, IllegalArgumentException, IOException, FetcherException, FeedException {
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
      alert.setInfoEvent("Earthquake");
      alert.setInfoUrgency("Past");
      alert.setInfoSeverity("Minor");
      alert.setInfoCertainty("Observed");
      alert.setInfoEventCode(SAME, EARTHQUAKE_WARNING);
      List<SyndCategory> categories = entry.getCategories();
      for (SyndCategory category : categories) {
        if (category.getTaxonomyUri() == null) {
          String name = category.getName();
          try {
            Float.parseFloat(name);
            alert.addInfoParameter("Magnitude", name);
            break;
          } catch (NullPointerException | NumberFormatException e) {
            // PASS
          }
        }
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
      (new PrintWriter(responseFile)).println(response.toString());
      final int status = response.getStatus();
      final StatusType statusInfo = response.getStatusInfo();
      final String message = response.readEntity(String.class);
      response.close();
      if (status != 200) {
        System.out.println(status);
        System.out.println(statusInfo);
        System.out.println(message);
      }
    }
  }
}
