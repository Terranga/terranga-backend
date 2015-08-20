package org.terranga.general;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Text;

import org.json.*;


public class Message {
	
	// auto-generated:
	private String id;
	private Date timestamp;

	private String profile;
	private String recipient;
	private String subject;
	private String thread;
	private String isMostRecent;
	private Text content;
	private ArrayList<String> participants;
	private ArrayList<String> read; // when one of the participants opens message, his id number goes in this array

	
	public Message(){
		setId(randomString(8));
		setTimestamp(new Date());
		
		setProfile("none");
		setRecipient("none");
		setSubject("none");
		setThread("none");
		setIsMostRecent("yes");
		setContent(new Text(""));
		
		ArrayList<String> a = new ArrayList<String>();
		a.add("none");
		setParticipants(a);
		
		ArrayList<String> r = new ArrayList<String>();
		r.add("none");
		setRead(r);

	}

	
	@SuppressWarnings("unchecked")
	public Message(Entity ent){
		setId(ent.getKey().getName());
		setTimestamp((Date)ent.getProperty("timestamp"));
		setProfile((String)ent.getProperty("profile"));
		setRecipient((String)ent.getProperty("recipient"));
		setSubject((String)ent.getProperty("subject"));
		setThread((String)ent.getProperty("thread"));
		setIsMostRecent((String)ent.getProperty("isMostRecent"));
		setContent((Text)ent.getProperty("content"));
		setParticipants((ArrayList<String>)ent.getProperty("participants"));
		setRead((ArrayList<String>)ent.getProperty("read"));
	}
	
	public Entity createEntityVersion(){
        Entity p = new Entity("Message", getId());
        p.setProperty("timestamp", getTimestamp());
        p.setProperty("profile", getProfile());
        p.setProperty("recipient", getRecipient());
        p.setProperty("subject", getSubject());
        p.setProperty("isMostRecent", getIsMostRecent());
        p.setProperty("thread", getThread());
        p.setProperty("content", getContent());
        p.setProperty("participants", getParticipants());
        p.setProperty("read", getRead());
        return p;
	}
	
	
	public Map<String, Object> getSummary(){
		Map<String, Object> summary = new HashMap<String, Object>();
		summary.put("id", getId());
		summary.put("timestamp", getTimestamp().toString());
		summary.put("profile", getProfile());
		summary.put("recipient", getRecipient());
		summary.put("subject", getSubject());
		summary.put("isMostRecent", getIsMostRecent());
		summary.put("thread", getThread());
		summary.put("content", getContent().getValue());
		summary.put("participants", getParticipants());
		summary.put("read", getRead());
		return summary;
	}
	
	public static String randomString(int length){
		double random = Math.random();
		String randomString = Double.toString(random).substring(2);
		return randomString.substring(randomString.length()-length);
	}

	public void save(){
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(createEntityVersion());
	}

	public void save(DatastoreService datastore){
        datastore.put(createEntityVersion());
	}

	
	public void update(JSONObject json) throws JSONException {
		ArrayList<String> a = new ArrayList<String>();
		if (json.has("profile")){
			String profileId = json.getString("profile");
			setProfile(profileId);
			if (a.contains(profileId)==false)
				a.add(profileId);
		}

		if (json.has("recipient")){ 
			String recipientId = json.getString("recipient");
			setRecipient(recipientId);
			if (a.contains(recipientId)==false)
				a.add(recipientId);
		}

		if (json.has("content"))
			setContent(new Text(json.getString("content")));

		if (json.has("subject"))
			setSubject(json.getString("subject"));

		if (json.has("thread"))
			setThread(json.getString("thread"));


		if (json.has("read")){
			ArrayList<String> r = new ArrayList<String>();
			JSONArray list = json.getJSONArray("read");
			for (int i=0; i<list.length(); i++){
				String participant = list.getString(i);
				r.add(participant);
			}
			
			setRead(r);
		}

		Collections.sort(a, String.CASE_INSENSITIVE_ORDER);
		setParticipants(a);
		
		String th = a.get(0)+a.get(1);
		setThread(th);
	}
	
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public Date getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}


	public String getProfile() {
		return profile;
	}


	public void setProfile(String profile) {
		this.profile = profile;
	}


	public String getRecipient() {
		return recipient;
	}


	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}


	public Text getContent() {
		return content;
	}


	public void setContent(Text content) {
		this.content = content;
	}


	public ArrayList<String> getParticipants() {
		return participants;
	}


	public void setParticipants(ArrayList<String> participants) {
		this.participants = participants;
	}
	
	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}

	public ArrayList<String> getRead() {
		return read;
	}


	public void setRead(ArrayList<String> read) {
		this.read = read;
	}

	public String getThread() {
		return thread;
	}


	public void setThread(String thread) {
		this.thread = thread;
	}

	public String getIsMostRecent() {
		return isMostRecent;
	}


	public void setIsMostRecent(String isMostRecent) {
		this.isMostRecent = isMostRecent;
	}




	
	
