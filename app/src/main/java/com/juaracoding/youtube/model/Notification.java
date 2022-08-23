package com.juaracoding.youtube.model;

import java.io.Serializable;

public class Notification implements Serializable {

    public long id = -1;
    public String title = "";
    public String content = "";
    public String image = "";

    // extra attribute
    public Boolean read = false;
}
