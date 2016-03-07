package utils;

import java.io.*;

/**
 * Created by Michał on 2015-02-24.
 */
// TODO DELETE?
public class FileManager
{
    public static void closeWriter(BufferedWriter bw)
    {
        try
        {
            bw.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void writeToFile(BufferedWriter bw, String data)
    {
        try
        {
            bw.write(data);
            bw.newLine();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public String readFromFile(BufferedReader br)
    {
        try
        {
            return br.readLine();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static FileWriter getFileWriter(File file)
    {
        try
        {
            return new FileWriter(file.getAbsoluteFile());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static FileReader getFileReader(File file)
    {
        try
        {
            return new FileReader(file.getAbsoluteFile());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedReader getBufferedReader(FileReader fr)
    {
        return new BufferedReader(fr);
    }

    public static void createDir(String path, String delim)
    {
        String sd[] = path.split(delim);

        String p = "";

        for (String d : sd)
        {
            p += (d + delim);
            createDir(p);
        }
    }

    @SuppressWarnings("all")
    public static void createDir(String path)
    {
        File file = new File(path);
        file.mkdir();
    }

    public static File getFile(String path)
    {
        return new File(path);
    }

}
