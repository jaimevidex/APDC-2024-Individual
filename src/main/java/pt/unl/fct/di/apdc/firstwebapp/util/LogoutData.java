package pt.unl.fct.di.apdc.firstwebapp.util;

public class LogoutData {
    public String username;
    public long expirationDate;

    public LogoutData(){
    }

    public LogoutData(String username,long expirationDate){
        this.username = username;
        this.expirationDate = expirationDate;
    }
}
