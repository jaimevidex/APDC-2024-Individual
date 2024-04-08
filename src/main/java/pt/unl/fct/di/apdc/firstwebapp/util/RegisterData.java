package pt.unl.fct.di.apdc.firstwebapp.util;

public class RegisterData {

    public String username,email,name,phone,password,passwordConfirmation,occupation,workspace, address, zip, nif, photo;
    public boolean isPublic;
    public RegisterData() {
    }

    public RegisterData(String username, String email, String name, String phone, String password, 
                        String passwordConfirmation, boolean isPublic, String occupation, String workspace,
                        String address, String zip, String nif, String photo) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.isPublic = isPublic;
        this.occupation = occupation;
        this.workspace = workspace;
        this.address = address;
        this.zip = zip;
        this.nif = nif;
        this.photo = photo;
    }

    public boolean isDataValid() {
        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty() || email.isEmpty()) {
            return false;
        } else if (!verifyEmail(email)) {
            return false;
        } else if (!isValidPassword(password)) {
            return false;
        } else return password.equals(passwordConfirmation);
    }

    public static boolean verifyEmail(String email) {
        String regex = "^[\\w!#$%&'*+/=?^`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
        return email.matches(regex);
    }

    private static boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasNumbers = password.matches(".*[0-9].*");
        boolean hasSymbols = password.matches(".*[!@#$%^&*()_+-=].*");

        return hasLowercase && hasUppercase && hasNumbers && hasSymbols;
    }
    
}
