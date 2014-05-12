package de.effms.agent.restaurantrecommender.decision;

import de.effms.jade.agent.Agent;
import de.effms.jade.agent.lifecycle.LifecycleSubscriber;
import de.effms.jade.service.query.QueryIfCallback;
import de.effms.jade.service.query.QueryRefCallback;
import de.effms.jade.service.query.Queryable;
import de.effms.jade.service.query.RemoteQueryable;
import de.effms.jade.service.search.CyclicSearchService;
import de.effms.jade.service.search.SearchResultListener;
import de.effms.jade.service.search.SearchService;
import de.effms.marsdemo.ontology.restaurant.RestaurantOntology;
import jade.content.abs.AbsIRE;
import jade.content.abs.AbsPredicate;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The RestaurantSelectorAdapter searches for the RestaurantSelectorAgent and acts as a proxy to it.
 */
public class RestaurantSelectorAdapter implements LifecycleSubscriber, Queryable
{
    private final Logger log = LoggerFactory.getLogger(RestaurantSelectorAdapter.class);

    private final Agent localAgent;

    private Queryable restaurantSelector;

    private final CyclicSearchService searchService;

    public RestaurantSelectorAdapter(Agent localAgent)
    {
        this.localAgent = localAgent;
        this.searchService = new CyclicSearchService(localAgent.asJadeAgent(), 500);
        localAgent.addBehaviour(this.searchService);
    }

    @Override
    public void onSetup()
    {
        /**
         * Prepare for future queries by already search for the correct agent.
         */
        final DFAgentDescription restaurantSelectorAgent = new DFAgentDescription();
        restaurantSelectorAgent.setName(new AID("restaurantSelectorAgent", false));

        searchService.addSearchResultListener(new SearchResultListener()
        {
            @Override
            public void onSearchResults(DFAgentDescription[] searchResults, SearchService service)
            {
                /**
                 * Cycle as long as we have found the agent.
                 */
                if (searchResults.length == 0) {
                    log.debug("Not found yet. Next cycle.");
                    return;
                }
                searchService.cancelQuery(this, restaurantSelectorAgent);

                /**
                 * We have found the remote agent, now act like a proxy.
                 */
                DFAgentDescription remoteAgent = searchResults[0];
                restaurantSelector = new RemoteQueryable(
                    localAgent,
                    remoteAgent.getName(),
                    RestaurantOntology.getInstance() // Incoming data ontology
                );
            }
        }, restaurantSelectorAgent);
    }

    @Override
    public void onTakeDown()
    {
    }

    @Override
    public void queryIf(AbsPredicate absPredicate, QueryIfCallback queryIfCallback)
    {
        if (null != restaurantSelector) {
            restaurantSelector.queryIf(absPredicate, queryIfCallback);
        } else {
            log.error("RestaurantSelectorAdapter not setup yet");
        }
    }

    @Override
    public void queryRef(AbsIRE absIRE, QueryRefCallback callback)
    {
        if (null != restaurantSelector) {
            restaurantSelector.queryRef(absIRE, callback);
        } else {
            log.error("RestaurantSelectorAdapter not setup yet");
        }
    }

    @Override
    public Ontology getOntology()
    {
        return RestaurantOntology.getInstance();
    }
}
