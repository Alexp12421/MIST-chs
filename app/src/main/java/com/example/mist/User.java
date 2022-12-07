package com.example.mist;

import java.util.ArrayList;

public class User {

    private String username, email;
    private float wallet;
    private ArrayList<String> library = new ArrayList<String>();

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
    public ArrayList<String> getLibrary(){return library;}

    public void addBalance(float money){
        this.wallet = this.wallet + money;
    }
    public void substractBalance(float money){this.wallet = this.wallet - money;}
    public void addGame(String game){this.library.add(game);}

}
