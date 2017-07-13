package com.shamik.dev.cts.labweek.movieAnalysis.processor;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by shamik.shah on 7/12/17.
 */
public class ElasticSearchProcessor implements ApplicationContextAware, MessageListener
{
    final static org.slf4j.Logger log = LoggerFactory.getLogger(ElasticSearchProcessor.class);

    private String httpEndPoint;

    public String getIndexName()
    {
        return indexName;
    }

    public void setIndexName(String indexName)
    {
        this.indexName = indexName;
    }

    private String indexName;

    public void setHttpEndPoint(String httpEndPoint)
    {
        this.httpEndPoint = httpEndPoint;
    }

    public String getHttpEndPoint()
    {
        return httpEndPoint;
    }

    public boolean indexWithES(String payload)
    {
        //System.out.println("got payload" + payload) ;

        String url = null;

        url = httpEndPoint + indexName + "/media";

        URL urlObj = null;

        try
        {
            urlObj = new URL(url);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        HttpURLConnection con = null;

        try
        {
            con = (HttpURLConnection)urlObj.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(payload);
            wr.flush();
            wr.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        StringBuffer response = null;
        try
        {
            int responseCode = con.getResponseCode();
            log.info("\nSending 'POST' request to URL : " + url);
            log.info("\nSending payload" + payload);
            log.info("Response Code : " + responseCode);


        }
        catch (IOException e)
        {
            e.printStackTrace();
        }



        return true;

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    private static ApplicationContext applicationContext = null;

    @Override
    public void onMessage(Message message)
    {

        ActiveMQTextMessage amgTxtObj;

        amgTxtObj= (ActiveMQTextMessage) message;

        try
        {
            indexWithES(amgTxtObj.getText());
        }
        catch (JMSException e)
        {
            e.printStackTrace();
        }
    }
}
