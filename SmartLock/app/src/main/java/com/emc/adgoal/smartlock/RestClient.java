package com.emc.adgoal.smartlock;

import android.os.StrictMode;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ravoop on 9/15/2016.
 */
public class RestClient {

    public RestClient(String url)
    {
        this.fullUrl = url;
        // Expected input : http://ip:port/file
        protocol = url.substring(0, url.indexOf(':'));
        String temp = url.substring(url.indexOf(':'));
        temp = temp.substring(3); // Remove "//"

        destIp = temp.substring(0, temp.indexOf(':'));

        temp = temp.substring(temp.indexOf(':') + 1);
        destPort = Integer.parseInt(temp.substring(0, temp.indexOf('/')));

        requestUrl = temp.substring(temp.indexOf('/') + 1);

        /*Log.i(module, "Protocol: " + protocol);
        Log.i(module, "Host: " + destIp);
        Log.i(module, "Port: " + destPort);
        Log.i(module, "Base URL: " + requestUrl);*/
    }

    public JSONObject doGet(Map<String, String> headers, Map<String, String> requestParams) throws Exception
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        URL url = null;
        HttpURLConnection connection = null;
        BufferedReader bis = null;
        try {
            StringBuffer fullUrlTmp = new StringBuffer(requestUrl);
            if ( null != requestParams )
            {
                fullUrlTmp.append("?");
                for ( String str : requestParams.keySet() )
                {
                    fullUrlTmp.append(str);
                    fullUrlTmp.append("=");
                    fullUrlTmp.append(requestParams.get(str));
                    fullUrlTmp.append("&");
                }
                // Strip the last comma
                fullUrlTmp.delete(fullUrlTmp.length()-1, fullUrlTmp.length());
            }
            url = new URL(protocol, destIp, destPort, fullUrlTmp.toString());
            //Log.i(module, url.toString());
            System.out.println(url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            if ( connection.getResponseCode() != 200 )
            {
                throw new Exception("Failed to connect");
            }

            bis = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer buf = new StringBuffer();
            String line = null;
            while( null != (line = bis.readLine()) )
            {
                buf.append(line);
            }
            bis.close();
            Log.i(module, "Buffer is: " + buf.toString());
            return new JSONObject(buf.toString());
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        finally
        {
            if ( null != connection) {
                connection.disconnect();
            }
        }
    }

    private String requestUrl;
    private String destIp;
    private int    destPort;
    private String fullUrl;
    private String protocol;
    private final static String module = "RstClnt";
}
