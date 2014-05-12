package de.effms.agent.restaurantselector;

import de.effms.agent.restaurantselector.state.RestaurantKnowledgeBase;
import de.effms.jade.agent.AbstractAgent;
import de.effms.jade.service.query.RemoteQueryService;
import de.effms.marsdemo.ontology.restaurant.RestaurantOntology;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestaurantSelectorAgent extends AbstractAgent
{
    private final Logger log = LoggerFactory.getLogger(RestaurantSelectorAgent.class);

    public RestaurantSelectorAgent()
    {
        /**
         * First, set up all ontologies the agent is able to speak remotely. We have to do this manually for now as we
         * don't know which components are indented to be used remotely.
         */
        final Codec lang = new SLCodec();
        this.getContentManager().registerLanguage(lang);
        this.getContentManager().registerOntology(RestaurantOntology.getInstance());  // This agent only knows one ontology

        /**
         * Setup our local knowledge base. This implementation is both Queryable and Subscribable.
         */
        RestaurantKnowledgeBase localKnowledgeBase = new RestaurantKnowledgeBase();

        /**
         * This agent does not have any internal state-changing and inference mechanisms.
         */

        /**
         * We don't actively perceive here either due to the random generation of restaurants. Yet, in an real implementation
         * we would add our services that collect restaurant information here.
         */

        /**
         * Make our fake restaurant knowledge base queryable, but not subscribable. See its implementation for details.
         */
        this.agentDescription.addOntologies(localKnowledgeBase.getOntology().getName());
        new RemoteQueryService(this, localKnowledgeBase);
    }

    @Override
    protected Logger log()
    {
        return log;
    }
}
