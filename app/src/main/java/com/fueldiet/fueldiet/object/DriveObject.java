package com.fueldiet.fueldiet.object;

import android.content.ContentValues;

import com.fueldiet.fueldiet.db.FuelDietContract;

import java.util.Calendar;

public class DriveObject {

    private int odo;
    private int trip;
    private int first;
    private int notFull;
    private double litres;
    private double costPerLitre;
    private Calendar date;
    private long carID;
    private long id;
    private String note;
    private String petrolStation;
    private String country;
    private Double latitude;
    private Double longitude;

    public DriveObject() {
        this.first = 0;
        this.notFull = 0;
    }

    public DriveObject(long id, Calendar calendar) {
        this.id = id;
        this.date = calendar;
        this.first = 0;
        this.notFull = 0;
    }

    public DriveObject(int odo, int trip, double litres, double costPerLitre, long date, long carID,
                       long id, String note, String petrolStation, String country, int first,
                       int notFull, Double latitude, Double longitude) {
        this.odo = odo;
        this.trip = trip;
        this.litres = litres;
        this.costPerLitre = costPerLitre;
        this.date = Calendar.getInstance();
        this.date.setTimeInMillis(date*1000);
        this.carID = carID;
        this.id = id;
        this.note = note;
        this.petrolStation = petrolStation;
        this.country = country;
        this.first = first;
        this.notFull = notFull;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public DriveObject(int odo, int trip, double litres, double costPerLitre, long date, long carID,
                       String note, String petrolStation, String country, int first,
                       int notFull, Double latitude, Double longitude) {
        this.odo = odo;
        this.trip = trip;
        this.litres = litres;
        this.costPerLitre = costPerLitre;
        this.date = Calendar.getInstance();
        this.date.setTimeInMillis(date*1000);
        this.carID = carID;
        this.note = note;
        this.petrolStation = petrolStation;
        this.country = country;
        this.first = first;
        this.notFull = notFull;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getOdo() {
        return odo;
    }

    public void setOdo(int odo) {
        this.odo = odo;
    }

    public boolean setOdo(String odo) {
        if (odo.equals(""))
            return false;
        this.odo = Integer.parseInt(odo);
        return true;
    }

    public int getTrip() {
        return trip;
    }

    public void setTrip(int trip) {
        this.trip = trip;
    }

    public boolean setTrip(String trip) {
        if (trip.equals(""))
            return false;
        this.trip = Integer.parseInt(trip);
        return true;
    }

    public double getLitres() {
        return litres;
    }

    public void setLitres(double litres) {
        this.litres = litres;
    }

    public boolean setLitres(String litres) {
        if (litres.equals(""))
            return false;
        this.litres = Double.parseDouble(litres);
        return true;
    }

    public double getCostPerLitre() {
        return costPerLitre;
    }

    public void setCostPerLitre(double costPerLitre) {
        this.costPerLitre = costPerLitre;
    }

    public boolean setCostPerLitre(String costPerLitre) {
        if (costPerLitre.equals(""))
            return false;
        this.costPerLitre = Double.parseDouble(costPerLitre);
        return true;
    }

    public Calendar getDate() {
        return date;
    }

    public boolean setDate(Calendar date) {
        this.date = date;
        return true;
    }

    public long getCarID() {
        return carID;
    }

    public boolean setCarID(long carID) {
        this.carID = carID;
        return true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDateEpoch() {
        return date.getTimeInMillis()/1000;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPetrolStation() {
        return petrolStation;
    }

    public void setPetrolStation(String petrolStation) {
        this.petrolStation = petrolStation;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getNotFull() {
        return notFull;
    }

    public void setNotFull(int notFull) {
        this.notFull = notFull;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(FuelDietContract.DriveEntry.COLUMN_CAR, this.getCarID());
        cv.put(FuelDietContract.DriveEntry.COLUMN_DATE, this.getDateEpoch());
        cv.put(FuelDietContract.DriveEntry.COLUMN_ODO, this.getOdo());
        cv.put(FuelDietContract.DriveEntry.COLUMN_TRIP, this.getTrip());
        cv.put(FuelDietContract.DriveEntry.COLUMN_LITRES, this.getLitres());
        cv.put(FuelDietContract.DriveEntry.COLUMN_PRICE_LITRE, this.getCostPerLitre());
        cv.put(FuelDietContract.DriveEntry.COLUMN_NOTE, this.getNote());
        cv.put(FuelDietContract.DriveEntry.COLUMN_PETROL_STATION, this.getPetrolStation());
        cv.put(FuelDietContract.DriveEntry.COLUMN_COUNTRY, this.getCountry());
        cv.put(FuelDietContract.DriveEntry.COLUMN_FIRST, this.getFirst());
        cv.put(FuelDietContract.DriveEntry.COLUMN_NOT_FULL, this.getNotFull());
        cv.put(FuelDietContract.DriveEntry.COLUMN_LATITUDE, this.getLatitude());
        cv.put(FuelDietContract.DriveEntry.COLUMN_LONGITUDE, this.getLongitude());
        return cv;
    }
}
