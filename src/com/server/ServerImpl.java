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

			DatagramSocket socket = new DatagramSocket(4445);

			byte[] buf = message.trim().getBytes();

			InetAddress group = InetAddress.getByName("230.0.0.1");
			DatagramPacket packet = new DatagramPacket(buf, buf.length, group,
					4446);

			socket.send(packet);
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

			datagramSocket = new DatagramSocket(9770);

			byte[] buffer = new byte[60000];

			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			datagramSocket.receive(packet);

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
