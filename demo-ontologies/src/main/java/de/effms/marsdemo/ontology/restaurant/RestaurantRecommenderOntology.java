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
import jade.content.schema.ConceptSchema;
import jade.content.schema.PrimitiveSchema;

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
        super(NAME, new Ontology[] { RestaurantOntology.getInstance(), RecommenderSystemOntology.getInstance()}, new ReflectiveIntrospector());

        try {
            final ConceptSchema eatAction = new ConceptSchema(EAT);
            eatAction.addSuperSchema((ConceptSchema) getSchema(ACTION));
            eatAction.add(EAT_WHERE, new ConceptSchema(ConceptSchema.BASE_NAME));

            this.add(eatAction);
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the AbsIRE to query for all restaurants located at a coordinate.
     *
     * ((all ?x (is_located ?x (Coordinate :x :y)))
     */
    public static AbsIRE getRestaurantsLocatedAt(int x, int y)
    {
        AbsConcept coordinate = new AbsConcept(COORDINATE);
        coordinate.set(X, x);
        coordinate.set(Y, y);

        return getRestaurantsLocatedAt(coordinate);
    }

    /**
     * Get the AbsIRE to query for all restaurants located at a coordinate.
     *
     * ((all ?x (is_located ?x (Coordinate :x :y)))
     */
    public static AbsIRE getRestaurantsLocatedAt(AbsConcept coordinate)
    {
        AbsVariable x = new AbsVariable("x", COORDINATE);

        AbsPredicate locatedAt = new AbsPredicate(IS_LOCATED);
        locatedAt.set(IS_WHO, x);
        locatedAt.set(UserMovementVocabulary.IS_WHAT, coordinate);

        AbsIRE absIota = new AbsIRE(SLVocabulary.IOTA);
        absIota.setVariable(x);
        absIota.setProposition(locatedAt);

        return absIota;
    }
}
