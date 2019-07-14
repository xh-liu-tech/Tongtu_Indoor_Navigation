package com.indoor_navigation.model;

import java.io.Serializable;

/**
 * Created by Ö¾ºÀ on 2015/7/22.
 */
public class Point implements Serializable {
    private String name;
    private String x;
    private String y;
    private String z;
    private String id;
    private int origin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getZ() {
        return z;
    }

    public void setZ(String z) {
        this.z = z;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOrigin() {
        return origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }
}
