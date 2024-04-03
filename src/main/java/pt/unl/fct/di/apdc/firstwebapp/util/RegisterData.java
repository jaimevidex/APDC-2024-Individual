package pt.unl.fct.di.apdc.firstwebapp.util;

public class RegisterData {

    public String username;
    public String email;
    public String name;
    public String phone;
    public String password;
    public String passwordConfirmation;
    public String perfil;
    public String ocupacao;
    public String localTrabalho;
    public String morada;
    public String codigoPostal;
    public String nif;

    public RegisterData() {
    }

    public RegisterData(String username, String email, String name, String phone, String password, 
                        String passwordConfirmation/*, String perfil, String ocupacao, String localTrabalho,
                        String morada, String codigoPostal, String nif*/) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.passwordConfirmation = passwordConfirmation;
        this.password = password;
        /*this.perfil = perfil;
        this.ocupacao = ocupacao;
        this.localTrabalho = localTrabalho;
        this.morada = morada;
        this.codigoPostal = codigoPostal;
        this.nif = nif;*/
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
