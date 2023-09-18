package com.noor.thenoorcar.Class;

public class SurahClass {
    private String idd, type,id,number,quranRecitation,arabicName,name,englishName,haveBismillah,contentUrl,totalVerses,turkiName;
    private String china;
    private String tamil;
    private String malay;
    private String indonesia;

    public SurahClass(String idd, String type, String id, String number, String quranRecitation, String arabicName,
                      String name, String englishName, String haveBismillah, String contentUrl, String totalVerses,
                      String chinas, String tamils, String malays, String indonesias, String turkiNames) {
        this.idd = idd;
        this.type = type;
        this.id = id;
        this.number = number;
        this.quranRecitation = quranRecitation;
        this.arabicName = arabicName;
        this.name = name;
        this.englishName = englishName;
        this.haveBismillah = haveBismillah;
        this.contentUrl = contentUrl;
        this.totalVerses = totalVerses;
        china = chinas;
        tamil = tamils;
        malay = malays;
        indonesia = indonesias;
        this.turkiName = turkiNames;
    }

    public String getTurkiName() {
        return turkiName;
    }

    public String getTamil() {
        return tamil;
    }

    public String getMalay() {
        return malay;
    }

    public String getIndonesia() {
        return indonesia;
    }

    public String getChina() {
        return china;
    }

    public String getTotalVerses() {
        return totalVerses;
    }

    public String getNumber() {
        return number;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getArabicName() {
        return arabicName;
    }

    public String getName() {
        return name;
    }

    public String getIdd() {
        return idd;
    }

    public String getQuranRecitation() {
        return quranRecitation;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getHaveBismillah() {
        return haveBismillah;
    }
}
