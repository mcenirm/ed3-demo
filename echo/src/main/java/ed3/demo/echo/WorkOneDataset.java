package ed3.demo.echo;

import com.sun.syndication.io.FeedException;
import ed3.demo.util.Misc;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.Set;
import java.util.TreeSet;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import org.rometools.fetcher.FetcherException;

public class WorkOneDataset {

  public static void main(String[] args) throws MalformedURLException, IllegalArgumentException, IOException, FeedException, FetcherException {
    Misc.switchToGMT();
    WorkOneDataset w1ds = new WorkOneDataset();
    w1ds.config = new WorkConfiguration();
    Datasets datasets = Datasets.loadDatasets();
    String datasetId = args.length > 0 ? args[0] : "ML2T_NRT";
    w1ds.dataset = datasets.findDataset(datasetId);
    if (w1ds.dataset == null) {
      System.out.println("could not find dataset \"" + datasetId + "\"");
      return;
    }
    w1ds.verbose = true;
    w1ds.work();
    w1ds.printStatistics();
  }
  public WorkConfiguration config;
  public Dataset dataset;
  public boolean verbose = false;
  public int countUpdated = 0;
  public int countNotUpdated = 0;
  public int countSkippedClosed = 0;
  public int countSkippedTrouble = 0;
  public int countUnrecognizedStatus = 0;

  public void work() throws MalformedURLException, IllegalArgumentException, IOException, FeedException, FetcherException {
    Set<String> seenWork = new TreeSet<>();
    Work work;
    while ((work = fetchWork()) != null && !seenWork.contains(work.id)) {
      seenWork.add(work.id);
      if (Work.NEW.equalsIgnoreCase(work.status)) {
        workNew(work);
      } else if (Work.CLOSED.equalsIgnoreCase(work.status)) {
        if (verbose) {
          System.out.println("Skipping " + work.status + " work " + work.id);
        }
        countSkippedClosed++;
      } else if (Work.TROUBLE.equalsIgnoreCase(work.status)) {
        if (verbose) {
          System.out.println("Skipping " + work.status + " work " + work.id);
        }
        countSkippedTrouble++;
      } else {
        System.out.println("Unrecognized status " + work.status + " for " + dataset.ed3id + " " + work.id);
        countUnrecognizedStatus++;
      }
    }
  }

  public Work fetchWork() {
    Client client = ClientFactory.newClient();
    WebTarget target = client.target(config.workflowEndpoint);
    Builder request = target.queryParam("ds", dataset.ed3id).request();
    Response response = request.get();
    int status = response.getStatus();
    StatusType statusInfo = response.getStatusInfo();
    String entity = response.readEntity(String.class);
    response.close();
    if (status != 200) {
      System.out.println(target.getUri());
      System.out.println(String.format("%3d %s", status, statusInfo));
      System.out.println(entity);
      System.out.println("-----------------------");
      return null;
    }
    if (entity.contains("<root><0>-1</0></root>")) {
      return null;
    }
    try {
      final Work work = JAXB.unmarshal(new StringReader(entity), Work.class);
      work.dataset = dataset;
      return work;
    } catch (DataBindingException e) {
      System.out.println(e.getLocalizedMessage());
      System.out.println(target.getUri());
      System.out.println(String.format("%3d %s", response.getStatus(), response.getStatusInfo()));
      System.out.println(entity);
      System.out.println("-----------------------");
      return null;
    }
  }

  protected void workNew(Work work) throws IllegalArgumentException, FeedException, FetcherException, IOException {
    try {
      askEcho(work);
    } catch (IllegalArgumentException | IOException | FeedException | FetcherException ex) {
      printStatistics();
      throw ex;
    }
    int numUrls = work.urls == null ? 0 : work.urls.size();
    if (Work.CLOSED.equalsIgnoreCase(work.status) || numUrls > 0) {
      if (verbose) {
        System.out.println("Updating work " + work.id + " " + work.status + " with " + numUrls + " url" + (numUrls == 1 ? "" : "s"));
      }
      updateWork(work);
      countUpdated++;
    } else {
      if (verbose) {
        System.out.println("Not updating work " + work.id + " " + work.status);
      }
      countNotUpdated++;
    }
  }

  public void askEcho(Work work) throws IllegalArgumentException, IOException, FeedException, FetcherException {
    Search search = new Search();
    search.config = config;
    search.setWork(work);
    search.verbose = verbose;
    try {
      search.search();
    } catch (IOException | IllegalArgumentException | FeedException | FetcherException ex) {
      System.out.println("Failed to search ECHO for " + work);
      throw ex;
    }
  }

  public void updateWork(Work work) {
    Client client = ClientFactory.newClient();
    WebTarget target = client.target(config.workflowEndpoint);
    Builder request = target.request(MediaType.TEXT_PLAIN_TYPE);
    Form form = new Form().param("ds", dataset.ed3id).param("action", "update").param("id", work.id).param("status", work.status);
    if (work.urls != null) {
      int n = 0;
      for (String url : work.urls) {
        form.param("url" + n, url);
        n++;
      }
    }
    Entity<Form> entity = Entity.form(form);
    Response response = request.post(entity);
    final int status = response.getStatus();
    final StatusType statusInfo = response.getStatusInfo();
    final String message = response.readEntity(String.class);
    response.close();
    if (status != 200) {
      System.out.println(String.format("%3d %s", status, statusInfo));
      System.out.println(message);
      System.out.println("-----------------------");
    }
  }

  public void printStatistics() {
    System.out.println("Updated " + countUpdated + " " + Work.pluralize(countUpdated));
    System.out.println("Not updated " + countNotUpdated + " " + Work.pluralize(countNotUpdated));
    System.out.println("Skipped closed " + countSkippedClosed + " " + Work.pluralize(countSkippedClosed));
    System.out.println("Skipped trouble " + countSkippedTrouble + " " + Work.pluralize(countSkippedTrouble));
    System.out.println("Unrecognized status " + countUnrecognizedStatus + " " + Work.pluralize(countUnrecognizedStatus));
  }
}
