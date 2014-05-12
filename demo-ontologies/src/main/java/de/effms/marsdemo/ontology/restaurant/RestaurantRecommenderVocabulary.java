package de.effms.marsdemo.ontology.restaurant;

import de.effms.jade.ontology.RecommenderSystemVocabulary;
import de.effms.marsdemo.ontology.coordinate.CoordinateVocabulary;

public interface RestaurantRecommenderVocabulary extends CoordinateVocabulary, RecommenderSystemVocabulary
{
    String NAME = "de.effms.marsdemo.ontology.RestaurantRecommender";

    static String EAT = "eat";

    static String EAT_WHERE = "eat_where";
}
