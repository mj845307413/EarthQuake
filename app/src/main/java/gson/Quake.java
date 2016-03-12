package gson;

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;

import android.location.Location;

public class Quake implements Serializable {
    private Date date;
    private String details;
    private Location location;
    private double magnitude;
    private String link;

    public Quake() {
    }
//
//    public static Quake getInstance_quake() {
//        return instance_quake;
//    }
//
//    private static Quake instance_quake = new Quake();


    public Date getDate() {
        return date;
    }

    public String getDetails() {
        return details;
    }

    public Location getLocation() {
        return location;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getLink() {
        return link;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public Quake(Date _d, String _det, Location _loc, double _mag, String _link) {
        date = _d;
        details = _det;
        location = _loc;
        magnitude = _mag;
        link = _link;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dateString = sdf.format(date);
        return dateString + ": " + magnitude + " " + details;
    }

}