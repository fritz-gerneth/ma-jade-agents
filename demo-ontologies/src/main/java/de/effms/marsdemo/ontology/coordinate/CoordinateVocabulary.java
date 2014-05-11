package de.effms.marsdemo.ontology.coordinate;

import de.effms.jade.ontology.RecommenderSystemVocabulary;

public interface CoordinateVocabulary extends RecommenderSystemVocabulary
{
    String NAME = "de.effms.marsdemo.ontology.coordinate.Coordinate";

    String IS_LOCATED = "is_located";

    String COORDINATE = "Coordinate";
    String X = "coordinate_x";
    String Y = "coordinate_y";

    String DISTANCE = "Distance";
    String DISTANCE_D = "distance_d";
}
