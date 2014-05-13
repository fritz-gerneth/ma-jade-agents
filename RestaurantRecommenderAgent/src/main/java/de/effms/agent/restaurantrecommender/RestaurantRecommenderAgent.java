package de.effms.agent.restaurantrecommender;

import de.effms.agent.restaurantrecommender.decision.NeedsRestaurantRecommendation;
import de.effms.agent.restaurantrecommender.decision.RestaurantSelectorAdapter;
import de.effms.agent.restaurantrecommender.perception.PerceptionManager;
import de.effms.agent.restaurantrecommender.state.RestaurantRecommendationKnowledgeBase;
import de.effms.agent.restaurantrecommender.state.UserDistanceKnowledgeBase;
import de.effms.jade.agent.AbstractAgent;
import de.effms.jade.service.publish.RemoteSubscriptionService;
import de.effms.marsdemo.ontology.restaurant.RestaurantOntology;
import de.effms.marsdemo.ontology.restaurant.RestaurantRecommenderOntology;
import de.effms.marsdemo.ontology.usermovement.UserMovementDistanceOntology;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestaurantRecommenderAgent extends AbstractAgent
{
    private final Logger log = LoggerFactory.getLogger(RestaurantRecommenderAgent.class);

    public RestaurantRecommenderAgent()
    {
        /**
         * First, set up all ontologies the agent is able to speak remotely. We have to do this manually for now as we
         * don't know which components are indented to be used remotely.
         */
        final Codec lang = new SLCodec();
        this.getContentManager().registerLanguage(lang);
        this.getContentManager().registerOntology(UserMovementDistanceOntology.getInstance()); // Ontology of incoming data from UserDistanceAnalysisAgent
        this.getContentManager().registerOntology(RestaurantOntology.getInstance()); // Ontology of incoming data RestaurantSelectorAgent
        this.getContentManager().registerOntology(RestaurantRecommenderOntology.getInstance()); // Ontology of outgoing ontology

        /**
         * Setup our local knowledge base. This implementation is both Queryable and Subscribable.
         */
        UserDistanceKnowledgeBase localKnowledgeBase = new UserDistanceKnowledgeBase(); // Representing our knowledge about the user we perceived
        RestaurantRecommendationKnowledgeBase restaurantRecommendationKnowledgeBase = new RestaurantRecommendationKnowledgeBase(); // Our recommendations

        /**
         * This agent does not have any internal state-changing mechanisms.
         */

        /**
         * Setup perception of the agent. Our implementation of perception is dependant on the lifecycle. See the
         * PerceptionManager for more details on this.
         *
         * In our case we want to perceive to user's location and destination.
         */
        this.registerLifecycleSubscriber(new PerceptionManager(this, localKnowledgeBase));

        /**
         * Setup of decision making components.
         *
         * In this agent, we decide to recommend a restaurant.
         */
        RestaurantSelectorAdapter adapter = new RestaurantSelectorAdapter(this);
        this.registerLifecycleSubscriber(adapter);
        new NeedsRestaurantRecommendation(localKnowledgeBase, adapter, restaurantRecommendationKnowledgeBase);

        /**
         * Last, make our recommendations available for subscription
         *
         * Technically, this is a cross-cutting concern: we perceive requests, and decide to answer.
         *
         * Registering the ontology of our knowledge base is optional but allows other agents to search this agent based on it.
         */
         this.agentDescription.addOntologies(RestaurantRecommenderOntology.getInstance().getName());
         new RemoteSubscriptionService(this, restaurantRecommendationKnowledgeBase);
    }

    @Override
    protected Logger log()
    {
        return log;
    }
}
