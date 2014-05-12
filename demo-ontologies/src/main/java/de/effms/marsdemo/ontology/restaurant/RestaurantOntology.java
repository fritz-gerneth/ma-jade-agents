package de.effms.marsdemo.ontology.restaurant;

import de.effms.marsdemo.ontology.coordinate.CoordinateOntology;
import de.effms.marsdemo.ontology.usermovement.UserMovementVocabulary;
import jade.content.abs.*;
import jade.content.lang.sl.SLVocabulary;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.*;

public class RestaurantOntology extends Ontology implements RestaurantOntologyVocabulary
{
    private static RestaurantOntology instance = new RestaurantOntology();

    public static RestaurantOntology getInstance()
    {
        return instance;
    }

    public static RestaurantOntology getNewInstance()
    {
        return new RestaurantOntology();
    }

    private RestaurantOntology()
    {
        super(NAME, CoordinateOntology.getInstance());

        try {
            final ConceptSchema restaurantConcept = new ConceptSchema(RESTAURANT);
            restaurantConcept.add(RESTAURANT_NAME, (PrimitiveSchema) getSchema(BasicOntology.STRING));

            this.add(restaurantConcept);
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
