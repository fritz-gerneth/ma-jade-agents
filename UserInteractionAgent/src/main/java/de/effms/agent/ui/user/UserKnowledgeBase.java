package de.effms.agent.ui.user;

import de.effms.jade.service.publish.Subscribable;
import de.effms.jade.service.publish.Subscription;
import de.effms.jade.service.publish.SubscriptionListener;
import de.effms.jade.service.query.QueryIfCallback;
import de.effms.jade.service.query.QueryRefCallback;
import de.effms.jade.service.query.Queryable;
import de.effms.marsdemo.ontology.usermovement.UserMovementOntology;
import jade.content.abs.*;
import jade.content.onto.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.effms.marsdemo.ontology.usermovement.UserMovementDistanceVocabulary.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class UserKnowledgeBase implements Queryable, Subscribable
{
    private final Logger log = LoggerFactory.getLogger(UserKnowledgeBase.class);

    private HashMap<String, LinkedList<Subscription>> subscriptions = new HashMap<>();

    private AbsConcept headedTo;

    private AbsConcept headedToCoordinate;

    private AbsConcept location;

    public UserKnowledgeBase()
    {
        this.headedToCoordinate = new AbsConcept(COORDINATE);
        this.setUserDestination(0, 0);

        this.headedTo = new AbsConcept(HEADED_TO);
        this.headedTo.set(HEADED_TO_POSITION, this.headedToCoordinate);

        this.location = new AbsConcept(COORDINATE);
        this.setUserLocation(0, 0);
    }

    public void setUserDestination(int x, int y)
    {
        this.headedToCoordinate.set(X, x);
        this.headedToCoordinate.set(Y, y);

        this.informSubscribers(HEADED);
        this.informSubscribers(HEADED_TO_POSITION);
    }

    public void setUserLocation(int x, int y)
    {
        this.location.set(X, x);
        this.location.set(Y, y);

        this.informSubscribers(IS_LOCATED);
    }

    public AbsConcept getUserDestination()
    {
        return this.headedToCoordinate;
    }

    public AbsConcept getUserLocation()
    {
        return this.location;
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
         * Answering the HEADED predicate
         *
         * Silently assuming the Variable is in the IS_WHAT slot. If asking for the user that is in HEADED towards,
         * the variable would be in the IS_WHO slot. As we don't do user referencing and validation in this example,
         * we just ignore it for now.
         *
         * Question for HEADED_TO:
         * ((all ?x (um_headed (rs_user :rel_identity_uid demoUserCar) ?x)))
         *
         * Question for HEADED_TO_POSITION:
         * ((all ?x (um_headed (rs_user :rel_identity_uid demoUserCar) (um_headed_to :um_headed_to_pos ?x))))
         *
         * In both cases, the evaluation is based on many implicit assumptions. As we only have one slot in both cases,
         * we don't have to check which one contains the variable and which ones are set as additional constraints.
         * We don't check if all variables are set.
         */
        if (propositionTypeName.equals(HEADED)) {
            AbsPredicate headed = query.getProposition();
            AbsObject what = headed.getAbsObject(IS_WHAT);
            if (what instanceof AbsVariable) {
                return this.headedTo;
            } else if (what instanceof AbsConcept) {
                return this.headedToCoordinate;
            }
        }
        /**
         * Answering the IS_LOCATED predicate
         *
         * Question for HEADED_TO:
         * ((all ?x (is_located (rs_user :rel_identity_uid demoUserCar) ?x)))
         *
         * The same limitations as for HEADED apply
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
        return UserMovementOntology.getInstance();
    }
}
