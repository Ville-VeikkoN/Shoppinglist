package fi.tamk.tiko.shoppinglist;
import fi.tamk.tiko.jsonparser.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;
import java.util.Scanner;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.*;
import javafx.geometry.*;
import javafx.scene.input.*;
import javafx.event.*;
import java.util.TreeSet;
import javafx.application.Platform;
import java.util.Optional;
import javafx.scene.effect.*;
import javafx.scene.text.*;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.text.Font;


/**
 * Runs the user interface.
 *
 * @author Ville-Veikko Nieminen
 * @version 1.8
 * @since 2018-11-20
 */
public class Gui extends Application {

    static File jsonFile;
    static JsonParser jsonParser;
    static DatabaseConnector connector;
    static JsonArray<JsonObject> veggies;
    static JsonArray<JsonObject> meat;
    static JsonArray<JsonObject> frozen;
    static JsonArray<JsonObject> cold;
    static JsonArray<JsonObject> other;
    static JsonArray<JsonArray> allCategories;
    static ListView<String> list;
    static ObservableList<String> shoppinglist;
    static ObservableList<JsonObject> shoppinglistObjects;

    /**
     * Starts the application.
     *
     * @param args Command line arguments
     */
    public static void main(String [] args) {
        System.out.println("Author: Ville-Veikko Nieminen");
    
        launch(args);        
    }

    /**
     * Starts the application and opens the window.
     */
    @Override
    public void start(Stage stage) {
        list = new ListView<String>();
        shoppinglist =FXCollections.observableArrayList();
        shoppinglistObjects =FXCollections.observableArrayList();
        DropboxUploader uploader = new DropboxUploader(jsonFile);
        connector = new DatabaseConnector();
        intializeArrays();
        generateJson();

        Button add = new Button("Add");
        Button remove = new Button("Remove");
        Button save = new Button("Save to Dropbox");
        BorderPane root = new BorderPane();
        ComboBox categoryBox = new ComboBox();
        categoryBox.getItems().addAll(
            veggies.getName(),
            meat.getName(),
            frozen.getName(),
            cold.getName(),
            other.getName() 
        );
        categoryBox.setValue(veggies.getName());
        updateShoppinglist();
        Label itemLabel = new Label("Item: ");
        Label amountLabel = new Label("Amount: ");
        Label categoryLabel = new Label("Category: ");
        TextField itemField = new TextField();
        TextField amountField = new TextField();
        itemField.setPromptText("Enter the item.");
        amountField.setPromptText("Enter the amount.");
        HBox group = new HBox();
        group.getChildren().addAll(categoryLabel,categoryBox,itemLabel,itemField,amountLabel,amountField,add,remove,save);
        group.setSpacing(10);
        root.setBottom(group);
        root.setCenter(list);
        Scene scene = new Scene(root,900,640);
        scene.getStylesheets().add("Stylesheet.css");
        stage.setTitle("Shoppinglist");
        stage.initStyle(StageStyle.UTILITY);
        stage.centerOnScreen();
        stage.setScene(scene);
        stage.show();
        
        /**
         * Handles events for add button.
         */
        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if(!itemField.getText().isEmpty() && !amountField.getText().isEmpty()) {
                    addItem(itemField.getText(),amountField.getText(),categoryBox.getValue());
                    jsonParser.toJson(allCategories);
                    updateShoppinglist();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText("Enter item and amount.");
                    alert.setContentText("Example: Item: \"carrot\" Amount: \"4\" ");
                    Optional<ButtonType> result = alert.showAndWait();
                }
            }
        });

        /**
         * Handles events for remove button.
         */
        remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                boolean deleted = false;
                JsonArray array;
                JsonObject jObj;
                if(list.getSelectionModel().getSelectedIndex() > 0) {
                    jObj = (JsonObject)shoppinglistObjects.get(list.getSelectionModel().getSelectedIndex());
                    for(int i=0; i<allCategories.size && !deleted; i++) {
                        array = (JsonArray) allCategories.get(i);
                        if(array.getName().equals(jObj.getCategoryName())) {
                            for(int j=0; j<array.size && !deleted; j++) {
                                if(((JsonObject)array.get(j)).getData().equals(jObj.getData())) {
                                    array.remove(j);
                                    connector.removeJObj(jObj.getId());
                                    jsonParser.toJson(allCategories);
                                    updateShoppinglist();
                                    deleted = true;
                                }
                            }
                        }
                    }
                }
            }
        });

        /**
         * Handles events for save button.
         */
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    uploader.saveToDropbox();
                } catch(Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });

    }

    /**
     * Intializes JsonArrays.
     */
    public void intializeArrays() {
        veggies = new JsonArray<>("veggies");
        meat = new JsonArray<>("meat");
        frozen = new JsonArray<>("frozen");
        cold = new JsonArray<>("cold");
        other = new JsonArray<>("other");
        allCategories = new JsonArray("categories");
        allCategories.add(veggies);
        allCategories.add(meat);
        allCategories.add(frozen);
        allCategories.add(cold);
        allCategories.add(other);
    }

    /**
     * Checks if .json file already exists.
     *
     * If there is no .json file already, create one, if there is, gets contents from it.
     */
    public void generateJson() {
        try {
            jsonFile = new File("groceries.json");
            jsonParser = new JsonParser(jsonFile);
            if(jsonFile.exists()) {
                jsonParser.fromJson(allCategories);
                connector.saveAllJObj(allCategories);
            } else {
                jsonFile.createNewFile();
                connector.fromDBtoJson(jsonParser, allCategories);
            }
        } catch(IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Asks item and amount for it from the user.
     *
     * Asks item and the amount for it and then adds it 
     * to given JsonArray.
     *
     * @param item Item to add to the JsonArray.
     * @param amount Amount for the item to add.
     * @param object Selected object from categoryBox.
     */
    public static void addItem(String item, String amount, Object object) {
        String categoryName = (String) object;
        JsonObject jObj = new JsonObject(categoryName);
        jObj.add("item", item);
        jObj.add("amount", amount);
        connector.saveJObj(jObj);
        for(int i=0; i<allCategories.size; i++) {
            if(((JsonArray)allCategories.get(i)).getName().equals(categoryName)) {
                ((JsonArray)allCategories.get(i)).add(jObj);
            }
        }
    }

    /**
     * Updates shoppinglist and its contents from JsonArrays.
     */
    public static void updateShoppinglist() {
        shoppinglist.clear();
        shoppinglistObjects.clear();
        for(int i=0; i<allCategories.size; i++) {
            JsonArray array = (JsonArray)allCategories.get(i);
            shoppinglist.add(array.getName().toUpperCase());
            shoppinglistObjects.add(new JsonObject(""));
            for(int j=0; j<array.size; j++) {
                JsonObject obj = (JsonObject) array.get(j);
                shoppinglist.add("    "+obj.getData());
                shoppinglistObjects.add(obj);
            }
        }
        list.setItems(shoppinglist);
    }

    /**
     * Closes database connection when application shuts down.
     */
    public void stop() {
        connector.closeFactory();
    }

}