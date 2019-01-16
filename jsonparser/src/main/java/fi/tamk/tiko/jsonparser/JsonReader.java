package fi.tamk.tiko.jsonparser;
import java.nio.file.*;
import java.nio.charset.*;
import java.io.*;
import java.util.List;
import java.io.IOException;

/**
 * Reads Json file.
 *
 * @author Ville-Veikko Nieminen
 * @version 1.8
 * @since 2018-11-20
 */
public class JsonReader {
    File jsonFile;
    Path filePath;

    /**
     * Sets variables for JsonReader.
     *
     * @param jsonFile JsonFile used.
     */
    public JsonReader(File jsonFile) {
        this.jsonFile = jsonFile;
        filePath = Paths.get("groceries.json");
    }

    /**
     * Reads .json file and get its contents to JsonArray in correct form for .json file.
     *
     * @param array JsonArray where contents is added.
     * @return JsonArray with the contents from .json file.
     */
    public JsonArray readAndMakeArray(JsonArray array) {
        boolean firstline =true;
        int categoryIndex = -1;
        String categoryName = "";
        try {
            List<String> lines = Files.readAllLines(filePath,Charset.defaultCharset());
            for(String line : lines) {
                line=line.trim();
                if(line.startsWith("{") && !firstline) {
                    JsonObject jObj = new JsonObject(categoryName);
                    line=line.replace("{","");
                    line=line.replace("\"", "");
                    line=line.replace("}","");
                    String[] strings = line.split(",");
                    for(String value : strings) {
                        String key = "";
                        int charIndex = 0;
                        for(int i=0; i<value.length(); i++) {
                            if(value.charAt(i) != ':') {
                                charIndex++;
                            } else {
                                key = value.substring(0,charIndex).trim();
                                value=value.substring(charIndex+1).trim();
                            }
                        }
                        jObj.add(key,value);
                    }
                    JsonArray category = (JsonArray) array.get(categoryIndex);
                    category.add(jObj);
                } else if(line.startsWith("\"")) {
                    line=line.replace("\"", "");
                    line=line.replace(":[","");
                    categoryName=line.trim();
                    categoryIndex++;
                }
                firstline =false;
            } 
        } catch(IOException e) {
                System.out.println(e);
        }

        return array;
    }

}