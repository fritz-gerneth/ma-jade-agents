package de.effms.marsdemo.ontology.usermovement;

import de.effms.marsdemo.ontology.coordinate.CoordinateOntology;
import jade.content.abs.*;
import jade.content.lang.sl.SLVocabulary;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.ConceptSchema;
import jade.content.schema.Facet;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PredicateSchema;

public class UserMovementOntology extends Ontology implements UserMovementVocabulary
{
    private static UserMovementOntology instance = new UserMovementOntology();

    public static UserMovementOntology getInstance()
    {
        return instance;
    }

    public static UserMovementOntology getNewInstance()
    {
        return new UserMovementOntology();
    }

    private UserMovementOntology()
    {
        super(NAME, CoordinateOntology.getInstance());

        try {
            final ConceptSchema headerToConcept = new ConceptSchema(HEADED_TO);
            headerToConcept.addSuperSchema((ConceptSchema) getSchema(SITUATION));
            headerToConcept.add(HEADED_TO_POSITION, (ConceptSchema) getSchema(COORDINATE));

            PredicateSchema headedToPredicate = new PredicateSchema(HEADED);
            headedToPredicate.addSuperSchema((PredicateSchema) getSchema(IS));

            // Only allow the MOVE concept to be used as the subject of the MOVES predicate
            headedToPredicate.addFacet(IS_WHAT, new Facet() {
                public void validate(AbsObject value, Ontology onto) throws OntologyException {
                    ObjectSchema valueSchema = onto.getSchema(value.getTypeName());
                    if (!valueSchema.isCompatibleWith(headerToConcept)) {
                        throw new OntologyException("Value " + value + " is not a " + HEADED_TO);
                    }
                }
            });

            this.add(headerToConcept);
            this.add(headedToPredicate);
        } catch (OntologyException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the AbsIRE to query for the coordinates of HEADED_TO.
     *
     * ((iota ?x (um_headed (rs_user :rel_identity_uid demoUserCar) (um_headed_to :um_headed_to_pos ?x))))
     */
    public static AbsIRE getQueryForHeadedToCoordinate()
    {
        AbsVariable x = new AbsVariable("x", UserMovementOntology.COORDINATE);

        AbsConcept user = new AbsConcept(UserMovementVocabulary.USER);
        user.set(UserMovementVocabulary.IDENTITY_UID, "demoUser");

        AbsConcept headedTo = new AbsConcept(UserMovementVocabulary.HEADED_TO);
        headedTo.set(UserMovementVocabulary.HEADED_TO_POSITION, x);

        AbsPredicate headed = new AbsPredicate(UserMovementVocabulary.HEADED);
        headed.set(UserMovementOntology.IS_WHO, user);
        headed.set(UserMovementOntology.IS_WHAT, headedTo);

        AbsIRE absIota = new AbsIRE(SLVocabulary.IOTA);
        absIota.setVariable(x);
        absIota.setProposition(headed);

        return absIota;
    }

    /**
     * Get the AbsIRE to query for HEADED_TO.
     *
     * ((iota ?x (um_headed (rs_user :rel_identity_uid demoUserCar) ?x)))
     */
    public static AbsIRE getQueryForHeadedTo()
    {
        AbsVariable x = new AbsVariable("x", UserMovementOntology.COORDINATE);

        AbsConcept user = new AbsConcept(UserMovementVocabulary.USER);
        user.set(UserMovementVocabulary.IDENTITY_UID, "demoUser");

        AbsPredicate headed = new AbsPredicate(UserMovementVocabulary.HEADED);
        headed.set(UserMovementOntology.IS_WHO, user);
        headed.set(UserMovementOntology.IS_WHAT, x);

        AbsIRE absIota = new AbsIRE(SLVocabulary.IOTA);
        absIota.setVariable(x);
        absIota.setProposition(headed);

        return absIota;
    }
}
