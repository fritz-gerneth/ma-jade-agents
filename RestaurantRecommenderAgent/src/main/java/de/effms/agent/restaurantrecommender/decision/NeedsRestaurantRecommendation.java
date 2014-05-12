package de.effms.agent.restaurantrecommender.decision;

import de.effms.agent.restaurantrecommender.state.RestaurantRecommendationKnowledgeBase;
import de.effms.agent.restaurantrecommender.state.UserDistanceKnowledgeBase;
import de.effms.jade.service.publish.Subscription;
import de.effms.jade.service.publish.SubscriptionListener;
import de.effms.jade.service.query.QueryRefCallback;
import de.effms.marsdemo.ontology.restaurant.RestaurantOntology;
import de.effms.marsdemo.ontology.usermovement.UserMovementDistanceVocabulary;
import de.effms.marsdemo.ontology.usermovement.UserMovementOntology;
import jade.content.abs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeedsRestaurantRecommendation
{
    final private Logger log = LoggerFactory.getLogger(NeedsRestaurantRecommendation.class);

    private final Subscription subscription;

    private final UserDistanceKnowledgeBase subscribable;

    public NeedsRestaurantRecommendation(final UserDistanceKnowledgeBase localKnowledgeBase, final RestaurantSelectorAdapter itemAdapter, final RestaurantRecommendationKnowledgeBase restaurantRecommendationKnowledgeBase)
    {
        this.subscribable = localKnowledgeBase;

        this.subscription = localKnowledgeBase.subscribe(UserMovementOntology.getQueryForHeadedTo());
        this.subscription.setCallback(new SubscriptionListener()
        {
            @Override
            public void onInform(AbsIRE query, AbsConcept result)
            {
                if (!result.getTypeName().equals(UserMovementDistanceVocabulary.HEADED_TO)) {
                    log.error("Received answer, but not an HEADED_TO: " + result);
                    return;
                }

                AbsConcept positionConcept = (AbsConcept) result.getAbsObject(UserMovementDistanceVocabulary.HEADED_TO_POSITION);
                AbsConcept distanceConcept = (AbsConcept) result.getAbsObject(UserMovementDistanceVocabulary.HEADED_TO_DISTANCE);

                int distance = distanceConcept.getInteger(UserMovementDistanceVocabulary.DISTANCE_D);

                /**
                 * If distance is low, we don't need to eat at arrival. Value was chosen arbitrarily by experimentation
                 * to have a decent balance of situation where not to recommend. In a real implementation this decision
                 * probably would be a bit more complicated.
                 */
                if (distance < 40) {
                    log.debug("Distance < 40, no recommendation required");
                    return;
                }
                log.debug("Distance " + distance + ". Requires recommendation for restaurant at destination.");

                /**
                 * Delegate item selection. At this point we actually don't know if were selecting from a local or remote
                 * knowledge base. This detail is hidden in abstraction.
                 */
                itemAdapter.queryRef(RestaurantOntology.getRestaurantsLocatedAt(positionConcept), new QueryRefCallback()
                {
                    @Override
                    public void onQueryRefResult(AbsIRE absIRE, AbsConcept concept)
                    {
                        log.info("Received restaurants to recommend: " + concept);

                        restaurantRecommendationKnowledgeBase.informSubscribers((AbsAggregate) concept);
                    }

                    @Override
                    public void onLogicError(AbsPredicate absPredicate, AbsContentElement absContentElement)
                    {

                    }
                });
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
