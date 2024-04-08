package pt.unl.fct.di.apdc.firstwebapp.util;

public class UserData {
    String username, email, name, phone, occupation, workspace, address, zip, nif, role, photo;
    boolean isPublic, active;

    public UserData(){}
    public UserData(String username, String email, String name) {
        this.username = username;
        this.email = email;
        this.name = name;
    }
    public UserData(String username, String email, String name, String phone, boolean isPublic, String occupation, String workspace,
                    String address, String zip, String nif, String role, boolean active, String photo) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.isPublic = isPublic;
        this.occupation = occupation;
        this.workspace = workspace;
        this.address = address;
        this.zip = zip;
        this.nif = nif;
        this.role = role;
        this.active = active;
        this.photo = photo;
    }
}
