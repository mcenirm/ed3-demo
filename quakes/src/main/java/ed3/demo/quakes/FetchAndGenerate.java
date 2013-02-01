package ed3.demo.quakes;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import static ed3.demo.quakes.Alert.APPLICATION_CAP_XML;
import static ed3.demo.quakes.SAMECodes.EARTHQUAKE_WARNING;
import static ed3.demo.quakes.SAMECodes.SAME;
import ed3.demo.util.FeedFetching;
import ed3.demo.util.Misc;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.rometools.fetcher.FeedFetcher;
import org.rometools.fetcher.FetcherException;

public class FetchAndGenerate {

    public static final String CAP_CONSUMER_LOCATION = "http://ed3test.itsc.uah.edu/ed3/events/new.php";
    public static final String FEED_LOCATION = "http://earthquake.usgs.gov/earthquakes/feed/atom/1.0/hour";

    public static void main(String[] args) throws MalformedURLException, IllegalArgumentException, IOException, FetcherException, FeedException {
        FeedFetcher fetcher = FeedFetching.newCachingFeedFetcher();
        SyndFeed feed = fetcher.retrieveFeed(new URL(FEED_LOCATION));
        System.out.println(feed);
        List<SyndEntryImpl> entries = feed.getEntries();
        Client client = ClientFactory.newClient();
        WebTarget target = client.target(CAP_CONSUMER_LOCATION);
        AlertBuilder builder = new AlertBuilder();
        File alertsDir = Misc.ensureDirectoryExists("alerts", "alerts");
        for (SyndEntryImpl entry : entries) {
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
            String link = alert.getInfo().getWeb();
            int lastIndexOf = link.lastIndexOf('/');
            String substring = link.substring(lastIndexOf + 1);
            File alertFile = new File(alertsDir, substring + ".xml");
            System.out.println(alertFile);
            javax.xml.bind.JAXB.marshal(alert, alertFile);
            Builder request = target.request();
            Entity<Alert> entity = Entity.entity(alert, APPLICATION_CAP_XML);
            Response response = request.post(entity);
            System.out.println(response.getStatus());
            System.out.println(response.getStatusInfo());
            System.out.println(response.readEntity(String.class));
            response.close();
        }
    }
}
