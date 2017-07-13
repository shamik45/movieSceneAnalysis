package com.shamik.dev.cts.labweek.movieAnalysis;

import com.shamik.dev.cts.labweek.movieAnalysis.mpx.auth.tokenGenerator;
import com.shamik.dev.cts.labweek.movieAnalysis.mpx.services.FtpConnector;
import com.theplatform.data.notification.api.client.NotificationClient;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by shamik.shah on 3/20/17.
 */



public class Runner
{
    final static org.slf4j.Logger log = LoggerFactory.getLogger(Runner.class);

    public static void main(String args[]){

        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        NotificationPoller nfPoller = (NotificationPoller) context.getBean("NotificationPoller");
        nfPoller.start();


        /*String onlyFileName = "/videoInput/ShamikShah/102/483/output-1499806122446.fs";
        //String onlyFileName = "aotg/input.txt";

        FtpConnector ftp = (FtpConnector) context.getBean("FtpConnector");
        ftp.getFile(onlyFileName);
        */

    }


}
