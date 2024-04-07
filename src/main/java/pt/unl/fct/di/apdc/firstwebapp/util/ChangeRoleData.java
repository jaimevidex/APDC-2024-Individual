package pt.unl.fct.di.apdc.firstwebapp.util;

public class ChangeRoleData {
    public String username,usernameDestiny,role;
    public long expirationDate;

    public ChangeRoleData(){

    }

    public ChangeRoleData(String username, long expirationDate, String usernameDestiny, String role){
        this.username = username;
        this.expirationDate = expirationDate;
        this.usernameDestiny = usernameDestiny;
        this.role = role;
    }
}
