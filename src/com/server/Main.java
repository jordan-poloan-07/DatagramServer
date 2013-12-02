package com.server;

import java.net.SocketException;

public class Main {
	public static void main(String[] args) {
		try {
			new Server();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
}
