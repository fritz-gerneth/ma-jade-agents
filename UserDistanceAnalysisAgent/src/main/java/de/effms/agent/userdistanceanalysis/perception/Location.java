package de.effms.agent.userdistanceanalysis.perception;

import de.effms.agent.userdistanceanalysis.state.UserDistanceKnowledgeBase;
import de.effms.jade.service.publish.Subscribable;
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

    public Location(Subscribable userKnowledgeBase, final UserDistanceKnowledgeBase localKnowledgeBase)
    {
        userKnowledgeBase.subscribe(UserMovementOntology.getQueryForLocationCoordinate())
            .setCallback(new SubscriptionListener()
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
}
