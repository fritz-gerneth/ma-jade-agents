package de.effms.agent.userdistanceanalysis.perception;

import de.effms.agent.userdistanceanalysis.state.UserDistanceKnowledgeBase;
import de.effms.jade.agent.Agent;
import de.effms.jade.agent.lifecycle.LifecycleSubscriber;
import de.effms.jade.service.publish.RemoteSubscribable;
import de.effms.jade.service.search.CyclicSearchService;
import de.effms.jade.service.search.SearchResultListener;
import de.effms.jade.service.search.SearchService;
import de.effms.marsdemo.ontology.usermovement.UserMovementOntology;
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

    private final UserDistanceKnowledgeBase localKnowledgeBase;

    private final CyclicSearchService searchService;

    private HeadedTo headedToPerception;

    private Location locationPerception;

    public PerceptionManager(Agent localAgent, UserDistanceKnowledgeBase localKnowledgeBase)
    {
        this.localAgent = localAgent;
        this.localKnowledgeBase = localKnowledgeBase;
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
        final DFAgentDescription userMovementAgent = new DFAgentDescription();
        userMovementAgent.setName(new AID("userInteractionAgent", false));

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
                searchService.cancelQuery(this, userMovementAgent);

                /**
                 * As we know the address of our remote agent now, we can start perception. For this agent, perception
                 * means subscription to HEADED_TO and LOCATION of the user.
                 */
                DFAgentDescription userAgent = searchResults[0];
                headedToPerception = new HeadedTo(
                    new RemoteSubscribable(
                        localAgent,
                        userAgent.getName(),
                        UserMovementOntology.getInstance() // Incoming data ontology
                    ),
                    localKnowledgeBase
                );
                locationPerception = new Location(
                    new RemoteSubscribable(
                        localAgent,
                        userAgent.getName(),
                        UserMovementOntology.getInstance() // Incoming data ontology
                    ),
                    localKnowledgeBase
                );
            }
        }, userMovementAgent);
    }

    @Override
    public void onTakeDown()
    {
        /**
         * If the agent is going down, we shut down perception here. As we are subscribed to remote agents, we therefore
         * unsubscribe.
         */
        log.info("Taking down perception components");
        if (null != headedToPerception) {
            headedToPerception.cancel();
        }
        if (null != locationPerception) {
            locationPerception.cancel();
        }
    }
}
