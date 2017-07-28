package org.firebirdsql.larek;

/**
 * Created by nazar.humeniuk on 7/28/17.
 */

public class Remains {
    public String name;
    private int prev_ost;
    private int now_ost;
    private double price;

    public void setName(String name) {
        this.name = name;
    }

    public void setNow_ost(int now_ost) {
        this.now_ost = now_ost;
    }

    public void setPrev_ost(int prev_ost) {
        this.prev_ost = prev_ost;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getNow_ost() {
        return now_ost;
    }

    public int getPrev_ost() {
        return prev_ost;
    }
}
