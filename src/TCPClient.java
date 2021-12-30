import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class TCPClient {

	public static void main(String[] args) throws IOException {

		//hardcoded in
		String hostName = "127.0.0.1";
        int portNumber = 30121;
        
        try (Socket clientSocket = new Socket(hostName, portNumber);
            PrintWriter requestWriter = // stream to write text to server
                new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader responseReader= // stream to read from server
                new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())); 
            BufferedReader stdIn = // standard input stream to get user's requests
                new BufferedReader(
                    new InputStreamReader(System.in))
	        ){
        	
        	String userInput;
        	String serverResponse;
        	
        	while ((userInput = stdIn.readLine()) != null) {
            	requestWriter.println(userInput);
            	serverResponse = responseReader.readLine();
            	ArrayList<String> packets = new ArrayList<>(); //to hold the packets
            	
            	boolean incomplete = true;
            	while(incomplete) { //while the client didn't receive all packets of the message
            		//add to arrayList of packets if the packet isn't the final one
	            	while(!serverResponse.substring(0,2).equals("FI")) { 
						packets.add(serverResponse);
						serverResponse = responseReader.readLine();
					}
	        
	            	String missingPackets = findMissing(packets);
	            	if(missingPackets.equals("")) { 	//If no missing packets
	            		incomplete = false;
	            	}
	            	else {
	            		requestWriter.println("ERROR621," + missingPackets);
	            		serverResponse = responseReader.readLine();
	            	}
            	}
            	//Order the packets so the message comes out in order
            	System.out.println("Server Response: " + orderPackets(packets));
        	}
        	
        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } 
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        } 
	}
	
	public static String findMissing(ArrayList<String> packets){
		StringBuilder str = new StringBuilder();
		
		int total = Integer.parseInt(packets.get(0).split("//")[1]);
		int[] receivedPackets = new int[total];
		
		//for each packet that was received, make the int in that index = 1
		for(String packet: packets) {
			int packetIndex = Integer.parseInt(packet.split("//")[0].substring(1));
			receivedPackets[packetIndex] = 1;
		}
		
		//if the element isn't = 1, that packet number hasn't been received and add that
		//index to the missing packet string
		for(int i = 0; i < receivedPackets.length; i++) {
			if(receivedPackets[i] == 0) {
				str.append(i + ",");
			}
		}
		return str.toString();
	}
	
	public static String orderPackets(ArrayList<String> packets) {
		String[] message = new String[packets.size()];
		StringBuilder str = new StringBuilder();
		
		//put packets in order
		for(String p: packets) {
			message[Integer.parseInt(p.split("//")[0].substring(1))] = p.substring(0,1);
		}
		
		//Create one string with the ordered packets
		for(String letter: message) {
			str.append(letter);
		}
		return str.toString();
	}
}
