package com.luckynum.data;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.luckynum.model.Contact;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FreeContacts {

    private List<Contact> contacts;
    private final JSONArray contactsJSON = new JSONArray();
    private static final String myURL = "http://myCoolURL.com";

    public FreeContacts() {
    }

    public FreeContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public void initDataTransfer() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());


        executor.execute(() -> {
            // convert list of contacts to JSON

            convertToJSON();
            handler.post(() -> {

                Thread dataThread = new Thread(() -> {
                    // after contacts have been converted to JSON
                    try {
                        // try to send the data to the URL
                        sendData();
                        // shut down the active thread, return to UI thread
                        executor.shutdownNow();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                dataThread.start();
            });
        });

    }

    private void convertToJSON() {
        // not the cleanest way
        for (Contact contact : contacts) {
            JSONObject contactObj = new JSONObject();
            try {
                contactObj.put("name", contact.getName());
                contactObj.put("number", contact.getNumber());
                contactsJSON.put(contactObj);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendData() throws IOException {

        // simple POST
        URL url = new URL(myURL);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Bearer Key");
        connection.setRequestProperty("Content-Type", "application/json");

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

        wr.writeBytes(contactsJSON.toString());

        wr.flush();
        Log.i(getClass().getSimpleName(), "response code: " + connection.getResponseCode());
        wr.close();

    }

}