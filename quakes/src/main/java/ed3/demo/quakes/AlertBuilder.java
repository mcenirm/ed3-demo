package ed3.demo.quakes;

import com.sun.syndication.feed.module.georss.GeoRSSModule;
import com.sun.syndication.feed.module.georss.geometries.Position;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import java.util.List;

public class AlertBuilder {

    Alert build(SyndEntryImpl entry) {
        Alert alert = new Alert();
        alert.setIdentifier(entry.getUri());
        alert.setSent(entry.getUpdatedDate());
        alert.setInfoWeb(entry.getLink());
        List<SyndLinkImpl> links = entry.getLinks();
        for (SyndLinkImpl link : links) {
            System.out.println("--- SyndLinkImpl ---");
            System.out.println(link);
        }
        alert.setInfoHeadline(entry.getTitle());
        alert.setInfoDescription(entry.getDescription().getValue());
        alert.setPublishedDate(entry.getPublishedDate());
        GeoRSSModule georss = (GeoRSSModule) entry.getModule(GeoRSSModule.GEORSS_GEORSS_URI);
        Position position = georss.getPosition();
        double latitude = position.getLatitude();
        double longitude = position.getLongitude();
        alert.setInfoAreaPoint(latitude, longitude);
        return alert;
    }
}
/*
 SyndEntryImpl.description.type=html

 SyndEntryImpl.categories[0].name=Past Hour
 SyndEntryImpl.categories[1].name=1.5

 SyndEntryImpl.modules[0].date=Sun Jan 27 18:58:15 CST 2013
 SyndEntryImpl.modules[0].dates[0]=Sun Jan 27 18:58:15 CST 2013
 SyndEntryImpl.modules[0].uri=http://purl.org/dc/elements/1.1/

 SyndEntryImpl.modules[1].position=com.sun.syndication.feed.module.georss.geometries.Position@365ce20
 SyndEntryImpl.modules[1].interface=class com.sun.syndication.feed.module.georss.GeoRSSModule
 SyndEntryImpl.modules[1].uri=http://www.georss.org/georss
 SyndEntryImpl.modules[1].geometry=com.sun.syndication.feed.module.georss.geometries.Point@10bf989e
 */
