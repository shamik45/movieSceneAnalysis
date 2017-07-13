package com.shamik.dev.cts.labweek.movieAnalysis.mpx.dataobjects.custom;

import java.io.Serializable;
import java.net.URI;


/**
 * Created by shamik.shah on 3/22/17.
 */
public class ProfileResultContainer implements Serializable
{
    public String cid;

    public URI getProfileId()
    {
        return profileId;
    }

    public void setProfileId(URI profileId)
    {
        this.profileId = profileId;
    }

    public URI getMediaId()
    {
        return mediaId;
    }

    public void setMediaId(URI mediaId)
    {
        this.mediaId = mediaId;
    }

    public String getCurrentStatus()
    {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus)
    {
        this.currentStatus = currentStatus;
    }

    public String getOldStatus()
    {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus)
    {
        this.oldStatus = oldStatus;
    }

    public String getProcessError()
    {
        return processError;
    }

    public void setProcessError(String processError)
    {
        this.processError = processError;
    }

    public URI profileId;
    public URI mediaId;
    public String currentStatus;
    public String oldStatus;
    public String processError;

    @Override
    public String toString(){
        return ("profileId=" + profileId
                + " mediaId=" + mediaId
                + " currentStatus=" + currentStatus
                + " oldStatus=" + oldStatus);

    }
}
