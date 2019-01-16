package fi.tamk.tiko.jsonparser;

import javax.persistence.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

/**
 * represents an JSON object value.
 *
 * @author Ville-Veikko Nieminen
 * @version 1.8
 * @since 2018-11-20
 */
@Entity(name = "shoppinglist")
public class JsonObject {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    int id;
    @Column(name="category")
    String categoryName;
    @Column(name="item")
    String item="";
    @Column(name="amount")
    String amount="";
    @Transient
    HashMap map;
 
    /**
     * Sets variables for JsonObject.
     *
     * @param categoryName Name of the category where JsonObject belongs.
     */
    public JsonObject(String categoryName) {
        this.map = new HashMap<String, String>();
        this.categoryName = categoryName;
    }

    public JsonObject(){}

    /**
     * Adds key and value to HashMap.
     *
     * @param k String representing name of the key.
     * @param v Integer representing value.
     */
    public void add(String k, String v) {
        map.put(k, v);
        if(map.size()==1) {
            item = v;
        } else if (map.size()==2) {
            amount = v;
        }
    }

    /**
     * Returns category name where JsonObject belongs.
     *
     * @return String represents category name.
     */
    public String getCategoryName() {
        return this.categoryName;
    }

    /**
     * Returns data from JsonObject separated with comma.
     *
     * @return String representing data from JsonObject.
     */
    public String getData() {
        String s ="";
               s += ",   ";
        return item+",   "+amount;
    }

    /**
     * Returns Object's data in correct form for Json.
     *
     * @return String representing Object's data.
     */
    public String toString() {
        return "{\"item\": \""+item+"\", \"amount\":"+" \""+amount+"\"}";
    }

    /**
     * Returns JsonObject's id.
     *
     * @return id JsonObjects id.
     */
    public int getId() {
        return id;
    }

}