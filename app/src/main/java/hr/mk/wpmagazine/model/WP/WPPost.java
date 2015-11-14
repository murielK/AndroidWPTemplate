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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Mur0 on 3/4/2015.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class WPPost extends RealmObject {

    @PrimaryKey
    private int ID;
    private String title;
    private String type;
    private String status;
    private String link;
    private Date date;
    private Date modified;
    private String format;
    private String guid;
    private String excerpt;
    private Terms terms;
    private String content;
    private boolean favorite;
    private WPAuthor author;
    private WPFeatureImage featured_image;

    public WPPost() {
    }

    @JsonCreator
    public WPPost(@JsonProperty("ID") int ID, @JsonProperty("content") String content, @JsonProperty("title") String title, @JsonProperty("type") String type, @JsonProperty("status") String status, @JsonProperty("link") String link, @JsonProperty("date") Date date, @JsonProperty("modified") Date modified, @JsonProperty("format") String format, @JsonProperty("guid") String guid, @JsonProperty("excerpt") String excerpt, @JsonProperty("terms") Terms terms, @JsonProperty("author") WPAuthor author, @JsonProperty("featured_image") WPFeatureImage featured_image) {
        this.ID = ID;
        this.title = title;
        this.type = type;
        this.status = status;
        this.link = link;
        this.date = date;
        this.modified = modified;
        this.format = format;
        this.guid = guid;
        this.excerpt = excerpt;
        this.terms = terms;
        this.author = author;
        this.featured_image = featured_image;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Terms getTerms() {
        return terms;
    }

    public void setTerms(Terms terms) {
        this.terms = terms;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public WPAuthor getAuthor() {
        return author;
    }

    public void setAuthor(WPAuthor author) {
        this.author = author;
    }

    public WPFeatureImage getFeatured_image() {
        return featured_image;
    }

    public void setFeatured_image(WPFeatureImage featured_image) {
        this.featured_image = featured_image;
    }
}
