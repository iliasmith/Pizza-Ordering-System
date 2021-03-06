package com.techelevator.model;

import java.math.BigDecimal;

public class Choice {

    private int choiceId;
    private int categoryId;
    private String name;
    private boolean isAvailable;
    private BigDecimal customPrice;
    private BigDecimal specialtyPrice;

    public int getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(int choiceId) {
        this.choiceId = choiceId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public BigDecimal getCustomPrice() {
        return customPrice;
    }

    public void setCustomPrice(BigDecimal customPrice) {
        this.customPrice = customPrice;
    }

    public BigDecimal getSpecialtyPrice() {
        return specialtyPrice;
    }

    public void setSpecialtyPrice(BigDecimal specialtyPrice) {
        this.specialtyPrice = specialtyPrice;
    }
}
