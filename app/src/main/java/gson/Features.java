package gson;

/**
 * Created by ws02 on 2016/1/7.
 */
public class Features {
    public String type;
    public MYProperties properties;
    public Geometry geometry;
    public String id;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MYProperties getMyProperties() {
        return properties;
    }

    public void setMyProperties(MYProperties properties) {
        this.properties = properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "gson.Features{" +
                "type='" + type + '\'' +
                ", myProperties=" + properties +
                ", geometry=" + geometry +
                ", id='" + id + '\'' +
                '}';
    }
}
