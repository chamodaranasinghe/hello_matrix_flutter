package com.hello.hello_matrix_flutter.src.directory;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "user_profile", primaryKeys = {"hello_id"})
public class UserProfile {
    /*@PrimaryKey(autoGenerate = true)
    public int localId;*/

    @ColumnInfo(name = "hello_id")
    @NotNull
    public String helloId;

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "contact")
    public String contact;

    @ColumnInfo(name = "job_title")
    public String jobTitle;

    @ColumnInfo(name = "photo_thumbnail")
    public String photoThumbnail;

    @ColumnInfo(name = "photo_url")
    public String photoUrl;

    @ColumnInfo(name = "org_prefix")
    public String orgPrefix;

    @ColumnInfo(name = "org_name")
    public String orgName;

    @ColumnInfo(name = "org_contact")
    public String orgContact;

    @ColumnInfo(name = "org_website")
    public String orgWebsite;


    //getters and setters

    /*public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }*/

    public String getHelloId() {
        return helloId;
    }

    public void setHelloId(String helloId) {
        this.helloId = helloId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getPhotoThumbnail() {
        return photoThumbnail;
    }

    public void setPhotoThumbnail(String photoThumbnail) {
        this.photoThumbnail = photoThumbnail;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getOrgPrefix() {
        return orgPrefix;
    }

    public void setOrgPrefix(String orgPrefix) {
        this.orgPrefix = orgPrefix;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgContact() {
        return orgContact;
    }

    public void setOrgContact(String orgContact) {
        this.orgContact = orgContact;
    }

    public String getOrgWebsite() {
        return orgWebsite;
    }

    public void setOrgWebsite(String orgWebsite) {
        this.orgWebsite = orgWebsite;
    }

}
