package org.firebirdsql.larek;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nazar.humeniuk on 6/26/17.
 */

public class OrderItem {
    public int id;
    public String orderItemName;
    public int order;
    public int si_id;
    public int si_count;
    public double si_price;
    public double si_total;
    public int ii_id;
    public int ii_count;
    public double ii_price;
    public double ii_total;



    public void setOrderItemName(String orderItemName) {
        this.orderItemName = orderItemName;
    }

    public String getOrderItemName() {
        return orderItemName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIi_count(int ii_count) {
        this.ii_count = ii_count;
    }

    public void setIi_id(int ii_id) {
        this.ii_id = ii_id;
    }

    public void setIi_price(double ii_price) {
        this.ii_price = ii_price;
    }

    public void setIi_total(double ii_total) {
        this.ii_total = ii_total;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setSi_count(int si_count) {
        this.si_count = si_count;
    }

    public void setSi_id(int si_id) {
        this.si_id = si_id;
    }

    public void setSi_price(double si_price) {
        this.si_price = si_price;
    }

    public void setSi_total(double si_total) {
        this.si_total = si_total;
    }

    public double getSi_price() {
        return si_price;
    }

    public int getId() {
        return id;
    }

    public double getIi_price() {
        return ii_price;
    }

    public double getIi_total() {
        return ii_total;
    }

    public double getSi_total() {
        return si_total;
    }

    public int getIi_count() {
        return ii_count;
    }

    public int getIi_id() {
        return ii_id;
    }

    public int getOrder() {
        return order;
    }

    public int getSi_count() {
        return si_count;
    }

    public int getSi_id() {
        return si_id;
    }

}
