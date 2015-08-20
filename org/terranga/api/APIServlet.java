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
import java.util.Collections;
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
import org.json.JSONException;
import org.json.JSONObject;
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
			
			
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			
			//ADD INSIGHTS TO PROFILE SUMMARY
			ArrayList<Map<String, Object>> insightSummaries = new ArrayList<Map<String, Object>>();
			ArrayList<Insight> insights = Insight.fetchInsightsWithProfileID(datastore, login.getProfile().getId(), 0);
			for (int i=0; i<insights.size(); i++){
				Insight insight = insights.get(i);
				insightSummaries.add(insight.getSummary());
			}
			currentUserSummary.put("insights", insightSummaries);
			
			//ADD DREAMS TO PROFILE SUMMARY
			ArrayList<Map<String, Object>> dreamSummaries = new ArrayList<Map<String, Object>>();
			ArrayList<Dream> dreams = Dream.fetchDreamsWithProfileID(datastore, login.getProfile().getId(), 0);
			for (int i=0; i<dreams.size(); i++){
				Dream dream = dreams.get(i);
				dreamSummaries.add(dream.getSummary());
			}
			currentUserSummary.put("dreams", dreamSummaries);
			
			//ADD REVIEWS TO PROFILE SUMMARY
			ArrayList<Map<String, Object>> reviewSummaries = new ArrayList<Map<String, Object>>();
			ArrayList<Review> reviews = Review.fetchReviewsWithReviewed(datastore, login.getProfile().getId(), 0);
			for (int i=0; i<reviews.size(); i++){
				Review review = reviews.get(i);
				reviewSummaries.add(review.getSummary());
			}
			currentUserSummary.put("reviews", reviewSummaries);
			
			//ADD ENDORSMEMENTS TO PROFILE SUMMARY
			ArrayList<Map<String, Object>> endorsementSummaries = new ArrayList<Map<String, Object>>();
			ArrayList<Endorsement> endorsements = Endorsement.fetchEndorsementsWithEndorsed(datastore, login.getProfile().getId(), 0);
			for (int i=0; i<endorsements.size(); i++){
				Endorsement endorsement = endorsements.get(i);
				endorsementSummaries.add(endorsement.getSummary());
			}
			currentUserSummary.put("endorsements", endorsementSummaries);
			
			
			response.put("currentUser", currentUserSummary);
		}
		
		if (resource.equals("test")){
//	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//	        Message.fetchMessagesWithRecipient(datastore, "12123", true);
	        
	        
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
	        String messageId = request.getResourceIdentifier();
	        
			if (messageId != null){
				Message message = Message.fetchMessage(datastore, messageId );
				if (message==null){
					response.put("confirmation", "fail");
					response.put("message", "Message "+messageId +" not found.");
			        	
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
			
			String limit = req.getParameter("limit");
			if (limit==null)
				limit = "0";
			
			String mostrecent = req.getParameter("mostrecent");
			if (mostrecent==null)
				mostrecent = "yes";
			
			boolean mostRecent = (mostrecent.equals("yes")) ? true : false;

			ArrayList<Message> messages = null;
			
			String participants = req.getParameter("participants");
			if (participants != null)
		        messages = Message.fetchMessagesWithParticipants(datastore, participants, mostRecent);
			
			String recipient = req.getParameter("recipient");
			if (recipient != null)
		        messages = Message.fetchMessagesWithRecipient(datastore, recipient, mostRecent);
			
			
			String conversation = req.getParameter("conversation");
			if (conversation != null){
				String[] p = conversation.split(",");
				if (p.length < 2){
					// error message
				}
				
				ArrayList<String> a = new ArrayList<String>();
				a.add(p[0]);
				a.add(p[1]);
				Collections.sort(a, String.CASE_INSENSITIVE_ORDER);
				String thread = a.get(0)+a.get(1);
		        
		        messages = Message.fetchConversation(datastore, thread);
			}
			
			if (messages == null){
		        response.put("confirmation", "fail");
				response.put("message", "Missing 'participants', 'conversation', or 'recipient' parameter.");
		        	
				JSONObject json = new JSONObject(response);
				resp.getWriter().println(json.toString());
				return;
			}

	        Map<String, Profile> profiles = new HashMap<String, Profile>();
	        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	        for (Message message : messages){
	        	Map<String, Object> summary = message.getSummary();
	        	
	        	String senderId = message.getProfile();
	        	Profile sender = profiles.get(senderId);
	        	if (sender == null){
	        		sender = Profile.fetchProfile(datastore, senderId);
	        		profiles.put(senderId, sender);
	        	}
	        	if (sender != null) // not sure why but for Lindsay, this comes back null
	        		summary.put("profile", sender.getSummary());
	        	
	        	String recipientId = message.getRecipient();
	        	Profile rec = profiles.get(recipientId);
	        	if (rec == null){
	        		rec = Profile.fetchProfile(datastore, recipientId);
	        		profiles.put(recipientId, rec);
	        	}
	        	summary.put("recipient", rec.getSummary());
	        	
	        	list.add(summary);
	        }
	        

	        response.put("confirmation", "success");
			response.put("messages", list);
	        	
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
			try {
				String requestBody = getBody(req);
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
			try{
				String body = getBody(req);
				JSONObject json = new JSONObject(body);
				Message message = new Message();
				message.update(json);
				
		        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				ArrayList<Message> thread = Message.fetchConversation(datastore, message.getThread());
				for (Message msg : thread){
					msg.setIsMostRecent("no");
					msg.save(datastore);
				}
				
				message.save(datastore);
				
				
				Profile recipient = Profile.fetchProfile(datastore, message.getRecipient());
				Profile sender = Profile.fetchProfile(datastore, message.getProfile());
				if (recipient != null && sender != null) {
					String firstName = sender.getFirstName().substring(0, 1).toUpperCase();
					if (sender.getFirstName().length() > 1)
						firstName = firstName+sender.getFirstName().substring(1, sender.getFirstName().length());
					
					String fullName = firstName+" "+sender.getLastName().substring(0, 1).toUpperCase();
					EmailService.sendEmail(recipient.getEmail(), fullName+" sent you a message on Terranga! Login <a href='http://beta.terranga.co'>here </a>to check it out!", "Message from "+fullName+"!");
					EmailService.sendEmail("dennykwon2@gmail.com", fullName+" sent you a message on Terranga! Login <a href='http://beta.terranga.co'>here </a>to check it out!", "Message from "+fullName+"!");
				}
				
				
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
			try{
				String body = getBody(req);
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
	
		if (resource.equals("insights")){
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        Insight insight = Insight.fetchInsight(datastore, identifier);
	        if (insight==null){
				response.put("confirmation", "fail");
				response.put("message", "Insight "+identifier+" not found.");
				
				JSONObject reply = new JSONObject(response);
				resp.getWriter().print(reply.toString());
				return;
	        }
	        
			try {
				String requestBody = getBody(req);
				JSONObject json = new JSONObject(requestBody);
				insight.update(json);
				insight.save(datastore);
		        
				response.put("confirmation", "success");
				response.put("insight", insight.getSummary());
				
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

			try {
				String requestBody = getBody(req);
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
		
		
		if (resource.equals("insights")){
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        Insight insight = Insight.fetchInsight(datastore, identifier);
	        if (insight != null)
	        	datastore.delete(insight.createEntityVersion().getKey());
	        
			response.put("confirmation", "success");
			response.put("message", "Insight deleted");
			
			JSONObject reply = new JSONObject(response);
			resp.getWriter().print(reply.toString());
			return;
		}
		
		if (resource.equals("dreams")){
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        Dream dream = Dream.fetchDream(datastore, identifier);
	        if (dream != null)
	        	datastore.delete(dream.createEntityVersion().getKey());
	        
			response.put("confirmation", "success");
			response.put("message", "Dream deleted");
			
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
	
//	private byte[] fetchFileBytes(String filepath) throws FileNotFoundException, IOException {
//		File file = new File(filepath);
//		
//		byte fileContent[] = new byte[(int)file.length()];
//		FileInputStream fin = new FileInputStream(file);
//		fin.read(fileContent);
//		fin.close();
//		return fileContent;
//	}
	
	

}
