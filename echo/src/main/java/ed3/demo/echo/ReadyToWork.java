package ed3.demo.echo;

import com.sun.syndication.feed.module.georss.geometries.Point;
import com.sun.syndication.feed.module.georss.geometries.Position;
import com.sun.syndication.io.FeedException;
import ed3.demo.util.Misc;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXB;
import org.rometools.fetcher.FetcherException;

public class ReadyToWork {

  public static void main(String[] args) throws IOException, MalformedURLException, IllegalArgumentException, FetcherException, FeedException {
    Misc.switchToGMT();
    Datasets datasets = Datasets.loadDatasets();
    ReadyToWork rtw = new ReadyToWork();
    rtw.myClientId = "mmceniry@itsc.uah.edu";
    rtw.workflowEndpoint = "http://ed3test.itsc.uah.edu:80/ed3/dataservices/doworkflow.php";
    rtw.echoEndpoint = "https://api.echo.nasa.gov/echo-esip/search/granule.atom";
    rtw.datasets = datasets;
    rtw.work();
  }
  public String workflowEndpoint;
  public Datasets datasets;
  public String echoEndpoint;
  public String myClientId;

  public void work() throws IOException, MalformedURLException, IllegalArgumentException, FetcherException, FeedException {
    for (Dataset dataset : datasets.datasetList) {
      workDataset(dataset);
    }
  }

  public void workDataset(String datasetId) throws IOException, MalformedURLException, IllegalArgumentException, FetcherException, FeedException {
    for (Dataset dataset : datasets.datasetList) {
      if (dataset.ed3id.equals(datasetId)) {
        workDataset(dataset);
        return;
      }
    }
    System.out.println("Dataset \"" + datasetId + "\" not found");
  }

  public void workDataset(Dataset dataset) throws IOException, MalformedURLException, IllegalArgumentException, FetcherException, FeedException {
    Set<String> seenWork = new HashSet<>();
    Work work;
    while ((work = fetchWork(dataset.ed3id)) != null && !seenWork.contains(work.id)) {
      seenWork.add(work.id);
      System.out.println(dataset.ed3id + " " + work.id);
      List<String> urls = askEcho(echoEndpoint, myClientId, dataset.echoShortName, dataset.echoVersionId, dataset.echoDataCenter, work);
      break;
    }
  }

  public Work fetchWork(String datasetId) {
    Client client = ClientFactory.newClient();
    WebTarget target = client.target(workflowEndpoint);
    Builder request = target.queryParam("ds", datasetId).request();
    Response response = request.get();
    String entity = response.readEntity(String.class);
    response.close();
    if (response.getStatus() != 200) {
      System.out.println(target.getUri());
      System.out.println(String.format("%3d %s", response.getStatus(), response.getStatusInfo()));
      System.out.println(entity);
      System.out.println("-----------------------");
    }
    Work work = JAXB.unmarshal(new StringReader(entity), Work.class);
    return work;
  }

  public List<String> askEcho(final String echoEndpoint, final String myClientId, final String dsShortName, final String dsVersionId, final String dsDataCenter, Work work) throws IOException, MalformedURLException, IllegalArgumentException, FetcherException, FeedException {
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
    List<String> urls = search.search();
    return urls;
  }

  public String updateWork(final String workflowEndpoint, final String datasetId, Work work, List<String> urls) {
    Client client = ClientFactory.newClient();
    WebTarget target = client.target(workflowEndpoint);
    Builder request = target.request(MediaType.TEXT_PLAIN_TYPE);
    Form form = new Form().param("ds", datasetId).param("action", "update").param("id", work.id).param("status", "groovy");
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
}
