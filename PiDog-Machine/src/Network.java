import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
/**
 * Network.java  
 *
 * @author:
 * Assignment #:
 * 
 * Brief Program Description:
 * 
 *
 */
public class Network
{
    private ArrayList<ArrayList<Neuron>> neurons; //array list of neurons
    private int numInputs; //number of inputs the network can process
    
    /**
     * Creates a Network of Neurons
     * @param int[] amount of neurons in each column
     * @param int number of inputs the Network can process
     */
    public Network(int[] numAmount, int inputs)
    {
        neurons = new ArrayList<ArrayList<Neuron>>();
        numInputs = inputs;
        for (int r = 0; r < numAmount.length; r++)
        {
            ArrayList<Neuron> temp = new ArrayList<Neuron>();
            for (int c = 0; c < numAmount[r]; c++)
            {
                if (r==0)
                {
                    temp.add(new Neuron(numInputs, r, c, this));
                    //System.out.println("(Net) Inputs: " + numInputs);
                }
                else
                {
                    temp.add(new Neuron(numAmount[r-1], r, c, this));
                    //System.out.println("(Net) Inputs "+ r+ ": " + numAmount[r-1]);
                }
            }
            neurons.add(temp);
        }
    }
    
    /**
     * Takes an input and lets the Network predict an output
     * @param double[] input
     * @return double[] predicted output
     */
    public double[] feedForward(double[] inputs)
    {   
        double[] output = new double[1];
        double[] output2;
        for (int r = 0; r < neurons.size(); r++)
        {
            if (r==0)
            {
                output = new double[neurons.get(r).size()];
                for (int c = 0; c < neurons.get(r).size(); c++)
                {
                    output[c] = (neurons.get(r).get(c)).feedForward(inputs);
                    //System.out.println("(Net) Inner Output "+r+": "+output[c]);
                }
            }
            else
            {
                output2 = new double[neurons.get(r).size()];
                for (int c = 0; c < neurons.get(r).size(); c++)
                {
                    output2[c] = (neurons.get(r).get(c)).feedForward(output);
                    //System.out.println("(Net) Outer Output "+r+": "+output[c]);
                }
                output=output2;
            }
        }
        return output;
    }

    /**
     * Trains the network based on an input and known output (for that input)
     * @param double[] input
     * @param double[] known/desired output
     */
    public void train(double[] inputs, double[] desired)
    {
        feedForward(inputs);
        for (int r = neurons.size()-1; r >= 0; r--)
        {
            for (int c = neurons.get(r).size()-1; c >= 0; c--)
            {
                neurons.get(r).get(c).train(inputs, desired);
            }
        }

        for (ArrayList<Neuron> r: neurons)
        {
            for (Neuron n: r)
            {
                n.updateWeights();
            }
        }
    }
    
    /**
     * Returns all the weights of each neuron (for debugging purposes)
     * @return String of all the weights
     */
    public String printWeights()
    {
        String out = "";
        for (int r = 0; r < neurons.size(); r++)
        {
            for (int c = 0; c < neurons.get(r).size(); c++)
            {
                out += "r: "+ r + " c: "+ c+" Weights:";
                for (double w: neurons.get(r).get(c).getWeights())
                {
                    out += "  "+w;
                }
                out+= "\n";
            }
        }
        return out;
    }
    
    /**
     * Returns an arrayList of an arrayList (2D arrayList) of the Neurons in the Network
     * @return ArrayList<ArrayList<Neuron>>
     */
    public ArrayList<ArrayList<Neuron>> getNeurons()
    {
        return neurons;
    }
    
    /**
     * Saves the weights (state of Network) to a text file
     * @param String fileName
     */
    public void saveWeights(String name)
    {
        try 
        {
            FileWriter writer = new FileWriter(name+".txt");
            for (ArrayList<Neuron> r: neurons)
            {
                for (Neuron n: r)
                {
                    for (double w: n.getWeights())
                    {
                        writer.write(w+" ");
                    }
                    writer.write("\n");
                }
            }
            writer.close();
            System.out.println("'"+name+".txt' was successfully saved");
        } 
        catch (IOException e) 
        {
            System.out.println("Unable to save '"+name+".txt'");
        }
    }
    
    /**
     * Loads weights (state of Network) from a text file
     * @param String fileName
     */
    public void loadWeights(String name)
    {
        // This will reference one line at a time
        String line = null;

        try 
        {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(name+".txt");

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            for (ArrayList<Neuron> r: neurons)
            {
                for (Neuron n: r)
                {
                    line = bufferedReader.readLine();
                    String[] splited = line.split("\\s+");
                    n.setWeights(convertArray(splited));
                }
            }

            // Always close files.
            bufferedReader.close();
            System.out.println("'"+name+".txt' was successfully loaded");
        }
        catch(IOException ex) 
        {
            System.out.println("Error reading file '" + name + ".txt'");
        }
    }
    /**
     * Converts an array of Strings to an array of doubles
     * @param String[] input
     * @return double[] output
     */
    public static double[] convertArray(String[] arr)
    {
        double[] out=new double[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            out[i] = Double.parseDouble(arr[i]);
        }
        return out;
    }
}
