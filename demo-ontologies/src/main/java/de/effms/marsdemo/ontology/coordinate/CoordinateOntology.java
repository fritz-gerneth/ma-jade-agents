package de.effms.marsdemo.ontology.coordinate;

import de.effms.jade.ontology.RecommenderSystemOntology;
import jade.content.abs.AbsObject;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.*;

public class CoordinateOntology extends Ontology implements CoordinateVocabulary
{
    private static CoordinateOntology instance = new CoordinateOntology();

    public static CoordinateOntology getInstance()
    {
        return instance;
    }

    private CoordinateOntology()
    {
        super(NAME, RecommenderSystemOntology.getInstance());

        try {
            final ConceptSchema coordinate = new ConceptSchema(COORDINATE);
            coordinate.addSuperSchema((ConceptSchema) getSchema(SITUATION));
            coordinate.add(X, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
            coordinate.add(Y, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));

            ConceptSchema distance = new ConceptSchema(DISTANCE);
            distance.add(DISTANCE_D, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));

            PredicateSchema isLocated = new PredicateSchema(IS_LOCATED);
            isLocated.addSuperSchema((PredicateSchema) getSchema(IS));
            isLocated.addFacet(IS_WHAT, new Facet()
            {
                public void validate(AbsObject value, Ontology onto) throws OntologyException
                {
                    ObjectSchema valueSchema = onto.getSchema(value.getTypeName());
                    if (!valueSchema.isCompatibleWith(coordinate)) {
                        throw new OntologyException("Value " + value + " is not a " + COORDINATE);
                    }
                }
            });

            this.add(coordinate);
            this.add(distance);
            this.add(isLocated);
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }
}
