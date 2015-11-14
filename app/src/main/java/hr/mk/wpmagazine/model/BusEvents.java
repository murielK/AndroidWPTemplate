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

package hr.mk.wpmagazine.model;


/**
 * Created by Mur0 on 3/15/2015.
 */
public class BusEvents {

    public Object object;
    public EvenType evenType;

    public BusEvents(Object object, EvenType evenType) {
        this.object = object;
        this.evenType = evenType;
    }

    public enum EvenType {

        CONNECTIVITY, GET_DATA_START, GET_DATA_DONE, GET_DATA_FAIL, DATA_BASE_UPDATED, DATA_BASE_IS_UP_TO_DATE, DATA_BASE_UPDATED_FAIL, GALLERY
    }

}
