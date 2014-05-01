package de.effms.marsdemo.ontology.coordinate;

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
            coordinate.add(X, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
            coordinate.add(Y, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));

            this.add(coordinate);
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }
}
