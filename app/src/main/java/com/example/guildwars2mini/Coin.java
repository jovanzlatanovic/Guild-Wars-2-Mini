package com.example.guildwars2mini;

public class Coin {
    private int amount = 0;

    public Coin(int gold, int silver, int copper) {
        this.amount += copper;
        this.amount += silver * 100;
        this.amount += gold * 10000;
    }

    public Coin() {
        this.amount = 0;
    }

    public Coin(int amount) {
        this.amount = amount;
    }

    public Coin(Coin coin) {
        this.amount = coin.amount;
    }

    public int getGold() {
        String value = String.valueOf(amount);

        if (value.length() < 5)
            return 0;
        else {
            String goldValue = value.substring(0, value.length()-4);
            int gold = Integer.parseInt(goldValue);
            return gold;
        }
    }

    public int getSilver() {
        String value = String.valueOf(amount);
        String silverValue = "0";

        if (value.length() < 3)
            return 0;
        else if (value.length() < 4)
            silverValue = value.substring(value.length()-3, value.length()-2);
        else
            silverValue = value.substring(value.length()-4, value.length()-2);

        int silver = Integer.parseInt(silverValue);
        return silver;
    }

    public int getCopper() {
        String value = String.valueOf(amount);
        if (value.length() < 3)
            return amount;
        else {
            String copperValue = value.substring(value.length()-2);
            int copper = Integer.parseInt(copperValue);
            return copper;
        }
    }

    // Usage: getTriplet()[0-2]; 0 - gold; 1 - silver; 2 copper;
    // Deprecated, doesn't account for this.amount being less than 1000
    private int[] getTriplet() {
        String value = String.valueOf(amount);
        String copper = value.substring(value.length()-2);
        String silver = value.substring(value.length()-4, value.length()-2);
        String gold = value.substring(0, value.length()-4);

        try {
            int goldValue = 0;
            int silverValue = 0;
            int copperValue = 0;

            if (!gold.equals(""))
                goldValue = Integer.parseInt(gold);
            if (!silver.equals(""))
                silverValue = Integer.parseInt(silver);
            if (!copper.equals(""))
                copperValue = Integer.parseInt(copper);

            return new int[]{goldValue, silverValue, copperValue};
        } catch (NumberFormatException e) {
            throw e;
        }
    }

    public void add(Coin coin) {
        this.amount += coin.amount;
    }

    public boolean subtract(Coin coin) {
        int value = this.amount - coin.amount;
        if (value < 0)
            return false;

        this.amount = value;
        return true;
    }
}
