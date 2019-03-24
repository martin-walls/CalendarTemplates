package com.martinwalls.calendartemplates;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FileIO {

    // Checks if external storage is available for read and write
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    // Checks if external storage is available to read
    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private static void writeFile(Context context, String toWrite, boolean append) {
        if (isExternalStorageWritable()) {
            File file = new File(context.getExternalFilesDir(null), "templates.txt");
            try {
                FileOutputStream fos = new FileOutputStream(file, append);
                fos.write(toWrite.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void updateTemplate(Context context, String templateToUpdate, String name,
                               String location, String description, String startTime,
                               String endTime, String color) {
        String templateToWrite = name+";" + location+";" + description+";" + startTime+";"
                + endTime+";" + color+";";

        // make string to write of all current templates so it can be written back to file
        StringBuilder toWrite = new StringBuilder();
        ArrayList<ArrayList<String>> templatesList = getTemplates(context);
        for (ArrayList<String> template : templatesList) {
            if (template.get(0).equals(templateToUpdate)) {
                toWrite.append(templateToWrite);
            } else {
                for (String value : template) {
                    toWrite.append(value).append(";");
                }
            }
            toWrite.append("\n");
        }
        writeFile(context, toWrite.toString(), false);
    }

    static void addNewTemplate(Context context, String name,
                               String location, String description, String startTime,
                               String endTime, String color) {
        String templateToWrite = name+";" + location+";" + description+";" + startTime+";"
                + endTime+";" + color+";";

        // add new template to the end of the list
        if (getTemplates(context).isEmpty()) {
            writeFile(context, templateToWrite, true);
        } else {
            writeFile(context, "\n" + templateToWrite, true);
        }
    }

    static void deleteTemplate(Context context, String templateToDelete) {
        StringBuilder toWrite = new StringBuilder();
        ArrayList<ArrayList<String>> templatesList = getTemplates(context);
        for (ArrayList<String> template : templatesList) {
            if (!template.get(0).equals(templateToDelete)) {
                for (String value : template) {
                    toWrite.append(value).append(";");
                }
                toWrite.append("\n");
            }
        }
        String stringToWrite = toWrite.toString();
        stringToWrite = stringToWrite.substring(0, stringToWrite.length()-1);
        writeFile(context, stringToWrite, false);
    }

    // return arraylist of all stored templates
    static ArrayList<ArrayList<String>> getTemplates(Context context) {
        ArrayList<ArrayList<String>> templates = new ArrayList<>();
        if (isExternalStorageReadable()) {
            File file = new File(context.getExternalFilesDir(null), "templates.txt");
            try {
                FileInputStream fis = new FileInputStream(file);
                DataInputStream in = new DataInputStream(fis);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    ArrayList<String> template = new ArrayList<>();
                    // for each line
                    String value = "";
                    // iterate through chars to separate by ';'
                    for (int i=0; i<strLine.length(); i++) {
                        char c = strLine.charAt(i);
                        if (c != ';') {
                            value += c;
                        } else {
                            template.add(value);
                            value = "";
                        }
                    }
                    templates.add(template);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return templates;
        }
        return templates;
    }

    // return arraylist of values for given template
    static ArrayList<String> getTemplateInfo(Context context, String templateName) {
        ArrayList<ArrayList<String>> templatesList = getTemplates(context);
        for (ArrayList<String> template: templatesList) {
            if (template.get(0).equals(templateName)) {
                return template;
            }
        }
        return null;
    }
}
