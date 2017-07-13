package com.shamik.dev.cts.labweek.movieAnalysis.mpx.services;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.*;

/**
 * Created by shamik.shah on 7/11/17.
 */
public class FtpConnector implements ApplicationContextAware
{
    private static ApplicationContext applicationContext = null;

    final static org.slf4j.Logger log = LoggerFactory.getLogger(FtpConnector.class);

    private String username;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    private String password;
    private String domain;


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    public String getFile(String fileName)
    {

        FTPClient ftpClient = new FTPClient();
        StringBuilder responseStrBuilder = new StringBuilder();

        InputStream returnInputStream = null;
        try
        {

            ftpClient.connect(domain, 21);
            ftpClient.login(username, password);
            log.info("FTP logged in");
            FTPFile[] ftpFiles = ftpClient.listFiles();

            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            String remoteFile1 = fileName;
            returnInputStream = ftpClient.retrieveFileStream(remoteFile1);

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(returnInputStream));

            String inputStr = "";

            while ((inputStr = streamReader.readLine()) != null)
            {
                responseStrBuilder.append(inputStr);
            }

        }
        catch (IOException ex)
        {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }

        String returnString = new String(responseStrBuilder);
        //log.info("the entire file is \n" + returnString);

        return returnString;
    }
}
