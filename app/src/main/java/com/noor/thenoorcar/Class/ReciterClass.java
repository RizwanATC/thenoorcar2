package com.noor.thenoorcar.Class;

public class ReciterClass {
    private String id,name,image_url;

    public ReciterClass(String id, String name, String image_url) {
        this.id = id;
        this.name = name;
        this.image_url = image_url;

    }

    public String getImage_url() {
        return image_url;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
