package robot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DNSClient {
    private static final String DNS_SERVER_ADDRESS = "8.8.8.8";
    private static final int DNS_SERVER_PORT = 53;

    public URLInfo DNSLookup(String domain) throws IOException {
        InetAddress ipAddress = InetAddress.getByName(DNS_SERVER_ADDRESS);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeShort(0x9999);
        dos.writeShort(0x0100);
        dos.writeShort(0x0001);
        dos.writeShort(0x0000);
        dos.writeShort(0x0000);
        dos.writeShort(0x0000);
	    writeQuestion(dos, domain);
        dos.writeShort(0x0001);
        dos.writeShort(0x0001);

        byte[] dnsFrame = baos.toByteArray();

        DatagramSocket socket = new DatagramSocket();
        System.out.println();
        DatagramPacket dnsReqPacket = new DatagramPacket(dnsFrame, dnsFrame.length, ipAddress, DNS_SERVER_PORT);
        socket.send(dnsReqPacket);

        byte[] buf = new byte[512];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        System.out.println("\n\nReceived: " + packet.getLength() + " bytes");

        for (int i = 0; i < packet.getLength(); i++) {
            System.out.print(" 0x" + String.format("%x", buf[i]) + " " );
        }
        System.out.println("\n");


        DataInputStream din = new DataInputStream(new ByteArrayInputStream(buf));
        System.out.println("Transaction ID: 0x" + String.format("%x", din.readShort()));
        System.out.println("Flags: 0x" + String.format("%x", din.readShort()));
        System.out.println("Questions: 0x" + String.format("%x", din.readShort()));
        System.out.println("Answers RRs: 0x" + String.format("%x", din.readShort()));
        System.out.println("Authority RRs: 0x" + String.format("%x", din.readShort()));
        System.out.println("Additional RRs: 0x" + String.format("%x", din.readShort()));

        int recLen = 0;
        while ((recLen = din.readByte()) > 0) {
            byte[] record = new byte[recLen];

            for (int i = 0; i < recLen; i++) {
                record[i] = din.readByte();
            }

            System.out.println("Record: " + new String(record, "UTF-8"));
        }

        System.out.println("Record Type: 0x" + String.format("%x", din.readShort()));
        System.out.println("Class: 0x" + String.format("%x", din.readShort()));

        System.out.println("Field: 0x" + String.format("%x", din.readShort()));
        System.out.println("Type: 0x" + String.format("%x", din.readShort()));
        System.out.println("Class: 0x" + String.format("%x", din.readShort()));
        int TTL = din.readShort();
        System.out.println("TTL: 0x" + String.format("%x", TTL));

        short addrLen = din.readShort();
        System.out.println("Len: 0x" + String.format("%x", addrLen));

        System.out.print("Address: ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < addrLen-1; i++ ) {
            sb.append("" + String.format("%d", (din.readByte() & 0xFF)) + ".");
        }
        sb.append("" + String.format("%d", (din.readByte() & 0xFF)));
        System.out.println(sb.toString());
        URLInfo url =  new URLInfo(sb.toString(), TTL);
        return url;
        
    }
    
    public static void writeQuestion(DataOutputStream dos, String domain) throws IOException {
    	int index = domain.indexOf("http://");
    	index = index == -1 ? 0 : 7;
    	int indexDot = domain.indexOf('.', index);
    	while (indexDot != -1){
    		int len = indexDot - index;
    		String substr = domain.substring(index, indexDot); 
    		dos.writeByte(len);
            dos.write(substr.getBytes());
            index = indexDot + 1;
            if (index < domain.length())
            	indexDot = domain.indexOf('.', index);
            else 
            	indexDot = -1;
    	}
    	int len = domain.length() - index;
		String substr = domain.substring(index, domain.length()); 
		dos.writeByte(len);
        dos.write(substr.getBytes());
    	dos.writeByte(0x00);
    }
}
