package de.effms.agent.restaurantrecommender.perception;

import de.effms.agent.restaurantrecommender.state.UserDistanceKnowledgeBase;
import de.effms.jade.service.publish.Subscribable;
import de.effms.jade.service.publish.Subscription;
import de.effms.jade.service.publish.SubscriptionListener;
import de.effms.marsdemo.ontology.usermovement.UserMovementDistanceVocabulary;
import de.effms.marsdemo.ontology.usermovement.UserMovementOntology;
import jade.content.abs.AbsConcept;
import jade.content.abs.AbsIRE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This component perceives where the user is HEADED_TO and saves it to the internal knowledge base. The
 * NeedsRestaurantRecommendation component is subscribed to this and decides on this information.
 */
public class HeadedTo
{
    final private Logger log = LoggerFactory.getLogger(HeadedTo.class);

    private final Subscription subscription;

    private final Subscribable subscribable;

    public HeadedTo(Subscribable userKnowledgeBase, final UserDistanceKnowledgeBase localKnowledgeBase)
    {
        this.subscribable = userKnowledgeBase;

        /**
         * Subscribe to knowledge base publishing the HEADED_TO. In our example this is the UserDistanceAnalysisAgent.
         */
        this.subscription = userKnowledgeBase.subscribe(UserMovementOntology.getQueryForHeadedTo());
        this.subscription.setCallback(new SubscriptionListener()
        {
            @Override
            public void onInform(AbsIRE query, AbsConcept result)
            {
                /**
                 * We received a new HEADED_TO concept. Analyze it and store it in our local knowledge base.
                 */
                if (!result.getTypeName().equals(UserMovementDistanceVocabulary.HEADED_TO)) {
                    log.error("Received answer, but not an HEADED_TO: " + result);
                    return;
                }

                AbsConcept position = (AbsConcept) result.getAbsObject(UserMovementDistanceVocabulary.HEADED_TO_POSITION);
                AbsConcept distance = (AbsConcept) result.getAbsObject(UserMovementDistanceVocabulary.HEADED_TO_DISTANCE);

                log.info("Received new position " + position);
                log.info("Received new distance " + distance);

                localKnowledgeBase.setUserDestinationCoordinate(
                    position.getInteger(UserMovementDistanceVocabulary.X),
                    position.getInteger(UserMovementDistanceVocabulary.Y)
                );
                localKnowledgeBase.setDistance(distance.getInteger(UserMovementDistanceVocabulary.DISTANCE_D));
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
