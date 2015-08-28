package org.terranga.payment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.stripe.*;
import com.stripe.model.*;
import com.stripe.exception.*;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCardCollection;
import com.twilio.sdk.TwilioRestException;
import org.terranga.general.*;


@SuppressWarnings("serial")
public class StripeServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		System.out.println("StripeServlet: GET");
		
		
		Map<String, Object> response = new HashMap<String, Object>();
		LoginHandler login = checkLogin(response, req); // Check user login
		if (login.getStatus() != 2){ // not logged in or unregistered. abort.
			resp.getWriter().print("User not logged in or unregistered.");
			return;
		}
		
		if (login.getProfile().getStripeId().equals("none")){
			resp.getWriter().print("User does not have a Stripe ID.");
			return;
		}
		
		RequestInfo request = new RequestInfo(req, resp);
		String resource = request.getResource();
		
		if (resource.equals("charge")){
			String stripeId = login.getProfile().getStripeId();
			if (stripeId.equals("none")){ // user did not enter credit card
				resp.getWriter().print("CONFIRMATION : Fail - user does not have credit card.");
				return;
			}
			
			Stripe.apiKey = "sk_live_FNZB1G4O0u9iyCrtTkzdIu6k";
			

			// charge the customer, not the card:
			Map<String, Object> chargeInfo = new HashMap <String, Object>();
			chargeInfo.put("amount", 150);
			chargeInfo.put("currency", "usd");
			chargeInfo.put("customer", login.getProfile().getStripeId());
			try {
				Charge.create(chargeInfo, Stripe.apiKey);
			} 
			catch (AuthenticationException e) {
				resp.getWriter().print("ERROR: "+e.getMessage());
				return;
			} 
			catch (InvalidRequestException e) {
				resp.getWriter().print("ERROR: "+e.getMessage());
				return;
			} 
			catch (APIConnectionException e) {
				resp.getWriter().print("ERROR: "+e.getMessage());
				return;
			} 
			catch (CardException e) {
				resp.getWriter().print("ERROR: "+e.getMessage());
				return;
			} 
			catch (APIException e) {
				resp.getWriter().print("ERROR: "+e.getMessage());
				return;
			}
			
			resp.getWriter().print("CONFIRMATION : Card successfully charged");
			return;
		}
	}
	
	
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");
		
		RequestInfo request = new RequestInfo(req, resp);
		String resource = request.getResource();
		
		// Apply stripe credit card to Profile account
		if (resource.equals("card")){ 
			
			Map<String, Object> response = new HashMap<String, Object>();
			LoginHandler login = checkLogin(response, req); // Check user login
			
			if (login.getStatus() != 2){ // not logged in or unregistered. abort.
				resp.setContentType("text/plain");
				resp.getWriter().print("User not logged in or unregistered.");
				return;
			}

			
			Exception exception = null;
			try { 
				Profile profile = login.getProfile();
				
				Stripe.apiKey = "sk_live_FNZB1G4O0u9iyCrtTkzdIu6k";
				Object token = req.getParameter("stripeToken");

				Map<String, Object> customerInfo = new HashMap <String, Object>();
				customerInfo.put("card", token);
				customerInfo.put("email", profile.getEmail());
				customerInfo.put("description", profile.getId());

				Customer customer = Customer.create(customerInfo);
				profile.setStripeId(customer.getId());
				
				try {
					Card card = customer.getCards().getData().get(0);
					
					JSONObject cardJson = new JSONObject();
					cardJson.put("lastFour", card.getLast4());
					cardJson.put("brand", card.getBrand());
					cardJson.put("expiration", Integer.toString(card.getExpMonth())+"/"+Integer.toString(card.getExpYear()));
					profile.setCardInfo(cardJson.toString());
				}
				catch(JSONException e){
					
				}

				profile.save();
				
				resp.sendRedirect("/site/account");
				return;

			}
			catch(CardException e){
				exception = e;
			} 
			catch (AuthenticationException e) { 
				exception = e;
			} 
			catch (InvalidRequestException e) {
				exception = e;
			}
			catch (APIConnectionException e) {
				exception = e;
			} 
			catch (APIException e) {
				exception = e;
			}
			
			if (exception != null){
				resp.setContentType("text/plain");
				resp.getWriter().print("ERROR: "+exception.getMessage());
				return;
				
			}
		}
		
		
		if (resource.equals("charge")){

			
		}
		
		
		/*
		// this comes from the Twilio handler:
		if (resource.equals("charge")){
			String phoneNumber = req.getParameter("From").replace("+1", ""); // check if authorized
			ArrayList<String> authorized = new ArrayList<String>();
			authorized.add("2037227160");
			authorized.add("2016284886"); // mauricio
			
			if (authorized.contains(phoneNumber)==false){
				sendEmail("dennykwon2@gmail.com", "ERROR\n\nUnauthorized phone number.");
				try {
					sendSMS("12014196862", phoneNumber, "ERROR\n\nUnauthorized phone number.");
				} catch(TwilioRestException e){
					sendEmail("dennykwon2@gmail.com", "ERROR: "+e.getMessage());
				}
				return;
			}

			String content = req.getParameter("Body").trim();
			String body = content.toLowerCase();

			if (body.contains(":")==false){
				sendEmail("dennykwon2@gmail.com", "FORMAT ERROR\n\nPlease follow this format:\n\norder number:order price (without delivery fee)");
				try {
					sendSMS("12014196862", phoneNumber, "FORMAT ERROR\n\nPlease follow this format:\n\norder number:order price (without delivery fee)");
				} catch(TwilioRestException e){
					sendEmail("dennykwon2@gmail.com", "ERROR: "+e.getMessage());
				}
				return;
			}
			
			String[] parts = body.split(":"); // order number:price
			if (parts.length < 3){
				sendEmail("dennykwon2@gmail.com", "FORMAT ERROR - Format the text like this\n\norder number : $price : $delivery fee");
				try {
					sendSMS("12014196862", phoneNumber, "FORMAT ERROR - Format the text like this\n\norder number : $price : $delivery fee");
				} catch(TwilioRestException e){
					sendEmail("dennykwon2@gmail.com", "ERROR: "+e.getMessage());
				}
				
				return;
			}
			
	        String orderId = parts[0].replace(" ", "");
	        String price = parts[1].replace(" ", "");
	        String deliveryFee = parts[2].replace(" ", "");

	        
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        Order order = Order.fetchOrder(datastore, orderId);
	        if (order==null){
				sendEmail("dennykwon2@gmail.com", "ERROR: Order "+orderId+" not found.");
				try {
					sendSMS("12014196862", phoneNumber, "ERROR: Order "+orderId+" not found.");
				} catch(TwilioRestException e){
					sendEmail("dennykwon2@gmail.com", "ERROR: "+e.getMessage());
				}
				
				return;
	        }
	        
	        price = price.replace("$", "");
	        Double p = Double.parseDouble(price);
	        if (p > 100){
				try {
					sendSMS("12014196862", phoneNumber, "ERROR: Price exceeds $100: "+Double.toString(p)+". You probably forgot the decimal point.");
				} 
				catch(TwilioRestException e){
					sendEmail("dennykwon2@gmail.com", "ERROR: "+e.getMessage());
				}

				return;
	        }
	        
	        deliveryFee = deliveryFee.replace("$", "");

	        
	        order.setPrice(Double.parseDouble(price));
	        order.setDeliveryFee(Double.parseDouble(deliveryFee));
	        order.save(datastore);
	        
	        
	        String profileId = order.getProfile();
	        Profile profile = Profile.fetchProfile(datastore, profileId);
	        if (profile==null) { // shouldn't happen
				try {
					sendSMS("12014196862", phoneNumber, "Profile "+profileId+" not found.");
				} catch(TwilioRestException e){
					sendEmail("dennykwon2@gmail.com", "ERROR: "+e.getMessage());
				}
	        	return;
	        }
	        
	        if (profile.getStripeId().equals("none")) { // no credit card on file, bail out.
				try {
					sendSMS("12014196862", phoneNumber, "Profile "+profileId+" does not have a credit card on file.");
				} 
				catch(TwilioRestException e){
					sendEmail("dennykwon2@gmail.com", "ERROR: "+e.getMessage());
				}
				return;
	        }

			
			Double total = p+Double.parseDouble(deliveryFee);
			
			if (total > 100){
				try {
					sendSMS("12014196862", phoneNumber, "ERROR: Total exceeds $100: "+Double.toString(total)+". You probably forgot the decimal point.");
				} 
				catch(TwilioRestException e){
					sendEmail("dennykwon2@gmail.com", "ERROR: "+e.getMessage());
				}
				return;
			}
			
			total = total*100; // convert to cents
			
			Stripe.apiKey = "sk_live_W7Hn8J2Cq4VDaT7KGjWS2n4u";
			Map<String, Object> chargeInfo = new HashMap <String, Object>(); // charge the customer, not the card:
			chargeInfo.put("amount", total.intValue());
			chargeInfo.put("currency", "usd");
			chargeInfo.put("customer", profile.getStripeId());
			
			try {
				Charge.create(chargeInfo, Stripe.apiKey);
				
//				if (profile.getEmail().equals("none")==false)
//					sendEmail(profile.getEmail(), "Email Body");
				
			} 
			catch (AuthenticationException e) {
				sendEmail("dennykwon2@gmail.com", "ERROR: "+e.getMessage());
				try {
					sendSMS("12014196862", phoneNumber, "ERROR: "+e.getMessage());
				} catch(TwilioRestException error){
					sendEmail("dennykwon2@gmail.com", "ERROR: "+error.getMessage());
				}
				return;
			} 
			catch (InvalidRequestException e) {
				sendEmail("dennykwon2@gmail.com", "ERROR: "+e.getMessage());
				try {
					sendSMS("12014196862", phoneNumber, "ERROR: "+e.getMessage());
				} catch(TwilioRestException error){
					sendEmail("dennykwon2@gmail.com", "ERROR: "+error.getMessage());
				}
				return;
			} 
			catch (APIConnectionException e) {
				sendEmail("dennykwon2@gmail.com", "ERROR: "+e.getMessage());
				try {
					sendSMS("12014196862", phoneNumber, "ERROR: "+e.getMessage());
				} catch(TwilioRestException error){
					sendEmail("dennykwon2@gmail.com", "ERROR: "+error.getMessage());
				}
				return;
			} 
			catch (CardException e) {
				sendEmail("dennykwon2@gmail.com", "ERROR: "+e.getMessage());
				try {
					sendSMS("12014196862", phoneNumber, "ERROR: "+e.getMessage());
				} catch(TwilioRestException error){
					sendEmail("dennykwon2@gmail.com", "ERROR: "+error.getMessage());
				}
				return;
			} 
			catch (APIException e) {
				sendEmail("dennykwon2@gmail.com", "ERROR: "+e.getMessage());
				try {
					sendSMS("12014196862", phoneNumber, "ERROR: "+e.getMessage());
				} catch(TwilioRestException error){
					sendEmail("dennykwon2@gmail.com", "ERROR: "+error.getMessage());
				}
				return;
			}

			
			try {
				total = total/100.0D; // convert back to dollars
				sendSMS("12014196862", phoneNumber, "CONFIRMATION : Card successfully charged , $"+Double.toString(total));
			} 
			catch(TwilioRestException error){
				sendEmail("dennykwon2@gmail.com", "ERROR: "+error.getMessage());
			}

			return;
		}
		*/
		
