package org.terranga.general;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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


public class Message {
	
	//AUTO GENERATED
	private String id;
	private Date timestamp;
	private String threadID;
		
	//USER INPUTED
	private String senderID;
	private String recipientID;
	private String subject;
	private Text body;

	//DEFAULT CONSTRUCTOR
	public Message(){
		//AUTO GENERATED
		setId(randomString(8));
		setTimestamp(new Date());
		setThreadID(randomString(8));
		
		//USER INPUTED
		setSenderID("none");
		setRecipientID("none");
		setSubject("none");
		setBody(new Text(""));
	}

	public Message(String thread){
		//AUTO GENERATED
		setId(randomString(8));
		setTimestamp(new Date());
		setThreadID(thread);
		
		//USER INPUTED
		setSenderID("none");
		setRecipientID("none");
		setSubject("none");
		setBody(new Text(""));
	}
	
	//CREATE PROFILE FROM ENTITY
	public Message(Entity ent){
		setId(ent.getKey().getName());
		setTimestamp((Date)ent.getProperty("timestamp"));
		setThreadID((String)ent.getProperty("threadID"));
		
		setSenderID((String)ent.getProperty("senderID"));
		setRecipientID((String)ent.getProperty("recipientID"));
		setSubject((String)ent.getProperty("subject"));
		setBody((Text)ent.getProperty("body"));
	}
	
	//CREATE ENTITY FROM PROFILE
	public Entity createEntityVersion(){
		Entity m = new Entity("Message", getId()); 
		m.setProperty("timestamp", getTimestamp());
		m.setProperty("threadID", getThreadID());
		
		m.setProperty("senderID", getSenderID());
		m.setProperty("recipientID", getRecipientID());
		m.setProperty("subject", getSubject());
		m.setProperty("body", getBody());
		return m;
	}
		
	
	public Map<String, Object> getSummary(){
		Map<String, Object> summary = new HashMap<String, Object>();
		summary.put("id", getId());
		summary.put("timestamp", getTimestamp());
		summary.put("threadID", getThreadID());
		
		summary.put("senderID", getSenderID());
		summary.put("recipientID", getRecipientID());
		summary.put("subject", getSubject());
		summary.put("body", getBody());

		return summary;
	}	
	
	public void update(JSONObject json) throws JSONException{
		if(json.has("threadID"))
			setThreadID(json.getString("threadID"));
		
		if(json.has("senderID"))
			setSenderID(json.getString("senderID"));
		
		if(json.has("recipientID"))
			setRecipientID(json.getString("recipientID"));
		
		if(json.has("senderID"))
			setSenderID(json.getString("senderID"));
		
		if(json.has("subject"))
			setSubject(json.getString("subject"));
		
		if(json.has("body"))
			setBody(new Text(json.getString("body")));
	}
	
	//SAVE MESSAGE
	public void save(){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(createEntityVersion());
	}
		
	public void save(DatastoreService datastore){
		datastore.put(createEntityVersion());
	}	
	
	
	//GENERATE RANDOM ID
	public static String randomString(int length){
		double random = Math.random();
		String randomString = Double.toString(random).substring(2);
		return randomString.substring(randomString.length()-length);
	}	
	
	//GETTERS AND SETTERS
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
	
	public String getThreadID() {
		return threadID;
	}

	public void setThreadID(String threadID) {
		this.threadID = threadID;
	}

	public String getSenderID() {
		return senderID;
	}

	public void setSenderID(String senderID) {
		this.senderID = senderID;
	}

	public String getRecipientID() {
		return recipientID;
	}

