package data;

import java.time.LocalDate;

public class Account {
    private final String name;
    private final String email;
    private final String password;
    private final LocalDate dateOfBirth;
    private final AccountStatus status;

    public Account(String name, String email, String password, LocalDate dateOfBirth, AccountStatus status) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
    }


    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public AccountStatus getStatus() {
        return status;
    }
}
