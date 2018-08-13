package com.myapp.miguel.collectonapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MIGUEL on 11/07/2018.
 */

public class UserInfo implements Parcelable{

    private String birth_date, country, email, gender, image_url, userLastName, userName;

    public UserInfo(){
    }

    public UserInfo(String birth_date, String country, String email, String gender, String image_url, String userLastName, String userName, String userId){
        this.birth_date = birth_date;
        this.country = country;
        this.email = email;
        this.gender = gender;
        this.image_url = image_url;
        this.userLastName = userLastName;
        this.userName = userName;
    }


    protected UserInfo(Parcel in) {
        birth_date = in.readString();
        country = in.readString();
        email = in.readString();
        gender = in.readString();
        image_url = in.readString();
        userLastName = in.readString();
        userName = in.readString();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setCountry(String country) {this.country = country;}

    public String getCountry() {
        return country;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setImage_url(String image_url) {this.image_url = image_url;}

    public String getImage_url() {
        return image_url;
    }

    public void setUserLastName(String userLastName) {this.userLastName = userLastName;}

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {return userName;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(birth_date);
        dest.writeString(country);
        dest.writeString(email);
        dest.writeString(gender);
        dest.writeString(image_url);
        dest.writeString(userLastName);
        dest.writeString(userName);
    }
}