// - - - - - - - - - - - - - - - - - - - - - - - - QUERIES - - - - - - - - - - - - - - - - - - - - - - - - - - - //	
	
	
	public static Message fetchMessage(DatastoreService datastore, String messageId){
		Message p = null;
		
		try {
			p = new Message(datastore.get(KeyFactory.createKey("Message", messageId)));
		}
		catch(EntityNotFoundException e){
		
		}
			
		return p;
	}
	
	
	public static ArrayList<Message> fetchMessages(DatastoreService datastore){
		Query q = new Query("Message").addSort("timestamp", Query.SortDirection.DESCENDING);
		ArrayList<Message> messages = executeQuery(datastore, q);
		return messages;
	}
	

	public static ArrayList<Message> fetchMessagesWithProfile(DatastoreService datastore, String profileId){
		Query q = new Query("Message").addSort("timestamp", Query.SortDirection.DESCENDING);
		
		Filter profileFilter = new FilterPredicate("profile", FilterOperator.EQUAL, profileId.trim());
		q.setFilter(profileFilter);
		ArrayList<Message> messages = executeQuery(datastore, q);
		return messages;
	}


	public static ArrayList<Message> fetchMessagesWithRecipient(DatastoreService datastore, String recipient, boolean mostRecent){
		Query q = new Query("Message").addSort("timestamp", Query.SortDirection.DESCENDING);
		
		Filter profileFilter = new FilterPredicate("recipient", FilterOperator.EQUAL, recipient);
		
		if (mostRecent==true){
			Filter mostRecentFilter = new FilterPredicate("isMostRecent", FilterOperator.EQUAL, "yes");
			CompositeFilter filters = CompositeFilterOperator.and(profileFilter, mostRecentFilter);
			q.setFilter(filters);
		}
		else{
			q.setFilter(profileFilter);
		}

		
		ArrayList<Message> messages = executeQuery(datastore, q);
		return messages;
	}

	public static ArrayList<Message> fetchConversation(DatastoreService datastore, String thread){
		Query q = new Query("Message").addSort("timestamp", Query.SortDirection.DESCENDING);
		
		Filter profileFilter = new FilterPredicate("thread", FilterOperator.EQUAL, thread);
		q.setFilter(profileFilter);
		ArrayList<Message> messages = executeQuery(datastore, q);
		return messages;
	}

	public static ArrayList<Message> fetchMessagesWithParticipants(DatastoreService datastore, String participant, boolean mostRecent){
		Query q = new Query("Message").addSort("timestamp", Query.SortDirection.DESCENDING);
		
		Filter profileFilter = new FilterPredicate("participants", FilterOperator.EQUAL, participant);
		
		if (mostRecent==true){
			Filter mostRecentFilter = new FilterPredicate("isMostRecent", FilterOperator.EQUAL, "yes");
			CompositeFilter filters = CompositeFilterOperator.and(profileFilter, mostRecentFilter);
			q.setFilter(filters);
		}
		else{
			q.setFilter(profileFilter);
		}
		
		
		ArrayList<Message> messages = executeQuery(datastore, q);
		return messages;
	}

	private static ArrayList<Message> executeQuery(DatastoreService datastore, Query q){
		PreparedQuery pq = datastore.prepare(q);
		QueryResultList<Entity> results = pq.asQueryResultList(FetchOptions.Builder.withDefaults());
		ArrayList<Message> messages = new ArrayList<Message>();
		for (Entity e : results){
			messages.add(new Message(e));
		}
		
		return messages;
	}


		
	
}
