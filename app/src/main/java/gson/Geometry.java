package gson;

import java.util.Arrays;

/**
 * Created by ws02 on 2016/1/7.
 */
public class Geometry {
    public String type;
    public float[] coordinates;

    @Override
    public String toString() {
        return "gson.Geometry{" +
                "type='" + type + '\'' +
                ", coordinates=" + Arrays.toString(coordinates) +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(float[] coordinates) {
        this.coordinates = coordinates;
    }
}
