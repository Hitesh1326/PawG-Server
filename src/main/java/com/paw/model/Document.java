package com.paw.model;


/**
 * Document Class is used to assign document id and calculate term frequency.
 */
public class Document {
   public int docId;
    public double tw;

    public Document(int did, double tw) {
        docId = did;
        this.tw = tw;
    }

    public String toString() {
        String docIdString = docId + ":" + tw;
        return docIdString;

    }

}
