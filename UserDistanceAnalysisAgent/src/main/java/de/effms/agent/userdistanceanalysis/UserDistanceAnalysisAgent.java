package de.effms.agent.userdistanceanalysis;

import de.effms.agent.userdistanceanalysis.perception.HeadedTo;
import de.effms.agent.userdistanceanalysis.perception.Location;
import de.effms.agent.userdistanceanalysis.state.UserDistanceKnowledgeBase;
import de.effms.jade.agent.AbstractAgent;
import de.effms.jade.agent.lifecycle.LifecycleSubscriber;
import de.effms.jade.service.publish.RemoteSubscribable;
import de.effms.jade.service.search.CyclicSearchService;
import de.effms.jade.service.search.SearchResultListener;
import de.effms.jade.service.search.SearchService;
import de.effms.marsdemo.ontology.usermovement.UserMovementDistanceOntology;
import de.effms.marsdemo.ontology.usermovement.UserMovementOntology;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDistanceAnalysisAgent extends AbstractAgent
{
    private final Logger log = LoggerFactory.getLogger(UserDistanceAnalysisAgent.class);

    private final UserDistanceKnowledgeBase localKnowledgeBase = new UserDistanceKnowledgeBase();

    private CyclicSearchService searchService = new CyclicSearchService(asJadeAgent(), 5000);

    public UserDistanceAnalysisAgent()
    {
        // Register the ontologies the agent is able to speak
        final Codec lang = new SLCodec();
        this.getContentManager().registerLanguage(lang);
        this.getContentManager().registerOntology(UserMovementOntology.getInstance());  // Ontology of incoming data
        this.getContentManager().registerOntology(UserMovementDistanceOntology.getInstance()); // Ontology of outgoing data

        this.registerLifecycleSubscriber(new LifecycleSubscriber()
        {
            @Override
            public void onSetup()
            {
                // We search for this agent by name.
                final DFAgentDescription userMovementAgent = new DFAgentDescription();
                userMovementAgent.setName(new AID("userInteractionAgent", false));

                addBehaviour(searchService);
                searchService.addSearchResultListener(new SearchResultListener()
                {
                    private AID currentUserAgent;

                    @Override
                    public void onSearchResults(DFAgentDescription[] searchResults, SearchService service)
                    {
                        if (searchResults.length == 0) {
                            log.debug("Not found yet. Next cycle.");
                            return;
                        }

                        // We simply look at the first result only
                        if (null == currentUserAgent) {
                            currentUserAgent = searchResults[0].getName();
                            log.info("Got result. Stopping. : " + currentUserAgent);
                        }

                        searchService.cancelQuery(this, userMovementAgent);

                        new HeadedTo(
                            new RemoteSubscribable(
                                UserDistanceAnalysisAgent.this,
                                currentUserAgent,
                                UserMovementOntology.getInstance() // Incoming data ontology
                            ),
                            localKnowledgeBase
                        );
                        new Location(
                            new RemoteSubscribable(
                                UserDistanceAnalysisAgent.this,
                                currentUserAgent,
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

            }
        });
    }

    @Override
    protected Logger log()
    {
        return log;
    }
}
