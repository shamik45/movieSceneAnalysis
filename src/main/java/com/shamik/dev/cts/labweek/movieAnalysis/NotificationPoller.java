package com.shamik.dev.cts.labweek.movieAnalysis;

import com.shamik.dev.cts.labweek.movieAnalysis.mpx.auth.tokenGenerator;
import com.theplatform.data.notification.api.client.NotificationClient;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * Created by shamik.shah on 6/23/17.
 */

@Configuration
@PropertySource("classpath:/resources/application.properties")
public class NotificationPoller  implements ApplicationContextAware
{

    final static org.slf4j.Logger log = LoggerFactory.getLogger(NotificationPoller.class);

    private static ApplicationContext context = null;

    public String getWorkflowUrl()
    {
        return workflowUrl;
    }

    String workflowUrl;


    public static ApplicationContext getApplicationContext()
    {
        return context;
    }


    public void init()
    {
        log.info("context is" + context.toString());

    }

    public void start(){

        tokenGenerator tkgn = (tokenGenerator) context.getBean("TokenGenerator");

        log.info("workflow url is " + workflowUrl);

        NotificationClient wfNC = new NotificationClient(workflowUrl,
                tkgn.getAuthClient(), "movieAnalysis"
        );


        wfNC.setIncludeFields(true);

        //NotificationConsumer nc1 = (NotificationConsumer) context.getBean("NotificationConsumer");
        NotificationConsumer nc2 = (NotificationConsumer) context.getBean("NotificationConsumer");

        wfNC.addListener(nc2);
        //mediaNf.addListener(nc3);

        try
        {
            wfNC.start();
            //mediaNf.start();
        }
        catch (Exception e)
        {
            System.out.println("Unknown host exception: " + e.getMessage());
            return;
        }

    }


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.context = applicationContext;
    }

    public void setWorkflowUrl(String workflowUrl)
    {
        this.workflowUrl = workflowUrl;
    }
}
