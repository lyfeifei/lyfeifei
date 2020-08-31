package org.xinhua.cbcloud.pojo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "data_temp")
public class DataTemp {

    private ObjectId _id;

    private String docId;

    private String photoPlateNum;

    private Date dateTime;

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

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
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
        return "DataTemp{" +
                "_id=" + _id +
                ", docId='" + docId + '\'' +
                ", photoPlateNum='" + photoPlateNum + '\'' +
                ", dateTime=" + dateTime +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
