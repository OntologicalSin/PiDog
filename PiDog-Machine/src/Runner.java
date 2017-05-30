import java.util.Scanner;

import java.io.*;
/**
 * Runner.java  
 *
 * This class lets the user interact with the an artificial neural network
 */
public class Runner
{
    private static final String syntaxError = "Invalid syntax - type 'help' for a list of commands";
    /**
     * Allows the user to interact with an artificial neural network
     * @param String[] args
     */
    public static void main(String[] args)
    {
        Scanner scan = new Scanner(System.in); //Creates a Scanner to get user inputs
        //Pix.doMaxLength(); //Calculates the length of the longest input 
        //System.out.println(Pix.getMaxLength());
        //double[][] inputs = Pix.getInputs(); //Converts images of numbers into arrays of numbers
        //double[][] outputs = Pix.getOutputs(); //Uses file name of the images to create known/desired outputs

        int[] setup = {80,40,22}; //The first layer has 32 Neurons, the 2nd has 16 and 3rd has 10.
        Network net = new Network(setup, Pix.getMaxLength()); //Creates the Network
        String again = "y"; //Allows user to enter multiple commands
        System.out.println("Enter 'help' for a list of commands\nEnter a command:");
        while (!again.equals("n")) //Allows user to enter multiple commands
        {
            System.out.print("> ");
            String cmd = getInput(scan); //Gets an input from the user
            String[] cmdSplit = cmd.split("\\s+"); //Splits the user's  input String by spaces into an array of Strings
            if (cmdSplit[0].equals("train"))
            {
                if (getIndex(cmdSplit,1)!=null)
                {
                    
                }
            }
            else if (cmdSplit[0].equals("feedforward") || cmdSplit[0].equals("ff"))
            {
                if (getIndex(cmdSplit,1)!=null)
                {
                    System.out.println("\n");
                    String in = cmd.substring(3)+".png";
                    double[] test = new double[1];
                    test = Pix.getPicArray(in,true);
                    System.out.println("Result: "+arrayToNum(net.feedForward(test)));
                    System.out.println(printArray(net.feedForward(test)));
                }
            }
            else if (cmdSplit[0].equals("save"))
            {
                if (getIndex(cmdSplit,1)!=null)
                {
                    net.saveWeights(cmdSplit[1]);
                }
            }
            else if (cmdSplit[0].equals("load"))
            {
                if (getIndex(cmdSplit,1)!=null)
                {
                    net.loadWeights(cmdSplit[1]);
                }
            }
            else if (cmdSplit[0].equals("reset"))
            {
                net = new Network(setup, Pix.getMaxLength()); //Creates the Network
                System.out.println("\n\nNeural network has been reset.");
            }
            else if (cmdSplit[0].equals("help"))
            {
                printCommands();
            }
            else if (cmdSplit[0].equals("stop"))
            {
                again = "n"; //Ends the program
            }
            else
            {
                System.out.println("Command not recognized, type 'help' for a list of commands.");
            }
        }
        System.out.print("...Done");
    }

    /**
     * Gets input from user and makes sure they don't just input a space or a '\n'
     * @param Scanner scan
     * @return String user's input
     */
    public static String getInput(Scanner scan)
    {
        String user = "";
        while (user.equals("") || user.equals("\n") || user.equals(" "))
        {
            user = scan.nextLine();
            //user = user.toLowerCase();
        }
        return user;
    }

    /**
     * Turns an array into a String
     * @param double[] array
     * @return String output
     */
    public static String printArray(double[] arr)
    {
        String out = "";
        int counter = 0;
        for (double i: arr)
        {
            double n = i * 1000;
            n = (int)(0.5+n);
            n = (double)n/1000;
            out+="#"+counter+": "+n+"  ";
            counter++;
            if (counter == 21)
                counter = -1;
        }
        return out;
    }

    /**
     * Converts the output array of the Network into a number (for humans to understand)
     * @param double[] output of Network
     * @return int number
     */
    public static double arrayToNum(double[] arr)
    {
        double biggest = arr[0];
        int biggestIndex = 0;
        for (int i = 1; i < arr.length; i++)
        {
            if (arr[i]>biggest)
            {
                biggest = arr[i];
                biggestIndex = i;
            }
        }
        if (biggestIndex==21)
            return -1;
        return biggestIndex/10.0;
    }

    /**
     * Gets a value from an array of Strings, catching the error (if the index does not exist)
     * @param String[] arr
     * @param int index
     * @return String at arr[index]
     */
    public static String getIndex(String[] arr, int index)
    {
        try
        {
            return arr[index];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            System.out.println(syntaxError);
        }
        return null;
    }

    /**
     * Prints out a list of commands
     */
    public static void printCommands()
    {
        System.out.println("-===- Command -===-\t\t\t-===- Description -===-");
        System.out.println("train [amount] \t\t\t\t- trains the neural network 'amount' times");
        System.out.println("feedforward [fileName] OR ff [fileName] - feeds the network a new input");
        System.out.println("save [fileName] \t\t\t- saves the state of the neural network");
        System.out.println("load [fileName] \t\t\t- loads the state of the neural network");
        System.out.println("reset \t\t\t\t\t- resets the neural network");
        System.out.println("help \t\t\t\t\t- displays a list of commands");
        System.out.println("stop \t\t\t\t\t- ends this program\n");
    }
}
