package org.firebirdsql.larek;

import java.io.Serializable;

/**
 * Created by nazar.humeniuk on 6/22/17.
 */

public class Order implements Serializable {
    public int id;
    public String soldtime;
    public int seller;
    public int employees;
    public double total;
    public String empName;
    public String empOccupation;

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public void setEmpOccupation(String empOccupation) {
        this.empOccupation = empOccupation;
    }

    public String getEmpName() {
        return empName;
    }

    public String getEmpOccupation() {
        return empOccupation;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setEmployees(int employees) {
        this.employees = employees;
    }

    public void setSeller(int seller) {
        this.seller = seller;
    }

    public void setSoldtime(String soldtime) {
        this.soldtime = soldtime;
    }

    public int getId() {
        return id;
    }

    public double getTotal() {
        return total;
    }

    public int getEmployees() {
        return employees;
    }

    public int getSeller() {
        return seller;
    }

    public String getSoldtime() {
        return soldtime;
    }

}
