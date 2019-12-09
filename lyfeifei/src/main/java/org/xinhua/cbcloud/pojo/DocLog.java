package org.xinhua.cbcloud.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Document(indexName = "doclog", type = "docs", shards = 1, replicas = 0)
public class DocLog implements Serializable {

    @Id
    private Long id;

    @Field(type = FieldType.Text, fielddata = true)
    private String docId;

    @Field(type = FieldType.Text)
    private String messageId;

    @Field(type = FieldType.Text)
    private String context;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "DocLog{" +
                "id=" + id +
                ", docId='" + docId + '\'' +
                ", messageId='" + messageId + '\'' +
                ", context='" + context + '\'' +
                '}';
    }
}