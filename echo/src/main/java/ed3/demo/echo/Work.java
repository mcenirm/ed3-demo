package ed3.demo.echo;

import java.util.Calendar;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Work {

  public final static String CLOSED = "CLOSED";
  public final static String NEW = "NEW";
  public String id;
  public Dataset dataset;
  public double latitude;
  public double longitude;
  public double spatial_buffer;
  public String ts;
  public int temporal_pre;
  public int temporal_post;
  public String status;
  public List<String> urls;

  @Override
  public String toString() {
    String prefix = this.getClass().getSimpleName() + ".";
    StringBuilder sb = new StringBuilder();
    sb.append(prefix).append("id=").append(id).append("\n");
    sb.append(prefix).append("dataset=").append(dataset == null ? "null" : dataset.ed3id).append("\n");
    sb.append(prefix).append("latitude=").append(latitude).append("\n");
    sb.append(prefix).append("longitude=").append(longitude).append("\n");
    sb.append(prefix).append("spatial_buffer=").append(spatial_buffer).append("\n");
    sb.append(prefix).append("ts=").append(ts).append("\n");
    sb.append(prefix).append("temporal_pre=").append(temporal_pre).append("\n");
    sb.append(prefix).append("temporal_post=").append(temporal_post).append("\n");
    sb.append(prefix).append("status=").append(status).append("\n");
    if (urls == null) {
      sb.append(prefix).append("urls=null\n");
    } else {
      int n = 0;
      for (String url : urls) {
        sb.append(prefix).append("url").append(n).append("=").append(url).append("\n");
        n++;
      }
    }
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
