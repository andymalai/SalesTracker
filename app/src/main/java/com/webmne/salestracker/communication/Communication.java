package com.webmne.salestracker.communication;

import java.io.Serializable;

/**
 * Created by vatsaldesai on 11-10-2016.
 */
public class Communication implements Serializable {


    /**
     * Title : test by services men2
     * Attachment : download.pdf
     */

    private String Title;
    private String Attachment;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getAttachment() {
        return Attachment;
    }

    public void setAttachment(String Attachment) {
        this.Attachment = Attachment;
    }
}
