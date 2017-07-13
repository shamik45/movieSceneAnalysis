package com.shamik.dev.cts.labweek.movieAnalysis.mpx.auth;

import com.theplatform.module.authentication.client.AuthenticationClient;
import org.bouncycastle.util.encoders.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//import org.apache.http.auth.AUTH;


/**
 * Created by shamik.shah on 3/13/17.
 */
@Component
public class tokenGenerator
{
    private String username;
    private String password;

    final static Logger log = LoggerFactory.getLogger(tokenGenerator.class);

    AuthenticationClient authClient;

    private String account;


    void generateToken(){

        authClient = new AuthenticationClient(
                "https://identity.auth.theplatform.com/idm",
                username,
                password,
                new String[]{account}
        );

        String token = "";

        StringBuilder result = new StringBuilder();
        String urlToRead = "https://identity.auth.theplatform.com/idm/web/Authentication/signIn?schema=1.0&form=json&pretty=true&duration=28800000&_idleTimeout=3600000";


        try
        {
            log.info("calling auth url");
            URL url = new URL(urlToRead);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");


            String authString = this.username + ":" + this.password;
            log.info(authString);
            byte[] authEncBytes = Base64.encode(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            conn.setRequestProperty("Authorization", "Basic " + authStringEnc);

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null)
            {
                result.append(line);
            }
            rd.close();
        }
        catch (IOException ioe)
        {

            //log.info("IOException calling url " + ioe.toString());
        }

        //log.info("result from signIn " + result.toString());

        JSONObject resultJson = new JSONObject(result.toString());
        //log.info("signIn response " + resultJson);
        token = ((JSONObject) resultJson.get("signInResponse")).getString("token");

        //log.info("extracted token is " + token);


        //return "wXmpmzI23OfAgV0IEWqa4TA0kACe0LDS";
        this.token= token;
    }

    public AuthenticationClient getAuthClient(){

        return authClient;

    }


    public String getToken()
    {
        return token;
    }

    private String token;


    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }

    public void setAccount(String account)
    {
        this.account = account;
    }

    public String getAccount()
    {
        return account;
    }
}