//		if (resource.equals("charge")){
//			
//		}
		
		

	}
	
	
	
	// Use this for extracting body data from http request:
	public static String getBody(HttpServletRequest request) throws IOException {
	    String body = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    BufferedReader bufferedReader = null;

	    try {
	        InputStream inputStream = request.getInputStream();
	        if (inputStream != null) {
	            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	            char[] charBuffer = new char[128];
	            int bytesRead = -1;
	            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
	                stringBuilder.append(charBuffer, 0, bytesRead);
	            }
	        } 
	        else {
	            stringBuilder.append("");
	        }
	    } 
	    catch (IOException ex) {
	        throw ex;
	    } 
	    
	    finally {
	        if (bufferedReader != null) {
	            try {
	                bufferedReader.close();
	            } 
	            catch (IOException ex) {
	                throw ex;
	            }
	        }
	    }

	    body = stringBuilder.toString();
	    return body;
	}
	
	
	private LoginHandler checkLogin(Map<String, Object> response, HttpServletRequest req){
		response.put("loggedIn", "no");
		
		LoginHandler login = new LoginHandler(req);
		if (login.getStatus() != 2)  // not logged in
			return login;

		response.put("loggedIn", "yes");
		response.put("currentUser", login.getProfile().getSummary());
		return login;
	}
	




}
