package ed3.demo.echo;

import java.io.InputStream;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;

@XmlType
public class Dataset {

  public static void main(String[] args) {
    InputStream in = Dataset.class.getResourceAsStream("/datasets.xml");
    Datasets datasets = JAXB.unmarshal(in, Datasets.class);
    for (Dataset dataset : datasets.datasetList) {
      System.out.println(dataset.toSqlInsert());
    }
  }
  @XmlAttribute(name = "ed3id")
  public String ed3id;
  public String title;
  @XmlElement(name = "shortName", namespace = "http://www.echo.nasa.gov/esip")
  public String echoShortName;
  @XmlElement(name = "versionId", namespace = "http://www.echo.nasa.gov/esip")
  public String echoVersionId;
  @XmlElement(name = "dataCenter", namespace = "http://www.echo.nasa.gov/esip")
  public String echoDataCenter;
  @XmlElement
  public Duration latency;

  @Override
  public String toString() {
    return "[" + ed3id + "|" + echoDataCenter + ":" + echoShortName + ":" + echoVersionId + "]";
  }

  public String toSqlInsert() {
    return "INSERT INTO dataset (id, name) VALUES ('" + ed3id + "', '" + title + "');";
  }
}
