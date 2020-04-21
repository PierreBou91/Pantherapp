package com.emergence.pantherapp.model;

// init class to define document structure
public class Document {
    // file name
    private String name;
    // file extension
    private  String extension;
    // folder in which a user
    private Folder folder;

    public Document(String name, String extension, Folder folder) {
        this.name = name;
        this.extension = extension;
        this.folder = folder;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    public Folder getFolder() {
        return folder;
    }
}
