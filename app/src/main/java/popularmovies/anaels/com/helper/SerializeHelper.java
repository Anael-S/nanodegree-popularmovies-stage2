package popularmovies.anaels.com.helper;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * Utility used to serialize/deserialize objects
 */
public class SerializeHelper {

    /**
     * Serialize an Object into a JSon String
     *
     * @param pObject the object to serialize
     * @return the object serialized in a JSon String
     */
    public static String serializeJson(Object pObject) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(pObject);
    }

    /**
     * Deserialize a Json String into an Object
     *
     * @param pJsonObject  the json string to deserialize
     * @param pObjectClass the class of the object to deserialize
     * @return the deserialized object
     */
    public static <T> T deserializeJson(String pJsonObject, Type pObjectClass) {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(pJsonObject, pObjectClass);
        } catch (Exception e) {
            Log.d("SerializeUtil", "Error while deserializing Json", e);
            return null;
        }
    }

}
