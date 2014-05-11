package de.effms.marsdemo.ontology.usermovement;

import de.effms.marsdemo.ontology.coordinate.CoordinateVocabulary;

public interface UserMovementVocabulary extends CoordinateVocabulary
{
    String NAME = "de.effms.marsdemo.ontology.usermovement.UserMovement";

    String HEADED = "um_headed";

    String HEADED_TO = "um_headed_to";

    String HEADED_TO_POSITION = "um_headed_to_pos";
}
