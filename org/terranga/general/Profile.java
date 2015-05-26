package org.terranga.general;

import java.util.ArrayList;
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
import com.google.appengine.api.datastore.Text;

import org.json.*;


public class Profile {
	
	// auto generated
	private String id;
	private Date timestamp;
	
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String phone;
	private String city;
	private String country;
	private String image;
	private String type;
	private Text bio;
	private Text dream;
	private Long age;
	
	
	private String isFeatured;
	
	
	public Profile(){
		setId(randomString(8));
		setTimestamp(new Date());
		
		setFirstName("none");
		setLastName("none");
		setEmail("none");
		setPassword("none");
		setIsFeatured("no");
		setPhone("none");
		setCity("none");
		setCountry("none");
		setImage("XPMHTdp4");
		setBio(new Text(""));
		setDream(new Text(""));
		setType("traveler");
		setAge(0L);
	}
	
	public Profile(Entity ent){
		setId(ent.getKey().getName());
		setTimestamp((Date)ent.getProperty("timestamp"));
		setFirstName((String)ent.getProperty("firstName"));
		setLastName((String)ent.getProperty("lastName"));
		setEmail((String)ent.getProperty("email"));
		setPhone((String)ent.getProperty("phone"));
		setPassword((String)ent.getProperty("password"));
		setCity((String)ent.getProperty("city"));
		setCountry((String)ent.getProperty("country"));
		setType((String)ent.getProperty("type"));
		setIsFeatured((String)ent.getProperty("isFeatured"));
		setAge((Long)ent.getProperty("age"));
		setBio((Text)ent.getProperty("bio"));
		setDream((Text)ent.getProperty("dream"));
		setImage((String)ent.getProperty("image"));
	}
	
	
	public Entity createEntityVersion(){
        Entity p = new Entity("Profile", getId());
        p.setProperty("email", getEmail());
        p.setProperty("timestamp", getTimestamp());
        p.setProperty("firstName", getFirstName());
        p.setProperty("lastName", getLastName());
        p.setProperty("phone", getPhone());
        p.setProperty("email", getEmail());
        p.setProperty("password", getPassword());
        p.setProperty("type", getType());
        p.setProperty("country", getCountry());
        p.setProperty("isFeatured", getIsFeatured());
        p.setProperty("city", getCity());
        p.setProperty("age", getAge());
        p.setProperty("bio", getBio());
        p.setProperty("dream", getDream());
        p.setProperty("image", getImage());
        return p;
	}
	
	public Map<String, Object> getSummary(){
		Map<String, Object> summary = new HashMap<String, Object>();
		summary.put("id", getId());
		summary.put("timestamp", getTimestamp().toString());
		
		summary.put("firstName", getFirstName());
		summary.put("city", getCity());
		summary.put("type", getType());
		summary.put("country", getCountry());
		summary.put("lastName", getLastName());
		summary.put("email", getEmail());
		summary.put("phone", getPhone());
		summary.put("isFeatured", getIsFeatured());
		summary.put("age", Long.toString(getAge()));
//		summary.put("password", getPassword());
		summary.put("bio", getBio().getValue());
		summary.put("dream", getDream().getValue());
		summary.put("image", getImage());
		return summary;
	}
	
	public void update(JSONObject json) throws JSONException {
		if (json.has("email")){
			String e = json.getString("email");
			if (e.length() > 0)
				setEmail(e);
		}
		
		if (json.has("firstName")){
			String fn = json.getString("firstName");
			if (fn.length() > 0)
				setFirstName(fn);
		}

		if (json.has("lastName")){
			String ln = json.getString("lastName");
			if (ln.length() > 0)
				setLastName(ln);
		}

		if (json.has("password")){
			String pw = json.getString("password");
			if (pw.length() > 0)
				setPassword(pw);
		}

		if (json.has("phone")){
			String p = json.getString("phone");
			if (p.length() > 0)
				setPhone(p);
		}

		if (json.has("city")){
			String c = json.getString("city");
			if (c.length() > 0)
				setCity(c);
		}

		if (json.has("country")){
			String c = json.getString("country");
			if (c.length() > 0)
				setCountry(c);
		}

		if (json.has("age"))
			setAge(Long.parseLong(json.getString("age")));

		if (json.has("type"))
			setType(json.getString("type"));

		if (json.has("bio"))
			setBio(new Text(json.getString("bio")));

		if (json.has("dream"))
			setDream(new Text(json.getString("dream")));

		if (json.has("image"))
			setImage(json.getString("image"));

	}

	
	public static String randomString(int length) {
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


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName.toLowerCase().trim();
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName.toLowerCase().trim();
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email.toLowerCase().trim();
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getIsFeatured() {
		return isFeatured;
	}

	public void setIsFeatured(String isFeatured) {
		this.isFeatured = isFeatured;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone.toLowerCase().trim();
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city.toLowerCase().trim();
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country.toLowerCase().trim();
	}

	public Long getAge() {
		return age;
	}

	public void setAge(Long age) {
		this.age = age;
	}


	public Text getBio() {
		return bio;
	}

	public void setBio(Text bio) {
		this.bio = bio;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Text getDream() {
		return dream;
	}

	public void setDream(Text dream) {
		this.dream = dream;
	}


// - - - - - - - - - - - - - - - - - - - - - QUERIES - - - - - - - - - - - - - - - - - - - - - - - - 
	
	

	public static Profile fetchProfile(DatastoreService datastore, String profileId){
		Profile p = null;
		try {
			p = new Profile(datastore.get(KeyFactory.createKey("Profile", profileId)));
		}
		catch (EntityNotFoundException e){
			
		}
		
		return p;
	}
	
	
	public static ArrayList<Profile> fetchProfiles(DatastoreService datastore, int limit){
		Query q = new Query("Profile").addSort("timestamp", Query.SortDirection.DESCENDING);
		ArrayList<Profile> profiles = executeQuery(datastore, q, limit);
		return profiles;
	}

	
	public static ArrayList<Profile> fetchProfilesWithEmail(DatastoreService datastore, String email, int limit){
		Query q = new Query("Profile").addSort("timestamp", Query.SortDirection.DESCENDING);
		
		email = email.trim().toLowerCase();
		Filter emailFilter = new FilterPredicate("email", FilterOperator.EQUAL, email);
		q.setFilter(emailFilter);

		ArrayList<Profile> profiles = executeQuery(datastore, q, limit);
		return profiles;
	}
	
	
	public static ArrayList<Profile> fetchProfilesWithFeatured(DatastoreService datastore, String isFeatured, int limit){
		Query q = new Query("Profile").addSort("timestamp", Query.SortDirection.DESCENDING);
		
		Filter emailFilter = new FilterPredicate("isFeatured", FilterOperator.EQUAL, isFeatured);
		q.setFilter(emailFilter);

		ArrayList<Profile> profiles = executeQuery(datastore, q, limit);
		return profiles;
	}


	private static ArrayList<Profile> executeQuery(DatastoreService datastore, Query q, int limit){
		PreparedQuery pq = datastore.prepare(q);
		
		FetchOptions options = (limit==0) ? FetchOptions.Builder.withDefaults() : FetchOptions.Builder.withLimit(limit);
		QueryResultList<Entity> results = pq.asQueryResultList(options);
		ArrayList<Profile> profiles = new ArrayList<Profile>();
		for (Entity e : results){
			profiles.add(new Profile(e));
		}
		
		return profiles;
	}



	

}
