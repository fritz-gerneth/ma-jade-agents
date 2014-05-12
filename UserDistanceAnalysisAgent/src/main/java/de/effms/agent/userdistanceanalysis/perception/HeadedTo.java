package de.effms.agent.userdistanceanalysis.perception;

import de.effms.agent.userdistanceanalysis.state.UserDistanceKnowledgeBase;
import de.effms.jade.service.publish.Subscribable;
import de.effms.jade.service.publish.Subscription;
import de.effms.jade.service.publish.SubscriptionListener;
import de.effms.marsdemo.ontology.usermovement.UserMovementDistanceVocabulary;
import de.effms.marsdemo.ontology.usermovement.UserMovementOntology;
import jade.content.abs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeadedTo
{
    final private Logger log = LoggerFactory.getLogger(HeadedTo.class);

    private final Subscription subscription;

    private final Subscribable subscribable;

    public HeadedTo(Subscribable userKnowledgeBase, final UserDistanceKnowledgeBase localKnowledgeBase)
    {
        this.subscribable = userKnowledgeBase;

        this.subscription = userKnowledgeBase.subscribe(UserMovementOntology.getQueryForHeadedToCoordinate());
        this.subscription.setCallback(new SubscriptionListener()
        {
            @Override
            public void onInform(AbsIRE query, AbsConcept result)
            {
                if (!result.getTypeName().equals(UserMovementDistanceVocabulary.COORDINATE)) {
                    log.error("Received answer, but not an coordinate: " + result);
                    return;
                }

                log.debug("Setting new destination to " + result);
                localKnowledgeBase.setUserDestinationCoordinate(
                    result.getInteger(UserMovementDistanceVocabulary.X),
                    result.getInteger(UserMovementDistanceVocabulary.Y)
                );
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
