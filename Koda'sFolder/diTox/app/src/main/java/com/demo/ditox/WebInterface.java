package com.demo.ditox;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class WebInterface {
    Context mContext;
    JSONArray jerryTheJellyfish = new JSONArray();

    // Instantiate interface, also sets context
    WebInterface(Context c) {
        mContext = c;
    }

    //Example function. Can be called from JS using Android.showToast(myString);
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void getNames() {

    }
}
