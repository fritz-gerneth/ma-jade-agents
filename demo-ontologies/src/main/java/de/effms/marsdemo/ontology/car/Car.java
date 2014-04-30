package de.effms.marsdemo.ontology.car;

import de.effms.marsdemo.ontology.coordinate.Coordinate;

public class Car
{
    private String id;

    private boolean driving;

    private int speed;

    private float fuel_usage;

    private float fuel_remaining;

    private Coordinate position;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public boolean isDriving()
    {
        return driving;
    }

    public void setDriving(boolean driving)
    {
        this.driving = driving;
    }

    public int getSpeed()
    {
        return speed;
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
    }

    public float getFuel_usage()
    {
        return fuel_usage;
    }

    public void setFuel_usage(float fuel_usage)
    {
        this.fuel_usage = fuel_usage;
    }

    public float getFuel_remaining()
    {
        return fuel_remaining;
    }

    public void setFuel_remaining(float fuel_remaining)
    {
        this.fuel_remaining = fuel_remaining;
    }

    public Coordinate getPosition()
    {
        return position;
    }

    public void setPosition(Coordinate position)
    {
        this.position = position;
    }
}
