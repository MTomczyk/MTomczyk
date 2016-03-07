package standard;

import java.io.*;

/**
 * Created by Michał on 2015-03-09.
 *
 */
public class FileUtil
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

    public static void closeReader(BufferedReader br)
    {
        try
        {
            br.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public static BufferedReader getBufferReader(String file)
    {
        BufferedReader in = null;

        try
        {
            in = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return in;
    }

    public static String readLine(BufferedReader buffer)
    {
        String line = null;
        try
        {
            line = buffer.readLine();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return line;
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
