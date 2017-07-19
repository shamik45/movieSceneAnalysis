package com.shamik.dev.cts.labweek.movieAnalysis.processor;

import com.shamik.dev.cts.labweek.movieAnalysis.mpx.auth.tokenGenerator;
import com.shamik.dev.cts.labweek.movieAnalysis.mpx.dataobjects.custom.ImageInfoContainer;
import com.shamik.dev.cts.labweek.movieAnalysis.mpx.dataobjects.custom.ProfileResultContainer;
import com.shamik.dev.cts.labweek.movieAnalysis.mpx.services.FtpConnector;


import com.shamik.dev.cts.labweek.movieAnalysis.mpx.services.MediaConnector;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.*;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by shamik.shah on 6/26/17.
 */
public class WorkflowNotifProcessor  implements ApplicationContextAware, MessageListener
{
    private static ApplicationContext applicationContext = null;

    final static org.slf4j.Logger log = LoggerFactory.getLogger(WorkflowNotifProcessor.class);

    private final JmsTemplate jmsTemplate;
    private String accountName;

    public String getProfileId()
    {
        return profileId;
    }

    public void setProfileId(String profileId)
    {
        this.profileId = profileId;
    }

    private String profileId;

    public String getOutputResultQueue()
    {
        return outputResultQueue;
    }

    public void setOutputResultQueue(String elasticSearchQueueName)
    {
        this.outputResultQueue = elasticSearchQueueName;
    }

    private String outputResultQueue;

    public tokenGenerator getTg()
    {
        return tg;
    }

    public void setTg(tokenGenerator tg)
    {
        this.tg = tg;
    }

    private tokenGenerator tg;

    public WorkflowNotifProcessor(JmsTemplate jmsTemplate)
    {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onMessage(Message message)
    {
        ActiveMQObjectMessage amqobj = null;

        log.info("Received object of type " + message.getClass().getName());

        if(message.getClass().getName() == "org.apache.activemq.command.ActiveMQObjectMessage" )
        {
            amqobj = (ActiveMQObjectMessage) message;


            Object obj = null;

            try
            {
                obj = amqobj.getObject();
            }
            catch (JMSException e)
            {
                e.printStackTrace();
            }

            if (obj != null)
            {
                if (obj instanceof ProfileResultContainer)
                {

                    ProfileResultContainer prc = (ProfileResultContainer) obj;
                    log.info("the profile object recevd is " + prc.toString());

                    if (prc.currentStatus.equalsIgnoreCase("Processed"))
                    {
                        if (prc.getProfileId().toString().equals(profileId))
                        {
                            log.info("media id is " + prc.getMediaId());
                            log.info("token is " + tg.getToken());

                            MediaConnector mc = (MediaConnector) applicationContext.getBean("MediaConnector");

                            String fileLocation = mc.getMediaVideoUrl(prc.getMediaId().toString());
                            String mediaTitle = mc.getMediaTitle(prc.getMediaId().toString());

                            log.info("location is " + fileLocation);

                            String[] fileLocationComponents = fileLocation.split("/");

                            String onlyFileName = "";

                            for (int loop = 3; loop < fileLocationComponents.length; loop++)
                            {
                                onlyFileName = onlyFileName + "/" + fileLocationComponents[loop];
                            }

                            log.info("only file name is " + onlyFileName);

                            FtpConnector ftp = (FtpConnector) applicationContext.getBean("FtpConnector");
                            String filmStripFileContents = ftp.getFile(onlyFileName);

                            JSONParser parser = new JSONParser();
                            try
                            {
                                Object jsonObj = parser.parse(filmStripFileContents);
                                org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonObj;
                                org.json.simple.JSONArray thumbnails = (org.json.simple.JSONArray) jsonObject
                                        .get("thumbnails");

                                Iterator<String> iterator = thumbnails.iterator();
                                int count = 0;

                                while (iterator.hasNext())
                                {

                                    ImageInfoContainer ic = (ImageInfoContainer) applicationContext
                                            .getBean("ImageContainer");
                                    ic.setImage((String) iterator.next());
                                    ic.setAccountId(accountName);
                                    ic.setTitle(mediaTitle);
                                    ic.setMediaId(prc.getMediaId().toString());
                                    //log.info("stuuf in json " + (String)iterator.next());
                                    jmsTemplate.convertAndSend(outputResultQueue, ic);
                                    count++;
                                }
                            }
                            catch (ParseException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }
        } else {
            log.info("Received object of type " + message.getClass().getName() + " and will not process");

            log.info("message is " + message.toString());
        }
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    public String getAccountName()
    {
        return accountName;
    }
}
