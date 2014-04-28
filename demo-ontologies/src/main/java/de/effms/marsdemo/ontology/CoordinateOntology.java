package de.effms.marsdemo.ontology;

import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.ConceptSchema;
import jade.content.schema.PrimitiveSchema;

public class CoordinateOntology extends Ontology implements CoordinateVocabulary
{
    private static CoordinateOntology instance = new CoordinateOntology();

    public static CoordinateOntology getInstance()
    {
        return instance;
    }

    private CoordinateOntology()
    {
        super(NAME, BasicOntology.getInstance());

        try {
            ConceptSchema coordinate = new ConceptSchema(COORDINATE);
            coordinate.add(LONGITUDE, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
            coordinate.add(LATITUDE, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));

            this.add(coordinate);
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }
}
