package com.ybs.nutriapp;

/**
 * Created by User on 20/10/2015.
 */
public class BadFoodItem extends FoodItem {
    public BadFoodItem(String fileName, String foodName, String transFoodName,  String foodDescription,
                       String foodCateogry) {
        super(fileName, foodName, transFoodName, foodDescription, foodCateogry);
    }

    @Override
    public boolean isGoodItem() {
        return false;
    }

}
