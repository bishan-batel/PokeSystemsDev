package engine;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

public final class Resource
{
    public static double constrain(double n, double min, double max)
    {
        return Math.max(Math.min(n, max), min);
    }

    // Image
    public static BufferedImage getImageFromTileSet(BufferedImage img, int tileX, int tileY, int x, int y)
    {
        int x1 = x * tileX;
        int y1 = y * tileY;
        BufferedImage tile = img.getSubimage(x1, y1, tileX, tileY);
        return tile;
    }

    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public static void drawStringMultiLine(Graphics g, String text, int lineWidth, int x, int y)
    {
        FontMetrics m = g.getFontMetrics();
        if (m.stringWidth(text) < lineWidth)
        {
            g.drawString(text, x, y);
        } else
        {
            String[] words = text.split(" ");
            String currentLine = words[0];
            for (int i = 1; i < words.length; i++)
            {
                if (m.stringWidth(currentLine + words[i]) < lineWidth)
                {
                    currentLine += " " + words[i];
                } else
                {
                    g.drawString(currentLine, x, y);
                    y += m.getHeight();
                    currentLine = words[i];
                }
            }
            if (currentLine.trim().length() > 0)
            {
                g.drawString(currentLine, x, y);
            }
        }
    }
    
    // Sound
    public static synchronized void playSound(final String url)
    {
       // TODO
    }

    // Reading JSON
    public static ArrayList<File> getAllJSONFilesFromList(File[] files)
    {
        ArrayList<File> fileList = new ArrayList<>();
        for (File file : files)
        {
            if (file.isFile() && file.getName().endsWith(".json"))
            {
                fileList.add(file);
            } else if (file.isDirectory())
            {
                ArrayList<File> dirFile = Resource.getAllJSONFilesFromList(file.listFiles());
                fileList.addAll(dirFile);
            }
        }
        return fileList;
    }

    public static JSONObject readJSONFile(String path)
    {
        File file = new File(path);
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line, fullJSONString = "";
            while ((line = br.readLine()) != null)
            {
                fullJSONString += line;
            }
            System.out.println("Read " + path + " succesfully");
            return new JSONObject(fullJSONString);
        } catch (IOException | JSONException e)
        {
            System.err.println("Failed to load " + path);
            return null;
        }
    }
}
