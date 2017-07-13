package com.shamik.dev.cts.labweek.movieAnalysis.mpx.dataobjects.custom;

import java.io.Serializable;

/**
 * Created by shamik.shah on 7/12/17.
 */
public class ImageInfoContainer implements Serializable
{

    String mediaId;

    public String getMediaId()
    {
        return mediaId;
    }

    public void setMediaId(String mediaId)
    {
        this.mediaId = mediaId;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    String accountId;

    String image;



}
