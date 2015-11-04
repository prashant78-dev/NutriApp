package com.ybs.nutriapp;

/**
 * Created by User on 20/10/2015.
 */
public class GoodFoodItem extends FoodItem {

    private int level;

    public GoodFoodItem(int level, String fileName, String foodName, String transFoodName,  String foodDescription,
                       String foodCateogry) {
        super(fileName, foodName, transFoodName, foodDescription, foodCateogry);
        this.level = level;
    }

    @Override
    public boolean isGoodItem() {
        return true;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
