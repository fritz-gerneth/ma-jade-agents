package de.effms.marsdemo.ontology;

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
        super(NAME, new Ontology[] {BasicOntology.getInstance(), RecommenderSystemOntology.getInstance(), CoordinateOntology.getInstance()}, new ReflectiveIntrospector());

        try {
            ConceptSchema userConcept = (ConceptSchema) getSchema(RecommenderSystemOntology.USER);
            userConcept.add(HEADING_TOWARDS, (ConceptSchema) getSchema(CoordinateVocabulary.COORDINATE), ObjectSchema.OPTIONAL, ObjectSchema.UNLIMITED);
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }
}
