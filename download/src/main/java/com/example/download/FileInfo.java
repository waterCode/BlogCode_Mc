package com.example.download;

import java.io.Serializable;

/**
 * Created by zmc on 2017/4/15.
 */
public class FileInfo implements Serializable{
    private int id;
    private String url;
    private String fileName;
    private long size;

    public FileInfo(String url,String fileName, int id, int size) {
        this.fileName = fileName;
        this.id = id;
        this.size = size;
        this.url = url;
    }

    public FileInfo() {
    }

    public String getFileName() {
        return fileName;
    }

    public int getId() {
        return id;
    }

    public long getSize() {
        return size;
    }

    public String getUrl() {
        return url;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileName='" + fileName + '\'' +
                ", id=" + id +
                ", url='" + url + '\'' +
                ", size=" + size +
                '}';
    }
}
