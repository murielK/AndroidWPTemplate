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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mur0 on 3/27/2015.
 */
public class ObjectParcel implements Parcelable {

    public static final Parcelable.Creator<ObjectParcel> CREATOR = new Parcelable.Creator<ObjectParcel>() {
        public ObjectParcel createFromParcel(Parcel source) {
            return new ObjectParcel(source);
        }

        public ObjectParcel[] newArray(int size) {
            return new ObjectParcel[size];
        }
    };
    public int ID;
    public String category;
    public int color;
    public int colorDark;
    public boolean fromNotification;

    public ObjectParcel(int ID, String category, int color, int colorDark) {
        this.ID = ID;
        this.category = category;
        this.color = color;
        this.colorDark = colorDark;
    }

    private ObjectParcel(Parcel in) {
        this.ID = in.readInt();
        this.category = in.readString();
        this.color = in.readInt();
        this.colorDark = in.readInt();
        this.fromNotification = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ID);
        dest.writeString(this.category);
        dest.writeInt(this.color);
        dest.writeInt(this.colorDark);
        dest.writeByte(fromNotification ? (byte) 1 : (byte) 0);
    }
}
