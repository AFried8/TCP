import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

//This server does the simple job of echoing the clients message back to the client
//since the focus of this assignment is the transmission protocol and not what exactly
//the server does with the client's message

public class TCPServer {

	public static void main(String[] args) throws IOException{
		
		//hardcoded because it runs in the console
		int portNumber = 30121;
		Random rand = new Random();
		
		try (ServerSocket serverSocket = new ServerSocket(portNumber);
				Socket clientSocket1 = serverSocket.accept();
				PrintWriter responseWriter= new PrintWriter(clientSocket1.getOutputStream(), true);
				BufferedReader requestReader1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
				
			) {
			String usersRequest;
			ArrayList<String> response = new ArrayList<>();
			while ((usersRequest = requestReader1.readLine()) != null) {
				System.out.println("\"" + usersRequest + "\" received");
				
				//If its not an error message
				if(usersRequest.length()< 8 || !usersRequest.substring(0, 8).equals("ERROR621")) {
					response = createPackets(usersRequest);
				}
				//If it is an error message
				else {
					response = createPacketsFromError(usersRequest, response);
				}
				
				Collections.shuffle(response);
				System.out.println("Sending message back to client");
				
				for(int i = 0; i <response.size(); i++) {
					if(rand.nextInt(100) <80) {
						responseWriter.println(response.get(i));
					}
				}
				responseWriter.println("FINAL");
				
			}
			
		}
		catch (IOException e) {
			System.out.println(
					"Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
		}
	}
	
	 public static ArrayList<String> createPackets (String message){
		 ArrayList<String> packets = new ArrayList<>();
		 if(message.equals("")) {
			 message = "No input entered";
		 }
		 //break down message into individual characters
		 char[] chars = message.toCharArray();
		 for(int i = 0; i<chars.length; i++) {
    		packets.add(chars[i] + String.valueOf(i) + "//" + chars.length);	
		 }
	    	return packets;
	    }
	 
	 public static ArrayList<String> createPacketsFromError(String error, ArrayList<String> oldPackets){
		 ArrayList<String> requestedPackets = new ArrayList<>();
		 
		 //Whatever's in the error message after "ERROR621"
		 error = error.substring(8);
		 
		    for(String i: oldPackets) {
		    	if(error.contains(","+i.split("//")[0].substring(1)+",")) {
		    		requestedPackets.add(i);
		    	}
		    }
		 return requestedPackets;
	 }

}
