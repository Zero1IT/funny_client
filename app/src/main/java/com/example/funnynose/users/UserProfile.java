package com.example.funnynose.users;

import java.io.Serializable;

public class UserProfile implements Serializable {

    public long index;
    public String nickname;
    public String city;
    public long lastParticipation;
    public long lastChange;

    public UserProfile(long index, String nickname, String city, long lastParticipation, long lastChange) {
        this.index = index;
        this.nickname = nickname;
        this.city = city;
        this.lastParticipation = lastParticipation;
        this.lastChange = lastChange;
    }
}
