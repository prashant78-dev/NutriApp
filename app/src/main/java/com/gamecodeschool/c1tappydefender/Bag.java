package com.gamecodeschool.c1tappydefender;

/**
 * Created by User on 20/10/2015.
 */
public class Bag {

    GoodFoodItem goodFoodItem;
    BadFoodItem badFoodItem;

    Bag(GoodFoodItem goodFoodItem, BadFoodItem badFoodItem) {
        this.goodFoodItem=goodFoodItem;
        this.badFoodItem=badFoodItem;
    }

    public BadFoodItem getBadFoodItem() {
        return badFoodItem;
    }

    public GoodFoodItem getGoodFoodItem() {
        return goodFoodItem;
    }
}
