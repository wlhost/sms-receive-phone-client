package com.example.administrator.myapplication;

public class Sms {
    private final String address;
    private final String date;
    private final String msg;
    private final SmsType type;


    public Sms(String address, String date, String msg, SmsType type) {
        this.address = address;
        this.date = date;
        this.msg = msg;
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public String getMsg() {
        return msg;
    }

    public SmsType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sms)) return false;

        Sms sms = (Sms) o;

        if (address != null ? !address.equals(sms.address) : sms.address != null) return false;
        if (date != null ? !date.equals(sms.date) : sms.date != null) return false;
        if (msg != null ? !msg.equals(sms.msg) : sms.msg != null) return false;
        if (type != sms.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (msg != null ? msg.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Sms{" +
                "address='" + address + '\'' +
                ", date='" + date + '\'' +
                ", msg='" + msg + '\'' +
                ", type=" + type +
                '}';
    }

    public String JSONStr(){
        return "address="+address+"&date="+date+"&msg="+msg+"&type="+type;
/*        return "{\"address\":"+"\""+address+"\" "+
                ", \"date\":"+"\""+date+"\" "+
                ", \"msg\":"+"\""+msg+"\" "+
                ", \"type\":"+"\""+type+"\" }";*/
    }
}
