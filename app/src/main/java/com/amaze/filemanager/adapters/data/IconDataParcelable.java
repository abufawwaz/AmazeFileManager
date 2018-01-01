package com.amaze.filemanager.adapters.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

/**
 * Saves data on what should be loaded as an icon for LayoutElementParcelable
 *
 * @author Emmanuel Messulam <emmanuelbendavid@gmail.com>
 *         on 6/12/2017, at 17:52.
 */
public class IconDataParcelable implements Parcelable {

    public static final int IMAGE_RES = 0, IMAGE_FROMFILE = 1;

    public final int type;
    public final String path;
    public final @DrawableRes int image;
    public final @DrawableRes int loadingImage;

    public IconDataParcelable(int type, @DrawableRes int img) {
        if(type == IMAGE_FROMFILE) throw new IllegalArgumentException();
        this.type = type;
        this.image = img;
        this.loadingImage = -1;
        this.path = null;
    }

    public IconDataParcelable(int type, String path, @DrawableRes int loadingImages) {
        if(type == IMAGE_RES) throw new IllegalArgumentException();
        this.type = type;
        this.path = path;
        this.loadingImage = loadingImages;
        this.image = -1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(type);
        parcel.writeString(path);
        parcel.writeInt(image);
        parcel.writeInt(loadingImage);
    }

    public IconDataParcelable(Parcel im) {
        type = im.readInt();
        path = im.readString();
        image = im.readInt();
        loadingImage = im.readInt();
    }

    public static final Parcelable.Creator<IconDataParcelable> CREATOR =
            new Parcelable.Creator<IconDataParcelable>() {
                public IconDataParcelable createFromParcel(Parcel in) {
                    return new IconDataParcelable(in);
                }

                public IconDataParcelable[] newArray(int size) {
                    return new IconDataParcelable[size];
                }
            };

}
