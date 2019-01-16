package fi.tamk.tiko.jsonparser;

import java.io.*;
import java.util.ArrayList;
import java.io.IOException;

/**
 * Writes the Json.
 *
 * @author Ville-Veikko Nieminen
 * @version 1.8
 * @since 2018-11-20
 */
public class JsonWriter {
    FileInputStream fStream;
    BufferedReader bReader;
    File jsonFile;

    /**
     * Sets variables for JsonWriter.
     *
     * @param jsonFile JsonFile we want to write to.
     */
    public JsonWriter(File jsonFile) {
        this.jsonFile = jsonFile;
    }

    /**
     * Writes data from object to JsonFile.
     *
     * @param o Object which data we want to write to Json.
     */
    public void write(Object o) {
        try(FileWriter writer = new FileWriter(jsonFile, false /*boolean to append or not*/)){
            writer.write("{\n");
            JsonArray array = (JsonArray) o;
            if(array.get(0) instanceof JsonArray) {
                for(int i=0; i<array.size; i++) {
                    JsonArray category = (JsonArray) array.get(i);
                    writer.write(category.toString());
                    if(i!=array.size-1) {
                        writer.write(",\n");
                    }
                }
            } else {
                writer.write(array.toString());
            }
            writer.write("\n}");
        } catch(IOException e) {
            System.out.println(e);
        }

    }
}