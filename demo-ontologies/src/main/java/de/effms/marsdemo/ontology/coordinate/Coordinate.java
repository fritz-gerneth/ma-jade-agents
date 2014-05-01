package de.effms.marsdemo.ontology.coordinate;

import jade.content.Concept;

public class Coordinate implements Concept
{
    private int x;

    private int y;

    public Coordinate()
    {

    }

    public Coordinate(int longitude, int latitude)
    {
        this.x = longitude;
        this.y = latitude;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }
}
