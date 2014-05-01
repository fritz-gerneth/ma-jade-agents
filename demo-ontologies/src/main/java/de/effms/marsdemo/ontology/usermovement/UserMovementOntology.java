package de.effms.marsdemo.ontology.usermovement;

import de.effms.jade.ontology.RecommenderSystemOntology;
import de.effms.marsdemo.ontology.coordinate.CoordinateOntology;
import de.effms.marsdemo.ontology.coordinate.CoordinateVocabulary;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.ReflectiveIntrospector;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;

public class UserMovementOntology extends Ontology implements UserMovementVocabulary
{
    private static UserMovementOntology instance = new UserMovementOntology();

    public static UserMovementOntology getInstance()
    {
        return instance;
    }

    private UserMovementOntology()
    {
        super(NAME,
            new Ontology[] {
                RecommenderSystemOntology.getInstance(),
                CoordinateOntology.getInstance()
            },
            new ReflectiveIntrospector()
        );

        try {
            ConceptSchema movesConcept = new ConceptSchema(MOVES_TO);
            movesConcept.addSuperSchema((ConceptSchema) getSchema(ACTION));
            movesConcept.add(MOVES_TO_POSITION, (ConceptSchema) getSchema(COORDINATE));

            this.add(movesConcept);
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }
}
