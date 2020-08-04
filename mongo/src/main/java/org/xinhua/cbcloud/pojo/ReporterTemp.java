package org.xinhua.cbcloud.pojo;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "reporter_temp")
public class ReporterTemp {

    private ObjectId _id;

    private String docId;

    private String photoPlateNum;

    private String dateTime;

    private String filePath;

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getPhotoPlateNum() {
        return photoPlateNum;
    }

    public void setPhotoPlateNum(String photoPlateNum) {
        this.photoPlateNum = photoPlateNum;
    }

    public String getDate() {
        return dateTime;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "ReporterTemp{" +
                ", docId='" + docId + '\'' +
                ", photoPlateNum='" + photoPlateNum + '\'' +
                ", dateTime=" + dateTime +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
