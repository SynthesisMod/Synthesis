package com.luna.synthesis.managers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.*;

public class BackpackManager {

    private JsonObject jsonObject;
    private File file;

    // Damn this is old and garbage lmao
    public BackpackManager(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
                this.file = file;
                createData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileReader reader = new FileReader(file);
                this.jsonObject = new Gson().fromJson(reader, JsonObject.class);
                this.file = file;
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void createData() throws IOException {
        JsonObject emptyData = new JsonObject();
        for (int i = 1; i <= 18; i++) {
            JsonArray array = new JsonArray();
            array.add(new JsonPrimitive(""));
            array.add(new JsonPrimitive(0));
            emptyData.add(String.valueOf(i), array);
        }
        this.jsonObject = emptyData;
        saveData();
    }

    private void saveData() {
        try {
            FileWriter writer = new FileWriter(this.file);
            new Gson().toJson(this.jsonObject, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void modifyData(String key, String name, int meta) {
        JsonArray array = new JsonArray();
        array.add(new JsonPrimitive(name));
        array.add(new JsonPrimitive(meta));
        this.jsonObject.add(key, array);
        saveData();
    }

    public String getName(String key) {
        if (this.jsonObject.has(key)) {
            return this.jsonObject.get(key).getAsJsonArray().get(0).getAsString();
        } else {
            return "";
        }
    }

    public int getMeta(String key) {
        if (this.jsonObject.has(key)) {
            return this.jsonObject.get(key).getAsJsonArray().get(1).getAsInt();
        } else {
            return 0;
        }
    }
}
