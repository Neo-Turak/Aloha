package cn.nurasoft.aloha;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class LifeUtil implements Serializable {

    public String getCelcius() {
        return Celcius;
    }

    public void setCelcius(String celcius) {
        Celcius = celcius;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String Celcius;
   public  String date;

    public LifeUtil(String celcius, String date) {
        Celcius = celcius;
        this.date = date;
    }

}

