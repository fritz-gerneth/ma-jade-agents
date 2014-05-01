package de.effms.marsdemo.ontology.usermovement;

import de.effms.jade.ontology.RecommenderSystemVocabulary;
import de.effms.marsdemo.ontology.coordinate.CoordinateVocabulary;

public interface UserMovementVocabulary extends CoordinateVocabulary, RecommenderSystemVocabulary
{
    String NAME = "de.effms.marsdemo.ontology.usermovement.UserMovement";

    String MOVES_TO = "um_moves_to";

    String MOVES_TO_POSITION = "um_moves_to_pos";
}
