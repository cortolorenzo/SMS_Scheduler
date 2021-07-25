package com.example.sms_scheduler_final;

public class Messages
{
    int ID;
    String MsgTxt;
    int GroupID;
    int Sent;

public Messages(){

}

    public String getMessage()
    {
        return MsgTxt;
    }

    public Messages(int msgID, String msgtxt, int msgGroup, int sent) {

        ID = msgID;
        MsgTxt = msgtxt;
        GroupID = msgGroup;

    }

    public int getID()
    {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getMsgTxt() {
        return MsgTxt;
    }

    public void setMsgTxt(String msgTxt) {
        MsgTxt = msgTxt;
    }

    public int getGroupID() {
        return GroupID;
    }

    public void setGroupID(int groupID) {
        GroupID = groupID;
    }

    public int getSent() {
        return Sent;
    }

    public void setSent(int sent) {
        Sent = sent;
    }
}

