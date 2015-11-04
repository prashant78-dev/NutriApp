package com.ybs.nutriapp;

import android.graphics.Bitmap;
import android.graphics.Rect;

public abstract class FoodItem {
    private Bitmap bitmap;
    private int x, y;
    public static int maxX;
    // A hit box for collision detection
    private Rect hitBox;
    private String fileName;
    private String foodName;
    private String transFoodName;
    private String foodDescription;
    private String foodCateogry;


    public FoodItem(String fileName, String foodName, String transFoodName,  String foodDescription,
                    String foodCateogry)
    {
        this.fileName = fileName;
        this.foodName = foodName;
        this.transFoodName = transFoodName;
        this.foodDescription = foodDescription;
        this.foodCateogry = foodCateogry;
        scaleBitmap();
    }


    //Getters and Setters
    public Bitmap getBitmap(){

        return bitmap;
    }
    public int getX() {

        return x;
    }

    public int getY() {

        return y;
    }

    public Rect getHitbox(){
        return hitBox;
    }

    public void setHitbox(int x, int y, Bitmap bMap) {
        bitmap = bMap;
        this.x=x;
        this.y=y;
        // Initialize the hit box
        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    // This is used by the TDView update() method to
    // Make an enemy out of bounds and force a re-spawn
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void scaleBitmap(){

        if(maxX < 1000) {
            bitmap = Bitmap.createScaledBitmap(bitmap,
                bitmap.getWidth() / 3,
                bitmap.getHeight() / 3,
                false);
        }else if(maxX < 1200){
            bitmap = Bitmap.createScaledBitmap(bitmap,
                bitmap.getWidth() / 2,
                bitmap.getHeight() / 2,
                false);
        }
    }

    public boolean isTouched(int xCor, int yCor) {
        if (xCor>x && xCor<x+bitmap.getWidth()) {
            if(yCor>y && yCor<y+bitmap.getHeight()) {
                return true;
            }
        }
        return false;
    }

    public abstract boolean isGoodItem();

    // Get Methods

    public String getFileName() {
        return fileName;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getTransFoodName() {
        return transFoodName;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public String getFoodCateogry() {
        return foodCateogry;
    }

    // Set Methods


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setTransFoodName(String transFoodName) {
        this.transFoodName = transFoodName;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public void setFoodCateogry(String foodCateogry) {
        this.foodCateogry = foodCateogry;
    }

}
