package org.firebirdsql.larek;

/**
 * Created by nazar.humeniuk on 6/22/17.
 */

public class ProductII {
    public int id;
    public String name;
    public String larekDep;
    public double price;

    public void setId(int id) {
        this.id = id;
    }

    public void setLarekDep(String larekDep) {
        this.larekDep = larekDep;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public int getId() {
        return id;
    }

    public String getLarekDep() {
        return larekDep;
    }

    public String getName() {
        return name;
    }
}
