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

    private UserMovementOntology()
    {
        super(NAME, CoordinateOntology.getInstance());

        try {
            final ConceptSchema movesConcept = new ConceptSchema(MOVE);
            movesConcept.addSuperSchema((ConceptSchema) getSchema(ACTION));
            movesConcept.add(MOVE_TO_POSITION, (ConceptSchema) getSchema(COORDINATE));

            PredicateSchema movingPredicate = new PredicateSchema(MOVES);
            movingPredicate.addSuperSchema((PredicateSchema) getSchema(DOES));

            // Only allow the MOVE concept to be used as the subject of the MOVES predicate
            movingPredicate.addFacet(DOES_WHAT, new Facet() {
                public void validate(AbsObject value, Ontology onto) throws OntologyException {
                    ObjectSchema valueSchema = onto.getSchema(value.getTypeName());
                    if (!valueSchema.isCompatibleWith(movesConcept)) {
                        throw new OntologyException("Value " + value + " is not a " + MOVE);
                    }
                }
            });

            this.add(movesConcept);
            this.add(movingPredicate);
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }
}
