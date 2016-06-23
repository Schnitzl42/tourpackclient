package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MainGenerator {

    private static final String PROJECT_DIR = System.getProperty("user.dir");

    public static void main(String[] args) {
        //db version, db file package
        //update version here to trigger on upgrade!
        Schema schema = new Schema(1, "at.sti_innsbruck.tourpackclient.logic.greendao");
        //avoids that sections are lost on update to new version
        schema.enableKeepSectionsByDefault();
        addTables(schema);
        try {
            new DaoGenerator().generateAll(schema, PROJECT_DIR + "\\app\\src\\main\\java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTables(final Schema schema) {

        Entity thing = schema.addEntity("Thing");
        //primary key
       // thing.addIdProperty().autoincrement();
        Property idProperty = thing.addStringProperty("thing_id").notNull().primaryKey().unique().getProperty();
        thing.addStringProperty("content");

        Entity extraProperties = schema.addEntity("ExtraProperties");
        extraProperties.addIdProperty().autoincrement();
        Property thingProp = extraProperties.addStringProperty("thing_id").getProperty();
        extraProperties.addToOne(thing, thingProp);
        extraProperties.addStringProperty("property_id");
        extraProperties.addStringProperty("content");
    }
}
