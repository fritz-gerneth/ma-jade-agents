package de.effms.marsdemo.ontology.usermovement;

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

    public static UserMovementDistanceOntology getNewInstance()
    {
        return new UserMovementDistanceOntology();
    }

    private UserMovementDistanceOntology()
    {
        /**
         * In this case we need our own copy of the base ontology:
         * As we retrieve the existing concept by reference and extend it, the extension would be added to the base
         * ontology. As we don't want this to be the case (the concept defined in this ontology would become mandatory
         * for users of the base ontology), we create our own instance of the base ontology. It is still
         * added to the base ontology but its not affecting the singletone instance we usually fetch.
         */
        super(NAME, UserMovementOntology.getNewInstance());

        try {
            final ConceptSchema headedToConcept = (ConceptSchema) getSchema(HEADED_TO);
            headedToConcept.add(HEADED_TO_DISTANCE, (ConceptSchema) getSchema(DISTANCE));
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }
}
