package com.chinmai.splitwise.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//jackson convert incoming json into UserRequest Object
//The password is present in the DTO temporarily. The DTO doesn't hash, hide or automatically discard it.
public class UserRequest {

    @NotBlank (message = "Name is required")
    private String name;

    @NotBlank (message = "Email is required")
    @Email (message= "email should be valid")
    private String email;

    @NotBlank (message = "Password is required")
    @Size (min=8, message = "Password must be atleast 8 characters")
    private String password;

    public UserRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}