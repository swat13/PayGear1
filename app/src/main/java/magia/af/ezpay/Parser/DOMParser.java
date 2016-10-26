package magia.af.ezpay.Parser;

import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.spec.InvalidKeySpecException;

public class DOMParser {

    public DOMParser(String token) {
        this.token = token;
    }

    public DOMParser() {
    }


    private String mainUrl = "http://my.tci.ir:8000/";
    private String mainUrl2 = "http://79.175.176.229:5000/";
    private String token = "";

    /**
     * @param number
     * @return
     */
    public RSSItem getBillInfo(String number) {

        try {

            URL url = new URL(mainUrl + "utility/single-bill/");
            Log.e("1111111", "doInBackground: " + url);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setAllowUserInteraction(false);
            httpConn.setRequestMethod("POST");
            httpConn.setConnectTimeout(20000);
            httpConn.setReadTimeout(20000);
            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setRequestProperty("Authorization", "JWT " + token);

            OutputStream os = httpConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            String request = "{\n" +
                    "\"tel_no\" : \"21" + number + "\"\n" +
                    "}";
            Log.e("999999999", "activateSong: " + request);
            writer.write(request);
            writer.flush();
            writer.close();
            os.close();

            int resCode = httpConn.getResponseCode();
            Log.e("0000000", "doInBackground: " + resCode);
            if (resCode == 400) {
//                return null;
            }

            InputStream in = httpConn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();

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

            Log.e("@@@@@@", sb.toString());
            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONObject jsonObject1 = jsonObject.getJSONObject("bill_info");
            RSSItem rssItem = new RSSItem();
            try {
                rssItem.setAmount(jsonObject1.getLong("amount"));
                rssItem.setPayDate((long) 1474370347);
                rssItem.setStatus(jsonObject1.getLong("status"));
                rssItem.setPayId(jsonObject1.getLong("payId"));
//                rssItem.set_AdslActive(true);
//                rssItem.set_StatusBandwidth("20kb/s");
//                rssItem.set_AdslData("50 GB");
                rssItem.setBillId(jsonObject1.getLong("billId"));
                if (!jsonObject1.isNull("message"))
                    rssItem.setMessage(jsonObject1.getString("message"));
                rssItem.setTelNo(jsonObject1.getLong("telNo"));
                return rssItem;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param number
     * @param password
     * @return
     **/

    public String logInUser(String number, String password) {

        try {

            URL url = new URL(mainUrl + "accounts/api-token-auth/");
            Log.e("1111111", "doInBackground: " + url);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setAllowUserInteraction(false);
            httpConn.setRequestMethod("POST");
            httpConn.setConnectTimeout(20000);
            httpConn.setReadTimeout(20000);
            httpConn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = httpConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            String request = "{\n" +
                    "\"username\" : \"09" + number + "\",\n" +
                    "\"password\" : \"" + password + "\"\n" +
                    "}";
            Log.e("999999999", "activateSong: " + request);
            writer.write(request);
            writer.flush();
            writer.close();
            os.close();

            int resCode = httpConn.getResponseCode();
            Log.e("0000000", "doInBackground: " + resCode);
            if (resCode == 400) {
                return "wrong";
            } else if (resCode == 500) {
                return "NI";
            }

            InputStream in = httpConn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();

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

            Log.e("@@@@@@", sb.toString());
            JSONObject jsonObject = new JSONObject(sb.toString());
            boolean active = jsonObject.getBoolean("active");
            Log.e("zzzzzzz", String.valueOf(active));
            if (active) {
                String token = jsonObject.getString("token");
                Log.e("ttt", token);
                return token;

            } else if (!active) {
                return "Not Active";

            }


        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "NI";
    }

    /**
     * @param phoneNumber
     * @return
     **/
    public RSSItem getRefreshBillInfo(String phoneNumber) {

        try {

            URL url = new URL(mainUrl + "utility/single-bill/");
            Log.e("1111111", "doInBackground: " + url);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setAllowUserInteraction(false);
            httpConn.setRequestMethod("POST");
            httpConn.setConnectTimeout(13000);
            httpConn.setReadTimeout(13000);
            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setRequestProperty("Authorization", "JWT " + token);

            OutputStream os = httpConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            String request = "{\"tel_no\":\"" + phoneNumber + "\"}";
            Log.e("999999999", "activateSong: " + request);
            writer.write(request);
            writer.flush();
            writer.close();
            os.close();

            int resCode = httpConn.getResponseCode();
            Log.e("0000000", "doInBackground: " + resCode);
            if (resCode == 400) {
                return null;
            }

            InputStream in = httpConn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();

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

            Log.e("@@@@@@", sb.toString());
            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONObject jsonObject1 = jsonObject.getJSONObject("bill_info");
            RSSItem rssItem = new RSSItem();
            try {
                rssItem.setAmount(jsonObject1.getLong("amount"));
                rssItem.setPayDate((long) 1474370347);
                rssItem.setStatus(jsonObject1.getLong("status"));
                rssItem.setPayId(jsonObject1.getLong("payId"));
//                rssItem.set_AdslActive(true);
//                rssItem.set_StatusBandwidth("20kb/s");
//                rssItem.set_AdslData("50 GB");
                rssItem.setBillId(jsonObject1.getLong("billId"));
                if (!jsonObject1.isNull("message"))
                    rssItem.setMessage(jsonObject1.getString("message"));
                rssItem.setTelNo(jsonObject1.getLong("telNo"));
                return rssItem;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param phoneNumber
     * @return
     **/
    public JSONObject getAdslInfo(String phoneNumber) {

        try {

            URL url = new URL(mainUrl + "utility/adsl/list/" + phoneNumber + "/");
            Log.e("1111111", "doInBackground: " + url);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoInput(true);
            httpConn.setAllowUserInteraction(false);
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(13000);
            httpConn.setReadTimeout(13000);
            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setRequestProperty("Authorization", "JWT " + token);

            int resCode = httpConn.getResponseCode();
            Log.e("0000000", "doInBackground: " + resCode);
            if (resCode == 400) {
                return null;
            }

            InputStream in = httpConn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();

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


            Log.e("$$$$$$$", "getAdslInfo:aa " + sb.toString());
            Log.e("@@@@@@", sb.toString());
            JSONObject jsonObject = new JSONObject(sb.toString());
            String js = jsonObject.getString("errcode");
            int b = Integer.parseInt(js);
            if (b == 0) {

                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("errmsg");
                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                    return jsonObject1;
                } catch (Exception e) {

                }
                return null;
            } else if (b == 1) {
                return null;

            }


        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param phoneNumber
     * @return
     **/
    public JSONObject getAdslStatus(String phoneNumber) {

        try {

            URL url = new URL(mainUrl + "utility/adsl/status/" + phoneNumber + "/");
            Log.e("1111111", "doInBackground: " + url);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoInput(true);
            httpConn.setAllowUserInteraction(false);
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(13000);
            httpConn.setReadTimeout(13000);
            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setRequestProperty("Authorization", "JWT " + token);

            int resCode = httpConn.getResponseCode();
            Log.e("0000000", "doInBackground: " + resCode);
            if (resCode == 400) {
                return null;
            }

            InputStream in = httpConn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();

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


            Log.e("@@@@@@", sb.toString());
            JSONObject jsonObject = new JSONObject(sb.toString());
            String js = jsonObject.getString("errcode");
            int b = Integer.parseInt(js);
            if (b == 0) {
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("errmsg");
                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                    return jsonObject1;
                } catch (Exception e) {

                }

                return null;
            } else if (b == 1) {
                return null;

            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param phoneNumber
     * @param number
     * @param pan
     * @param pin2
     * @param key
     * @return
     * @throws InvalidKeySpecException The timeout should be more than 30 second
     **/
    public JSONObject payBill(String phoneNumber, String number, String pan, String pin2, String key) throws InvalidKeySpecException {

        try {

            String PPP = pan + ":" + pin2;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", "09" + phoneNumber);
            jsonObject.put("number", number);
//            jsonObject.put("payload", RSA.encryptWithKey(key, PPP));

            URL url = new URL(mainUrl + "payment/quickpay/");
            Log.e("1111111", "doInBackground: " + url);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setAllowUserInteraction(false);
            httpConn.setRequestMethod("POST");
            httpConn.setConnectTimeout(30000);
            httpConn.setReadTimeout(30000);
            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setRequestProperty("Authorization", "JWT " + token);
            Log.e("((((((((((", "payBill: " + token);

            OutputStream os = httpConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            Log.e("999999999", "activateSong: " + jsonObject.toString());
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();
            os.close();
            Log.e("1111111111", "doInBackground: ******");
            int resCode = httpConn.getResponseCode();
            Log.e("0000000", "doInBackground: " + resCode);
            if (resCode == 400) {
                return null;
            }

            InputStream in = httpConn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();

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

            Log.e("@@@@@@", sb.toString());
            JSONObject jsonObject1 = new JSONObject(sb.toString());
            return jsonObject1;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * @param username
     * @param number
     * @return
     **/
    public String Send_IVR_Registration_Request(String username, String number) {

        try {
            URL url = new URL(mainUrl + "accounts/phones/create/");
            Log.e("1111111", "doInBackground: " + url);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setAllowUserInteraction(false);
            httpConn.setRequestMethod("POST");
            httpConn.setConnectTimeout(10000);
            httpConn.setReadTimeout(10000);
            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setRequestProperty("Authorization", "JWT " + token);

            OutputStream os = httpConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            Log.e("999999999", "TOKEN: " + token);
            String request = "{\n" +
                    "\"username\" : \"09" + username + "\",\n" +
                    "\"number\" : \"09" + username + "\"\n" +  // that's should be the phone number that you want to follow but for test i set the username
                    "}";

            Log.e("999999999", "activateSong: " + request);
            writer.write(request);
            writer.flush();
            writer.close();
            os.close();

            int resCode = httpConn.getResponseCode();
            Log.e("0000000", "doInBackground: " + resCode);
            if (resCode == 400) {
                return "wrong";
            }

            InputStream in = httpConn.getInputStream();
            Log.e("1111111", "doInBackground: " + resCode);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            Log.e("22222", "doInBackground: " + resCode);
            StringBuilder sb = new StringBuilder();

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

            Log.e("@@@@@@", sb.toString());
            return "Success";
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "NI";

    }

    /**
     * @param username
     * @param number
     * @param code
     * @return
     **/
    public String Phone_IVR_Verification(String username, String number, String code) {

        try {
            URL url = new URL(mainUrl + "accounts/phone/activation/verify/");
            Log.e("1111111", "doInBackground: " + url);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setAllowUserInteraction(false);
            httpConn.setRequestMethod("POST");
            httpConn.setConnectTimeout(10000);
            httpConn.setReadTimeout(10000);
            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setRequestProperty("Authorization", "JWT " + token);

            OutputStream os = httpConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            String request = "{\n" +
                    "\"username\" : \"09" + username + "\",\n" +
                    "\"number\" : \"" + "09127764165" + "\",\n" +
                    "\"activation_code\" : \"" + code + "\"\n" +
                    "}";

            Log.e("999999999", "activateSong: " + request);
            writer.write(request);
            writer.flush();
            writer.close();
            os.close();

            int resCode = httpConn.getResponseCode();
            Log.e("0000000", "doInBackground: " + resCode);
            if (resCode == 400) {
                return "wrong";
            }

            InputStream in = httpConn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();

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

            Log.e("@@@@@@", sb.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Success";

    }

    /**
     * @param phoneNumber
     * @param pass
     * @return
     **/
    public String register(String phoneNumber, String pass) {

        try {

            URL url = new URL(mainUrl + "accounts/users/");
            Log.e("1111111", "doInBackground: " + url);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setAllowUserInteraction(false);
            httpConn.setRequestMethod("POST");
            httpConn.setConnectTimeout(10000);
            httpConn.setReadTimeout(10000);
            httpConn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = httpConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            String request = "{" +
                    "\"user\":" +
                    "{\n" +
                    "\"username\" : \"09" + phoneNumber + "\",\n" +
                    "\"password\" : \"" + pass + "\"\n" +
                    "}}";

            Log.e("999999999", "activateSong: " + request);
            writer.write(request);
            writer.flush();
            writer.close();
            os.close();

            int resCode = httpConn.getResponseCode();
            Log.e("0000000", "doInBackground: " + resCode);
            if (resCode == 500) {
                return "NI";
            } else if (resCode == 409) {
                return "exist";
            }

            InputStream in = httpConn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();

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
            Log.e("@@@@@@", sb.toString());
            if (sb.toString().contains("exist"))
                return "exist";
            return "ok";

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "NI";

    }


    public String resendSMS(String phoneNumber) {

        try {

            URL url = new URL(mainUrl + "accounts/user/activation/resend/09" + phoneNumber);
            Log.e("1111111", "doInBackground: " + url);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setAllowUserInteraction(false);
            httpConn.setRequestMethod("PUT");
            httpConn.setConnectTimeout(10000);
            httpConn.setReadTimeout(10000);
            httpConn.setRequestProperty("Content-Type", "application/json");

//            OutputStream os = httpConn.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//
//            String request = "{" +
//                    "\"user\":" +
//                    "{\n" +
//                    "\"Number\" : \"09" + phoneNumber + "\",\n" +
//                    "\"password\" : \"" + pass + "\"\n" +
//                    "}}";
//
//            Log.e("999999999", "activateSong: " + request);
//            writer.write(request);
//            writer.flush();
//            writer.close();
//            os.close();

            int resCode = httpConn.getResponseCode();
            Log.e("0000000", "doInBackground: " + resCode);
            if (resCode == 500) {
                return "NI";
            }

            InputStream in = httpConn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();

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
            Log.e("@@@@@@", sb.toString());
            return "ok";

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "NI";

    }

    /**
     * @param username
     * @param activeCode
     * @return
     **/
    public String Verify_Login_Activation_Code(String username, String activeCode) {

        try {

            URL url = new URL(mainUrl + "accounts/user/activation/verify/");
            Log.e("1111111", "doInBackground: " + url);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setAllowUserInteraction(false);
            httpConn.setRequestMethod("POST");
            httpConn.setConnectTimeout(10000);
            httpConn.setReadTimeout(10000);
            httpConn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = httpConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            String request = "{\n" +
                    "\"username\" : \"09" + username + "\",\n" +
                    "\"activation_code\" : \"" + activeCode + "\"\n" +
                    "}";

            Log.e("999999999", "activateSong: " + request);
            writer.write(request);
            writer.flush();
            writer.close();
            os.close();

            int resCode = httpConn.getResponseCode();
            Log.e("0000000", "doInBackground: " + resCode);
            if (resCode == 400) {
                return "wrong";
            }

            InputStream in = httpConn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();

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

            Log.e("@@@@@@", sb.toString());
            JSONObject jsonObject = new JSONObject(sb.toString());
            String token = jsonObject.getString("token");
            return token;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "NI";

    }

    /**
     * @param number
     * @return
     **/
    public String forgetPass(String number) {

        try {

            URL url = new URL(mainUrl + "accounts/user/password/" + "09" + number + "/");
            Log.e("1111111", "doInBackground: " + url);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setAllowUserInteraction(false);
            httpConn.setRequestMethod("PUT");
            httpConn.setConnectTimeout(20000);
            httpConn.setReadTimeout(20000);
            httpConn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = httpConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            String request = "{\n" +
                    "\"username\" : \"09" + number + "\"\n" +
                    "}";
            Log.e("999999999", "activateSong: " + request);
            writer.write(request);
            writer.flush();
            writer.close();
            os.close();

            int resCode = httpConn.getResponseCode();
            Log.e("0000000", "doInBackground: " + resCode);
            if (resCode == 400) {
                return "Failed";
            }

            InputStream in = httpConn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();

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

            Log.e("@@@@@@", sb.toString());


            /**
             * TODO: check if activated then return the token to Splash class
             *
             * */


        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Success";
    }

}
