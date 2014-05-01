package de.effms.marsdemo.ontology.car;

import de.effms.marsdemo.ontology.coordinate.CoordinateVocabulary;

public interface CarInformationVocabulary extends CoordinateVocabulary
{
    String NAME = "de.effms.marsdemo.ontology.car.CarInformation";

    String CAR = "car";

    String SPEED = "car_speed";

    String FUEL_USAGE = "car_fuel_usage";

    String FUEL_REMAINING = "car-fuel-remaining";

    String POSITION = "car_position";
}
