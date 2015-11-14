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

import io.realm.RealmObject;

/**
 * Created by Mur0 on 3/4/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WPLinks extends RealmObject {

    private String self;
    private String archives;
    private String author;
    private String collection;
    private String replies;

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getArchives() {
        return archives;
    }

    public void setArchives(String archives) {
        this.archives = archives;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getReplies() {
        return replies;
    }

    public void setReplies(String replies) {
        this.replies = replies;
    }
}
