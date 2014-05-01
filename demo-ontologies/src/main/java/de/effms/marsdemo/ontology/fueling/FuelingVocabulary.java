package de.effms.marsdemo.ontology.fueling;

import de.effms.marsdemo.ontology.car.CarInformationVocabulary;
import de.effms.marsdemo.ontology.usermovement.UserMovementVocabulary;

public interface FuelingVocabulary extends CarInformationVocabulary, UserMovementVocabulary
{
    String NAME = "de.effms.marsdemo.ontology.fueling.Fueling";

    String REFUEL = "fuel_refuel";

    String REFUEL_AT = "fuel_refuel_at";
}
