package magia.af.ezpay.Parser;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

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

  public String connect(String url) {
    this.url = url;
    StringBuilder sb = null;
    try {
      URL mUrl = new URL(url);
      HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
      InputStream in = connection.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      sb = new StringBuilder();

      String line = "";
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
    } catch (IOException e) {
      e.printStackTrace();
    }
    assert sb != null;
    return sb.toString();
  }

  public String connect(String url, String requestMethod, int readTimeOut, int connectionTimeOut, String authorization, String json) {
    StringBuilder sb = new StringBuilder();
    try {
      URL apiUrl = new URL(url);
      HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
      connection.setRequestMethod(requestMethod);
      connection.setConnectTimeout(connectionTimeOut);
      connection.setReadTimeout(readTimeOut);
      connection.setRequestProperty("Content-Type", "application/json");
      if (authorization != null) {
        connection.setRequestProperty("Authorization", authorization);
      }

      if (json != null) {
        OutputStream outputStream = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        writer.write(json);
        writer.flush();
        writer.close();
        outputStream.close();
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
        return "";
      }
      if (connection.getResponseCode() == 200 || connection.getResponseCode() == 201) {
        if (sb.toString() != null || !sb.toString().isEmpty()) {
          return sb.toString();
        } else {
          return "";
        }
      } else {
        return "Error With Code " + connection.getResponseCode();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
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

  public void setUserInteraction(boolean userInteraction) {
    this.userInteraction = userInteraction;
  }

  public void setRequestPropertyKey(String requestPropertyKey) {
    this.requestPropertyKey = requestPropertyKey;
  }

  public void setRequestPropertyValue(String requestPropertyValue) {
    this.requestPropertyValue = requestPropertyValue;
  }

  public class Connect extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... params) {
      return null;
    }

    @Override
    protected void onPostExecute(String s) {
      setJsonValue(s);
      super.onPostExecute(s);
    }
  }

  private String getJsonValue() {
    return jsonValue;
  }

  public void setJsonValue(String jsonValue) {
    this.jsonValue = jsonValue;
  }
}
