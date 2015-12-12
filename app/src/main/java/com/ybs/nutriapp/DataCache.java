package com.ybs.nutriapp;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ybs.nutriapp.R;

/**
 * Created by Steve on 10/24/2015.
 */
public class DataCache {
    private ArrayList<Bag> bagList;
    /*
    Change variables & paths here for csv files and image files
     */
    private static final Map<Integer,List<String>> goodFoodItems = new HashMap<>();
    private static final List<String> badFoodItems = new ArrayList<>();
    private static Random randomGenerator;

    public static void main(String[] args)
    {

        ArrayList<Bag> randomBag = new ArrayList<Bag>();
        randomBag = getRandomBagList(2,5);
        for (int i = 0; i < randomBag.size(); i++)
        {
            Bag bagItem = randomBag.get(i);
            GoodFoodItem goodItem = bagItem.getGoodFoodItem();
            BadFoodItem badItem = bagItem.getBadFoodItem();
            System.out.println("Pair: "+(i+1));
            System.out.println("Good Food: "+goodItem.getLevel()+"#"+goodItem.getFileName()+"#"+goodItem.getFoodName()+"#"+goodItem.getTransFoodName()+"#"+goodItem.getFoodDescription()+"#"+goodItem.getFoodCateogry());
            System.out.println("Bad Food: "+badItem.getFileName()+"#"+badItem.getFoodName()+"#"+badItem.getTransFoodName()+"#"+badItem.getFoodDescription()+"#"+badItem.getFoodCateogry());
        }
    }
    /*
    Get a list of <request number> of Bag (contain 1 good food, 1 bad food)
    E.g: 5 pair of good food and bad food ==> requestNumber = 5;
     */

    public static ArrayList<Bag> getRandomBagList(int requestGoodFoodItemLevel, int requestNumber)
    {
        ArrayList<Bag> bagList = new ArrayList<Bag>();
        String foodCategory = "";
        randomGenerator = new Random();
        int randomNumber = randomGenerator.nextInt(5);
        if (randomNumber >= 2)
        {
            foodCategory = "food";
        } else {
            foodCategory = "drink";
        }
        try
        {
            ArrayList<GoodFoodItem> randomGoodFoodItems = getRandomGoodFoodItem(requestGoodFoodItemLevel, requestNumber, foodCategory);
            ArrayList<BadFoodItem> randomBadFoodItems = getRandomBadFoodItem(requestNumber, foodCategory);
            if ((randomGoodFoodItems.size()!=requestNumber) || (randomBadFoodItems.size() != requestNumber)){
                return null;
            }
            else{
                for (int i = 0; i < requestNumber; i++)
                {
                    Bag bagItem = new Bag(randomGoodFoodItems.get(i),randomBadFoodItems.get(i));
                    bagList.add(bagItem);
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        return bagList;
    }

    // Get Random Number of Good Food Object by Level
    // param: request level (e.g: level 3), request number (e.g: 5 good food)
    public static ArrayList<GoodFoodItem> getRandomGoodFoodItem(int requestLevel, int requestNumber, String foodCategory)
    {
        ArrayList<GoodFoodItem> goodFoodList = new ArrayList<GoodFoodItem>();
        try
        {
            randomGenerator = new Random();
            ArrayList<String> goodFoodListByLevel = getAllGoodFoodItem(requestLevel);
          
            if (requestNumber > goodFoodListByLevel.size())
            {
                return null;
            }
            else
            {
                int i = 0;
                while (i < requestNumber)
                {

                    int indexItem = randomGenerator.nextInt(goodFoodListByLevel.size());
                    String[] stringItem = goodFoodListByLevel.get(indexItem).split("\\|");
                    for (int j = 0; j < stringItem.length; j++) {
                        if (stringItem[j] == null) {
                            stringItem[j] = "";
                        }

                    }
                    if (stringItem[5].equals(foodCategory)) {
                        String display = goodFoodListByLevel.get(indexItem);
                        System.out.println(display);
                        GoodFoodItem goodFoodItem = new GoodFoodItem(Integer.parseInt(stringItem[0]), stringItem[1], stringItem[2], stringItem[3], stringItem[4], stringItem[5]);
                        goodFoodList.add(goodFoodItem);
                        goodFoodListByLevel.remove(indexItem);
                        i = i + 1;
                    }

                }


                return goodFoodList;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

    }


    // Get Random Number of Bad Food Object
    // param: request number (e.g: 5 bad food)
    public static ArrayList<BadFoodItem> getRandomBadFoodItem(int requestNumber, String foodCategory)
    {
        ArrayList<BadFoodItem> badFoodList = new ArrayList<BadFoodItem>();
        try
        {
            randomGenerator = new Random();
            ArrayList<String> badFoodStringList = getAllBadFoodItem();
            if (requestNumber > badFoodStringList.size())
            {
                return null;
            }
            else
            {
                int i = 0;
                while (i < requestNumber) {

                    int indexItem = randomGenerator.nextInt(badFoodStringList.size());
                    String[] stringItem = badFoodStringList.get(indexItem).split("\\|");

                    for (int j = 0; j < stringItem.length; j ++)
                    {
                        if (stringItem[j] == null)
                        {
                            stringItem[j] = "";
                        }

                    }
                    if (stringItem[4].equals(foodCategory)) {
                        BadFoodItem badFoodItem = new BadFoodItem(stringItem[0], stringItem[1], stringItem[2], stringItem[3], stringItem[4]);
                        badFoodList.add(badFoodItem);
                        badFoodStringList.remove(indexItem);
                        i = i + 1;
                    }
                }


                return badFoodList;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

    }

    public static void init(Context context) {
        try {
            InputStream is = context.getResources().openRawResource(R.raw.goodfood);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr, 8192);
            String headerLine = br.readLine();
            String line = br.readLine();
            ArrayList<String> allGoodFoodItem = new ArrayList<String>();
            int level = 0;
            while (line != null) {
                level=Integer.parseInt(line.substring(0, line.indexOf("|")));
                if(!goodFoodItems.containsKey(level)){
                    goodFoodItems.put(level, new ArrayList<String>());
                }
                goodFoodItems.get(level).add(line);
                line = br.readLine();
            }

            is = context.getResources().openRawResource(R.raw.badfood);
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr, 8192);
            headerLine = br.readLine();
            line = br.readLine();
            while (line != null) {
                badFoodItems.add(line);
                line = br.readLine();
            }
            br.close();
            isr.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Get all good food by Level
    // param: good food level
    private static ArrayList<String> getAllGoodFoodItem(int level)
    {
        return new ArrayList<String>(goodFoodItems.get(level));
    }

    private static ArrayList<String> getAllBadFoodItem()
    {
        return new ArrayList<String>(badFoodItems);
    }

}
