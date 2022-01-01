package com.example.project1_2;

public class Item {
    private String item_name;
    private String item_number;
    private String item_email;
    private String item_job;

    public Item(String item_name, String item_number, String item_email, String item_job) {
        this.item_name = item_name;
        this.item_number = item_number;
        this.item_email = item_email;
        this.item_job = item_job;
    }

    public String getItem_name() {
        return item_name;
    }
    public String getItem_number() { return item_number; }
    public String getItem_email() { return item_email; }
    public String getItem_job() { return item_job; }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }
    public void setItem_number(String item_number) { this.item_number = item_number; }
    public void setItem_email(String item_email) { this.item_email = item_email; }
    public void setItem_job(String item_job) { this.item_job = item_job; }
}
