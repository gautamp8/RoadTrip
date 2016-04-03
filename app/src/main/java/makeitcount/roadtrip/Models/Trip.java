package makeitcount.roadtrip.Models;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by root on 3/4/16.
 */
public class Trip {
    private String crashDetail;
    private float damageRating;
    private Timestamp dateTime;
    private Integer injured;
    private Integer killed;
    private Location location;

    public Trip(){
    }

    public Trip(String crashDetail, float damageRating, Timestamp dateTime , Integer killed, Integer injured, Location location){
        this.crashDetail = crashDetail;
        this.damageRating = damageRating;
        this.location = location;
        this.dateTime = dateTime;
        this.injured = injured;
        this.killed = killed;
    }


    public String getCrashDetail(){
        return crashDetail;
    }
    public Float getDamageRating(){
        return damageRating;
    }
    public Location getLocation(){
        return location;
    }
    public Timestamp getDateTime(){
        return dateTime;
    }
    public Integer getKilled(){
        return killed;
    }
    public Integer getInjured(){
        return injured;
    }

    public class Location{
        float longitude;
        float latitude;

        public Location(float longitude, float latitude){
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public Float getLongitude(){
            return longitude;
        }

        public Float getLatitude(){
            return latitude;
        }
    }
}
