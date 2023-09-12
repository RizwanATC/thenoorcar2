package com.noor.thenoorcar.Class;

public class AsmaClass {
    private String id,number,nameTranscription,arabicName,name,imageUrl;
    int position;

    public AsmaClass(String id, String number, String nameTranscription, String arabicName, String name, int position,String imageUrl) {
        this.id = id;
        this.number = number;
        this.nameTranscription = nameTranscription;
        this.arabicName = arabicName;
        this.name = name;
        this.position = position;
        this.imageUrl = imageUrl;

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public String getNumber() {
        return number;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArabicName() {
        return arabicName;
    }

    public String getNameTranscription() {
        return nameTranscription;
    }
}
