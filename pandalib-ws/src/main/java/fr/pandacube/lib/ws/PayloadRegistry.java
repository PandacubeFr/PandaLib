package fr.pandacube.lib.ws;

import com.google.gson.JsonParseException;
import fr.pandacube.lib.core.json.Json;
import fr.pandacube.lib.util.BiMap;
import fr.pandacube.lib.ws.payloads.ErrorPayload;
import fr.pandacube.lib.ws.payloads.LoginPayload;
import fr.pandacube.lib.ws.payloads.LoginSucceedPayload;
import fr.pandacube.lib.ws.payloads.MessagePayload;
import fr.pandacube.lib.ws.payloads.Payload;

import java.util.regex.Pattern;

/**
 * Handles the registration of all the {@link Payload} types, the serialization and deserialization of the payload data.
 */
public class PayloadRegistry {

    private static final String PAYLOAD_TYPE_SEPARATOR = ":";

    private static final BiMap<String, Class<? extends Payload>> payloadClasses = new BiMap<>();


    private static boolean isTypeValid(String type) {
        return !type.contains(PAYLOAD_TYPE_SEPARATOR);
    }

    private static String validateType(String type) {
        if (isTypeValid(type))
            return type;
        throw new IllegalArgumentException("Invalid characters in type identifier '" + type + "'");
    }

    /**
     * Register a new {@link Payload} type.
     * @param type the id of the payload type.
     * @param clazz the payload class to register.
     */
    public static void registerPayloadType(String type, Class<? extends Payload> clazz) {
        payloadClasses.put(validateType(type), clazz);
    }

    /**
     * Deserialize the provided String into a valid Payload.
     * @param message the serialized data.
     * @return the {@link Payload}.
     * @throws IllegalArgumentException if the serialized data does not have the proper format.
     */
    public static Payload fromString(String message) {
        String[] split = message.split(Pattern.quote(PAYLOAD_TYPE_SEPARATOR), 2);
        if (split.length != 2) {
            throw new IllegalArgumentException("Malformed message: does not respect format '<type>" + PAYLOAD_TYPE_SEPARATOR + "<jsonObject>'.");
        }

        Class<? extends Payload> detectedClass = payloadClasses.get(split[0]);
        if (detectedClass == null) {
            throw new IllegalArgumentException("Unrecognized data type '" + split[0] + "'.");
        }

        try {
            return Json.gson.fromJson(split[1], detectedClass);
        } catch (JsonParseException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

    /**
     * Serialize the provided {@link Payload}.
     * @param p the {@link Payload} to serialize. Must be of a registered type.
     * @return the serialized data.
     */
    public static String toString(Payload p) {
        String type = payloadClasses.getKey(p.getClass());
        if (type == null)
            throw new IllegalArgumentException(p.getClass() + " is not a registered payload type.");
        return arbitraryToString(type, p, false);
    }


    /**
     * Serialize the provided arbitrary data, that consist of a type and an object that will be converted
     * to a Json string.
     * @param type the type
     * @param obj the object to Jsonify
     * @param serializeNulls if null propreties must be included in the json object.
     * @return the String to send through the websocket
     */
    public static String arbitraryToString(String type, Object obj, boolean serializeNulls) {
        return validateType(type) + PAYLOAD_TYPE_SEPARATOR + (serializeNulls ? Json.gsonSerializeNulls : Json.gson).toJson(obj);
    }



    static {
        registerPayloadType("error", ErrorPayload.class);
        registerPayloadType("message", MessagePayload.class);
        registerPayloadType("login", LoginPayload.class);
        registerPayloadType("login-succeed", LoginSucceedPayload.class);
    }

}
