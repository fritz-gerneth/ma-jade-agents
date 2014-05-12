package de.effms.agent.userdistanceanalysis.state;

import de.effms.jade.service.publish.Subscribable;
import de.effms.jade.service.publish.Subscription;
import de.effms.jade.service.publish.SubscriptionListener;
import de.effms.jade.service.query.QueryIfCallback;
import de.effms.jade.service.query.QueryRefCallback;
import de.effms.jade.service.query.Queryable;
import de.effms.marsdemo.ontology.usermovement.UserMovementDistanceOntology;
import jade.content.abs.*;
import jade.content.onto.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.effms.marsdemo.ontology.usermovement.UserMovementDistanceVocabulary.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class UserDistanceKnowledgeBase implements Queryable, Subscribable
{
    private final Logger log = LoggerFactory.getLogger(UserDistanceKnowledgeBase.class);

    private HashMap<String, LinkedList<Subscription>> subscriptions = new HashMap<>();

    private AbsConcept headedTo = new AbsConcept(HEADED_TO);

    private AbsConcept headedToCoordinate = new AbsConcept(COORDINATE);

    private AbsConcept headedToDistance = new AbsConcept(DISTANCE);

    private AbsConcept location = new AbsConcept(COORDINATE);

    public UserDistanceKnowledgeBase()
    {
        this.headedToDistance.set(DISTANCE_D, 0);

        this.setUserLocationCoordinate(0, 0);
        this.setUserDestinationCoordinate(0, 0);

        this.headedTo.set(HEADED_TO_POSITION, this.headedToCoordinate);
        this.headedTo.set(HEADED_TO_DISTANCE, this.headedToDistance);
    }

    public AbsConcept getUserDestinationCoordinate()
    {
        return this.headedToCoordinate;
    }

    public void setUserDestinationCoordinate(int x, int y)
    {
        this.headedToCoordinate.set(X, x);
        this.headedToCoordinate.set(Y, y);

        this.informSubscribers(HEADED);
        this.informSubscribers(HEADED_TO_POSITION);
    }

    public AbsConcept getUserLocationCoordinate()
    {
        return this.location;
    }

    public void setUserLocationCoordinate(int x, int y)
    {
        this.location.set(X, x);
        this.location.set(Y, y);

        this.informSubscribers(IS_LOCATED);
    }

    /**
     * An alternate approach would be to subscribe to HEADED_TO_POSITION and IS_LOCATED and trigger updates in the subscription listener.
     * This would decouple things better.
     */
    private void updateDistance()
    {
        if (null == this.headedToCoordinate || null == this.location) {
            return;
        }

        int xDistance = this.headedToCoordinate.getInteger(X)
            - this.location.getInteger(X);
        int yDistance = this.headedToCoordinate.getInteger(Y)
            - this.location.getInteger(Y);
        int distance = (int) Math.round(Math.sqrt(Math.pow(xDistance,2) + Math.pow(yDistance, 2)));

        if (this.headedToDistance.getInteger(DISTANCE_D) != distance) {
            this.headedToDistance.set(DISTANCE_D, distance);
            this.informSubscribers(IS_LOCATED);
        }
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

    private AbsConcept answer(AbsIRE query)
    {
        String propositionTypeName = query.getProposition().getTypeName();

        /**
         * The same as for the UserKnowledgeBase of the UserInteractionAgent for most of it.
         *
         * In this knowledge base the HEADED_TO concept has an additional slot: HEADED_TO_DISTANCE.
         * Therefore when not requesting the complete HEADED_TO concept we have to search the variable
         */
        if (propositionTypeName.equals(HEADED)) {
            AbsPredicate headed = query.getProposition();
            AbsObject what = headed.getAbsObject(IS_WHAT);
            if (what instanceof AbsVariable) {
                return this.headedTo;
            } else if (what instanceof AbsConcept) {
                AbsConcept headedTo = (AbsConcept) what;
                AbsObject headedToPos = headedTo.getAbsObject(HEADED_TO_POSITION);
                AbsObject headedToDistance = headedTo.getAbsObject(HEADED_TO_DISTANCE);

                if (headedToPos instanceof AbsVariable) {
                    return this.headedToCoordinate;
                } else if (headedToDistance instanceof AbsVariable) {
                    return this.headedToDistance;
                }
            }
        }
        /**
         * The same as for the UserKnowledgeBase of the UserInteractionAgent
         */
        else if (propositionTypeName.equals(IS_LOCATED)) {
            AbsPredicate isLocated = query.getProposition();
            AbsObject isWhat = isLocated.getAbsObject(IS_WHAT);
            if (isWhat instanceof AbsVariable) {
                return this.location;
            }
            // as IS_WHO is the only other slot, its either this (not supported) or no variable at all (not supported)
        }

        return null;
    }

    @Override
    public Subscription subscribe(AbsIRE query)
    {
        Subscription subscription = new Subscription(query);
        if (!this.subscriptions.containsKey(HEADED_TO_POSITION)) {
            this.subscriptions.put(HEADED_TO_POSITION, new LinkedList<Subscription>());
        }

        this.subscriptions.get(HEADED_TO_POSITION).add(subscription);

        SubscriptionListener callback = subscription.getCallback();
        if (null != callback) {
            callback.onInform(query, this.answer(query));
        }

        return subscription;
    }

    private void informSubscribers(String predicate)
    {
        List<Subscription> subscriptionList = this.subscriptions.get(predicate);
        if (null == subscriptionList) {
            log.debug("No subscribers for predicate " + predicate);
            return;
        }

        log.debug("Informing " + subscriptionList.size() + " subscribers about change of " + predicate);
        for (Subscription s: subscriptionList) {
            SubscriptionListener callback = s.getCallback();
            if (null != callback) {
                log.debug("Informing subscriber with query " + s.getQuery());
                callback.onInform(s.getQuery(), this.answer(s.getQuery()));
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
        return UserMovementDistanceOntology.getInstance();
    }
}
