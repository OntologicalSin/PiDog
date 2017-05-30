
/**
 * A neuron
 *
 */
public class Neuron
{
    private double[] weights; //the actual weights
    private double[] weights2; //holds weights for 'updateWeights' method
    private static double r = 0.01; //training rate
    private int row; //the row in which this neuron is located in a network
    private int col; //the column in which this neuron is located in a network
    private double lastOutput; //holds the last output of this neuron (for effeciency)
    private double part; //holds a part of the calculations of 'train' method that can be used by other neurons
    private Network net; //the network that this neuron is in
    /**
     * Creates a single Neuron
     * 
     * @param int Number of inputs
     * @param int The row of the neuron in the network
     * @param int The column of the neuron in the network
     * @param Network The Network object that the Neuron is located in
     */
    public Neuron(int n, int ro, int co, Network work)
    {
        weights = new double[n+1];
        row = ro;
        col = co;
        net = work;
        //generates random weights between -1 and 1
        for (int i = 0; i<weights.length; i++)
        {
            weights[i] = Math.random()*2-1;
            //weights[i] = 1;
        }
    }
    
    /**
     * Takes in inputs and predicts an output
     * @param double[] Inputs
     * @return double Output
     */
    public double feedForward(double[] inputs)
    {
        //System.out.println("(neuron) Inputs: "+inputs.length+", Weights: "+weights.length);
        double[] inputs2 = new double[weights.length];
        for (int i = 0; i < inputs.length; i++)
            inputs2[i] = inputs[i];
        inputs2[inputs2.length-1] = 1; //bias input
        //System.out.print("(n)");
        double sum = 0;
        for (int i = 0; i < weights.length; i++)
        {
            sum += inputs2[i]*weights[i]; //multiplies the inputs by weights
            //System.out.print(", in "+(1+i)+": "+inputs2[i]);
        }

        lastOutput = sigmoid(sum);
        //System.out.println(", out: "+lastOutput);
        return lastOutput;
    }

    /**
     * Adjusts weights based on errors (doesn't actually update them, 
     * just calculates how much each weight should change, call 'updateWeights' after the 'train' method
     * to actually change the weights)
     * @param double[] Inputs, double[] Known outputs/desired outputs
     */
    public void train(double[] inputs, double desired[])
    {
        weights2 = copyArray(weights);
        if (row == net.getNeurons().size() - 1) //if outer layer
        {
            part = (lastOutput - desired[col]) * lastOutput*(1-lastOutput);
            //System.out.print("(n) Outer");
        }
        else //if hidden layer
        {
            part = 0;
            for (int c = 0; c < net.getNeurons().get(row+1).size(); c++)
            {
                Neuron n = net.getNeurons().get(row+1).get(c);
                part += n.getPart() * n.getWeights()[col];
            }
            part *= lastOutput * (1 - lastOutput);
            //System.out.print("(n) Inner");
        }
        for (int i = 0; i <weights.length; i++)
        {
            double in = 1;
            
            if (row == 0 && i != weights.length-1)
                in = inputs[i];
            else if(i != weights.length-1)
                in = net.getNeurons().get(row - 1).get(i).getLastOutput();
            else
                in = 1; //bias input
                
            //System.out.println(", Part: "+part+", in: " + in + ", out:" + lastOutput);
            weights2[i] -= r * part * in;
        }
        //System.out.println();
    }
    
    /**
     * After calling the 'train' methods, call this method to actually change the weight
     */
    public void updateWeights()
    {
        weights = weights2;
    }
    
    /**
     * Mimics a sigmoid function f(x) = 1/(1+e^(-x)
     * @param double inputs
     * @return double output
     */
    public static double sigmoid(double n)
    {
        return 1/(1+Math.exp(-n));
    }
    
    /**
     * Returns an array of doubles of the weights
     * @return double[] weights
     */
    public double[] getWeights()
    {
        return weights;
    }
    
    /**
     * Sets the weights to the input array of doubles
     * @param double[] new weights
     */
    public void setWeights(double[] w)
    {
        weights = w;
    }
    
    /**
     * Gets last output of this neuron without actually going through the calculations again
     * @return double last output
     */
    public double getLastOutput()
    {
        return lastOutput;
    }
    
    /**
     * Gets a part of the calculations used in the 'train' method
     * that can be used by other neurons in a network
     * @return double part
     */
    public double getPart()
    {
        return part;
    }
    
    /**
     * Copies an array of doubles (the actual value, not the reference)
     * @param double[] inputs
     * @return double[] output
     */
    public double[] copyArray(double[] in)
    {
        double[] out = new double[in.length];
        for (int i = 0; i < in.length; i++)
        {
            out[i]=in[i];
        }
        return out;
    }
}
