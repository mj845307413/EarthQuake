package gson;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ws02 on 2016/1/7.
 */
public class Status {
    public String type;
    public MetaData metadata;
    public List<Features> features;
    public float[] bbox;

    public MetaData getMetaData() {
        return metadata;
    }

    public void setMetaData(MetaData metadata) {
        this.metadata = metadata;
    }

    public List<Features> getFeatures() {
        return features;
    }

    public void setFeatures(List<Features> features) {
        this.features = features;
    }

    public float[] getBbox() {
        return bbox;
    }

    public void setBbox(float[] bbox) {
        this.bbox = bbox;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "gson.Status{" +
                "type='" + type + '\'' +
                ", metaData=" + metadata +
                ", features=" + features +
                ", bbox=" + Arrays.toString(bbox) +
                '}';
    }
}
