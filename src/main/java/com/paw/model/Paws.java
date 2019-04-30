package com.paw.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.bson.types.ObjectId;

/*
 *This is a object model for retrieving data from database.
 *
 */
public class Paws {
    @Id
    public ObjectId _id;


    public Integer docID;

    public String AnimalDesc;

    public String img;

    public Paws() {

    }

    public Paws(ObjectId _id) {

        this._id = _id;
    }

    @JsonIgnore
    public String get_id() {
        return _id.toHexString();

    }

    @JsonIgnore
    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    @JsonIgnore
    public Integer getDocID() {
        return docID;
    }

    @JsonIgnore
    public void setDocID(Integer id) {
        this.docID = id;
    }

    @JsonIgnore
    public String getAnimalDesc() {
        return AnimalDesc;
    }

    @JsonIgnore
    public void setAnimalDesc(String description) {
        this.AnimalDesc = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }


}
