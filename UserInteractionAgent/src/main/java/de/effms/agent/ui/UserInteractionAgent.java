package de.effms.agent.ui;

import de.effms.agent.ui.user.UserKnowledgeBase;
import de.effms.jade.agent.AbstractAgent;
import de.effms.jade.service.publish.RemoteSubscriptionService;
import de.effms.jade.service.query.RemoteQueryService;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class UserInteractionAgent extends AbstractAgent
{
    private final Logger log = LoggerFactory.getLogger(UserInteractionAgent.class);

    public UserInteractionAgent()
    {
        // Set up the internal knowledge base of the agent
        final UserKnowledgeBase userKnowledgeBase = new UserKnowledgeBase();

        // Register the ontologies the agent is able to speak
        final Codec lang = new SLCodec();
        this.getContentManager().registerLanguage(lang);
        this.getContentManager().registerOntology(userKnowledgeBase.getOntology());

        this.addBehaviour(new OneShotBehaviour()
        {
            @Override
            public void action()
            {
                new RemoteQueryService(UserInteractionAgent.this, userKnowledgeBase);
                new RemoteSubscriptionService(UserInteractionAgent.this, userKnowledgeBase);
            }
        });

        /**
        AbsVariable x = new AbsVariable("x", UserMovementOntology.COORDINATE);

        AbsConcept user = new AbsConcept(UserMovementVocabulary.USER);
        user.set(UserMovementVocabulary.IDENTITY_UID, "demoUserCar");

        AbsConcept headedTo = new AbsConcept(UserMovementVocabulary.HEADED_TO);
        headedTo.set(UserMovementVocabulary.HEADED_TO_POSITION, x);

        AbsPredicate carProperties = new AbsPredicate(UserMovementVocabulary.HEADED);
        carProperties.set(UserMovementOntology.IS_WHO, user);
        carProperties.set(UserMovementOntology.IS_WHAT, x);

        AbsIRE absIota = new AbsIRE(SLVocabulary.ALL);
        absIota.setVariable(x);
        absIota.setProposition(carProperties);

        userKnowledgeBase.subscribe(absIota)
            .setCallback(new SubscriptionListener()
            {
                @Override
                public void onInform(AbsPredicate result)
                {
                    ACLMessage m = new ACLMessage(ACLMessage.QUERY_REF);
                    m.setLanguage(lang.getName());
                    m.setOntology(ontUser.getName());
                    try {
                        getContentManager().fillContent(m, result);
                        System.out.println(m.toString());
                    } catch (Codec.CodecException | OntologyException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancel()
                {

                }
            });

        ACLMessage m = new ACLMessage(ACLMessage.QUERY_REF);
        m.setLanguage(lang.getName());
        m.setOntology(ontUser.getName());
        try {
            this.getContentManager().fillContent(m, absIota);
            System.out.println(m.toString());
        } catch (Codec.CodecException | OntologyException e) {
            e.printStackTrace();
        }

        userKnowledgeBase.setUserDestination(5, 10); */

        // Add some random updates to create subscription updates
        this.addBehaviour(new TickerBehaviour(this, 50000)
        {
            @Override
            public void onTick()
            {
                userKnowledgeBase.setUserDestination(randInt(0, 100), randInt(0, 100));

                log.debug("New destination for user: " + userKnowledgeBase.getUserDestination().toString());
            }
        });
        this.addBehaviour(new TickerBehaviour(this, 5000)
        {
            @Override
            public void onTick()
            {
                userKnowledgeBase.setUserLocation(randInt(0, 100), randInt(0, 100));

                log.debug("New location for user: " + userKnowledgeBase.getUserLocation().toString());
            }
        });
    }

    public int randInt(int min, int max)
    {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    @Override
    protected Logger log()
    {
        return this.log;
    }
}
