package ed3.demo.echo;

import java.io.InputStream;
import java.util.List;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Datasets {

    @XmlElement(name = "dataset")
    public List<Dataset> datasetList;

    @Override
    public String toString() {
        return datasetList.toString();
    }

    public static Datasets loadDatasets(String resource) {
        InputStream in = Dataset.class.getResourceAsStream(resource);
        Datasets datasets = JAXB.unmarshal(in, Datasets.class);
        return datasets;

    }

    public static Datasets loadDatasets() {
        return loadDatasets("/datasets.xml");
    }

    public Dataset findDataset(String datasetId) {
        for (Dataset dataset : datasetList) {
            if (dataset.ed3id.equals(datasetId)) {
                return dataset;
            }
        }
        return null;
    }
}
