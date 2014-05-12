package de.effms.agent.userdevice.perception;

import de.effms.jade.service.publish.Subscribable;
import de.effms.jade.service.publish.Subscription;
import de.effms.jade.service.publish.SubscriptionListener;
import de.effms.marsdemo.ontology.restaurant.RestaurantRecommenderOntology;
import de.effms.marsdemo.ontology.restaurant.RestaurantRecommenderVocabulary;
import de.effms.marsdemo.ontology.usermovement.UserMovementDistanceVocabulary;
import de.effms.marsdemo.ontology.usermovement.UserMovementOntology;
import jade.content.abs.AbsAggregate;
import jade.content.abs.AbsConcept;
import jade.content.abs.AbsIRE;
import jade.util.leap.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Recommendation
{
    final private Logger log = LoggerFactory.getLogger(Recommendation.class);

    private final Subscription subscription;

    private final Subscribable subscribable;

    public Recommendation(Subscribable userKnowledgeBase)
    {
        this.subscribable = userKnowledgeBase;

        this.subscription = userKnowledgeBase.subscribe(RestaurantRecommenderOntology.getRecommendations());
        this.subscription.setCallback(new SubscriptionListener()
        {
            @Override
            public void onInform(AbsIRE query, AbsConcept result)
            {
                /**
                 * So we receive a new recommendation items. Unpack it and print it for the user.
                 */
                if (!result.getTypeName().equals(RestaurantRecommenderVocabulary.RECOMMENDATION)) {
                    log.error("Received answer, but not a RECOMMENDATION: " + result);
                    return;
                }

                log.debug("Received a recommendation: " + result);

                AbsConcept recommendation = (AbsConcept) result.getAbsObject(RestaurantRecommenderVocabulary.RECOMMENDATION_ITEM);
                AbsConcept eatWhere = (AbsConcept) recommendation.getAbsObject(RestaurantRecommenderVocabulary.EAT_WHERE);
                if (eatWhere instanceof AbsAggregate) {
                    AbsAggregate restaurants = (AbsAggregate) eatWhere;
                    Iterator restaurantIterator = restaurants.iterator();

                    System.out.println("You are probably hungry when you arrive. Want some good restaurants at your destination?:");
                    while(restaurantIterator.hasNext()) {
                        Object o = restaurantIterator.next();
                        if (o instanceof AbsConcept && ((AbsConcept) o).getTypeName().equals(RestaurantRecommenderVocabulary.RESTAURANT)) {
                            System.out.println(((AbsConcept) o).getString(RestaurantRecommenderVocabulary.RESTAURANT_NAME));
                        } else {
                            log.error("Unexpected item in aggregate: " + o);
                        }
                    }
                } else {
                    log.error("Expected AbsAggregate of recommendation items");
                }
            }

            @Override
            public void onCancel()
            {
                log.info("Subscription got cancelled.");
            }
        });
    }

    public void cancel()
    {
        this.subscribable.cancel(this.subscription);
    }
}
