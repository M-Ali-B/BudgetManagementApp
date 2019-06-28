package com.github.malib.budgetmanagementapp.Modal;

import java.io.Serializable;

public class MyData implements Serializable {


   public String title ;
   public  String description;
   public  String budget;
   public  String date;
   public  String id;


    public MyData() {
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getBudget() {
        return budget;
    }

    public String getDate() {
        return date;
    }



    public MyData(String title, String description, String budget, String date, String id) {
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.date = date;
        this.id = id;
    }
}
