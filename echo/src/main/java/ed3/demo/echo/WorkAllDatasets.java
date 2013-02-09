package ed3.demo.echo;

import com.sun.syndication.io.FeedException;
import ed3.demo.util.Misc;
import java.io.IOException;
import java.net.MalformedURLException;
import org.rometools.fetcher.FetcherException;

public class WorkAllDatasets {

  public static void main(String[] args) throws MalformedURLException, IllegalArgumentException, IOException, FeedException, FetcherException {
    Misc.switchToGMT();
    WorkAllDatasets rtw = new WorkAllDatasets();
    rtw.config = new WorkConfiguration();
    rtw.datasets = Datasets.loadDatasets();
    rtw.work();
  }
  public WorkConfiguration config;
  public Datasets datasets;

  public void work() throws MalformedURLException, IllegalArgumentException, IOException, FeedException, FetcherException {
    for (Dataset dataset : datasets.datasetList) {
      WorkOneDataset w1ds = new WorkOneDataset();
      w1ds.config = config;
      w1ds.dataset = dataset;
      w1ds.work();
    }
  }
}
