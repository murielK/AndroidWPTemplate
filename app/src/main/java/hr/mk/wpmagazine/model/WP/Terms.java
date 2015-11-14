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

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Mur0 on 3/23/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Terms extends RealmObject {

    private RealmList<Category> category;

    public RealmList<Category> getCategory() {
        return category;
    }

    public void setCategory(RealmList<Category> category) {
        this.category = category;
    }
}
