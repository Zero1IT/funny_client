package by.funnynose.app.chat;

import java.util.Date;

public class Message {
    public String text, nickname;
    public long key;
    public Date time;

    public Message(String text, String nickname, long time, long key) {
        this.text = text;
        this.nickname = nickname;
        this.time = new Date(time);
        this.key = key;
    }
}