package org.terranga.general;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class Review {
	//AUTO GENERATED
	private String id;
	private Date timestamp;
				
	//USER INPUTED
	private String reviewedBy;
	private String reviewed;
	private Long score;
	private Text description;
	
	
	//DEFAULT CONSTRUCTOR
	public Review(){
		setId(randomString(8));
		setTimestamp(new Date());
		
		setReviewed("none");
		setReviewedBy("none");
		setScore(0L);
		setDescription(new Text(""));
	}
	
	//CONSTRUCTOR FROM ENTITY
	public Review(Entity ent){
		setId(ent.getKey().getName());
		setTimestamp((Date)ent.getProperty("timestamp"));
		setReviewed((String)ent.getProperty("reviewed"));
		setReviewedBy((String)ent.getProperty("reviewedBy"));
		setDescription((Text)ent.getProperty("description"));
		setScore((Long)ent.getProperty("score"));
	}
	
	//ENTITY CONSTRUCTOR
	public Entity createEntityVersion(){
		Entity e = new Entity("Review", getId());
		e.setProperty("timestamp", getTimestamp());
		e.setProperty("reviewed", getReviewed());
		e.setProperty("reviewedBy", getReviewedBy());
        e.setProperty("score", getScore());
		e.setProperty("description", getDescription());
		
		return e;
	}	
	
	//GET SUMMARY FOR JSON USE
	public Map<String, Object> getSummary(){
		Map<String, Object> summary = new HashMap<String, Object>();
		
		//AUTO GENERATED
		summary.put("id", getId());
		summary.put("timestamp", getTimestamp().toString());
		
		//REVIEWED ID
		if(getReviewed().equals("none"))
			summary.put("reviewed", "");
		else
			summary.put("reviewed", getReviewed());
		
		//CATEGORY TAG
		if(getReviewedBy().equals("none"))
			summary.put("reviewedBy", "");
		else
			summary.put("reviewedBy", getReviewedBy());
		
		//DESCRIPTION
		if(getDescription().getValue().equals("none"))
			summary.put("description", "");
		else
			summary.put("description", getDescription().getValue());
		
		summary.put("score", Long.toString(getScore()));
		
		return summary;
	}
	
	//UPDATE FROM JSON-SUMMARY
	public void update(JSONObject json) throws JSONException {
		if (json.has("reviewed")){
			String e = json.getString("reviewed");
			if (e.length() > 0)
				setReviewed(e);
		}

		if (json.has("reviewedBy")){
			String eb = json.getString("reviewedBy");
			if (eb.length() > 0)
				setReviewedBy(eb);
		}
		
		if (json.has("score"))
			setScore(Long.parseLong(json.getString("score")));
		
		if (json.has("description"))
			setDescription(new Text(json.getString("description")));
		
	}	
	
	//SAVE METHODS
	public void save(){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(createEntityVersion());
	}

	public void save(DatastoreService datastore){
		datastore.put(createEntityVersion());
	}	
	
	//RANDOM STRING GENERATOR
	public static String randomString(int length){
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replace("-", "");
		uuid = uuid.substring(0, length);
		return uuid;
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

	public String getReviewedBy() {
		return reviewedBy;
	}

	public void setReviewedBy(String reviewedBy) {
		this.reviewedBy = reviewedBy;
	}

	public String getReviewed() {
		return reviewed;
	}

	public void setReviewed(String reviewed) {
		this.reviewed = reviewed;
	}

	public Text getDescription() {
		return description;
	}

	public void setDescription(Text description) {
		this.description = description;
	}
	
	public Long getScore() {
		return score;
	}

	public void setScore(Long score) {
		this.score = score;
	}	

	
//- - - - - - - - - - - - - - - - - - - - - - QUERIES - - - - - - - - - - - - - - - - - - - - - -//
	
	//EXECUTE QUERY
	private static ArrayList<Review> executeQuery(DatastoreService datastore, Query q, int limit){
		PreparedQuery pq = datastore.prepare(q);
						
		FetchOptions options = (limit==0) ? FetchOptions.Builder.withDefaults() : FetchOptions.Builder.withLimit(limit);
		QueryResultList<Entity> results = pq.asQueryResultList(options); //THE REQUEST
		ArrayList<Review> reviews = new ArrayList<Review>();
		for(Entity e : results){
			reviews.add(new Review(e));
		}
		return reviews;
	}
				
	//FETCH SINGLE REVIEW (ID)
	public static Review fetchReview(DatastoreService datastore, String reviewID){
		Review search = null;
		try {
			Entity ent = datastore.get(KeyFactory.createKey("Review", reviewID));
			search = new Review(ent);
		}
		catch(EntityNotFoundException e){
		}
		return search;
	}
			
		//FETCH ALL REVIEWS
		public static ArrayList<Review> fetchReviews(DatastoreService datastore, int limit){
			Query q = new Query("Review").addSort("timestamp", Query.SortDirection.DESCENDING);
			ArrayList<Review> reviews = executeQuery(datastore, q, limit);
			return reviews;
		}
		
		
		//FETCH REVIEWS (reviewed)
		public static ArrayList<Review> fetchReviewsWithReviewed(DatastoreService datastore, String reviewed,  int limit){
			Query q = new Query("Review").addSort("timestamp", Query.SortDirection.DESCENDING);
			
			Filter reviewedFilter = new FilterPredicate("reviewed", FilterOperator.EQUAL, reviewed);
			q.setFilter(reviewedFilter);
			
			ArrayList<Review> reviews = executeQuery(datastore, q, limit);
			return reviews;
		}
		
		//FETCH REVIEWS (reviewedBy)
		public static ArrayList<Review> fetchReviewsWithReviewedBy(DatastoreService datastore, String reviewedBy,  int limit){
			Query q = new Query("Review").addSort("timestamp", Query.SortDirection.DESCENDING);
			
			Filter reviewedByFilter = new FilterPredicate("reviewedBy", FilterOperator.EQUAL, reviewedBy);
			q.setFilter(reviewedByFilter);
			
			ArrayList<Review> reviews = executeQuery(datastore, q, limit);
			return reviews;
		}
	
}
