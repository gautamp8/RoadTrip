package makeitcount.roadtrip.Activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by root on 3/4/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Driver implements Serializable {
    private String name;
    private float speed;
    private String location;
    private String videoURL;
    private String id;
    private Integer upvotes;

    public Driver(){
    }

    public Driver(String id, String name, String location , float speed, String videoURL, Integer upvotes){
        this.id = id;
        this.name = name;
        this.location = location;
        this.speed = speed;
        this.videoURL = videoURL;
        this.upvotes = upvotes;
    }


    public String getName(){
        return name;
    }
    public String getLocation(){
        return location;
    }
    public Float getSpeed(){
        return speed;
    }
    public String getVideoURL(){
        return videoURL;
    }
    public Integer getUpvotes(){
        return upvotes;
    }
    public String getId(){
        return id;
    }
}

