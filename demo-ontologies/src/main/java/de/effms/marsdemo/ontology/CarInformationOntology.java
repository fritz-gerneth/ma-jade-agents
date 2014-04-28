package de.effms.marsdemo.ontology;

import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PrimitiveSchema;

public class CarInformationOntology extends Ontology implements CarInformationVocabulary
{
    private static CarInformationOntology instance = new CarInformationOntology();

    public static CarInformationOntology getInstance()
    {
        return instance;
    }

    private CarInformationOntology()
    {
        super(NAME, new Ontology[] {BasicOntology.getInstance(), CoordinateOntology.getInstance()}, new jade.content.onto.ReflectiveIntrospector());

        try {
            ConceptSchema car = new ConceptSchema(CAR);
            car.add(ID, (PrimitiveSchema) getSchema(BasicOntology.STRING));
            car.add(DRIVING, (PrimitiveSchema) getSchema(BasicOntology.BOOLEAN), ObjectSchema.OPTIONAL);
            car.add(SPEED, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.OPTIONAL);
            car.add(FUEL_USAGE, (PrimitiveSchema) getSchema(BasicOntology.FLOAT), ObjectSchema.OPTIONAL);
            car.add(FUEL_Remaining, (PrimitiveSchema) getSchema(BasicOntology.FLOAT), ObjectSchema.OPTIONAL);
            car.add(POSITION, (ConceptSchema) CoordinateOntology.getInstance().getSchema(CoordinateVocabulary.COORDINATE));

            this.add(car);
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }
}
