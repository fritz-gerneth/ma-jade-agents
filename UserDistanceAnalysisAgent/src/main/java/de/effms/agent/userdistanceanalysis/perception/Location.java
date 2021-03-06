package de.effms.agent.userdistanceanalysis.perception;

import de.effms.agent.userdistanceanalysis.state.UserDistanceKnowledgeBase;
import de.effms.jade.service.publish.Subscribable;
import de.effms.jade.service.publish.Subscription;
import de.effms.jade.service.publish.SubscriptionListener;
import de.effms.marsdemo.ontology.usermovement.UserMovementDistanceVocabulary;
import de.effms.marsdemo.ontology.usermovement.UserMovementOntology;
import jade.content.abs.AbsConcept;
import jade.content.abs.AbsIRE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Location
{
    final private Logger log = LoggerFactory.getLogger(Location.class);

    private final Subscription subscription;

    private final Subscribable subscribable;

    public Location(Subscribable userKnowledgeBase, final UserDistanceKnowledgeBase localKnowledgeBase)
    {
        this.subscribable = userKnowledgeBase;

        this.subscription = userKnowledgeBase.subscribe(UserMovementOntology.getQueryForLocationCoordinate());
        this.subscription.setCallback(new SubscriptionListener()
        {
            @Override
            public void onInform(AbsIRE query, AbsConcept result)
            {
                if (!result.getTypeName().equals(UserMovementDistanceVocabulary.COORDINATE)) {
                    log.error("Received answer, but not an coordinate: " + result);
                    return;
                }

                log.debug("Setting new location to " + result);
                localKnowledgeBase.setUserLocationCoordinate(
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
