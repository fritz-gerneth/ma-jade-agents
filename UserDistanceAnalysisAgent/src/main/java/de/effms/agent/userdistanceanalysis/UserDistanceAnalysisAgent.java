package de.effms.agent.userdistanceanalysis;

import de.effms.agent.userdistanceanalysis.next.UpdateDistance;
import de.effms.agent.userdistanceanalysis.perception.PerceptionManager;
import de.effms.agent.userdistanceanalysis.state.UserDistanceKnowledgeBase;
import de.effms.jade.agent.AbstractAgent;
import de.effms.jade.service.publish.RemoteSubscriptionService;
import de.effms.jade.service.query.RemoteQueryService;
import de.effms.marsdemo.ontology.usermovement.UserMovementDistanceOntology;
import de.effms.marsdemo.ontology.usermovement.UserMovementOntology;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDistanceAnalysisAgent extends AbstractAgent
{
    private final Logger log = LoggerFactory.getLogger(UserDistanceAnalysisAgent.class);

    public UserDistanceAnalysisAgent()
    {
        /**
         * First, set up all ontologies the agent is able to speak remotely. We have to do this manually for now as we
         * don't know which components are indented to be used remotely.
         */
        final Codec lang = new SLCodec();
        this.getContentManager().registerLanguage(lang);
        this.getContentManager().registerOntology(UserMovementOntology.getInstance());  // Ontology of incoming data
        this.getContentManager().registerOntology(UserMovementDistanceOntology.getInstance()); // Ontology of outgoing data

        /**
         * Setup our local knowledge base. This implementation is both Queryable and Subscribable.
         */
        UserDistanceKnowledgeBase localKnowledgeBase = new UserDistanceKnowledgeBase();

        /**
         * Setup of internal knowledge operators and inferring mechanisms. This is what we have described as "next" in
         * the conceptual model of an agent.
         *
         * In our case the agent will automatically infer the distance between the user and its destination.
         */
        new UpdateDistance(localKnowledgeBase);

        /**
         * Setup perception of the agent. Our implementation of perception is dependant on the lifecycle. See the
         * PerceptionManager for more details on this.
         *
         * In our case we want to perceive to user's location and destination.
         */
        this.registerLifecycleSubscriber(new PerceptionManager(this, localKnowledgeBase));

        /**
         * Last, make our local knowledge base available for querying and subscription.
         *
         * Technically, this is a cross-cutting concern: we perceive requests, and decide to answer.
         *
         * Registering the ontology of our knowledge base is optional but allows other agents to search this agent based on it.
         */
        this.agentDescription.addOntologies(localKnowledgeBase.getOntology().getName());
        new RemoteQueryService(this, localKnowledgeBase);
        new RemoteSubscriptionService(this, localKnowledgeBase);
    }

    @Override
    protected Logger log()
    {
        return log;
    }
}
