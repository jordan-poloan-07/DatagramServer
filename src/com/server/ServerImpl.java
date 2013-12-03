package com.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ServerImpl {

	interface ServerImplCallBack {
		void callback(String message);
	}

	private ServerImplCallBack sImplCallBack;

	public ServerImpl(ServerImplCallBack sImplCallBack) {
		this.sImplCallBack = sImplCallBack;
	}

	public void startServerImpl() {
		new MainReceiveThread().start();
	}

	public void sendToAll(String message) {

		try {

			DatagramSocket socket = new DatagramSocket(4445); // create a datagram socket in port 4445

			byte[] buf = message.trim().getBytes(); // get the byte array of the message

			InetAddress group = InetAddress.getByName("230.0.0.1"); // 230.0.0.1 is a valid a multicasting address
			// server will broadcast the message in the multicast address
			
			// create the datagrampacket to be broadcast
			// pass the byte array and its length and the InetAddress of the multicast address, and the port to be used
			DatagramPacket packet = new DatagramPacket(buf, buf.length, group,
					4446);

			// send the pocket using the socket in port 4445 of the server
			socket.send(packet);
			// close after sending
			socket.close();
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void receivePacket() {

		DatagramSocket datagramSocket = null;

		try {

			// listen for messages in port 9770
			datagramSocket = new DatagramSocket(9770);

			// create and empty byte buffer to store the received message
			byte[] buffer = new byte[60000];

			// the packet will be used to fille the byte array
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			
			// after it receiveing a message, the packet will be filled with message
			datagramSocket.receive(packet);
			
			// run a thread that broadcasts message to the multicast address
			// (needed to broadcast one message of a client to other clients
			new ServerReceiveThread(new String(buffer).trim()).start();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			datagramSocket.close();
		}

	}

	class MainReceiveThread extends Thread {
		@Override
		public void run() {
			while (true) {
				receivePacket();
			}
		}

	}

	class ServerReceiveThread extends Thread {

		private String message;

		public ServerReceiveThread(String message) {
			this.message = message;
		}

		@Override
		public void run() {
			sImplCallBack.callback(message);
			sendToAll(message);
		}

	}

}
