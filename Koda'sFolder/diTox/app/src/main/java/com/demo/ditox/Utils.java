package com.demo.ditox;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Utils {
    static String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return jsonString;
    }

    public static void saveJsonToAssets(Context applicationContext, String filename, String json) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(applicationContext.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(json);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> jsonArrayToList(JSONArray peer_ids) throws JSONException {
        List<String> peer_Ids = null;
        for(int i = 0; i < peer_ids.length(); i++) {
            peer_Ids.add(peer_ids.getString(i));
        }
        return peer_Ids;
    }

    public static String toJSON(Object any) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        return gson.toJson(any);
    }

    
    public static ConcurrentMap<String, ConcurrentMap<String, Message>> jsonToMessageMap(String jsonFileString) throws JSONException {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        JSONObject obj = new JSONObject(jsonFileString);
        JSONArray history = obj.getJSONArray("history");
        ConcurrentMap<String, ConcurrentMap<String, Message>> messages = new ConcurrentHashMap<>();
        for(int i = 0; i < history.length(); i++){
            HistoryItem h = gson.fromJson(history.getJSONObject(i).toString(), HistoryItem.class);
            messages.put(h.peerId, new ConcurrentHashMap<>());
            for(Message m : h.messages) {
                messages.get(h.peerId).put(m.messageId, m);
            }
//            JSONObject item = history.getJSONObject(i);
//            String peer_id = item.getString("peer_id");
//            JSONArray messagesArray = item.getJSONArray("messages");
//            ConcurrentMap<String, Message> messageMap = new ConcurrentHashMap<>();
//            messages.put(peer_id, messageMap);
//            for(int j = 0; j < messagesArray.length(); j++) {
//                JSONObject message = messagesArray.getJSONObject(j);
//                Message msg = new Message();
//                msg.setSender(message.getString("peer_id"));
//                msg.setReceiver(message.getString("receiver"));
//                msg.setTimestamp(message.getLong("timestamp"));
//                msg.setMessage(message.getString("message"));
//                messages.get(peer_id).put(msg.getMessageId(), msg);
//            }
       }
        return messages;
    }

    public static String messageMapToJson(ConcurrentMap<String, ConcurrentMap<String, Message>> messages) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        return gson.toJson(messages);
    }

    public static List<HistoryItem> jsonArrayToHistoryItemList(JSONArray history) throws JSONException {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        List<HistoryItem> historyItems = new ArrayList<>();
        for(int i = 0; i < history.length(); i++) {
            JSONObject item = history.getJSONObject(i);
            historyItems.add(gson.fromJson(item.toString(), HistoryItem.class));
            }
        return historyItems;
    }
}
