/*
 *
 *  *
 *  *  * ****************************************************************************
 *  *  * Copyright (c) 2015. Muriel Kamgang Mabou
 *  *  * All rights reserved.
 *  *  *
 *  *  * This file is part of project AndroidWPTemplate.
 *  *  * It can not be copied and/or distributed without the
 *  *  * express permission of Muriel Kamgang Mabou
 *  *  * ****************************************************************************
 *  *
 *  *
 *
 */

package hr.mk.wpmagazine.model.WP;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Mur0 on 3/4/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WPFeatureImage extends RealmObject {

    private String source;
    private Date date;
    private Date modified;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}

