
package org.emsg.wifiauto.modle;

import java.io.Serializable;

public class FileItem implements Serializable
{
    private String name = "";
    private String path = "/";
    private boolean isFile = false;
    
    private String localPath;

    private int fileSize;
    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String isLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public FileItem()
    {
        super();
    }

    public FileItem(String name, String path, boolean isFile)
    {
        super();
        this.name = name;
        this.path = path;
        this.isFile = isFile;
    }
    public FileItem(String name, String path, boolean isFile,int filesize)
    {
        super();
        this.name = name;
        this.path = path;
        this.isFile = isFile;
        this.fileSize = filesize;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public boolean isFile()
    {
        return isFile;
    }

    public void setFile(boolean isFile)
    {
        this.isFile = isFile;
    }

    @Override
    public String toString()
    {
        return name;
    }

}
