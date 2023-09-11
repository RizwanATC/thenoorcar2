package com.noor.thenoorcar.Class;

public class WaktuSolatClass {
    private String hijri, date,imsak,subuh,syuruk,zuhur,asar,maghrib,isyak,weather,temperatureUnit,weatherDescription,nextDayHijriDate;



    public WaktuSolatClass(String hijri, String date, String imsak, String subuh, String syuruk, String zuhur, String asar, String maghrib, String isyak,
                           String weather, String temperatureUnit, String weatherDescription, String nextDayHijriDate) {
        this.hijri = hijri;
        this.date = date;
        this.imsak = imsak;
        this.subuh = subuh;
        this.syuruk = syuruk;
        this.zuhur = zuhur;
        this.asar = asar;
        this.maghrib = maghrib;
        this.isyak = isyak;
        this.weather = weather;
        this.temperatureUnit = temperatureUnit;
        this.weatherDescription = weatherDescription;
        this.nextDayHijriDate = nextDayHijriDate;
    }
    public String getNextDayHijriDate() {
        return nextDayHijriDate;
    }

    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public String getWeather() {
        return weather;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public String getAsar() {
        return asar;
    }

    public String getImsak() {
        return imsak;
    }

    public String getSubuh() {
        return subuh;
    }

    public String getIsyak() {
        return isyak;
    }

    public String getMaghrib() {
        return maghrib;
    }

    public String getSyuruk() {
        return syuruk;
    }

    public String getZuhur() {
        return zuhur;
    }

    public String  getDate() {
        return date;
    }

    public String getHijri() {
        return hijri;
    }
}
