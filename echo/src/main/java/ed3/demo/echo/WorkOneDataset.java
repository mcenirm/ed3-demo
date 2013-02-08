package ed3.demo.echo;

import ed3.demo.util.Misc;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class WorkOneDataset {

    public static void main(String[] args) {
        Misc.switchToGMT();
        WorkOneDataset w1ds = new WorkOneDataset();
        w1ds.config = new WorkConfiguration();
        Datasets datasets = Datasets.loadDatasets();
        String datasetId = "MYD09GQ";
        w1ds.dataset = datasets.findDataset(datasetId);
        if (w1ds.dataset == null) {
            System.out.println("could not find dataset \"" + datasetId + "\"");
            return;
        }
        w1ds.work();
    }
    public WorkConfiguration config;
    public Dataset dataset;

    public void work() {
        Set<String> seenWork = new TreeSet<>();
        Work work;
        while ((work = fetchWork(dataset.ed3id)) != null && !seenWork.contains(work.id)) {
            seenWork.add(work.id);
            System.out.println(dataset.ed3id + " " + work.id);
            List<String> urls = askEcho(work);
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
}
