package com.noor.thenoorcar.Class;

public class AsmaReciterClass {
    private String idd,type,id,name,description,imageUrl,isDefaultRecital,fileUrl;
    Boolean select;

    public AsmaReciterClass(String idd, String type, String id, String name, String description, String imageUrl
            , String isDefaultRecital, String fileUrl,boolean select) {
        this.idd = idd;
        this.type = type;
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isDefaultRecital = isDefaultRecital;
        this.fileUrl = fileUrl;
        this.select = select;
    }

    public Boolean getSelect() {
        return select;
    }

    public void setSelect(Boolean select) {
        this.select = select;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIdd() {
        return idd;
    }

    public String getType() {
        return type;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getIsDefaultRecital() {
        return isDefaultRecital;
    }

}

