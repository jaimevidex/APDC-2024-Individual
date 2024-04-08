package pt.unl.fct.di.apdc.firstwebapp.util;

public class ChangeAttributeData {

    public String username, usernameDestiny, user_email, user_name, user_phone, user_public, user_occupation, user_workspace, user_address, user_zip, user_nif, user_role, user_active, user_photo;
    public long expirationDate;

    public ChangeAttributeData() {
    }

    public ChangeAttributeData(String username, long expirationDate, String usernameDestiny, String user_email, 
                               String user_name, String user_phone, String user_public, String user_occupation,
                               String user_workspace, String user_address, String user_zip, String user_nif,
                               String user_role, String user_active, String user_photo) {
        this.username = username;
        this.expirationDate = expirationDate;
        this.usernameDestiny = usernameDestiny;
        this.user_email = user_email;
        this.user_name = user_name;
        this.user_phone = user_phone;
        this.user_public = user_public;
        this.user_occupation = user_occupation;
        this.user_workspace = user_workspace;
        this.user_address = user_address;
        this.user_zip = user_zip;
        this.user_nif = user_nif;
        this.user_role = user_role;
        this.user_active = user_active;
        this.user_photo = user_photo;
    }
}
