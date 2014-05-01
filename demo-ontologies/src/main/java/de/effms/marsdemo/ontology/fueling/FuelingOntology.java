package de.effms.marsdemo.ontology.fueling;

import de.effms.marsdemo.ontology.car.CarInformationOntology;
import de.effms.marsdemo.ontology.coordinate.CoordinateOntology;
import de.effms.marsdemo.ontology.usermovement.UserMovementOntology;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.ReflectiveIntrospector;
import jade.content.schema.ConceptSchema;

public class FuelingOntology extends Ontology implements FuelingVocabulary
{
    private static FuelingOntology instance = new FuelingOntology();

    public static FuelingOntology getInstance()
    {
        return instance;
    }

    private FuelingOntology()
    {
        super(NAME, new Ontology[] {
            CarInformationOntology.getInstance(),
            UserMovementOntology.getInstance()
        }, new ReflectiveIntrospector());

        try {
            ConceptSchema refuelConcept = new ConceptSchema(REFUEL);
            refuelConcept.addSuperSchema((ConceptSchema) getSchema(ACTION));
            refuelConcept.add(REFUEL_AT, (ConceptSchema) getSchema(COORDINATE));

            this.add(refuelConcept);
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }
}
