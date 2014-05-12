package de.effms.agent.restaurantrecommender.state;

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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static de.effms.marsdemo.ontology.usermovement.UserMovementDistanceVocabulary.*;

public class UserDistanceKnowledgeBase implements Queryable, Subscribable
{
    private final Logger log = LoggerFactory.getLogger(UserDistanceKnowledgeBase.class);

    private HashMap<String, LinkedList<Subscription>> subscriptions = new HashMap<>();

    private AbsConcept headedTo = new AbsConcept(HEADED_TO);

    private AbsConcept headedToCoordinate = new AbsConcept(COORDINATE);

    private AbsConcept headedToDistance = new AbsConcept(DISTANCE);

    public UserDistanceKnowledgeBase()
    {
        // Set default values to prevent NullPointerExceptions
        this.headedToCoordinate.set(X, 0);
        this.headedToCoordinate.set(Y, 0);
        this.headedToDistance.set(DISTANCE_D, 0);

        this.headedTo.set(HEADED_TO_POSITION, this.headedToCoordinate);
        this.headedTo.set(HEADED_TO_DISTANCE, this.headedToDistance);
    }

    public AbsConcept getUserDestinationCoordinate()
    {
        return this.headedToCoordinate;
    }

    public void setUserDestinationCoordinate(int x, int y)
    {
        if (this.headedToCoordinate.getInteger(X) == x && this.headedToCoordinate.getInteger(Y) == y) {
            return;
        }

        this.headedToCoordinate.set(X, x);
        this.headedToCoordinate.set(Y, y);

        this.informSubscribers(HEADED);
    }

    public void setDistance(int distance)
    {
        if (this.headedToDistance.getInteger(DISTANCE_D) == distance) {
            return;
        }

        this.headedToDistance.set(DISTANCE_D, distance);
        this.informSubscribers(HEADED);
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

        return null;
    }

    @Override
    public Subscription subscribe(AbsIRE query)
    {
        Subscription subscription = new Subscription(query);
        // We should actually check where the variable is located and narrow it down the slot containing it, not
        // use the top-level predicate of the query.
        String predicateName = query.getProposition().getTypeName();
        log.info("New Subscriber for predicate " + predicateName + ": " + query);

        if (!this.subscriptions.containsKey(predicateName)) {
            this.subscriptions.put(predicateName, new LinkedList<Subscription>());
        }
        this.subscriptions.get(predicateName).add(subscription);

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
            log.info("No subscribers for predicate " + predicate);
            return;
        }

        log.info("Informing " + subscriptionList.size() + " subscribers about change of " + predicate);

        for (Subscription s: subscriptionList) {
            SubscriptionListener callback = s.getCallback();
            if (null != callback) {
                log.info("Informing subscriber with query " + s.getQuery());
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
