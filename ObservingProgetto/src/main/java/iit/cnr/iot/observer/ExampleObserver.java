/*******************************************************************************
 * Copyright (c) 2012, Institute for Pervasive Computing, ETH Zurich.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the Californium (Cf) CoAP framework.
 ******************************************************************************/
package iit.cnr.iot.observer;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import ch.ethz.inf.vs.californium.CaliforniumLogger;
import ch.ethz.inf.vs.californium.Utils;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.scandium.ScandiumLogger;


public class ExampleObserver {
	
	
	
	
	private static int count=0;
	
	private static final int NUM_PARK=5;
	
	private static String hostAccess="aaaa::c30c:0:0:66";
	private static String hostExit="aaaa::c30c:0:0:66";

	private static URI uri = null;
	
	
	public synchronized static void incrementCounter(){
		if(count<NUM_PARK){
			count++;
			if(count==NUM_PARK){
				sendLedMessage(true);
			}
		}
	}
	
	public synchronized static void decrementCounter(){
		if(count>0){
			count--;
			if(count==0){
				sendLedMessage(false);
			}
		}
	}
	
	
	
	static {
		CaliforniumLogger.initialize();
		CaliforniumLogger.setLevel(Level.WARNING);
		
		ScandiumLogger.initializeLogger();
		ScandiumLogger.setLoggerLevel(Level.INFO);
	}
	

	/*
	 * Main method of this client.
	 */
	public static void main(String[] args) {
		
		ThreadObserver accessThread= new ThreadObserver(hostAccess);
		accessThread.start();
		
		
		
		ThreadObserver exitThread= new ThreadObserver(hostExit);
		exitThread.start();
		
		/*		
		String prova= "<?xml version=\"1.0\" encoding=\"UTF-8\"?><message><type>Accesso</type><timestamp>2002-05-30T09:00:00</timestamp></message>";
		XmlValidator xmlV=new XmlValidator(prova);
		try {
			
			Stato stato= xmlV.parseDocument();
			if(stato.equals(Stato.Accesso)){
				count++;
			}
			else
				count--;
			
			if(count==NUM_PARK){
				sendLedMessage(true);
			}
			
			else{
				sendLedMessage(false);
			}
			
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
	}
	
	private static void sendLedMessage(boolean on){
		// PUT with payload
				try {
					uri = new URI("coap://["+hostAccess+"]:5683/actuators/led");
				} catch (URISyntaxException e) {
					System.err.println("Failed to parse URI: " + e.getMessage());
					System.exit(-1);
				}

				// create request according to specified method
				Request request = Request.newPut();
				request.setURI(uri);
				request.setPayload("on=" + on);
				request.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN);
				
				// execute request
				try {
					request.send();
			
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
					} else {
						// no response received	
						System.err.println("Request timed out");
					}
						
				} catch (Exception e) {
					System.err.println("Failed to execute request: " + e.getMessage());
					System.exit(-1);
				}

	}
	
	
	
	
	
		
	
	
	
	
	

}
