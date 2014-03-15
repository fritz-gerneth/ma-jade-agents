package de.effms.jade.agent.gpsfakedevice;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;

import java.util.LinkedList;
import java.util.Queue;

public class GpsFakeDevice extends Agent
{
    private Gui gui;

    private LinkedList<SubscriptionResponder.Subscription> subscribers = new LinkedList<SubscriptionResponder.Subscription>();

    private Queue<String> coordinateQueue = new LinkedList<String>();

    protected void setup() {
        System.out.println("[FakeGpsDevice] Fake GPS-Device starting");
        this.registerAsService();
        this.startGui();

        MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE),
            MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE)
        );

        this.addBehaviour(new SubscriptionResponder(this, template, new SubscriptionResponder.SubscriptionManager()
        {
            @Override
            public boolean register(SubscriptionResponder.Subscription subscription) throws RefuseException, NotUnderstoodException
            {
                GpsFakeDevice.this.subscribers.add(subscription);
                System.out.println("[FakeGpsDevice] New subscriber: " + subscription.getMessage());
                return true;
            }

            @Override
            public boolean deregister(SubscriptionResponder.Subscription subscription) throws FailureException
            {
                GpsFakeDevice.this.subscribers.remove(subscription);
                System.out.println("[FakeGpsDevice] Removed subscriber: " + subscription.getMessage());
                return true;
            }
        }));

        this.addBehaviour(new CyclicBehaviour() {
            @Override
            public void action()
            {
                while (!GpsFakeDevice.this.coordinateQueue.isEmpty()) {
                    ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                    message.setContent(GpsFakeDevice.this.coordinateQueue.remove());

                    System.out.println("[FakeGpsDevice] Informing about new coordinate: " + message);

                    for (SubscriptionResponder.Subscription subscription : GpsFakeDevice.this.subscribers) {
                        subscription.notify(message);
                    }
                }
            }
        });
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }

        this.gui.dispose();

        System.out.println("[FakeGpsDevice] Fake GPS-Device stopped");
    }

    public void sendNewCoordinates(final String longitude, final String latitude) {
        this.addBehaviour(new OneShotBehaviour() {
            @Override
            public void action()
            {
                System.out.println("[FakeGpsDevice] Queuing new coordinates " + longitude + "/" +  latitude);
                GpsFakeDevice.this.coordinateQueue.add(longitude + "/" + latitude);
            }
        });
    }

    private void registerAsService() {
        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(this.getAID());

        ServiceDescription fakeGpsService = new ServiceDescription();
        fakeGpsService.setType("fake-gps-position");
        fakeGpsService.setName("Fake GPS-Device");
        agentDescription.addServices(fakeGpsService);

        try {
            DFService.register(this, agentDescription);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }
        System.out.println("[FakeGpsDevice] Registered as agent");
    }

    private void startGui() {
        this.gui = new Gui(this);
        this.gui.showGui();
        System.out.println("[FakeGpsDevice] GUI Started");
    }
}
