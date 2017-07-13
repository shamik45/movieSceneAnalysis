package com.shamik.dev.cts.labweek.movieAnalysis.processor;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.util.IOUtils;
import com.google.gson.Gson;
import com.shamik.dev.cts.labweek.movieAnalysis.mpx.dataobjects.custom.ImageInfoContainer;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by shamik.shah on 7/11/17.
 */
public class AWSRekognitionProcessor implements ApplicationContextAware, MessageListener
{

    private static ApplicationContext applicationContext = null;

    Gson gson = new Gson();

    final static org.slf4j.Logger log = LoggerFactory.getLogger(WorkflowNotifProcessor.class);

    private final JmsTemplate jmsTemplate;

    AmazonRekognition rekognitionClient;

    AWSCredentials credentials;

    public String getOutputQueue()
    {
        return outputQueue;
    }

    public void setOutputQueue(String outputQueue)
    {
        this.outputQueue = outputQueue;
    }

    String outputQueue;

    //private int threadNum = 0;

    public AWSRekognitionProcessor(JmsTemplate jmsTemplate)
    {
        this.jmsTemplate = jmsTemplate;
        //this.threadNum = threadNum;


    }

    public void init()
    {
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
                    + "Please make sure that your credentials file is at the correct "
                    + "location (/Users/userid.aws/credentials), and is in a valid format.",
                    e);
        }

        rekognitionClient = AmazonRekognitionClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_2)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();


    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onMessage(Message message)
    {

        Hashtable<String, Integer> labelFrequency = new Hashtable<String, Integer>();

        ActiveMQObjectMessage amgObj;

        log.info("got message in aws processor");

        log.info("Received object of type in aws" + message.getClass().getName());

        amgObj= (ActiveMQObjectMessage) message;

        try {

            ImageInfoContainer ic = (ImageInfoContainer)amgObj.getObject();

            String base64String = ic.getImage();

            base64String = base64String.replace("data:image/jpeg;base64,", "");
            //System.out.println(base64String);

            byte[] imagedata= Base64.getDecoder().decode(base64String);

            ByteBuffer imageBytes;

            InputStream is = new ByteArrayInputStream(imagedata);
            imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(is));


            DetectLabelsRequest request = new DetectLabelsRequest()
                    .withImage(new Image()
                                    .withBytes(imageBytes))
                    .withMaxLabels(5)
                    .withMinConfidence(75F);

            DetectLabelsResult result = rekognitionClient.detectLabels(request);

            List<Label> labels = result.getLabels();
            if (labels.size() > 0)
            {
                //System.out.println("Detected labels for " + photoFile);

                LinkedList<String> scrubLabels = new LinkedList<String>();

                for (Label label : labels)
                {
                    scrubLabels.add(label.getName());
                }

                //create a JSON to be posted to elastic search
                for (String scrubLabel : scrubLabels)
                {
                    HashMap<String, String> jsonMedia = new HashMap<String, String>();
                    jsonMedia.put("id", ic.getMediaId());
                    jsonMedia.put("account", ic.getAccountId());
                    jsonMedia.put("label", scrubLabel);

                    log.info(gson.toJson(jsonMedia));

                    jmsTemplate.convertAndSend(outputQueue, gson.toJson(jsonMedia));
                }
            }  else {
                log.info("Not reaching out to ES as the lable list is empty");
            }



        } catch (Exception e) {
            e.printStackTrace();
        }


    }



}
