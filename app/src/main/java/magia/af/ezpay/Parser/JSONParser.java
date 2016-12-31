package magia.af.ezpay.Parser;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import magia.af.ezpay.Utilities.DialogMaker;

/**
 * Created by Android Developer on 12/17/2016.
 */

public class JSONParser {
  private int resCode;
  private int connectionTimeOut;
  private int readTimeOut;
  private String url;
  private String requestMethod;
  private boolean output;
  private boolean input;
  private boolean userInteraction;
  private String requestPropertyKey;
  private String requestPropertyValue;
  private String jsonValue;
  private Execute execute;
  private String authorization;
  public static final String GET = "GET";
  public static final String POST = "POST";
  public static final String PUT = "PUT";
  public static final String PATCH = "PATCH";
  public static final String DELETE = "DELETE";
  public static final String COPY = "COPY";
  public static final String HEAD = "HEAD";
  public static final String OPTIONS = "OPTIONS";
  public static final String LINK = "LINK";
  public static final String UNLINK = "UNLINK";
  public static final String PURGE = "PURGE";
  public static final String LOCK = "LOCK";
  public static final String UNLOCK = "UNLOCK";
  public static final String PROPFIND = "PROPFIND";
  public static final String VIEW = "VIEW";

  public JSONParser() {
  }

  public static JSONParser connect(String url) {
    JSONParser parser = new JSONParser();
    parser.setUrl(url);
    return parser;
  }

  public String getAuthorization() {
    return authorization;
  }

  public void setAuthorization(String authorization) {
    this.authorization = authorization;
  }


  public int getReadTimeOut() {
    return readTimeOut;
  }

  public int getConnectionTimeOut() {
    return connectionTimeOut;
  }

  public String getRequestPropertyKey() {
    return requestPropertyKey;
  }

  public String getRequestPropertyValue() {
    return requestPropertyValue;
  }

  public String getRequestMethod() {
    return requestMethod;
  }

  public void setRequestMethod(String requestMethod) {
    this.requestMethod = requestMethod;
  }

  //    public String connect(String url) {
//        this.url = url;
//        StringBuilder sb = null;
//        try {
//            URL mUrl = new URL(url);
//            HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
//            InputStream in = connection.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//            sb = new StringBuilder();
//
//            String line = "";
//            try {
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    in.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        assert sb != null;
//        return sb.toString();
//    }


  private String getUrl() {
    return url;
  }

  private void setUrl(String url) {
    this.url = url;
  }

  private String connect(String url, String requestMethod, int readTimeOut, int connectionTimeOut, String authorization, String json) {
    StringBuilder sb = new StringBuilder();
    try {
      URL apiUrl = new URL(url);
      HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
      connection.setRequestMethod(requestMethod);
      connection.setConnectTimeout(connectionTimeOut);
      connection.setReadTimeout(readTimeOut);
      connection.setRequestProperty("Content-Type", "application/json");
      if (authorization != null) {
        connection.setRequestProperty("Authorization", "bearer " + authorization);
      }

      if (json != null) {
        OutputStream outputStream = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        writer.write(json);
        writer.flush();
        writer.close();
        outputStream.close();
        Log.e("Response Code", "connect: " + connection.getResponseCode());
        setResCode(connection.getResponseCode());
      }
      if (connection.getInputStream() != null) {
        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line = null;
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
      } else {
        return null;
      }
      if (connection.getResponseCode() == 200 || connection.getResponseCode() == 201) {
        if (sb.toString() != null || !sb.toString().isEmpty()) {
          return sb.toString();
        } else {
          return null;
        }
      } else {
        return "Error With Code " + connection.getResponseCode();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public int getResultCode() {
    return resCode;
  }

  private void setResCode(int resCode) {
    this.resCode = resCode;
  }

  public void setConnectionTimeOut(int connectionTimeOut) {
    this.connectionTimeOut = connectionTimeOut;
  }

  public void setReadTimeOut(int readTimeOut) {
    this.readTimeOut = readTimeOut;
  }

  public void setOutput(boolean output) {
    this.output = output;
  }

  public void setInput(boolean input) {
    this.input = input;
  }

  private String getJsonValue() {
    return jsonValue;
  }

  private void setJsonValue(String jsonValue) {
    this.jsonValue = jsonValue;
  }

  public void setJson(String jsonValue) {
    this.jsonValue = jsonValue;
  }

  public interface Execute {
    void onPreExecute();
    void onPostExecute(String s);
  }

  public void execute(Execute execute) {
    new Connect(execute, getUrl(), getRequestMethod(), getReadTimeOut(), getConnectionTimeOut(), getAuthorization(), getJsonValue()).execute();
  }

  private class Connect extends AsyncTask<Void, Void, String> {
    private Execute execute;
    private String url;
    private String method;
    private int readTimeout;
    private int connectionTimeOut;
    private String authorization;
    private String json;

    public Connect(Execute execute, String url, String method, int readTimeOut, int connectionTimeOut, String authorization, String json) {
      this.execute = execute;
      this.url = url;
      this.method = method;
      this.readTimeout = readTimeOut;
      this.connectionTimeOut = connectionTimeOut;
      this.authorization = authorization;
      this.json = json;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      execute.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
      return connect(url, method, readTimeout, connectionTimeOut, authorization, json);
    }

    @Override
    protected void onPostExecute(String s) {
      if (s != null) {
        execute.onPostExecute(s);
      }
      super.onPostExecute(s);
    }
  }
}
