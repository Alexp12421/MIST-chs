package com.example.mist;

public class InsertGame {

   private String GameName, Image, GamePrice;

    public InsertGame() {

    }

    public InsertGame(String image, String gameName, String gamePrice) {
        GameName = gameName;
        GamePrice = gamePrice;
        this.Image = image;
    }

    public String getGameName() {
        return GameName;
    }

    public void setGameName(String gameName) {
        GameName = gameName;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getGamePrice() {
        return GamePrice;
    }

    public void setGamePrice(String gamePrice) {
        GamePrice = gamePrice;
    }

    @Override
    public String toString() {
        return GameName;
    }
}
