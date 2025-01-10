package fr.pandacube.lib.paper.json;

import fr.pandacube.lib.core.json.Json;

/**
 * Utility class to register Json adapters related to paper API classes.
 */
public class PaperJson {

    /**
     * Registers Json adapters related to paper API classes.
     */
    public static void init() {
        Json.registerTypeAdapterFactory(ItemStackAdapter.FACTORY);
        Json.registerTypeAdapterFactory(ConfigurationSerializableAdapter.FACTORY);
    }


    private PaperJson() {}
}
