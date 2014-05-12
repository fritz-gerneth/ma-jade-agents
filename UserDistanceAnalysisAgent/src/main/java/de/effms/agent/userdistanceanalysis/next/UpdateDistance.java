package de.effms.agent.userdistanceanalysis.next;

import de.effms.agent.userdistanceanalysis.state.UserDistanceKnowledgeBase;
import de.effms.jade.service.publish.SubscriptionListener;
import de.effms.marsdemo.ontology.usermovement.UserMovementOntology;
import jade.content.abs.AbsConcept;
import jade.content.abs.AbsIRE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.effms.marsdemo.ontology.usermovement.UserMovementDistanceVocabulary.*;

/**
 * This is an internal state-changing behavior of the agent, solely operating on the internal knowledge: if the user's location or
 * destination changes, we update the distance between the user and its destination.
 */
public class UpdateDistance
{
    final private Logger log = LoggerFactory.getLogger(UpdateDistance.class);

    public UpdateDistance(final UserDistanceKnowledgeBase userDistanceKnowledgeBase)
    {
        log.debug("Starting UpdateDistance state-changing behavior");

        userDistanceKnowledgeBase.subscribe(UserMovementOntology.getQueryForLocationCoordinate())
            .setCallback(new SubscriptionListener()
            {
                @Override
                public void onInform(AbsIRE query, AbsConcept result)
                {
                    if (!result.getTypeName().equals(COORDINATE)) {
                        log.error("Received answer, but not an coordinate: " + result);
                        return;
                    }

                    // Recalculate distance based on new knowledge, partially using old one too
                    int locationX = result.getInteger(X);
                    int locationY = result.getInteger(Y);
                    int destinationX = userDistanceKnowledgeBase.getUserDestinationCoordinate().getInteger(X);
                    int destinationY =  userDistanceKnowledgeBase.getUserDestinationCoordinate().getInteger(Y);
                    int xDistance = destinationX - locationX;
                    int yDistance = destinationY - locationY;
                    int distance = (int) Math.round(Math.sqrt(Math.pow(xDistance,2) + Math.pow(yDistance, 2)));

                    log.debug("Location changed, inferred new distance " + distance);

                    userDistanceKnowledgeBase.setDistance(distance);
                }

                @Override
                public void onCancel()
                {

                }
            });

        userDistanceKnowledgeBase.subscribe(UserMovementOntology.getQueryForHeadedToCoordinate())
            .setCallback(new SubscriptionListener()
            {
                @Override
                public void onInform(AbsIRE query, AbsConcept result)
                {
                    if (!result.getTypeName().equals(COORDINATE)) {
                        log.error("Received answer, but not an coordinate: " + result);
                        return;
                    }

                    // Recalculate distance based on new knowledge, partially using old one too
                    int locationX = userDistanceKnowledgeBase.getUserLocationCoordinate().getInteger(X);
                    int locationY = userDistanceKnowledgeBase.getUserLocationCoordinate().getInteger(Y);
                    int destinationX = result.getInteger(X);
                    int destinationY =  result.getInteger(Y);
                    int xDistance = destinationX - locationX;
                    int yDistance = destinationY - locationY;
                    int distance = (int) Math.round(Math.sqrt(Math.pow(xDistance,2) + Math.pow(yDistance, 2)));

                    log.debug("Destination changed, inferred new distance " + distance);

                    userDistanceKnowledgeBase.setDistance(distance);
                }

                @Override
                public void onCancel()
                {

                }
            });
    }
}
