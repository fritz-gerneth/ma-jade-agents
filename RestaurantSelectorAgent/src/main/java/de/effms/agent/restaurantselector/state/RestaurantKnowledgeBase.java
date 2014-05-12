package de.effms.agent.restaurantselector.state;

import de.effms.jade.service.query.QueryIfCallback;
import de.effms.jade.service.query.QueryRefCallback;
import de.effms.jade.service.query.Queryable;
import de.effms.marsdemo.ontology.usermovement.UserMovementDistanceOntology;
import jade.content.abs.*;
import jade.content.lang.sl.SLVocabulary;
import jade.content.onto.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static de.effms.marsdemo.ontology.restaurant.RestaurantOntologyVocabulary.*;

/**
 * The Restaurant Knowledge Base acts as a knowledge base to all known restaurants. It is only Queryable. Subscriptions would
 * trigger informs when new restaurants open or close.
 *
 * This ItemSelection Component does not take the preferences into consideration.
 * Furthermore we don't let the recommender component specify any more complex queries than where the restaurant should be
 * located. A possible extension would be to express a maximum distance from the some Coordinate (restaurants nearby). Yet
 * such questions become very complex and tedious to evaluate manually. But the general idea should become clear.
 */
public class RestaurantKnowledgeBase implements Queryable
{
    private final Logger log = LoggerFactory.getLogger(RestaurantKnowledgeBase.class);

    private String[] restaurantNames = new String[] {"Dinnermite", "Piccola Osteria", "Asado", "Rosso Pizza", "Willi's Flammkuchen",
        "Nido", "Theresa Grill", "Tokami", "Sehnsucht", "Backspielhaus"
    };

    public RestaurantKnowledgeBase()
    {
        log.info("Creating Restaurant KnowledgeBase");
    }

    @Override
    public void queryIf(AbsPredicate query, QueryIfCallback callback)
    {
        callback.onLogicError(query, null);
    }

    @Override
    public void queryRef(AbsIRE query, QueryRefCallback callback)
    {
        callback.onQueryRefResult(query, this.answer(query));
    }

    private AbsConcept answer(AbsIRE query)
    {
        /**
         * Restaurants are located (IS_LOCATED) at some Coordinate. While they could have some other properties too,
         * that is the only one we handle in this example.
         *
         * IS_WHO refers to the restaurant
         * IS_WHAT refers to its coordinate
         *
         * Instead of maintaining a database of restaurants here we just create between 0 and 3 randomly. Any serious
         * implementation of course would actually look them up.
         *
         * The answer is therefor a set of restaurants.
         */
        String propositionTypeName = query.getProposition().getTypeName();
        if (propositionTypeName.equals(IS_LOCATED)) {
            AbsPredicate isLocated = query.getProposition();
            AbsObject isWho = isLocated.getAbsObject(IS_WHO);
            AbsObject isWhat = isLocated.getAbsObject(IS_WHAT);
            if (isWho instanceof AbsVariable && isWhat.getTypeName().equals(COORDINATE)) {
                return createRandomRestaurants();
            }
        }

        log.error("Could not handle query " + query);
        return null;
    }

    /**
     * Create some random restaurants. Base-Names are defined at the top. For some distinction we just add a random number
     * to the name
     */
    private AbsAggregate createRandomRestaurants()
    {
        int itemCount = randInt(0, 3);
        AbsAggregate items = new AbsAggregate(SLVocabulary.SET);
        for (int i = 0; i < itemCount; i++) {
            AbsConcept restaurant = new AbsConcept(RESTAURANT);
            restaurant.set(RESTAURANT_NAME, restaurantNames[new Random().nextInt(restaurantNames.length)] + " " + randInt(0, 5));
            items.add(restaurant);
        }
        return items;
    }

    private int randInt(int min, int max)
    {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    @Override
    public Ontology getOntology()
    {
        return UserMovementDistanceOntology.getInstance();
    }
}