	public void setRecipientID(String recipientID) {
		this.recipientID = recipientID;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Text getBody() {
		return body;
	}

	public void setBody(Text string) {
		this.body = string;
	}
	
//- - - - - - - - - - - - - - - - - - - - - - QUERIES - - - - - - - - - - - - - - - - - - - - - -//
	
	//FETCH SINGLE MESSAGE (ID)
	public static Message fetchMessage(DatastoreService datastore, String messageID){
		Message m = null;
		try {
			Entity ent = datastore.get(KeyFactory.createKey("Message", messageID));
			m = new Message(ent);
		}
		catch(EntityNotFoundException e){
		}
		return m;
	}	
	
	//FETCH ALL MESSAGES
	public static ArrayList<Message> fetchMessages(DatastoreService datastore, int limit){
		Query q = new Query("Message").addSort("timestamp", Query.SortDirection.DESCENDING);
		ArrayList<Message> messages = executeQuery(datastore, q, limit);
		return messages;
	}
	
	
	//FETCH MESSAGES (FILTER: THREAD ID)
	public static ArrayList<Message> fetchMessagesByThread(DatastoreService datastore, String threadID,  int limit){
		Query q = new Query("Message").addSort("timestamp", Query.SortDirection.DESCENDING);
		
		Filter threadFilter = new FilterPredicate("threadID", FilterOperator.EQUAL, threadID);
		q.setFilter(threadFilter);
		
		ArrayList<Message> messages = executeQuery(datastore, q, limit);
		return messages;
	}
	
	//FETCH MESSAGES (FILTER: SENDER ID)
	public static ArrayList<Message> fetchMessagesBySender(DatastoreService datastore, String senderID,  int limit){
		Query q = new Query("Message").addSort("timestamp", Query.SortDirection.DESCENDING);
		
		Filter senderFilter = new FilterPredicate("senderID", FilterOperator.EQUAL, senderID);
		q.setFilter(senderFilter);
		
		ArrayList<Message> messages = executeQuery(datastore, q, limit);
		return messages;
	}
	
	//FETCH MESSAGES (FILTER: RECIPIENT ID)
	public static ArrayList<Message> fetchMessagesByRecipient(DatastoreService datastore, String recipientID,  int limit){
		Query q = new Query("Message").addSort("timestamp", Query.SortDirection.DESCENDING);
		
		Filter recipientFilter = new FilterPredicate("recipientID", FilterOperator.EQUAL, recipientID);
		q.setFilter(recipientFilter);
		
		ArrayList<Message> messages = executeQuery(datastore, q, limit);
		return messages;
	}
	
	//FETCH MESSAGES (FILTER: RECIPIENT ID & SENDER ID)
	public static ArrayList<Message> fetchMessagesWithSenderAndReciever(DatastoreService datastore, String recipientID, String senderID,  int limit){
		Query q = new Query("Message").addSort("timestamp", Query.SortDirection.DESCENDING);
		
		//FOR FIRST SET
		Filter recipientFilter = new FilterPredicate("recipientID", FilterOperator.EQUAL, recipientID);
		Filter senderFilter = new FilterPredicate("senderID", FilterOperator.EQUAL, senderID);
		
		CompositeFilter combinedFilter = CompositeFilterOperator.and(recipientFilter, senderFilter);
		
		//FOR FIRST SET
		Filter recipientFilterB = new FilterPredicate("recipientID", FilterOperator.EQUAL, senderID);
		Filter senderFilterB = new FilterPredicate("senderID", FilterOperator.EQUAL, recipientID);
		
		CompositeFilter combinedFilterB = CompositeFilterOperator.and(recipientFilterB, senderFilterB);
		
		//COMBINE TWO COMPOSITE FILTERS
		CompositeFilter totalCombinedFilter = CompositeFilterOperator.or(combinedFilter, combinedFilterB);
		q.setFilter(totalCombinedFilter);
		
		
		ArrayList<Message> messages = executeQuery(datastore, q, limit);
		return messages;
	}
	
	
	//EXECUTE QUERY
	private static ArrayList<Message> executeQuery(DatastoreService datastore, Query q, int limit){
		PreparedQuery pq = datastore.prepare(q);
		
		FetchOptions options = (limit==0) ? FetchOptions.Builder.withDefaults() : FetchOptions.Builder.withLimit(limit);
		QueryResultList<Entity> results = pq.asQueryResultList(options); //THE REQUEST
		ArrayList<Message> messages = new ArrayList<Message>();
		for(Entity e : results){
			messages.add(new Message(e));
		}
		return messages;
	}


}
