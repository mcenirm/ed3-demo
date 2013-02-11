package ed3.demo.quakes;

import com.sun.syndication.feed.module.georss.GeoRSSModule;
import com.sun.syndication.feed.module.georss.geometries.AbstractGeometry;
import com.sun.syndication.feed.module.georss.geometries.Point;
import com.sun.syndication.feed.module.georss.geometries.Position;
import com.sun.syndication.feed.synd.SyndEntry;

public class AlertBuilder {

  Alert build(SyndEntry entry) {
    Alert alert = new Alert();
    alert.setIdentifier(entry.getUri());
    alert.setSent(entry.getUpdatedDate());
    alert.setInfoWeb(entry.getLink());
    alert.setInfoHeadline(entry.getTitle());
    alert.setInfoDescription(entry.getDescription().getValue());
    GeoRSSModule georss = (GeoRSSModule) entry.getModule(GeoRSSModule.GEORSS_GEORSS_URI);
    AbstractGeometry geometry = georss.getGeometry();
    if (geometry instanceof Point) {
      Point point = (Point) geometry;
      Position position = point.getPosition();
      double latitude = position.getLatitude();
      double longitude = position.getLongitude();
      alert.setInfoAreaPoint(latitude, longitude);
    } else {
      System.out.println("unexpected geometry (" + geometry.getClass() + ") for entry " + entry.getUri());
    }
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
