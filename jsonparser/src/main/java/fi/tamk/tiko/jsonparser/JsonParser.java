package fi.tamk.tiko.jsonparser;

import java.util.ArrayList;
import java.io.*;

/**
 * Parses, writes and reads the Json.
 *
 * @author Ville-Veikko Nieminen
 * @version 1.8
 * @since 2018-11-20
 */
public class JsonParser {
    JsonWriter jsonWriter;
    JsonReader jsonReader;
    File jsonFile;

    /**
     * Sets variables for JsonParser.
     *
     * @param jsonFile JsonFile what user wants to edit.
     */
    public JsonParser(File jsonFile) {
        this.jsonFile = jsonFile;
        jsonWriter = new JsonWriter(jsonFile);
        jsonReader = new JsonReader(jsonFile);
    }

    /**
     * Calls JsonWriter and gives object to it.
     *
     * @param o Object which data we want to write to Json.
     */
    public void toJson(Object o) {
        jsonWriter.write(o);
    }

    /**
     * Calls method that reads .json file and adds its contents to array.
     *
     * @param array JsonArray where .json file contents goes.
     */
    public void fromJson(JsonArray array) {
        array = jsonReader.readAndMakeArray(array);
    }
}