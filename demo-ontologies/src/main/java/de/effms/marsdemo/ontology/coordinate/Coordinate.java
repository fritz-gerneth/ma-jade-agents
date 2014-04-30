package de.effms.marsdemo.ontology.coordinate;

import jade.content.Concept;

public class Coordinate implements Concept
{
    private float longitude;

    private float latitude;

    public Coordinate()
    {

    }

    public Coordinate(float longitude, float latitude)
    {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public float getLongitude()
    {
        return longitude;
    }

    public void setLongitude(float longitude)
    {
        this.longitude = longitude;
    }

    public float getLatitude()
    {
        return latitude;
    }

    public void setLatitude(float latitude)
    {
        this.latitude = latitude;
    }
}
