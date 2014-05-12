package de.effms.agent.userdevice;

import de.effms.agent.userdevice.perception.PerceptionManager;
import de.effms.jade.agent.AbstractAgent;
import de.effms.marsdemo.ontology.restaurant.RestaurantRecommenderOntology;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDeviceAgent extends AbstractAgent
{
    private final Logger log = LoggerFactory.getLogger(UserDeviceAgent.class);

    public UserDeviceAgent()
    {
        /**
         * First, set up all ontologies the agent is able to speak remotely. We have to do this manually for now as we
         * don't know which components are indented to be used remotely.
         */
        final Codec lang = new SLCodec();
        this.getContentManager().registerLanguage(lang);
        this.getContentManager().registerOntology(RestaurantRecommenderOntology.getInstance()); // Ontology of recommendations we receive

        /**
         * We only perceive recommendations and show them to the user.
         *
         * The more proper way would be to store the recommendations in the internal state and have a decision making behavior
         * to decide to present the recommendation to the user.
         * This principle was already shown in the restaurantRecommenderAgent, no need to repeat the extra work here.
         */
        this.registerLifecycleSubscriber(new PerceptionManager(this));
    }

    @Override
    protected Logger log()
    {
        return log;
    }
}
