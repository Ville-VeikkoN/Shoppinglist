package fi.tamk.tiko.shoppinglist;

import fi.tamk.tiko.jsonparser.JsonObject;
import fi.tamk.tiko.jsonparser.JsonArray;
import fi.tamk.tiko.jsonparser.JsonParser;

import org.hibernate.*;
import org.hibernate.cfg.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.*;
/**
 * Class to connect, save and remove data from database.
 * 
 * @author Ville-Veikko Nieminen
 * @version 1.8
 * @since 2018-12-17
 */
public class DatabaseConnector {
    Configuration config;
    SessionFactory factory;
    Session session;

    /**
     * Initializes the connector and open the connection.
     */
    public DatabaseConnector () {
        config = new Configuration();
        config.configure();
        config.addAnnotatedClass(JsonObject.class);

        factory = config.buildSessionFactory();
        session = factory.openSession();
    }

    /**
     * Save one object to database.
     * 
     * @param jObj JsonObject to save.
     */
    public void saveJObj(JsonObject jObj) {
        if (!session.isOpen()) {
            session = factory.openSession();
        }

        Transaction tx = session.beginTransaction();

        session.persist(jObj);
        tx.commit();

        session.close();
    }

    /**
     * Removes one JsonObject from database.
     * 
     * @param key Id which item to remove.
     */
    @SuppressWarnings("rawtypes")
    public void removeJObj(int key) {
        if (!session.isOpen()) {
            session = factory.openSession();
        }
        Transaction tx = session.beginTransaction();
        Query removeQuery = session.createQuery("delete from shoppinglist where id = "+key);

        removeQuery.executeUpdate();

        tx.commit();
        session.close();
    }

    /**
     * Writes Json using database content.
     * 
     * @param parser JsonParser.
     * @param allCategories JsonArray which gets the data and gives it to the parser.
     */
    @SuppressWarnings("unchecked")
    public void fromDBtoJson(JsonParser parser, JsonArray allCategories) {
        if (!session.isOpen()) {
            session = factory.openSession();
        }

        List<JsonObject> objects = session.createQuery("FROM shoppinglist").list();

        for (JsonObject jObj : objects) {
            for(int i=0; i<allCategories.size(); i++) {
                JsonArray array = (JsonArray) allCategories.get(i);
                if(jObj.getCategoryName().equals(array.getName())) {
                    array.add(jObj);
                }
            }
        }

        parser.toJson(allCategories);

        session.close();
    }

    /**
     * Saves JsonObject from JsonArray to database.
     * 
     * @param array JsonArray containing all data.
     * @return true or false, depending if saving was succesful.
     */
    @SuppressWarnings("rawtypes")
    public boolean saveAllJObj(JsonArray array) {
        if (!session.isOpen()) {
            session = factory.openSession();
        }
        try {
            Transaction tx = session.beginTransaction();
            Query removeQuery = session.createQuery("DELETE FROM shoppinglist");
            removeQuery.executeUpdate();
            tx.commit();
    
            for(int i=0; i<array.size(); i++) {
                JsonArray subArray = (JsonArray)array.get(i);
                for(int j=0; j<subArray.size(); j++) {
                    saveJObj((JsonObject)subArray.get(j));
                }
            }
            tx.commit();
            session.close();
            return true;
        } catch (Exception e) {
            session.close();
            return false;
        }
    }

    /**
     * Closes the connection.
     */
    public void closeFactory() {
        factory.close();
    }
}