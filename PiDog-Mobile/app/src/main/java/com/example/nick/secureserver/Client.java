package com.example.nick.secureserver;

import java.io.*;
import java.net.Socket;

public class Client {

	private String host;
	private int port;
	private boolean connection;
    private Socket client;
    private PrintStream output;

	public Client(String h, int p) throws Exception {
		host = h;
		port = p;
		connection = false;
	}

	public void establish() throws Exception {
        client = new Socket(host, port);
        output = new PrintStream(client.getOutputStream());
	}

	public void send(String s)
    {
        output.println(s);
    }

	}

