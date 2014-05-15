package iit.cnr.iot.observer;

import iit.cnr.iot.observer.XmlValidator.Stato;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.jdom2.JDOMException;

import ch.ethz.inf.vs.californium.Utils;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;

public class ThreadObserver extends Thread {
	
	
	private String host;
	
	
	public ThreadObserver(String host) {
		this.host=host;
	}
	
	@Override
	public void run() {
		URI uri = null;
		try {
			uri = new URI("coap://["+host+"]:5683/periodic");
		} catch (URISyntaxException e) {
			System.err.println("Failed to parse URI: " + e.getMessage());
			System.exit(-1);
		}

		// create request according to specified method
		Request request = Request.newGet();
		request.setURI(uri);
		request.setPayload("");
		request.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN);
		request.getOptions().setObserve(1);
		
		// execute request
		try {
			request.send();
	
			do {
				// receive response
				Response response = null;
				try {
					response = request.waitForResponse();
				} catch (InterruptedException e) {
					System.err.println("Failed to receive response: " + e.getMessage());
					System.exit(-1);
				}
	
				// output response
				if (response != null) {
					System.out.println(Utils.prettyPrint(response));
					System.out.println("Time elapsed (ms): " + response.getRTT());
					XmlValidator xmlV=new XmlValidator(response.getPayloadString());
					try {
						Stato stato= xmlV.parseDocument();
						if(stato.equals(Stato.Accesso)){
							ExampleObserver.incrementCounter();
						}
						else
							ExampleObserver.decrementCounter();
					} catch (JDOMException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} else {
					// no response received	
					System.err.println("Request timed out");
				}
			} while (true);
				
		} catch (Exception e) {
			System.err.println("Failed to execute request: " + e.getMessage());
			System.exit(-1);
		}	
	
	}
	
	
	

}
