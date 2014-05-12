package de.effms.marsdemo.ontology.restaurant;

import de.effms.jade.ontology.RecommenderSystemOntology;
import de.effms.marsdemo.ontology.coordinate.CoordinateOntology;
import de.effms.marsdemo.ontology.usermovement.UserMovementVocabulary;
import jade.content.abs.AbsConcept;
import jade.content.abs.AbsIRE;
import jade.content.abs.AbsPredicate;
import jade.content.abs.AbsVariable;
import jade.content.lang.sl.SLVocabulary;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.ReflectiveIntrospector;
import jade.content.schema.AggregateSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PrimitiveSchema;

import java.util.UUID;

public class RestaurantRecommenderOntology extends Ontology implements RestaurantRecommenderVocabulary
{
    private static RestaurantRecommenderOntology instance = new RestaurantRecommenderOntology();

    public static RestaurantRecommenderOntology getInstance()
    {
        return instance;
    }

    public static RestaurantRecommenderOntology getNewInstance()
    {
        return new RestaurantRecommenderOntology();
    }

    private RestaurantRecommenderOntology()
    {
        super(NAME, new Ontology[] { RestaurantOntology.getInstance(), RecommenderSystemOntology.getInstance(), CoordinateOntology.getInstance()}, new ReflectiveIntrospector());

        try {
            final ConceptSchema eatAction = new ConceptSchema(EAT);
            eatAction.addSuperSchema((ConceptSchema) getSchema(ACTION));
            eatAction.add(EAT_WHERE, new AggregateSchema(AggregateSchema.BASE_NAME));

            this.add(eatAction);
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the AbsIRE to subscribe for all recommendation items
     *
     * ((all ?x (does (Agent) (recommend ?x)
     */
    public static AbsIRE getRecommendations()
    {
        AbsVariable x = new AbsVariable("x", RECOMMENDATION_ITEM);

        AbsConcept agent = new AbsConcept(AGENT);
        agent.set(IDENTITY_UID, UUID.randomUUID().toString());

        AbsConcept recommend = new AbsConcept(RECOMMENDATION);
        recommend.set(RECOMMENDATION_ITEM, x);

        AbsPredicate does = new AbsPredicate(DOES);
        does.set(DOES_WHO, agent);
        does.set(DOES_WHAT, recommend);

        AbsIRE absIota = new AbsIRE(SLVocabulary.IOTA);
        absIota.setVariable(x);
        absIota.setProposition(does);

        return absIota;
    }
}
