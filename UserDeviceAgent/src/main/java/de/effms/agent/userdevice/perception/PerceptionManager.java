package de.effms.agent.userdevice.perception;

import de.effms.jade.agent.Agent;
import de.effms.jade.agent.lifecycle.LifecycleSubscriber;
import de.effms.jade.service.publish.RemoteSubscribable;
import de.effms.jade.service.search.CyclicSearchService;
import de.effms.jade.service.search.SearchResultListener;
import de.effms.jade.service.search.SearchService;
import de.effms.marsdemo.ontology.restaurant.RestaurantRecommenderOntology;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The PerceptionManager is responsible for managing the lifecycle of our perception components.
 */
public class PerceptionManager implements LifecycleSubscriber
{
    private final Logger log = LoggerFactory.getLogger(PerceptionManager.class);

    private final Agent localAgent;

    private final CyclicSearchService searchService;

    private Recommendation recommendationPerception;

    public PerceptionManager(Agent localAgent)
    {
        this.localAgent = localAgent;
        this.searchService = new CyclicSearchService(localAgent.asJadeAgent(), 500);
        localAgent.addBehaviour(this.searchService);
    }

    @Override
    public void onSetup()
    {
        /**
         * First step of perception setup is to search for agents providing our input data.
         *
         * In this case with simply search for the agent by name. We could search by provided ontology too.
         */
        final DFAgentDescription recommenderAgent = new DFAgentDescription();
        recommenderAgent.setName(new AID("restaurantRecommenderAgent", false));

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
                searchService.cancelQuery(this, recommenderAgent);

                /**
                 * As we know the address of our remote agent now, we can start perception. For this agent, perception
                 * means subscription to HEADED_TO of the user.
                 */
                DFAgentDescription userAgent = searchResults[0];
                recommendationPerception = new Recommendation(
                    new RemoteSubscribable(
                        localAgent,
                        userAgent.getName(),
                        RestaurantRecommenderOntology.getInstance() // Incoming data ontology
                    )
                );
            }
        }, recommenderAgent);
    }

    @Override
    public void onTakeDown()
    {
        /**
         * If the agent is going down, we shut down perception here. As we are subscribed to remote agents, we therefore
         * unsubscribe.
         */
        log.info("Taking down perception components");
        if (null != recommendationPerception) {
            recommendationPerception.cancel();
        }
    }
}
