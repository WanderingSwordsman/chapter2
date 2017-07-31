package org.smart4j.chapter2.model;

/**
 * Created by ShangJun on 2016/7/3.
 */
public class Customer {

    private long id;
    private String name;
    private String contcat;
    private String telephone;
    private String email;
    private String reamrk;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReamrk() {
        return reamrk;
    }

    public void setReamrk(String reamrk) {
        this.reamrk = reamrk;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {

        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getContcat() {

        return contcat;
    }

    public void setContcat(String contcat) {
        this.contcat = contcat;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
