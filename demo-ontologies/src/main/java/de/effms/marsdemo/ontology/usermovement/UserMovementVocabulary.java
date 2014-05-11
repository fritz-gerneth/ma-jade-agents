package de.effms.marsdemo.ontology.usermovement;

import de.effms.marsdemo.ontology.coordinate.CoordinateVocabulary;

public interface UserMovementVocabulary extends CoordinateVocabulary
{
    String NAME = "de.effms.marsdemo.ontology.usermovement.UserMovement";

    String MOVE = "um_move";

    String MOVE_TO_POSITION = "um_move_to_pos";
}
