package de.effms.marsdemo.ontology.usermovement;

import de.effms.marsdemo.ontology.coordinate.CoordinateOntology;
import jade.content.abs.AbsObject;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.ConceptSchema;
import jade.content.schema.Facet;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PredicateSchema;

public class UserMovementOntology extends Ontology implements UserMovementVocabulary
{
    private static UserMovementOntology instance = new UserMovementOntology();

    public static UserMovementOntology getInstance()
    {
        return instance;
    }

    public static UserMovementOntology getNewInstance()
    {
        return new UserMovementOntology();
    }

    private UserMovementOntology()
    {
        super(NAME, CoordinateOntology.getInstance());

        try {
            final ConceptSchema headerToConcept = new ConceptSchema(HEADED_TO);
            headerToConcept.addSuperSchema((ConceptSchema) getSchema(SITUATION));
            headerToConcept.add(HEADED_TO_POSITION, (ConceptSchema) getSchema(COORDINATE));

            PredicateSchema headedToPredicate = new PredicateSchema(HEADED);
            headedToPredicate.addSuperSchema((PredicateSchema) getSchema(IS));

            // Only allow the MOVE concept to be used as the subject of the MOVES predicate
            headedToPredicate.addFacet(IS_WHAT, new Facet() {
                public void validate(AbsObject value, Ontology onto) throws OntologyException {
                    ObjectSchema valueSchema = onto.getSchema(value.getTypeName());
                    if (!valueSchema.isCompatibleWith(headerToConcept)) {
                        throw new OntologyException("Value " + value + " is not a " + HEADED_TO);
                    }
                }
            });

            this.add(headerToConcept);
            this.add(headedToPredicate);
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }
}
