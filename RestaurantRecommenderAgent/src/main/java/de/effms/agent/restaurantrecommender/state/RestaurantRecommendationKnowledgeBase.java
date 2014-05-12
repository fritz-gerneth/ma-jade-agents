package de.effms.agent.restaurantrecommender.state;

import de.effms.jade.service.publish.Subscribable;
import de.effms.jade.service.publish.Subscription;
import de.effms.jade.service.publish.SubscriptionListener;
import de.effms.marsdemo.ontology.restaurant.RestaurantRecommenderOntology;
import jade.content.abs.*;
import jade.content.onto.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Implicitly imports the RecommenderSystemVocabulary
import static de.effms.marsdemo.ontology.restaurant.RestaurantRecommenderVocabulary.*;

import java.util.LinkedList;
import java.util.UUID;

/**
 * This Knowledge Base is probably the most simple one. It pushes new recommendations to subscribed devices.
 *
 * To simplify things even further we actually ignore the query the device subscribes to. We have seen enough query-evaulation
 * in the other agents and the queries involved here are actually quite complex.
 */
public class RestaurantRecommendationKnowledgeBase implements Subscribable
{
    private final Logger log = LoggerFactory.getLogger(RestaurantRecommendationKnowledgeBase.class);

    private LinkedList<Subscription> subscriptions = new LinkedList<>();

    public RestaurantRecommendationKnowledgeBase()
    {
        log.info("RestaurantRecommendationKnowledgeBase started");
    }

    @Override
    public Subscription subscribe(AbsIRE query)
    {
        Subscription subscription = new Subscription(query);

        /**
         * This actually would match every where for the concept someone does. In our case this are only recommendations
         * this agent does:
         * (iota :Variable (Variable :Name x) :Proposition (rel_does :rel_does_who (rs_agent :rel_identity_uid ID) :rel_does_what (rs_recommendation :rs_recommendation_item (Variable :Name x))))
         */
        String propositionTypeName = query.getProposition().getTypeName();
        if (propositionTypeName.equals(DOES)) {
            AbsPredicate isLocated = query.getProposition();
            AbsObject doesWhat = isLocated.getAbsObject(DOES_WHAT);
            AbsObject recommendationItem = doesWhat.getAbsObject(RECOMMENDATION_ITEM);
            if (recommendationItem instanceof AbsVariable) {
                this.subscriptions.add(subscription);
                return subscription;
            }
        }

        log.error("Could not handle subscription " + query);
        return null;
    }

    public void informSubscribers(AbsAggregate restaurants)
    {
        /**
         * The only question we answer is the one for all recommendations:
         * ((all ?x (does (agent :rel_identity_uid ID) ?x)))
         *
         * So our answer is a RECOMMENDATION with the restaurants to recommend in the RECOMMENDATION_ITEM slot.
         *
         * Optionally we could set the reason for recommendation too.
         */

        AbsConcept eat = new AbsConcept(EAT);
        eat.set(EAT_WHERE, restaurants);

        AbsConcept recommendation = new AbsConcept(RECOMMENDATION);
        recommendation.set(RECOMMENDATION_ITEM, eat);

        log.info("Informing " + subscriptions.size() + " subscribers about change recommendation");
        log.debug("Recommendation is " + recommendation);

        for (Subscription s: subscriptions) {
            SubscriptionListener callback = s.getCallback();
            if (null != callback) {
                log.info("Informing subscriber with query " + s.getQuery());
                callback.onInform(s.getQuery(), recommendation);
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
        return RestaurantRecommenderOntology.getInstance();
    }
}
