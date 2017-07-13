package com.shamik.dev.cts.labweek.movieAnalysis;

import com.shamik.dev.cts.labweek.movieAnalysis.mpx.dataobjects.custom.ProfileResultContainer;
import com.theplatform.data.notification.api.client.NotificationCommitter;
import com.theplatform.data.notification.api.client.NotificationListener;
import com.theplatform.data.notification.api.objects.Notification;
import com.theplatform.profile.data.workflow.api.objects.ProfileResult;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jms.core.JmsTemplate;

import java.util.HashMap;
import java.util.List;

public class NotificationConsumer implements NotificationListener, ApplicationContextAware
{
    final static org.slf4j.Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private static ApplicationContext applicationContext = null;


    JmsTemplate jmsTemplate;

    public String getQueueName()
    {
        return queueName;
    }

    public void setQueueName(String queueName)
    {
        this.queueName = queueName;
    }

    private String queueName;


    public NotificationConsumer(JmsTemplate jmsTemplate)
    {
        this.jmsTemplate = jmsTemplate;
    }


    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }


    public void init()
    {
        //ctx =new ClassPathXmlApplicationContext("beans.xml");
        log.info("context is" + applicationContext.toString());
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    public void receiveNotifications(List<Notification> list, NotificationCommitter notificationCommitter)
    {
        log.info("sent notification to queue");

        for (Notification notification : list)
        {
            log.info("got notified " + notification.getObjectType());

            if (notification.getObjectType().equalsIgnoreCase("ProfileResult")){

                ProfileResult newPr = (ProfileResult) notification.getEntry();
                ProfileResult oldPr = (ProfileResult) notification.getOldEntry();

                ProfileResultContainer prc = new ProfileResultContainer();

                prc.profileId = newPr.getProfileId();
                prc.mediaId = newPr.getMediaId();
                prc.currentStatus = newPr.getStatus().toString();
                if (oldPr != null)
                {
                    if(oldPr.getStatus() != null)
                    {
                        prc.oldStatus = oldPr.getStatus().toString();
                    }
                }
                prc.processError = newPr.getProcessError();
                prc.cid = notification.getCid();

                jmsTemplate.convertAndSend(queueName, prc);
            }
        }
        notificationCommitter.commit();
    }

    public void onException(Throwable throwable)
    {

    }
}