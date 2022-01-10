package gr.uop;

import java.io.File;

public class FileHolder
{
    private File file;

    private static final FileHolder holder = new FileHolder();

    private FileHolder(){}

    public static FileHolder getInstance()
    {
        return holder;
    }

    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
    }
}
