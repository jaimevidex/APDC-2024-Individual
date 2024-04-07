package pt.unl.fct.di.apdc.firstwebapp.util;

public class ChangeStateData {
    public String username,usernameDestiny;
    public long expirationDate;

    public ChangeStateData(){

    }

    public ChangeStateData(String username, long expirationDate, String usernameDestiny){
        this.username = username;
        this.expirationDate = expirationDate;
        this.usernameDestiny = usernameDestiny;
    }
}
