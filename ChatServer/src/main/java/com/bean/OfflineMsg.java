package com.bean;

/**
 * description：
 *
 * @author ajie
 * data 2018/12/13 0:05
 */
public class OfflineMsg {

    private int id;

    public int getId() {
        return id;
    }

    private String toName;
    private String fromName;
    private int type;
    private String fileName;
    private String msg;
    public OfflineMsg(){}

    public OfflineMsg(int id, String toName, String fromName, int type, String fileName, String msg) {
        this.id = id;
        this.toName = toName;
        this.fromName = fromName;
        this.type = type;
        this.fileName = fileName;
        this.msg = msg;
    }

    public String getToName() {
        return toName;
    }

    public String getFromName() {
        return fromName;
    }

    public int getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "OfflineMsg{" +
                "id=" + id +
                ", toName='" + toName + '\'' +
                ", fromName='" + fromName + '\'' +
                ", type=" + type +
                ", fileName='" + fileName + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


}
