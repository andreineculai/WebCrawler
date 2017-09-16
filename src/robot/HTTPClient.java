package robot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.net.UnknownHostException;

public class HTTPClient {
	public String version;
	public int port;

	public HTTPClient(String version, int port) {
		super();
		this.version = version;
		this.port = port;
	}

	
	public HTTPClient() {
		super();
		version = "HTTP/1.1";
		port = 80;
	}


	public String GET(String URL) throws Exception {
//		int indexStartHost = URL.indexOf("http://");
//		indexStartHost = indexStartHost == -1 ? 0 : 7;
		int indexStartLocal = URL.indexOf('/');
		String host = URLParsingUtils.getHost(URL);
		String localAddress = URLParsingUtils.getLocalAddress(URL);
		//DNSClient dnsClient = new DNSClient();
		//URLInfo hostAddress = dnsClient.DNSLookup(host);
		//URI uri = new URI(host);
		String IP = null;
		try {
			IP = InetAddress.getByName(host).getHostAddress();
		} catch (Exception e) {
			return null;
		}

		//URLInfo urlInfo = new URLInfo(uri.getHost(), uri.get)
		return SendRequest(IP, port, buildGetRequest(localAddress, host));
	}

	public String SendRequest(String host, int port, String req) throws Exception {
		Socket socket = new Socket();
		socket.setSoTimeout(1000);
		try {
			socket.connect(new InetSocketAddress(host,  port), 1000);	
		} catch (Exception e) {
			socket.close();
			return null;
		}
		PrintWriter wtr = new PrintWriter(socket.getOutputStream());
		//System.out.println(req);
		wtr.print(req);
		wtr.flush();
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String outStr;
		StringBuilder fullResponse = new StringBuilder();
		// Prints each line of the response
		try {
			while ((outStr = br.readLine()) != null) {
				//System.out.println(outStr);
				fullResponse.append(outStr+'\n');
			}	
		} catch (Exception e) {
			return null;
		}
		// Closes out buffer and writer
		br.close();
		wtr.close();
		socket.close();
		return fullResponse.toString();
	}

	public String buildGetRequest(String localAddress, String hostAddress) throws Exception {
		StringBuilder request = new StringBuilder();
		request.append("GET " + localAddress + ' ' + version + "\r\n");
		request.append("Host: " + hostAddress + "\r\n");
		request.append("User-Agent: RIW\r\n");
		request.append("Connection: close\r\n");
		request.append("\r\n");
		return request.toString();
	}

}
