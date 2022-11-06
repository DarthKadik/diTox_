package com.demo.ditox;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class UserData {
    String clientId;
    List<String> peerIds;
    String secretKey;
    String publicKey;
    List<HistoryItem> history;
    List<String> keywords;

    public UserData(String jsonString) throws JSONException {
        JSONObject json_obj = new JSONObject(jsonString);
        this.clientId = json_obj.getString("client_id");
        this.secretKey = json_obj.getString("secretKey");
        this.publicKey = json_obj.getString("publicKey");
        this.peerIds = Utils.jsonArrayToList(json_obj.getJSONArray("peer_ids"));
        this.history = Utils.jsonArrayToHistoryItemList(json_obj.getJSONArray("history"));

    }
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<String> getPeerIds() {
        return peerIds;
    }

    public void setPeerIds(List<String> peerIds) {
        this.peerIds = peerIds;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public List<HistoryItem> getHistory() {
        return history;
    }

    public void setHistory(List<HistoryItem> history) {
        this.history = history;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "clientId='" + clientId + '\'' +
                ", peerIds=" + peerIds +
                ", secretKey='" + secretKey + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", history=" + history +
                ", keywords=" + keywords +
                '}';
    }
}
