package com.demo.ditox;

import static android.content.ContentValues.TAG;

import android.os.StrictMode;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ditoxWebClient extends WebViewClient {
    JSONArray jerryTheJellyfish = new JSONArray();

    /* For chat tab */
    private Socket socket;
    private String clientId;
    private WebView webView;
    private ChatInterface chatInterface;

    public void onChatTabSelected(WebView webView) {
        // todo needs to be changed configured to different ids
        this.clientId = "1";
        this.webView = webView;

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        chatInterface = new ChatInterface(webView.getContext(), clientId);

        socket = chatInterface.getSocket();
        socket.connect();

        Log.d(TAG, "onChatTabSelected: successfully connect socket");

        socket.on("json", onJSON);

//        new Thread(() ->{
//            chatInterface.fetch("2", "", "");
//        } ).run();

        chatInterface.load();

        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.addJavascriptInterface(chatInterface, "Android");
    }

    public void onChatTabUnselected(WebView webView) {
        if (chatInterface != null) {
            chatInterface.save();
            chatInterface = null;
        }

        if (socket != null) {
            socket.off("json", onJSON);
            socket.close();
            socket = null;
        }
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

    //called when index.html finished loading
    @Override
    public void onPageFinished(WebView webView, String url) {
        Log.d(TAG, "onPageFinished: " + url);
        if (Objects.equals(url, Constants.CHAT_PAGE_URL)) {
            Log.d(TAG, "changed to chat tab");
            onChatTabSelected(webView);
        }

        else {
            Log.d(TAG, "changed from chat tab");
            onChatTabUnselected(webView);
            refresh(webView);
        }
    }

    //refreshes app usage data on screen with 5 apps by default
    public static void refresh(WebView webView) {
        int numberOfApps = 10;
        Log.i("Ez ewgupsaegopsdg: ", UsageDataManager.getUsageDataForThisWeek(webView.getContext()).toString());
        ArrayList<Long> valueList = new ArrayList<>(UsageDataManager.getNLargestAndOthers(UsageDataManager.getUsageDataForThisWeek(webView.getContext()), numberOfApps).values());
        ArrayList<String> keyList = new ArrayList<>(UsageDataManager.getNLargestAndOthers(UsageDataManager.getUsageDataForThisWeek(webView.getContext()), numberOfApps).keySet());
        StringBuilder textToPass = new StringBuilder("javascript:appDataUpdater([");
        for (int i = 0; i < numberOfApps; i++) {
            textToPass.append(valueList.get(i)).append(",");
        }
        textToPass.append(valueList.get(numberOfApps)).append("], [\"");
        for (int i = 0; i < numberOfApps; i++) {
            textToPass.append(keyList.get(i)).append("\",\"");
        }
        textToPass.append(keyList.get(numberOfApps)).append("\"])");
        webView.loadUrl(textToPass.toString());
    }

    @JavascriptInterface
    public void getNames() {
        
    }
}
