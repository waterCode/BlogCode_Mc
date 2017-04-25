package com.example.download;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zmc on 2017/4/15.
 */
@Entity
public class ThreadInfo {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String url;
    @NotNull
    private String fileName;
    private long start;
    private long end;
    private long finished;
    private long fileLength;



    @Generated(hash = 2068897720)
    public ThreadInfo(Long id, @NotNull String url, @NotNull String fileName,
            long start, long end, long finished, long fileLength) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.start = start;
        this.end = end;
        this.finished = finished;
        this.fileLength = fileLength;
    }
    @Generated(hash = 930225280)
    public ThreadInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getFileName() {
        return this.fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public long getStart() {
        return this.start;
    }
    public void setStart(long start) {
        this.start = start;
    }
    public long getEnd() {
        return this.end;
    }
    public void setEnd(long end) {
        this.end = end;
    }
    public long getFinished() {
        return this.finished;
    }
    public void setFinished(long finished) {
        this.finished = finished;
    }
    public long getFileLength() {
        return this.fileLength;
    }
    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "end=" + end +
                ", fileLength=" + fileLength +
                ", fileName='" + fileName + '\'' +
                ", finished=" + finished +
                ", id=" + id +
                ", start=" + start +
                '}';
    }
}
