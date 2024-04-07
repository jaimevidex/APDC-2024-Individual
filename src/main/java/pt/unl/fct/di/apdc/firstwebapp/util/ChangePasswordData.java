package pt.unl.fct.di.apdc.firstwebapp.util;

public class ChangePasswordData {
    public String username,oldPassword,password,confirmPassword;
    public long expirationDate;

    public ChangePasswordData(){

    }

    public ChangePasswordData(String username, long expirationDate, String oldPassword, String password, String confirmPassword){
        this.username = username;
        this.expirationDate = expirationDate;
        this.oldPassword = oldPassword;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public boolean isDataValid() {
        if (!isValidPassword(password)) {
            return false;
        } else return password.equals(confirmPassword);
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
