package com.example.fueldiet.object;

import com.example.fueldiet.activity.MainActivity;


public class ManufacturerObject {

    private String name;
    private String fileName;
    private String url;
    private boolean original;

    public ManufacturerObject(String name, final String fileName) {
        this.name = name;
        this.fileName = fileName;
        this.url = String.format(MainActivity.LOGO_URL, fileName);
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileNameModNoType() {
        String tmp = fileName.toLowerCase().replace("-", "_").replace(" ", "__");
        int len = tmp.length();
        return tmp.substring(0, len-4);
    }

    public String getFileNameMod() {
        return fileName.toLowerCase().replace("-", "_").replace(" ", "__");
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() { return this.url; }

    public boolean isOriginal() {
        return original;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }
}