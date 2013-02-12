package ed3.demo.echo;

import java.util.Calendar;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Work {

  public String id;
  public double latitude;
  public double longitude;
  public double spatial_buffer;
  public String ts;
  public int temporal_pre;
  public int temporal_post;
  public String status;

  @Override
  public String toString() {
    String prefix = this.getClass().getSimpleName() + ".";
    StringBuilder sb = new StringBuilder();
    sb.append(prefix).append("id=").append(id).append("\n");
    sb.append(prefix).append("latitude=").append(latitude).append("\n");
    sb.append(prefix).append("longitude=").append(longitude).append("\n");
    sb.append(prefix).append("spatial_buffer=").append(spatial_buffer).append("\n");
    sb.append(prefix).append("ts=").append(ts).append("\n");
    sb.append(prefix).append("temporal_pre=").append(temporal_pre).append("\n");
    sb.append(prefix).append("temporal_post=").append(temporal_post).append("\n");
    sb.append(prefix).append("status=").append(status).append("\n");
    return sb.toString();
  }

  private Calendar x(int days) {
    final String iso = ts.replaceFirst(" ", "T") + "Z";
    Calendar cal = DatatypeConverter.parseDateTime(iso);
    cal.add(Calendar.DATE, days);
    return cal;
  }

  public Calendar getStartTime() {
    return x(-temporal_pre);
  }

  public Calendar getEndTime() {
    return x(temporal_post);
  }
}
