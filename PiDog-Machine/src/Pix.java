import java.awt.image.BufferedImage;

import java.awt.*;

import java.io.*;

import javax.imageio.ImageIO;

/**
 * This class is used to convert the training data into something the neural network can understand (an array of numbers)
 * Change this class to work with your specific training data
 */
public class Pix
{
    private static int maxTypes = 100; //This represents the max that x can be in the file name "#.x.png"
    private static int maxLength = 50*50; //The size of the largest image, set it to -1
    public static String[] names;
    /**
     * Converts an image file into an array of numbers that the Neural Network can understand
     * @param String File Name, boolean Print out error message if it occurs?
     * @return double[] output
     */
    public static double[] getPicArray(String fileName, boolean exception)
    {
        double[] out = new double[1];
        int width=0;
        int height=0;
        try
        {
            //fileName+=".png";
            File input = new File("Pix.java");
            String path = "C:\\Users\\Mark\\Documents\\Heather\\";
            input = new File(fileName);
            //System.out.println("C:\\Users\\Mark\\Documents\\Heather\\coke\\"+fileName);
            BufferedImage image = ImageIO.read(input);
            width = image.getWidth();
            height = image.getHeight();
            if (maxLength == -1)
                out = new double[width*height];
            else
                out = new double[maxLength];
            int counter=0;
            int biggest;
            if (width>height)
                biggest=width;
            else
                biggest=height;
            //System.out.println("\n");
            //System.out.println(fileName+": ("+width+", "+height+") biggest: "+biggest+", length: "+out.length);
            for (int i=0; i<biggest; i++)
            {
                //System.out.println("ii: "+i);
                int curr=i;
                int x=i;
                int y=0;
                if (height<=i)
                {
                    x=i;
                    //System.out.println("height <= i: "+height+" <= "+i);
                    curr=height-1;
                }
                else if (width<=i)
                {
                    y=i;
                    //System.out.println("width <= i: "+width+" <= "+i);
                    curr=width-1;
                }
                else
                {
                    for (y=0; y<curr; y++) //down
                    {
                        //System.out.println("x: "+x+", y: "+y+", i: "+i);
                        Color c = new Color(image.getRGB(x,y));
                        out[counter] = avgCol(c);
                        counter++;
                        //System.out.println("Counter: "+counter+", x: "+x+", y: "+y);
                    }
                }
                if (height<=i)
                {
                    x=i;
                    //System.out.println("height <= i: "+height+" <= "+i);
                }
                else
                {
                    for (x=curr; x>=0; x--) //left
                    {
                        //System.out.println("x: "+x+", y: "+y+", i: "+i);
                    	Color c = new Color(image.getRGB(x,y));
                        out[counter] = avgCol(c);
                        counter++;
                        //System.out.println("Counter: "+counter+", x: "+x+", y: "+y);
                    }
                }
            }
        }
        catch (IOException e)
        {
            if (exception)
                System.out.println("Failed to open '"+fileName+"'");
            out[0]=-1;
        }
        //if (count!=0)
        //    System.out.println(fileName+": count: "+count+", ideal:"+width*height);
        return out;
    }
    
    /**
     * Converts an image file into an array of numbers that the Neural Network can understand
     * (It will not print out error message if file cannot be open)
     * @param String File Name
     * @return double[] output
     */
    public static double[] getPicArray(String fileName)
    {
        return getPicArray(fileName, true);
    }
    
    /**
     * Calculates the size of the largest image and stores it in "maxLength"
     */
    public static void doMaxLength()
    {
        File folder = new File("C:\\Users\\Mark\\Documents\\Heather\\coke");
        int temp = -1;
        int count = 0;
        for (final File fileEntry : folder.listFiles())
        {
            String name = fileEntry.getName();
            double[] pic = getPicArray(name);
            //System.out.println(name+", "+maxLength+", "+pic.length);
            if (pic[0]!=-1 && pic.length>temp)
            {
                temp = pic.length;
            }
            count++;
        }
        maxLength = temp;
        names = new String[count];
        count = 0;
        for (final File fileEntry : folder.listFiles())
        {
            names[count] = fileEntry.getName();
            count++;
        }
    }
    public static double avgCol(Color c)
    {
    	double red = c.getRed();
        double green = c.getGreen();
        double blue = c.getBlue();
        double newColor = ((red+green+blue)/3.0);
        if (newColor>255)
        {
            newColor = 255;
        }
        return red/255;
    }
    
    /**
     * Creates a giant 2D array of all inputs in the training set
     * @return double[][] input training data
     */
    public static double[][] getInputs()
    {
        File folder = new File("C:\\Users\\Mark\\Documents\\Heather\\coke");
        int counter=0;
        for (String name: names)
        {
            double[] pic = getPicArray(name);
            if (pic[0]!=-1)
            {
                counter++;
            }
        }

        double[][] out = new double[counter][maxLength];
        counter=0;
        for (String name: names)
        {
            double[] pic = getPicArray(name);
            if (pic[0]!=-1)
            {
                out[counter]=pic;
                counter++;
            }
        }
        return out;
    }
    
    /**
     * Creates a giant 2D array of all the outputs of the training data
     * @return double[][] output training data
     */
    public static double[][] getOutputs()
    {
        File folder = new File("C:\\Users\\Mark\\Documents\\Heather\\coke");
        int counter=0;
        for (String name: names)
        {
            double[] pic = getPicArray(name);
            if (pic[0]!=-1)
            {
                counter++;
            }
        }

        double[][] out = new double[counter][22];
        counter=0;
        for (String name: names)
        {
            double[] pic = getPicArray(name);
            String valString = name.split("\\s+")[1];
            double val = Double.parseDouble(valString.substring(0,valString.length()-4));
            if (pic[0]!=-1)
            {
                if (val!=-1)
                {
                    out[counter][(int)(val*10)]=1;
                }
                else
                {
                    out[counter][21]=1;
                }
                //System.out.println((int)(val*10)+", "+val+", "+names[counter]+", "+Runner.arrayToNum(out[counter]));
                counter++;
            }
        }
        return out;
    }
    /**
     * Returns the size of the largest image
     * @return int Largest size
     */
    public static int getMaxLength()
    {
        return maxLength;
    }
}
