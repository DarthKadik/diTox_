package com.demo.ditox;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WebInterface {
    Context mContext;
    JSONArray jerryTheJellyfish = new JSONArray();

    // Instantiate interface, also sets context
    WebInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void getIcon() throws PackageManager.NameNotFoundException {
        List<String> topPackages = new ArrayList<>(UsageDataManager.getNLargestAndOthers(UsageDataManager.getUsageDataForThisWeekAsPackageNames(AppContext.getAppContext()), 8).keySet());
        Drawable icon = AppContext.getAppContext().getPackageManager().getApplicationIcon("com.demo.ditox");;
        try {
            icon = AppContext.getAppContext().getPackageManager().getApplicationIcon(topPackages.get(0));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = ((BitmapDrawable)icon).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();

        String userDetailsBytesToStrings = new String(
                bitmapdata, StandardCharsets.UTF_8);
        JsonParser jsonParser = new JsonParser();

        JsonObject jsonObjectOutput = (JsonObject) jsonParser.parse(userDetailsBytesToStrings);
        System.out.println("Output : " + jsonObjectOutput);

    }
}
