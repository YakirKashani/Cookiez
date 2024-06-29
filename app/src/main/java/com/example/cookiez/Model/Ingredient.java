package com.example.cookiez.Model;
public class Ingredient {

    private String name;
    private String unit;
    private String amount;

    public Ingredient(){

    }

    public Ingredient(String name,String unit,String amount){
        this.name = name;
        this.unit = unit;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public Ingredient setName(String name) {
        this.name = name;
        return this;
    }

    public Ingredient setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public Ingredient setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public String getAmount() {
        return amount;
    }


    public String getUnits() {
        return unit;
    }
}
