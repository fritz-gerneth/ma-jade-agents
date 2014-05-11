package de.effms.agent.ui.user;

import de.effms.jade.service.publish.Subscribable;
import de.effms.jade.service.publish.Subscription;
import de.effms.jade.service.publish.SubscriptionListener;
import de.effms.jade.service.query.QueryIfCallback;
import de.effms.jade.service.query.QueryRefCallback;
import de.effms.jade.service.query.Queryable;
import de.effms.marsdemo.ontology.car.CarInformationVocabulary;
import de.effms.marsdemo.ontology.coordinate.CoordinateVocabulary;
import de.effms.marsdemo.ontology.usermovement.UserMovementOntology;
import de.effms.marsdemo.ontology.usermovement.UserMovementVocabulary;
import jade.content.abs.*;
import jade.content.lang.sl.SLVocabulary;
import jade.content.onto.Ontology;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class UserKnowledgeBase implements Queryable, Subscribable
{
    private UserMovementOntology ontology = UserMovementOntology.getInstance();

    private HashMap<String, LinkedList<Subscription>> subscriptions = new HashMap<>();

    private AbsConcept headedTo;

    private AbsConcept headedToCoordinate;

    public UserKnowledgeBase()
    {
        this.headedToCoordinate = new AbsConcept(UserMovementVocabulary.COORDINATE);
        this.setUserDestination(0, 0);

        this.headedTo = new AbsConcept(UserMovementVocabulary.HEADED_TO);
        this.headedTo.set(UserMovementVocabulary.HEADED_TO_POSITION, this.headedToCoordinate);
    }

    public void setUserDestination(int x, int y)
    {
        this.headedToCoordinate.set(UserMovementVocabulary.X, x);
        this.headedToCoordinate.set(UserMovementVocabulary.Y, y);

        this.informSubscribers(UserMovementVocabulary.HEADED);
        this.informSubscribers(UserMovementVocabulary.HEADED_TO_POSITION);
    }

    public AbsConcept getUserDestination()
    {
        return this.headedToCoordinate;
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
        if (propositionTypeName.equals(UserMovementVocabulary.HEADED)) {
            AbsPredicate headed = query.getProposition();
            AbsObject what = headed.getAbsObject(UserMovementVocabulary.IS_WHAT);
            if (what instanceof AbsVariable) {
                answer.set(SLVocabulary.EQUALS_RIGHT, this.headedTo);
            } else if (what instanceof AbsConcept) {
                answer.set(SLVocabulary.EQUALS_RIGHT, this.headedToCoordinate);
            }
        }

        return answer;
    }

    @Override
    public Subscription subscribe(AbsIRE query)
    {
        Subscription subscription = new Subscription(query);
        String propositionTypeName = query.getProposition().getTypeName();

        if (!this.subscriptions.containsKey(UserMovementVocabulary.HEADED_TO_POSITION)) {
            this.subscriptions.put(UserMovementVocabulary.HEADED_TO_POSITION, new LinkedList<Subscription>());
        }

        this.subscriptions.get(UserMovementVocabulary.HEADED_TO_POSITION).add(subscription);

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
