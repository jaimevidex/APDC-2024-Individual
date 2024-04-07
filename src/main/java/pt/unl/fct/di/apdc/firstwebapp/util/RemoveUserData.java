package pt.unl.fct.di.apdc.firstwebapp.util;

public class RemoveUserData {
    public String username;
    public long expirationDate;

    public RemoveUserData() {}

    public RemoveUserData(String username, long expirationDate) {
        this.username = username;
        this.expirationDate = expirationDate;
    }
}
