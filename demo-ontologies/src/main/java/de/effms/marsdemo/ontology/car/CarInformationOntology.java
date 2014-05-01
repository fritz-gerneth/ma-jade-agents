package de.effms.marsdemo.ontology.car;

import de.effms.jade.ontology.RelationalOntology;
import de.effms.jade.ontology.RelationalVocabulary;
import de.effms.marsdemo.ontology.coordinate.CoordinateOntology;
import de.effms.marsdemo.ontology.coordinate.CoordinateVocabulary;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PredicateSchema;
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
        super(CarInformationVocabulary.NAME,
            new Ontology[] {
                CoordinateOntology.getInstance(), RelationalOntology.getInstance()
            },
            new jade.content.onto.ReflectiveIntrospector()
        );

        try {
            ConceptSchema car = new ConceptSchema(CAR);
            car.addSuperSchema((ConceptSchema) getSchema(IDENTITY));

            PredicateSchema speedPredicate = new PredicateSchema(SPEED);
            speedPredicate.addSuperSchema((PredicateSchema) getSchema(HAS));

            PredicateSchema fuelUsagePredicate = new PredicateSchema(FUEL_USAGE);
            fuelUsagePredicate.addSuperSchema((PredicateSchema) getSchema(HAS));

            PredicateSchema fuelRemainingPredicate = new PredicateSchema(FUEL_REMAINING);
            fuelRemainingPredicate.addSuperSchema((PredicateSchema) getSchema(HAS));

            PredicateSchema positionPredicate = new PredicateSchema(POSITION);
            positionPredicate.addSuperSchema((PredicateSchema) getSchema(IS));

            this.add(car);
            this.add(speedPredicate);
            this.add(fuelUsagePredicate);
            this.add(fuelRemainingPredicate);
            this.add(positionPredicate);
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }
}
