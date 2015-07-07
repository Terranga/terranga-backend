package org.terranga.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

import org.terranga.general.*;

@SuppressWarnings("serial")
public class APIServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		allowCORSAccess(req, resp);
		resp.setContentType("application/json");
		
		RequestInfo request = new RequestInfo(req, resp);
		String resource = request.getResource();
		Map<String, Object> response = new HashMap<String, Object>();
		LoginHandler login = checkLogin(response, req); // Check user login
		
		if (login.getStatus() == 2){
			Map<String, Object> currentUserSummary = login.getProfile().getSummary();
			currentUserSummary.put("loggedIn", "yes");
			response.put("currentUser", currentUserSummary);
		}
		
		if (resource.equals("test")){
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        Profile.fetchProfiles(datastore, 0);
	        
	        
	        
	        //Endorsement.fetchEndorsements(datastore, 0);
//	        Insight.fetchInsightsWithProfileID(datastore, "123123", 0);	  
//	        Endorsement.fetchEndorsementsWithEndorsed(datastore, "1", 0);
//	        Review.fetchReviewsWithReviewed(datastore, "1", 0);
//	        Dream.fetchDreamsWithProfileID(datastore, "123", 0);
	        
		}
		
		
		
		if (resource.equals("upload")){ 
			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			response.put("confirmation", "success");
			
			String media = req.getParameter("media"); // images, videos, pdf, etc
			if (media==null)
				media = "images";
			
			response.put("upload", blobstoreService.createUploadUrl("/api/"+media));
			JSONObject json = new JSONObject(response);
			resp.getWriter().println(json.toString());
			return;
		}
		
		
		if (resource.equals("currentuser")){
			if (login.getStatus() != 2){
				response.put("confirmation", "fail");
				response.put("message", "Not logged in.");
		        	
				JSONObject json = new JSONObject(response);
				resp.getWriter().println(json.toString());
				return;
			}

			response.put("confirmation", "success");
			JSONObject json = new JSONObject(response);
			resp.getWriter().println(json.toString());
			return;
		}

		
		if (resource.equals("profiles")){
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			String profileId = request.getResourceIdentifier();
			if (profileId != null){
				Profile profile = Profile.fetchProfile(datastore, profileId);
				if (profile==null){
					response.put("confirmation", "fail");
					response.put("message", "Profile "+profileId+" not found.");
			        	
					JSONObject json = new JSONObject(response);
					resp.getWriter().println(json.toString());
					return;
				}
				
				Map<String, Object> summary = profile.getSummary();
				
				//ADD INSIGHTS TO PROFILE SUMMARY
				ArrayList<Map<String, Object>> insightSummaries = new ArrayList<Map<String, Object>>();
				ArrayList<Insight> insights = Insight.fetchInsightsWithProfileID(datastore, profile.getId(), 0);
				for (int i=0; i<insights.size(); i++){
					Insight insight = insights.get(i);
					insightSummaries.add(insight.getSummary());
				}
				summary.put("insights", insightSummaries);
				
				//ADD DREAMS TO PROFILE SUMMARY
				ArrayList<Map<String, Object>> dreamSummaries = new ArrayList<Map<String, Object>>();
				ArrayList<Dream> dreams = Dream.fetchDreamsWithProfileID(datastore, profile.getId(), 0);
				for (int i=0; i<dreams.size(); i++){
					Dream dream = dreams.get(i);
					dreamSummaries.add(dream.getSummary());
				}
				summary.put("dreams", dreamSummaries);
				
				//ADD REVIEWS TO PROFILE SUMMARY
				ArrayList<Map<String, Object>> reviewSummaries = new ArrayList<Map<String, Object>>();
				ArrayList<Review> reviews = Review.fetchReviewsWithReviewed(datastore, profile.getId(), 0);
				for (int i=0; i<reviews.size(); i++){
					Review review = reviews.get(i);
					reviewSummaries.add(review.getSummary());
				}
				summary.put("reviews", reviewSummaries);
				
				//ADD ENDORSMEMENTS TO PROFILE SUMMARY
				ArrayList<Map<String, Object>> endorsementSummaries = new ArrayList<Map<String, Object>>();
				ArrayList<Endorsement> endorsements = Endorsement.fetchEndorsementsWithEndorsed(datastore, profile.getId(), 0);
				for (int i=0; i<endorsements.size(); i++){
					Endorsement endorsement = endorsements.get(i);
					endorsementSummaries.add(endorsement.getSummary());
				}
				summary.put("endorsements", endorsementSummaries);
				
				
				
				response.put("confirmation", "success");
				response.put("profile", summary);
		        	
				JSONObject json = new JSONObject(response);
				resp.getWriter().println(json.toString());
				return;
			}

			
			String limit = req.getParameter("limit");
			if (limit==null)
				limit = "0";
			
	        ArrayList<Profile> profiles = null;
			String featured = req.getParameter("featured");
			if (featured != null)
				profiles = Profile.fetchProfilesWithFeatured(datastore, featured, Integer.parseInt(limit));
			
			if (profiles==null)
		        profiles = Profile.fetchProfiles(datastore, Integer.parseInt(limit));

			
	        ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
	        for (Profile profile : profiles){
	        	
				Map<String, Object> summary = profile.getSummary();
				ArrayList<Map<String, Object>> insightSummaries = new ArrayList<Map<String, Object>>();
				ArrayList<Insight> insights = Insight.fetchInsightsWithProfileID(datastore, profile.getId(), 0);
				for (int i=0; i<insights.size(); i++){
					Insight insight = insights.get(i);
					insightSummaries.add(insight.getSummary());
				}
				
				summary.put("insights", insightSummaries);
				
				//ADD DREAMS TO PROFILE SUMMARY
				ArrayList<Map<String, Object>> dreamSummaries = new ArrayList<Map<String, Object>>();
				ArrayList<Dream> dreams = Dream.fetchDreamsWithProfileID(datastore, profile.getId(), 0);
				for (int i=0; i<dreams.size(); i++){
					Dream dream = dreams.get(i);
					dreamSummaries.add(dream.getSummary());
				}
				summary.put("dreams", dreamSummaries);
				
				//ADD REVIEWS TO PROFILE SUMMARY
				ArrayList<Map<String, Object>> reviewSummaries = new ArrayList<Map<String, Object>>();
				ArrayList<Review> reviews = Review.fetchReviewsWithReviewed(datastore, profile.getId(), 0);
				for (int i=0; i<reviews.size(); i++){
					Review review = reviews.get(i);
					reviewSummaries.add(review.getSummary());
				}
				summary.put("reviews", reviewSummaries);
				
				//ADD ENDORSMEMENTS TO PROFILE SUMMARY
				ArrayList<Map<String, Object>> endorsementSummaries = new ArrayList<Map<String, Object>>();
				ArrayList<Endorsement> endorsements = Endorsement.fetchEndorsementsWithEndorsed(datastore, profile.getId(), 0);
				for (int i=0; i<endorsements.size(); i++){
					Endorsement endorsement = endorsements.get(i);
					endorsementSummaries.add(endorsement.getSummary());
				}
				summary.put("endorsements", endorsementSummaries);
				
				
	        	results.add(summary);
	        }

			response.put("confirmation", "success");
			response.put("profiles", results);
	        	
			JSONObject json = new JSONObject(response);
			resp.getWriter().println(json.toString());
			return;
		}
		
		if (resource.equals("messages")){
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        
	        String messageID = request.getResourceIdentifier();
	        
			if (messageID != null){
				Message message = Message.fetchMessage(datastore, messageID);
				if (message==null){
					response.put("confirmation", "fail");
					response.put("message", "Message "+messageID+" not found.");
			        	
					JSONObject json = new JSONObject(response);
					resp.getWriter().println(json.toString());
					return;
				}
				
				response.put("confirmation", "success");
				response.put("message", message.getSummary());
		        	
				JSONObject json = new JSONObject(response);
				resp.getWriter().println(json.toString());
				return;
			}
	        
	        String senderID = req.getParameter("senderID");
	        String recipientID = req.getParameter("recipientID");
	        String threadID = req.getParameter("threadID");  
	        
			String limit = req.getParameter("limit");
			if (limit==null)
				limit = "0";
	        
	        ArrayList<Message> messages = null;
	        
	        if(senderID !=null && recipientID !=null)
	        	messages = Message.fetchMessagesWithSenderAndReciever(datastore, recipientID, senderID, Integer.parseInt(limit));
	        
	        else if(senderID != null)
	        	messages = Message.fetchMessagesBySender(datastore, senderID, Integer.parseInt(limit));
	        
	        else if(recipientID != null)
	        	messages = Message.fetchMessagesByRecipient(datastore, recipientID, Integer.parseInt(limit));
	        
	        
	        else if(threadID != null)
	        	messages = Message.fetchMessagesByThread(datastore, threadID, Integer.parseInt(limit));
	        
	        else{
	        	response.put("confirmation", "failure");
				response.put("messages", "Invalid Request");
		        	
				JSONObject json = new JSONObject(response);
				resp.getWriter().println(json.toString());
				return;
	        }
	        

	        
	        
	        ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
	        for (Message message : messages)
	        	results.add(message.getSummary());
	        
	        response.put("confirmation", "success");
			response.put("messages", results);
	        	
			JSONObject json = new JSONObject(response);
			resp.getWriter().println(json.toString());
			return;

		}
		
		
		if (resource.equals("insights")){
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        String insightID = request.getResourceIdentifier();
	        
			if (insightID != null){
				Insight insight = Insight.fetchInsight(datastore, insightID);
				if (insight==null){
					response.put("confirmation", "fail");
					response.put("message", "Insight "+insightID+" not found.");
			        	
					JSONObject json = new JSONObject(response);
					resp.getWriter().println(json.toString());
					return;
				}
				
				response.put("confirmation", "success");
				response.put("insight", insight.getSummary());
		        	
				JSONObject json = new JSONObject(response);
				resp.getWriter().println(json.toString());
				return;
			}
	        
	        String profileID = req.getParameter("profile");
	        String categoryTag = req.getParameter("category");
	        
			String limit = req.getParameter("limit");
			if (limit==null)
				limit = "0";
	        
	        ArrayList<Insight> insights = null;
	        
	        if (profileID != null)
	        	insights = Insight.fetchInsightsWithProfileID(datastore, profileID, Integer.parseInt(limit));
	        	        
	        else if (categoryTag != null)
	        	insights = Insight.fetchInsightsWithCategoryTag(datastore, categoryTag, Integer.parseInt(limit));
	        
	        if (insights == null)
	        	insights = Insight.fetchInsights(datastore, 0);
	        
	        ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
	        for (Insight insight : insights)
	        	results.add(insight.getSummary());
	        
	        response.put("confirmation", "success");
			response.put("insights", results);
	        	
			JSONObject json = new JSONObject(response);
			resp.getWriter().println(json.toString());
			return;

		}
		
		if (resource.equals("dreams")){
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        String dreamID = request.getResourceIdentifier();
	        
			if (dreamID != null){
				Dream dream = Dream.fetchDream(datastore, dreamID);
				if (dream==null){
					response.put("confirmation", "fail");
					response.put("message", "Dream "+dreamID+" not found.");
			        	
					JSONObject json = new JSONObject(response);
					resp.getWriter().println(json.toString());
					return;
				}
				
				response.put("confirmation", "success");
				response.put("dream", dream.getSummary());
		        	
				JSONObject json = new JSONObject(response);
				resp.getWriter().println(json.toString());
				return;
			}
			
			
			String limit = req.getParameter("limit");
			if (limit==null)
				limit = "0";
			
			ArrayList<Dream> dreams = null;
			dreams = Dream.fetchDreams(datastore, Integer.parseInt(limit));
			
			ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
	        for (Dream dream : dreams)
	        	results.add(dream.getSummary());
	        
	        response.put("confirmation", "success");
			response.put("dreams", results);
	        	
			JSONObject json = new JSONObject(response);
			resp.getWriter().println(json.toString());
			return;
		}
		
		
		if (resource.equals("endorsements")){
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        String endorsementID = request.getResourceIdentifier();
	        
			if (endorsementID != null){
				Endorsement endorsement = Endorsement.fetchEndorsement(datastore, endorsementID);
				if (endorsement==null){
					response.put("confirmation", "fail");
					response.put("message", "Endorsement "+endorsementID+" not found.");
			        	
					JSONObject json = new JSONObject(response);
					resp.getWriter().println(json.toString());
					return;
				}
				
				response.put("confirmation", "success");
				response.put("endorsement", endorsement.getSummary());
		        	
				JSONObject json = new JSONObject(response);
				resp.getWriter().println(json.toString());
				return;
			}
	        
	        String endorsed = req.getParameter("endorsed");
	        String endorsedBy = req.getParameter("endorsedBy");
	        
			String limit = req.getParameter("limit");
			if (limit==null)
				limit = "0";
	        
	        ArrayList<Endorsement> endorsements = null;
	        
	        if (endorsed != null)
	        	endorsements = Endorsement.fetchEndorsementsWithEndorsed(datastore, endorsed, Integer.parseInt(limit));
	        	        
	        else if (endorsedBy != null)
	        	endorsements = Endorsement.fetchEndorsementsWithEndorsedBy(datastore, endorsedBy, Integer.parseInt(limit));
	        
	        if (endorsements == null)
	        	endorsements = Endorsement.fetchEndorsements(datastore, 0);
	        
	        ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
	        for (Endorsement endorsement : endorsements)
	        	results.add(endorsement.getSummary());
	        
	        response.put("confirmation", "success");
			response.put("endorsements", results);
	        	
			JSONObject json = new JSONObject(response);
			resp.getWriter().println(json.toString());
			return;

		}
		
		if (resource.equals("reviews")){
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        String reviewID = request.getResourceIdentifier();
	        
			if (reviewID != null){
				Review review = Review.fetchReview(datastore, reviewID);
				if (review==null){
					response.put("confirmation", "fail");
					response.put("message", "Review "+reviewID+" not found.");
			        	
					JSONObject json = new JSONObject(response);
					resp.getWriter().println(json.toString());
					return;
				}
				
				response.put("confirmation", "success");
				response.put("review", review.getSummary());
		        	
				JSONObject json = new JSONObject(response);
				resp.getWriter().println(json.toString());
				return;
			}
	        
	        String reviewed = req.getParameter("reviewed");
	        String reviewedBy = req.getParameter("reviewedBy");
	        
			String limit = req.getParameter("limit");
			if (limit==null)
				limit = "0";
	        
	        ArrayList<Review> reviews = null;
	        
	        if (reviewed != null)
	        	reviews = Review.fetchReviewsWithReviewed(datastore, reviewed, Integer.parseInt(limit));
	        	        
	        else if (reviewedBy != null)
	        	reviews = Review.fetchReviewsWithReviewedBy(datastore, reviewedBy, Integer.parseInt(limit));
	        
	        if (reviews == null)
	        	reviews = Review.fetchReviews(datastore, Integer.parseInt(limit));
	        
	        ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
	        for (Review review : reviews)
	        	results.add(review.getSummary());
	        
	        response.put("confirmation", "success");
			response.put("reviews", results);
	        	
			JSONObject json = new JSONObject(response);
			resp.getWriter().println(json.toString());
			return;

		}
		
		if (resource.equals("profilePage")){
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        String pageID = request.getResourceIdentifier();
	        
			if (pageID != null){
				ProfilePage page = ProfilePage.fetchProfilePage(datastore, pageID);
				if (page==null){
					response.put("confirmation", "fail");
					response.put("message", "Page "+pageID+" not found.");
			        	
					JSONObject json = new JSONObject(response);
					resp.getWriter().println(json.toString());
					return;
				}
				
				response.put("confirmation", "success");
				response.put("profilePage", page.getSummary());
		        	
				JSONObject json = new JSONObject(response);
				resp.getWriter().println(json.toString());
				return;
			}
	       
	        
			String limit = req.getParameter("limit");
			if (limit==null)
				limit = "0";
	        
	        ArrayList<ProfilePage> pages = null;
	        pages = ProfilePage.fetchProfilePages(datastore, Integer.parseInt(limit));
	        
	        ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
	        for (ProfilePage page : pages)
	        	results.add(page.getSummary());
	        
	        response.put("confirmation", "success");
			response.put("profilePages", results);
	        	
			JSONObject json = new JSONObject(response);
			resp.getWriter().println(json.toString());
			return;

		}
		
		
	}

	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		allowCORSAccess(req, resp);
		resp.setContentType("application/json");
		
		RequestInfo request = new RequestInfo(req, resp);
		String resource = request.getResource();
		Map<String, Object> response = new HashMap<String, Object>();
		LoginHandler login = checkLogin(response, req); // Check user login
		
		
		if (resource.equals("profiles")){
			String requestBody = getBody(req);

			try {
				JSONObject json = new JSONObject(requestBody);
				
				Profile profile = new Profile();
				profile.update(json);
				profile.save();
				
				
				// send email to local with word document application:
				
				if (profile.getType().equals("local")){
//					Exception exception = null;
//					try {
//						byte[] file = fetchFileBytes("resources/application.docx");
//						sendEmailWithAttachment(profile.getEmail(), "This is a TEST email", file, "application.docx");
//						
//						response.put("confirmation", "success");
//						response.put("profile", profile.getSummary());
//						JSONObject reply = new JSONObject(response);
//						resp.getWriter().print(reply.toString());
//						return;
//					}
//					catch(FileNotFoundException e){
//						sendEmail("dennykwon2@gmail.com", "FileNotFoundException: "+e.getMessage());
//						exception = e;
//					}
//					catch(IOException e){
//						sendEmail("dennykwon2@gmail.com", "IOException: "+e.getMessage());
//						exception = e;
//					}
//					catch(AddressException e){
//						sendEmail("dennykwon2@gmail.com", "AddressException: "+e.getMessage());
//						exception = e;
//					}
//					catch(MessagingException e){
//						sendEmail("dennykwon2@gmail.com", "MessagingException: "+e.getMessage());
//						exception = e;
//					}
//					
//					if (exception != null){
//						response.put("confirmation", "fail");
//						response.put("message", exception.getMessage());
//						JSONObject reply = new JSONObject(response);
//						resp.getWriter().print(reply.toString());
//						return;
//					}
				}
				else{
					insertCookie(req, profile);
				}
				
				response.put("confirmation", "success");
				response.put("profile", profile.getSummary());
				JSONObject reply = new JSONObject(response);
				resp.getWriter().print(reply.toString());
				return;
			} 
			catch (JSONException e) {
				response.put("confirmation", "fail");
				response.put("message", e.getMessage());
				
				JSONObject reply = new JSONObject(response);
				resp.getWriter().print(reply.toString());
				return;
			}
		}
		
		if (resource.equals("logout")){
			login.logout(req);
			
			response.put("confirmation", "success");
			response.put("loggedIn", "no");
			response.remove("currentUser");
			JSONObject reply = new JSONObject(response);
			resp.getWriter().print(reply.toString());
			return;
		}
		
		
		if (resource.equals("images")){
		    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	        BlobKey blobKey = extractBlobKey(blobstoreService, "file", request);
	        
	        if (blobKey == null){
				response.put("confirmation", "fail");
				response.put("message", "Missing Blob Key.");
				JSONObject reply = new JSONObject(response);
				resp.getWriter().print(reply.toString());
	        	return;
	        }
	        
			BlobInfoFactory b = new BlobInfoFactory();
			BlobInfo info = b.loadBlobInfo(blobKey);
			String mime = info.getContentType().toLowerCase();
			
			ArrayList<String> validMimes = new ArrayList<String>();
			validMimes.add("png");
			validMimes.add("jpg");
			validMimes.add("jpeg");
			validMimes.add("image/png");
			validMimes.add("image/jpg");
			validMimes.add("image/jpeg");
			
			if (validMimes.contains(mime)==false){
				blobstoreService.delete(blobKey);
				response.put("confirmation", "fail");
				response.put("message", "Inavlid MIME type.");
				JSONObject reply = new JSONObject(response);
				resp.getWriter().print(reply.toString());
	        	return;
			}

        	String keyString = blobKey.getKeyString();
        	Image img = new Image();
        	img.setName(info.getFilename());
        	img.setKey(keyString);
        	img.setUniqueId(keyString.substring(keyString.length()-8));
        	
		    ImagesService imagesService = ImagesServiceFactory.getImagesService();
		    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(info.getBlobKey());
		    img.setAddress(imagesService.getServingUrl(options));
        	
	        img.save();
	        
			response.put("confirmation", "success");
			response.put("image", img.getSummary());
			JSONObject reply = new JSONObject(response);
			resp.getWriter().print(reply.toString());
			return;
		}
		
		
		if (resource.equals("login")){
//			System.out.print("LOGIN");
			
			String requestBody = getBody(req);
			
			try {
				JSONObject json = new JSONObject(requestBody);
				if (json.has("email")==false){
					response.put("confirmation", "fail");
					response.put("message", "Missing Email.");
					
					JSONObject reply = new JSONObject(response);
					resp.getWriter().print(reply.toString());
					return;
				}
				
				if (json.has("password")==false){
					response.put("confirmation", "fail");
					response.put("message", "Missing Password.");
					
					JSONObject reply = new JSONObject(response);
					resp.getWriter().print(reply.toString());
					return;
				}
				
		        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

				String email = json.getString("email");
				ArrayList<Profile> profiles = Profile.fetchProfilesWithEmail(datastore, email, 1);
				if (profiles.size()==0){
					response.put("confirmation", "fail");
					response.put("message", "Profile with email "+email+" not found.");
					
					JSONObject reply = new JSONObject(response);
					resp.getWriter().print(reply.toString());
					return;
				}
				
				Profile profile = profiles.get(0); // most recently registered profile with email
				
				String password = json.getString("password");
				if (password.equals(profile.getPassword())==false){
					response.put("confirmation", "fail");
					response.put("message", "Incorrect Password.");
					
					JSONObject reply = new JSONObject(response);
					resp.getWriter().print(reply.toString());
					return;
				}
				
				insertCookie(req, profile);
				response.put("confirmation", "success");
				response.put("profile", profile.getSummary());
				
				JSONObject reply = new JSONObject(response);
				resp.getWriter().print(reply.toString());
				return;
			} 
			catch (JSONException e) {
				response.put("confirmation", "fail");
				response.put("message", e.getMessage());
				
				JSONObject reply = new JSONObject(response);
				resp.getWriter().print(reply.toString());
				return;
			}
		}
		
		if (resource.equals("messages")){
			String body = getBody(req);
			
			try{
				JSONObject json = new JSONObject(body);
				Message message = new Message();
				message.update(json);
				message.save();
				
				response.put("confirmation", "success");
				response.put("message", message.getSummary()); 
				JSONObject jsonResponse = new JSONObject(response);
				resp.getWriter().print(jsonResponse.toString());
				return;
			}
			catch(JSONException e){
				response.put("confirmation", "fail");
				response.put("message", e.getMessage());
				JSONObject jsonResponse = new JSONObject(response);
				resp.getWriter().print(jsonResponse.toString());
				return;
			}
		}
			
		if (resource.equals("insights")){
			String body = getBody(req);
			
			try{
				JSONObject json = new JSONObject(body);
				Insight insight = new Insight();
				insight.update(json);
				insight.save();
				
				response.put("confirmation", "success");
				response.put("insight", insight.getSummary()); 
				JSONObject jsonResponse = new JSONObject(response);
				resp.getWriter().print(jsonResponse.toString());
				return;
			}
			catch(JSONException e){
				response.put("confirmation", "fail");
				response.put("message", e.getMessage());
				JSONObject jsonResponse = new JSONObject(response);
				resp.getWriter().print(jsonResponse.toString());
				return;
			}
		}
		
		if (resource.equals("dreams")){
			String body = getBody(req);
			
			try{
				JSONObject json = new JSONObject(body);
				Dream dream = new Dream();
				dream.update(json);
				dream.save();
				
				response.put("confirmation", "success");
				response.put("dream", dream.getSummary()); 
				JSONObject jsonResponse = new JSONObject(response);
				resp.getWriter().print(jsonResponse.toString());
				return;
			}
			catch(JSONException e){
				response.put("confirmation", "fail");
				response.put("message", e.getMessage());
				JSONObject jsonResponse = new JSONObject(response);
				resp.getWriter().print(jsonResponse.toString());
				return;
			}
		}
		
		if (resource.equals("endorsements")){
			String body = getBody(req);
			
			try{
				JSONObject json = new JSONObject(body);
				Endorsement endorsement = new Endorsement();
				endorsement.update(json);
				endorsement.save();
				
				response.put("confirmation", "success");
				response.put("endorsement", endorsement.getSummary()); 
				JSONObject jsonResponse = new JSONObject(response);
				resp.getWriter().print(jsonResponse.toString());
				return;
			}
			catch(JSONException e){
				response.put("confirmation", "fail");
				response.put("message", e.getMessage());
				JSONObject jsonResponse = new JSONObject(response);
				resp.getWriter().print(jsonResponse.toString());
				return;
			}
		}
		
		if (resource.equals("reviews")){
			String body = getBody(req);
			
			try{
				JSONObject json = new JSONObject(body);
				Review review = new Review();
				review.update(json);
				review.save();
				
				response.put("confirmation", "success");
				response.put("review", review.getSummary()); 
				JSONObject jsonResponse = new JSONObject(response);
				resp.getWriter().print(jsonResponse.toString());
				return;
			}
			catch(JSONException e){
				response.put("confirmation", "fail");
				response.put("message", e.getMessage());
				JSONObject jsonResponse = new JSONObject(response);
				resp.getWriter().print(jsonResponse.toString());
				return;
			}
		}
		
		if (resource.equals("profilePage")){
			String body = getBody(req);
			
			try{
				JSONObject json = new JSONObject(body);
				ProfilePage page = new ProfilePage();
				page.update(json);
				page.save();
				
				response.put("confirmation", "success");
				response.put("profilePage", page.getSummary()); 
				JSONObject jsonResponse = new JSONObject(response);
				resp.getWriter().print(jsonResponse.toString());
				return;
			}
			catch(JSONException e){
				response.put("confirmation", "fail");
				response.put("message", e.getMessage());
				JSONObject jsonResponse = new JSONObject(response);
				resp.getWriter().print(jsonResponse.toString());
				return;
			}
		}
		
	}
	
	
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		allowCORSAccess(req, resp);
		resp.setContentType("application/json");
		
		RequestInfo request = new RequestInfo(req, resp);
		String resource = request.getResource();
		Map<String, Object> response = new HashMap<String, Object>();
		LoginHandler login = checkLogin(response, req); // Check user login
		
		// every PUT request requires a resource identifer:
		String identifier = request.getResourceIdentifier();
		if (identifier==null){
			response.put("confirmation", "fail");
			response.put("message", "Missing identifier for "+resource.toUpperCase());
			
			JSONObject reply = new JSONObject(response);
			resp.getWriter().print(reply.toString());
			return;
		}
		
		if (resource.equals("profiles")){
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        Profile profile = Profile.fetchProfile(datastore, identifier);
	        if (profile==null){
				response.put("confirmation", "fail");
				response.put("message", "Profile "+identifier+" not found.");
				
				JSONObject reply = new JSONObject(response);
				resp.getWriter().print(reply.toString());
				return;
	        }

	        
			String requestBody = getBody(req);
			try {
				JSONObject json = new JSONObject(requestBody);
		        profile.update(json);
		        profile.save(datastore);
		        
				response.put("confirmation", "success");
				response.put("profile", profile.getSummary());
				
				JSONObject reply = new JSONObject(response);
				resp.getWriter().print(reply.toString());
				return;
			}
			catch(JSONException e){
				response.put("confirmation", "fail");
				response.put("message", e.getMessage());
				
				JSONObject reply = new JSONObject(response);
				resp.getWriter().print(reply.toString());
				return;
			}
		}
		
		if (resource.equals("dreams")){
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        Dream dream = Dream.fetchDream(datastore, identifier);
	        if (dream==null){
				response.put("confirmation", "fail");
				response.put("message", "Dream "+identifier+" not found.");
				
				JSONObject reply = new JSONObject(response);
				resp.getWriter().print(reply.toString());
				return;
	        }

	        
			String requestBody = getBody(req);
			try {
				JSONObject json = new JSONObject(requestBody);
		        dream.update(json);
		        dream.save(datastore);
		        
				response.put("confirmation", "success");
				response.put("dream", dream.getSummary());
				
				JSONObject reply = new JSONObject(response);
				resp.getWriter().print(reply.toString());
				return;
			}
			catch(JSONException e){
				response.put("confirmation", "fail");
				response.put("message", e.getMessage());
				
				JSONObject reply = new JSONObject(response);
				resp.getWriter().print(reply.toString());
				return;
			}
		}
		if (resource.equals("profilePage")){
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        ProfilePage page = ProfilePage.fetchProfilePage(datastore, identifier);
	        if (page==null){
				response.put("confirmation", "fail");
				response.put("message", "ProfilePage "+identifier+" not found.");
				
				JSONObject reply = new JSONObject(response);
				resp.getWriter().print(reply.toString());
				return;
	        }

	        
			String requestBody = getBody(req);
			try {
				JSONObject json = new JSONObject(requestBody);
		        page.update(json);
		        page.save(datastore);
		        
				response.put("confirmation", "success");
				response.put("profilePage", page.getSummary());
				
				JSONObject reply = new JSONObject(response);
				resp.getWriter().print(reply.toString());
				return;
			}
			catch(JSONException e){
				response.put("confirmation", "fail");
				response.put("message", e.getMessage());
				
				JSONObject reply = new JSONObject(response);
				resp.getWriter().print(reply.toString());
				return;
			}
		}
	}
	
	
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		allowCORSAccess(req, resp);
		resp.setContentType("application/json");
		
		RequestInfo request = new RequestInfo(req, resp);
		String resource = request.getResource();
		Map<String, Object> response = new HashMap<String, Object>();
		LoginHandler login = checkLogin(response, req); // Check user login
		
		
		// every DELETE request requires a resource identifer:
		String identifier = request.getResourceIdentifier();
		if (identifier==null){
			response.put("confirmation", "fail");
			response.put("message", "Missing identifier for "+resource.toUpperCase());
			
			JSONObject reply = new JSONObject(response);
			resp.getWriter().print(reply.toString());
			return;
		}
		
		if (resource.equals("profiles")){
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        Profile profile = Profile.fetchProfile(datastore, identifier);
	        if (profile != null){
	        	
	        	// remove insights first:
	        	ArrayList<Insight> insights = Insight.fetchInsightsWithProfileID(datastore, profile.getId(), 0);
	        	for (Insight insight : insights){
		        	datastore.delete(insight.createEntityVersion().getKey());
	        	}
	        			
	        	datastore.delete(profile.createEntityVersion().getKey());
	        	
	        }
	        
			response.put("confirmation", "success");
			response.put("message", "Profile deleted");
			
			JSONObject reply = new JSONObject(response);
			resp.getWriter().print(reply.toString());
			return;
	        
		}
		
		

		
	}

	
	public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		resp.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
		resp.addHeader("Access-Control-Max-Age", "1728000");
	}
	

	private void allowCORSAccess(HttpServletRequest req, HttpServletResponse resp){
		resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		resp.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
		resp.addHeader("Access-Control-Max-Age", "1728000");
	}


	
	private void insertCookie(HttpServletRequest req, Profile profile){
		HttpSession session = req.getSession();
		Date now = new Date();
		Long expirationTime = now.getTime()+1000*60*60*24;
		session.setAttribute("user", profile.getId());
		session.setAttribute("expires", Long.toString(expirationTime));
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
	
	private BlobKey extractBlobKey(BlobstoreService blobstoreService, String fileHandle, RequestInfo reqInfo){
        Map<String, List<BlobKey>> uploads = blobstoreService.getUploads(reqInfo.getRequest());
        BlobKey blobKey = uploads.get(fileHandle).get(0);
        if (blobKey==null)
        	return null;
        
        return blobKey;
	}
	

	private byte[] fetchFileBytes(String filepath) throws FileNotFoundException, IOException {
		File file = new File(filepath);
		
		byte fileContent[] = new byte[(int)file.length()];
		FileInputStream fin = new FileInputStream(file);
		fin.read(fileContent);
		fin.close();
		return fileContent;
	}
	
	/*
	private void sendEmailWithAttachment(String recipient, String content, byte[] attch, String fileName) throws AddressException, MessagingException, UnsupportedEncodingException {
		Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);
	    
        Message msg = new MimeMessage(session);
        InternetAddress from = new InternetAddress("info@thegridmedia.com", "Terranga");
        msg.setFrom(from);
        InternetAddress to = new InternetAddress(recipient, recipient);
        msg.addRecipient(Message.RecipientType.TO, to);
        msg.setSubject("Terranga");
        
        Multipart mp = new MimeMultipart();
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(content, "text/html");
        mp.addBodyPart(htmlPart);
        
        MimeBodyPart attachment = new MimeBodyPart();
        
//        DataSource src =  new ByteArrayDataSource(attch, "text/csv");
//        DataSource src =  new ByteArrayDataSource(attch, "application/msword");
        
        DataSource src =  new ByteArrayDataSource(attch, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        DataHandler handler = new DataHandler(src);
        attachment.setFileName(fileName);
        attachment.setDisposition(Part.ATTACHMENT);
        attachment.setDataHandler(handler); 
        
        mp.addBodyPart(attachment);
        msg.setContent(mp);
        Transport.send(msg);
	} */

	
	/*
	public boolean sendEmail(String recipient, String content, String subject){
		boolean confirmation = false;
		Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);
	    try {
	        Message msg = new MimeMessage(session);
            InternetAddress from = new InternetAddress("info@thegridmedia.com", "Terranga");
            msg.setFrom(from);
            InternetAddress to = new InternetAddress(recipient, recipient);
            msg.addRecipient(Message.RecipientType.TO, to);
            msg.setSubject(subject);
            
            Multipart mp = new MimeMultipart();
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(content, "text/html");
            mp.addBodyPart(htmlPart);
            
            msg.setContent(mp);
            Transport.send(msg);
            return true;
	    }
        catch (UnsupportedEncodingException e){ 
        	e.printStackTrace(); 
        	confirmation = false;
        }
	    catch (AddressException e) {
        	e.printStackTrace(); 
        	confirmation = false;
	    }
	    catch (MessagingException e) {
        	e.printStackTrace(); 
        	confirmation = false;
	    }	
	    return confirmation;
	}
	
	public boolean sendEmail(String recipient, String content){
		return sendEmail(recipient, content, "Terranga");
	}
	*/


}
