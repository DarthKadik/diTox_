package com.demo.ditox;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends Activity {
    private Socket socket;
    private WebView webView;
    private String clientId;
    private ChatInterface chatInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // todo: setContentView()

        clientId = savedInstanceState.getString("clientId");

        chatInterface = new ChatInterface(this, clientId);
        socket = chatInterface.getSocket();
        socket.connect();

        // register all the events
        socket.on("json", onJSON);

        // load data
        chatInterface.load();

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///"); // todo: chat html
        webView.addJavascriptInterface(chatInterface, "Android");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        chatInterface.save();

        // deregister all the events
        socket.off("json", onJSON);
    }

    private Emitter.Listener onJSON = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            String peerId = "";
            String messageId = "";
            Long timestamp = Long.valueOf(0);
            String message = "";

            try {
                peerId = data.getString("peer_id");
                messageId = data.getString("message_id");
                timestamp = data.getLong("timestamp");
                message = data.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();;
            }

            Message msg = new Message();
            msg.messageId = messageId;
            msg.sender = peerId;
            msg.receiver = clientId;
            msg.timestamp = timestamp;
            msg.message = message;

            webView.loadUrl("javascript:receiveMessage" + "('" + clientId + "','" + peerId + "','" + messageId + "','" + timestamp + "','" + message + "')");

            chatInterface.onReceiveMessage(msg);
        }
    };



}
