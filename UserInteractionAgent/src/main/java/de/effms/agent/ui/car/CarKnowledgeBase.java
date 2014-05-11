package de.effms.agent.ui.car;

import de.effms.jade.service.publish.Subscribable;
import de.effms.jade.service.publish.Subscription;
import de.effms.jade.service.publish.SubscriptionListener;
import de.effms.jade.service.query.QueryIfCallback;
import de.effms.jade.service.query.QueryRefCallback;
import de.effms.jade.service.query.Queryable;
import de.effms.marsdemo.ontology.car.CarInformationOntology;
import de.effms.marsdemo.ontology.car.CarInformationVocabulary;
import de.effms.marsdemo.ontology.coordinate.CoordinateVocabulary;
import jade.content.abs.*;
import jade.content.lang.sl.SLVocabulary;
import jade.content.onto.Ontology;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CarKnowledgeBase implements Queryable, Subscribable
{
    private CarInformationOntology ontology = CarInformationOntology.getInstance();

    private HashMap<String, LinkedList<Subscription>> subscriptions = new HashMap<>();

    private String carId = "demoUserCar";

    private int fuelRemaining;

    private int fuelUsage;

    private int speed;

    private int xPosition;

    private int yPosition;

    public String getCarId()
    {
        return carId;
    }

    public void setCarId(String carId)
    {
        this.carId = carId;
    }

    public int getFuelRemaining()
    {
        return fuelRemaining;
    }

    public void setFuelRemaining(int fuelRemaining)
    {
        this.fuelRemaining = fuelRemaining;
        this.informSubscribers(CarInformationVocabulary.FUEL_REMAINING);
    }

    public int getFuelUsage()
    {
        return fuelUsage;
    }

    public void setFuelUsage(int fuelUsage)
    {
        this.fuelUsage = fuelUsage;
        this.informSubscribers(CarInformationVocabulary.FUEL_USAGE);
    }

    public int getSpeed()
    {
        return speed;
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
        this.informSubscribers(CarInformationVocabulary.SPEED);
    }

    public int getxPosition()
    {
        return xPosition;
    }

    public void setxPosition(int xPosition)
    {
        this.xPosition = xPosition;
    }

    public int getyPosition()
    {
        return yPosition;
    }

    public void setyPosition(int yPosition)
    {
        this.yPosition = yPosition;
    }

    public void setPosition(int x, int y)
    {
        this.xPosition = x;
        this.yPosition = y;

        this.informSubscribers(CarInformationVocabulary.POSITION);
    }

    @Override
    public void queryIf(AbsPredicate query, QueryIfCallback callback)
    {
        callback.onLogicError(query, null);
    }

    @Override
    public void queryRef(AbsIRE query, QueryRefCallback callback)
    {
        callback.onQueryRefResult(query, this.answer(query));
    }

    private AbsPredicate answer(AbsIRE query)
    {
        String propositionTypeName = query.getProposition().getTypeName();
        AbsPredicate answer = new AbsPredicate(SLVocabulary.EQUALS);
        answer.set(SLVocabulary.EQUALS_LEFT, query);

        switch (propositionTypeName) {
            case CarInformationVocabulary.FUEL_REMAINING:
                // should check HAS_WHO predicate to be our car
                answer.set(SLVocabulary.EQUALS_RIGHT, fuelRemaining);
                break;
            case CarInformationVocabulary.FUEL_USAGE:
                answer.set(SLVocabulary.EQUALS_RIGHT, fuelUsage);
                break;
            case CarInformationVocabulary.SPEED:
                answer.set(SLVocabulary.EQUALS_RIGHT, speed);
                break;
            case CarInformationVocabulary.POSITION:
                answer.set(SLVocabulary.EQUALS_RIGHT, builtCoordinateConcept());
                break;
        }

        return answer;
    }

    private AbsConcept builtCoordinateConcept()
    {
        AbsConcept c = new AbsConcept(CoordinateVocabulary.COORDINATE);
        c.set(CoordinateVocabulary.X, xPosition);
        c.set(CoordinateVocabulary.Y, yPosition);

        return c;
    }

    @Override
    public Subscription subscribe(AbsIRE query)
    {
        Subscription subscription = new Subscription(query);
        String propositionTypeName = query.getProposition().getTypeName();

        if (!this.subscriptions.containsKey(propositionTypeName)) {
            this.subscriptions.put(propositionTypeName, new LinkedList<Subscription>());
        }

        this.subscriptions.get(propositionTypeName).add(subscription);

        SubscriptionListener callback = subscription.getCallback();
        if (null != callback) {
            callback.onInform(this.answer(query));
        }

        return subscription;
    }

    private void informSubscribers(String predicate)
    {
        List<Subscription> subscriptionList = this.subscriptions.get(predicate);

        if (null == subscriptionList) {
            return;
        }

        for (Subscription s: subscriptionList) {
            SubscriptionListener callback = s.getCallback();
            if (null != callback) {
                callback.onInform(this.answer(s.getQuery()));
            }
        }
    }

    @Override
    public void cancel(Subscription subscription)
    {

    }

    @Override
    public Ontology getOntology()
    {
        return ontology;
    }
}
