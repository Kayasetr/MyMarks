package com.firebase.myapplication;

public class Marks {
    String subject, nameStudent;
    Object mark;

    public Marks(){

    }

    public Marks(String subject, Object mark) {
        this.subject = subject;
        this.mark = mark;
    }

    public final String getSubject() {
        return subject;
    }

    public Object getMark() {
        return mark;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }
}
