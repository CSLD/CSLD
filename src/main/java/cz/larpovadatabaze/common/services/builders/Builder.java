package cz.larpovadatabaze.common.services.builders;

public interface Builder {
    /**
     * Build the state of the application and store it in the relevant stores.
     *
     * @return Relevant entities to be used in further querying.
     */
    MasqueradeEntities build();
}
