package br.com.eduardo.dscatalog.dto;

public class UserInsertDTO extends UserDTO{
    private static final long serialVersionUID = 1L;

    private String password;

    UserInsertDTO() {}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
