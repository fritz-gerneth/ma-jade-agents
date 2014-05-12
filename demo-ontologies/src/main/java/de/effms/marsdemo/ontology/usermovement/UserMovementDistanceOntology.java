package de.effms.marsdemo.ontology.usermovement;

import de.effms.marsdemo.ontology.coordinate.CoordinateOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.ConceptSchema;

public class UserMovementDistanceOntology extends Ontology implements UserMovementDistanceVocabulary
{
    private static UserMovementDistanceOntology instance = new UserMovementDistanceOntology();

    public static UserMovementDistanceOntology getInstance()
    {
        return instance;
    }

    private UserMovementDistanceOntology()
    {
        super(NAME, UserMovementOntology.getInstance());

        try {
            final ConceptSchema headedToConcept = (ConceptSchema) getSchema(HEADED_TO);
            headedToConcept.add(HEADED_TO_DISTANCE, (ConceptSchema) getSchema(DISTANCE));
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }
}
