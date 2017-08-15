package com.lk.service.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The listener interface for receiving service events.
 * The class that is interested in processing a service
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addServiceListener<code> method. When
 * the service event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ServiceEvent
 */
public class ServiceListener implements Runnable {
	
	/** The m socket. */
	private final Socket mSocket;
	
	/** The m num. */
	private final int mNum;

	/**
	 * Instantiates a new service listener.
	 *
	 * @param socket the socket
	 * @param num the num
	 */
	ServiceListener(Socket socket, int num) {
		mSocket = socket;
		mNum = num;

		Thread handler = new Thread(this, "handler-" + mNum);
		handler.start();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			try {
				System.out.println(mNum + " Connected.");
				BufferedReader in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
				OutputStreamWriter out = new OutputStreamWriter(mSocket.getOutputStream());
				out.write("Welcome connection #" + mNum + "\n\r");
				out.flush();

				while (true) {
					String line = in.readLine();
					if (line == null) {
						System.out.println(mNum + " Closed.");
						return;
					} else {
						System.out.println(mNum + " Read: " + line);
						if (line.equals("exit")) {
							System.out.println(mNum + " Closing Connection.");
							return;
						}
						else {
							System.out.println(mNum + " Write: echo " + line);
							out.write("echo " + line + "\n\r");
							out.flush();
						}
					}
				}
			} finally {
				mSocket.close();
			}
		} catch (IOException e) {
			System.out.println(mNum + " Error: " + e.toString());
		}
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		int port = 9000;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		}
		System.out.println("Accepting connections on port: " + port);
		int nextNum = 1;
		ServerSocket serverSocket = new ServerSocket(port);
		while (true) {
			Socket socket = serverSocket.accept();
			ServiceListener hw = new ServiceListener(socket, nextNum++);
		}
	}
}