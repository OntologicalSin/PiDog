import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
/**
 * SecureServer.java
 *
 * @author: Nick L
 * Brief Program Description: A server that regulars clients that are communicating
 */
public class SecureServer {
    private int port;
    private List<PrintStream> clients;
    private ServerSocket server;
    /**
     * Main Runner
     */
    public static void main(String[] args) throws Exception
    {
        SecureServer s = new SecureServer(420);
        s.establish();
    }

    /**
     * Constructor
     * @param port number and passphrase
     */
    public SecureServer(int p) throws Exception {
        port = p;
        clients = new ArrayList<PrintStream>();
    }

    /**
     * Establishes the server
     */
    public void establish() throws Exception {
        server = new ServerSocket(port);
        System.out.println("Port: "+port+" is now open to accept connections.");
        while (true) {
            Socket client = server.accept();  
            System.out.println("Connection established with client: " + client.getInetAddress().getHostAddress());
            this.clients.add(new PrintStream(client.getOutputStream()));
            new Thread(new Handler(this, client.getInputStream())).start();
        }
    }

    /**
     * Distributes messages to clients
     */
    public void distribute(String m) throws Exception { //add selective sending
        for (PrintStream client : this.clients) {
            client.println(m);
        }
    }

    class Handler implements Runnable{ //adds compatibility with Threads
        private final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin #01 as an output pin and turn on
        private  final GpioPinDigitalOutput pin17 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "pin17", PinState.HIGH);
        private final GpioPinDigitalOutput pin22 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "pin22", PinState.HIGH);
        private final GpioPinDigitalOutput pin23 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "pin23", PinState.HIGH);
        private  final GpioPinDigitalOutput pin24 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "pin24", PinState.HIGH);

        private String ch;

        private SecureServer server;
        private InputStream client;
        /**
         * Constructor
         * @param A Server and an input stream belonging to clien
         */
        public Handler(SecureServer s, InputStream c) {
            server = s;
            client = c;
        }

        /**
         * An overriden method using the Runnable interface for compatability with threading
         * Verifys the Clients password, and allows it to connect to the server
         */
        @Override
        public void run() {
            int[] setup = {80,40,22}; //The first layer has 32 Neurons, the 2nd has 16 and 3rd has 10.
            Network net = new Network(setup, Pix.getMaxLength()); //Creates the Network
            net.loadWeights("single14");
            Scanner s = new Scanner(this.client);//currently all, add selective sending
            while (s.hasNextLine()) {
                try{
                    ch = s.nextLine();
                    System.out.println(ch);
                    pin17.setShutdownOptions(true, PinState.LOW);
                    pin22.setShutdownOptions(true, PinState.LOW);
                    pin23.setShutdownOptions(true, PinState.LOW);
                    pin24.setShutdownOptions(true, PinState.LOW);
                    pin17.low();
                    pin22.low();
                    pin23.low();
                    pin24.low();
                    while(!(ch.equals("stopped"))){
                        System.out.println(ch);
                        System.out.println("ﬁnding coke");
                        String[] args = new String[] {"/bin/bash", "-c", "raspistill -t 1000 -o test.png -w 50 -h 50", "with", "args"};
                        Process proc = new ProcessBuilder(args).start();
                        Thread.sleep(1500);
                        double[] act = net.feedForward(Pix.getPicArray("test.png"));
                        double high = Runner.arrayToNum(act);
                        System.out.println(high);
                        if(high==-1)//no cola: spin
                        {
                            pin22.pulse(250, true);
                            args = new String[] {"/bin/bash", "-c", "raspistill -t 1000 -o test.png -w 50 -h 50", "with", "args"};
                            proc = new ProcessBuilder(args).start();
                            Thread.sleep(1000);
                            act = net.feedForward(Pix.getPicArray("test.png"));
                            high = Runner.arrayToNum(act);
                            System.out.println(high);
                        }
                         if(high<0.8) //turn right
                        {
                            pin24.pulse(250,true);
                            pin22.toggle();
                            pin24.toggle();
                        }
                        else if(high<1.2) //go straight
                        {
                            pin22.toggle();
                            pin24.toggle();
                        }
                        else //turn left
                        {
                          pin22.pulse(250,true);
                            pin22.toggle();
                            pin24.toggle();
                        }
                        ch = s.nextLine();
                    }

                }
                catch(Exception e)
                {}
            }
            s.close();
        }

    }
}