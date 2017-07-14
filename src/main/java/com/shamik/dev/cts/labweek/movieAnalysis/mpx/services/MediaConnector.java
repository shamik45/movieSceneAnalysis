package com.shamik.dev.cts.labweek.movieAnalysis.mpx.services;



import com.shamik.dev.cts.labweek.movieAnalysis.mpx.auth.tokenGenerator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by shamik.shah on 5/19/17.
 */
public class MediaConnector implements ApplicationContextAware
{
    private static ApplicationContext applicationContext = null;

    //String MEDIA_URL = "http://data.media2.theplatform.com/media/data/Media?schema=1.9.0&searchSchema=1.0.0&form=cjson&fields=guid";

    final static org.slf4j.Logger log = LoggerFactory.getLogger(MediaConnector.class);
    private String mediaUrl;


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }


    public String getMediaVideoUrl(String id){

        tokenGenerator tkgn = (tokenGenerator) applicationContext.getBean("TokenGenerator");
        String token = tkgn.getToken();

        String[] idToken = id.split("/");
        String idToUse = idToken[idToken.length - 1 ];

        String mediaUrlToCall = mediaUrl + "&token=" + token + "&byId=" + idToUse;

        String url = "";

        System.out.println("invoking media at " + mediaUrlToCall);

        String mediaPayload = makeHttpCall(mediaUrlToCall);

        JSONObject mediaJson =  new JSONObject(mediaPayload);

        int numOfMediaFiles =   ((JSONObject)mediaJson.getJSONArray("entries").get(0)).getJSONArray("content").length();

        int loop;

        for(loop=0; loop < numOfMediaFiles; loop++){
            url = (String) ((JSONObject)((JSONObject) mediaJson.getJSONArray("entries").get(0)).getJSONArray("content").get(loop)).get("id");

            log.info("url in loop is " + url + "  loop is " + loop);

            if (url.endsWith(".fs")){
                break;
            }

        }
        //url = (String) ((JSONObject)((JSONObject) mediaJson.getJSONArray("entries").get(0)).getJSONArray("content").get(0)).get("url");

        log.info("guid for media for cid is = " + url);

        return url;
    }


    public String makeHttpCall(String httpEndPoint){

        StringBuilder result = new StringBuilder();

        try
        {
            URL url = new URL(httpEndPoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null)
            {
                result.append(line);
            }
            rd.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result.toString();
    }

    public void setMediaUrl(String mediaUrl)
    {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaUrl()
    {
        return mediaUrl;
    }
}
