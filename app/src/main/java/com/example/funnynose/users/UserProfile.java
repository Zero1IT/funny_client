package com.example.funnynose.users;

import java.io.Serializable;
import java.util.Date;

public class UserProfile implements Serializable {

    public long index;
    public String nickname;
    public String city;
    public Date lastParticipation;
    public Date lastChange;

    public UserProfile(long index, String nickname, String city, long lastParticipation, long lastChange) {
        this.index = index;
        this.nickname = nickname;
        this.city = city;
        this.lastParticipation = new Date(lastParticipation);
        this.lastChange = new Date(lastChange);
    }
}
