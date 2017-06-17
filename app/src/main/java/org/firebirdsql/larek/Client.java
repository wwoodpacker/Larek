package org.firebirdsql.larek;

/**
 * Created by nazar.humeniuk on 15.06.17.
 */

public class Client {
    private String ID;
    private String Surname;
    private String Name;
    private String Patronimic;
    private String Occupation;
    private String Larek_Dep;
    private String Status;

    public String getID() {
        return ID;
    }

    public String getLarek_Dep() {
        return Larek_Dep;
    }

    public String getName() {
        return Name;
    }

    public String getOccupation() {
        return Occupation;
    }

    public String getPatronimic() {
        return Patronimic;
    }

    public String getStatus() {
        return Status;
    }

    public String getSurname() {
        return Surname;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setLarek_Dep(String larek_Dep) {
        Larek_Dep = larek_Dep;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setOccupation(String occupation) {
        Occupation = occupation;
    }

    public void setPatronimic(String patronimic) {
        Patronimic = patronimic;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

}
