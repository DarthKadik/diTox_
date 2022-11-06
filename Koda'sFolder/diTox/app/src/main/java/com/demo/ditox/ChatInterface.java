package com.demo.ditox;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.client.IO;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class ChatInterface {
    private Socket socket;
    private OkHttpClient client;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final Object empty = new Object();
    private String mClientId;
    private ConcurrentMap<String, ConcurrentMap<String, Message>> messages;
    private List<String> peer_ids;
    private ConcurrentMap<String, Object> onces;
    private String public_key;
    private String private_key;
    private String jsonState;
    private String base_url;
    Context mContext;

    {
        try {
            socket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        client = new OkHttpClient();
    }

    ChatInterface(Context c, String clientId) {
        mContext = c;
        mClientId = clientId;
        messages = new ConcurrentHashMap<>();
        onces = new ConcurrentHashMap<>();
    }

    public Socket getSocket() {
        return socket;
    }

    public void onReceiveMessage(Message msg) {
        messages.putIfAbsent(msg.sender, new ConcurrentHashMap<>());
        messages.get(msg.sender).put(msg.messageId, msg);
    }

    public void load() {
        this.jsonState = Utils.getJsonFromAssets(mContext.getApplicationContext(), String.format("%s.json", this.mClientId));
        if (this.jsonState != null) {
            try {
                JSONObject jsonState = new JSONObject(this.jsonState);
                this.public_key = jsonState.getString("public_key");
                this.private_key = jsonState.getString("private_key");
                this.messages = Utils.jsonToMessageMap(jsonState.getString("messages"));
                this.peer_ids = Utils.jsonArrayToList(jsonState.getJSONArray("peer_ids"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        JSONObject object = null;
        try {
            object = new JSONObject(this.jsonState);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            object.put("messages", Utils.messageMapToJson(messages));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Utils.saveJsonToAssets(mContext.getApplicationContext(), String.format("%s.json", this.mClientId), this.jsonState);
    }


    @JavascriptInterface
    public String fetchMessages(String peerId, String challenge, String signature) {
        if (!onces.containsKey(peerId)) {
            fetch(peerId, challenge, signature);
            onces.put(peerId, empty);
        }


        return Utils.toJSON(messages.get(peerId).values());
    }

    @JavascriptInterface
    public String fetchPeerIds() {
        return Utils.toJSON(peer_ids);
    }

    public void fetch(String peerId, String challenge, String signature) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("client_id", mClientId);
            obj.put("peer_id", peerId);
            obj.put("challenge", challenge);
            obj.put("signature", signature);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = String.format("%s/get_messages", Constants.CHAT_SERVER_URL);
        RequestBody body = RequestBody.create(obj.toString(), JSON);
        Request request = new Request.Builder().url(url).post(body).build();

        try (Response response = client.newCall(request).execute()) {
            String resp = response.body().string();

            try {
                // todo: do some decryption
                JSONArray respArray = new JSONArray(resp);
                for (int i = 0; i < respArray.length(); i++) {
                    JSONObject msgOBJ = respArray.getJSONObject(i);
                    Message msg = new Message();
                    msg.messageId = msgOBJ.getString("message_id");
                    msg.timestamp = msgOBJ.getLong("timestamp");
                    msg.message = msgOBJ.getString("message");
                    msg.sender = peerId;
                    msg.receiver = mClientId;

                    messages.putIfAbsent(peerId, new ConcurrentHashMap<>());
                    messages.get(peerId).put(msg.messageId, msg);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @JavascriptInterface
    public void sendToId(String peerId, String message, String signature) {
        JSONObject obj = new JSONObject();

        String messageId = String.format("%s:%s", mClientId, UUID.randomUUID().toString());
        Long timestamp = System.currentTimeMillis();

        try {
            obj.put("client_id", mClientId);
            obj.put("peer_id", peerId);
            obj.put("message_id", messageId);
            obj.put("timestamp", timestamp);
            obj.put("message", message);
            obj.put("signature", signature);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("send_to_id", obj);

        Message msg = new Message();
        msg.messageId = messageId;
        msg.sender = mClientId;
        msg.receiver = peerId;
        msg.timestamp = timestamp;
        msg.message = message;

        messages.putIfAbsent(peerId, new ConcurrentHashMap<>());
        messages.get(peerId).put(msg.messageId, msg);
    }

    private String getChallenge() {
        Request request = new Request.Builder()
                .url(base_url + "/get_challenge/" + mClientId)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @JavascriptInterface
    public void join(String signature) {
        JSONObject obj = new JSONObject();
        String challenge = getChallenge();

        try {
            obj.put("client_id", mClientId);
            obj.put("challenge", challenge);
            obj.put("signature", signature);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("join", obj);
    }

    @JavascriptInterface
    public void leave(String signature) {
        JSONObject obj = new JSONObject();
        String challenge = getChallenge();
        try {
            obj.put("client_id", mClientId);
            obj.put("challenge", challenge);
            obj.put("signature", signature);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("leave", obj);
    }

    @JavascriptInterface
    public void register(){
        JSONObject obj = new JSONObject();

        try {
            obj.put("client_id", mClientId);
            obj.put("public_key", public_key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("register", obj);
    }
}
