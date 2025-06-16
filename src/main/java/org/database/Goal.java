package org.database;

public class Goal {
    private int id;
    private String type;
    private String category;
    private double amount;
    private String deadline;

    // Default constructor
    public Goal() {
    }

    // Parameterized constructor
    public Goal(int id, String type, String category, double amount, String deadline) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.deadline = deadline;
    }

    // Getter and Setter for id
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    // Getter and Setter for type
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    // Getter and Setter for category
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    // Getter and Setter for amount
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    // Getter and Setter for deadline
    public String getDeadline() {
        return deadline;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
