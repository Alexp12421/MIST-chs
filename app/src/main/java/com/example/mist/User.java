package com.example.mist;

public class User {

    private String username, email;
    private float wallet;

    public User(){

    }

    public User(String username, String email){
        this.username = username;
        this.email = email;
        this.wallet = 0;
    }

    public String getUsername(){
        return this.username;
    }
    public String getEmail(){
        return this.email;
    }
    public float getWallet(){
        return this.wallet;
    }

    public void addBalance(float money){
        this.wallet = this.wallet + money;
    }

}
