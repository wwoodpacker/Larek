package org.firebirdsql.larek;

/**
 * Created by nazar.humeniuk on 6/30/17.
 */

public class Pad {
    private int id;
    private String serial;
    private String mac;
    private String model;
    private String name;
    private String larek_dep;

    public void setId(int id) {
        this.id = id;
    }

    public void setLarek_dep(String larek_dep) {
        this.larek_dep = larek_dep;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public int getId() {
        return id;
    }

    public String getLarek_dep() {
        return larek_dep;
    }

    public String getMac() {
        return mac;
    }

    public String getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    public String getSerial() {
        return serial;
    }
}
