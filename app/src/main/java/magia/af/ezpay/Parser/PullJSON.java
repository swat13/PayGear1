package magia.af.ezpay.Parser;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PullJSON {
  private String apiUrl;
  private String json;
  private String method;
  private String tokenIfExist;
  private String token;

  public PullJSON(String apiUrl, String json, String method ,String tokenIfExist) {
    this.apiUrl = apiUrl;
    this.json = json;
    this.method = method;
    this.tokenIfExist = tokenIfExist;
  }

  public String getResponse() {
    try {

      URL url = new URL(apiUrl);
      Log.e("API Url", "URL: " + url);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setDoOutput(true);
      httpConn.setDoInput(true);
      httpConn.setAllowUserInteraction(false);
      httpConn.setRequestMethod(method);
      httpConn.setConnectTimeout(10000);
      httpConn.setReadTimeout(10000);
      httpConn.setRequestProperty("Content-Type", "application/json");
      if (tokenIfExist != null) {
        httpConn.setRequestProperty("Authorization", "bearer " + tokenIfExist);
      }

      OutputStream os = httpConn.getOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

      Log.e("Raw JSON format", "JSON: " + json);
      writer.write(json);
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.w("ResultCode", "Code: " + resCode);
      if (resCode == 200) {
        Log.e("ResultCode", "Code: " + resCode);
      } else
        Log.e("Error", "an error has occurred");
      InputStream in = httpConn.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      StringBuilder sb = new StringBuilder();

      String line;
      try {
        while ((line = reader.readLine()) != null) {
          sb.append(line);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      Log.e("PARSE DATA", sb.toString());
      JSONObject jsonObject = new JSONObject(sb.toString());
      token = jsonObject.getString("access_token");
    } catch (IOException | JSONException e) {
      e.printStackTrace();
    }
    String outputString;
    if (token!=null){
      outputString = token;
    }
    else {
      outputString = json;
    }
    return outputString;
  }
}
