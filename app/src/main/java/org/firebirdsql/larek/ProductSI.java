package org.firebirdsql.larek;

/**
 * Created by nazar.humeniuk on 6/22/17.
 */

public class ProductSI {
    public int id;
    public String name;
    public int productII;
    public int count;

    public void setName(String name) {
        this.name = name;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProductII(int productII) {
        this.productII = productII;
    }

    public int getProductII() {
        return productII;
    }

    public int getCount() {
        return count;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
