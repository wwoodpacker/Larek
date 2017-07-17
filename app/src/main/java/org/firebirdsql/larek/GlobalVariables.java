package org.firebirdsql.larek;

/**
 * Created by nazar.humeniuk on 14.06.17.
 */

public class GlobalVariables {

    private static GlobalVariables instance=null;
    private String ID;
    private String name;
    private String phone;
    private String usersArraybaseURL;
    private String login;
    private String password;
    private String DriverName;
    private String padName;
    private String larekDep;
    public static synchronized GlobalVariables getInstance(){
        if(instance==null){
            instance=new GlobalVariables();

        }
        return instance;
    }

    public void setLarekDep(String larekDep) {
        this.larekDep = larekDep;
    }

    public String getLarekDep() {
        return larekDep;
    }

    public void setPadName(String padName) {
        this.padName = padName;
    }

    public String getPadName() {
        return padName;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setUsersArraybaseURL(String url){
        usersArraybaseURL=url;
    }

    public String getUsersArraybaseURL() {
        return usersArraybaseURL;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }
}
