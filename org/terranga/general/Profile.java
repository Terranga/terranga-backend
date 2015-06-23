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
	private String homeCity;
	private String homeCountry;
	private String image;
	private String type;
	private Text bio;
	private Text dream;
	private Long age;
	
	private ArrayList<String> languages;
	
	private String isFeatured;
	private String profession;
	private Long points;
	private String endorsementID;
	
	
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
		setHomeCity("none");
		setHomeCountry("none");
		setImage("XPMHTdp4");
		setBio(new Text(""));
		setDream(new Text(""));
		setType("traveler");
		setAge(0L);
		setProfession("none");
		setPoints(0L);
		setEndorsementID("none");
		
		ArrayList<String> temp = new ArrayList<String>();
		temp.add("none");
		setLanguages(temp);
	}
	
	@SuppressWarnings("unchecked") //GO BACK TO THIS!
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
		setHomeCity((String)ent.getProperty("homeCity"));
		setHomeCountry((String)ent.getProperty("homeCountry"));
		setType((String)ent.getProperty("type"));
		setIsFeatured((String)ent.getProperty("isFeatured"));
		setAge((Long)ent.getProperty("age"));
		setBio((Text)ent.getProperty("bio"));
		setDream((Text)ent.getProperty("dream"));
		setImage((String)ent.getProperty("image"));
		setLanguages((ArrayList<String>)ent.getProperty("languages"));
		setProfession((String)ent.getProperty("profession"));
		setPoints((Long)ent.getProperty("points"));
		setEndorsementID((String)ent.getProperty("endorsementID"));
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
        p.setProperty("homeCountry", getHomeCountry());
        p.setProperty("isFeatured", getIsFeatured());
        p.setProperty("city", getCity());
        p.setProperty("homeCity", getHomeCity());
        p.setProperty("age", getAge());
        p.setProperty("bio", getBio());
        p.setProperty("dream", getDream());
        p.setProperty("image", getImage());
        p.setProperty("languages", getLanguages());
        p.setProperty("profession", getProfession());
        p.setProperty("points", getPoints());
        p.setProperty("endorsementID", getEndorsementID());
        return p;
	}
	
	public Map<String, Object> getSummary(){
		Map<String, Object> summary = new HashMap<String, Object>();
		summary.put("id", getId());
		summary.put("timestamp", getTimestamp().toString());
		
		summary.put("firstName", getFirstName());
		summary.put("type", getType());
		summary.put("city", getCity());
		summary.put("country", getCountry());
		summary.put("homeCity", getHomeCity());
		summary.put("homeCountry", getHomeCountry());
		summary.put("lastName", getLastName());
		summary.put("email", getEmail());
		summary.put("phone", getPhone());
		summary.put("isFeatured", getIsFeatured());
		summary.put("age", Long.toString(getAge()));
//		summary.put("password", getPassword());
		summary.put("bio", getBio().getValue());
		summary.put("dream", getDream().getValue());
		summary.put("image", getImage());
		summary.put("profession", getProfession());
		summary.put("points", getPoints());
		summary.put("endorsementID", getEndorsementID());
		
		ArrayList<String> workAround = (getLanguages().contains("none")) ? new ArrayList<String>() : getLanguages();
		summary.put("languages", workAround);
		
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
		if (json.has("homeCity")){
			String c = json.getString("homeCity");
			if (c.length() > 0)
				setHomeCity(c);
		}

		if (json.has("homeCountry")){
			String c = json.getString("homeCountry");
			if (c.length() > 0)
				setHomeCountry(c);
		}
		
		if (json.has("profession")){
			String p = json.getString("profession");
			if (p.length() > 0)
				setProfession(p);
		}

		if (json.has("points"))
			setPoints(Long.parseLong(json.getString("points")));
		
		if (json.has("endorsementID")){
			String eID = json.getString("endorsementID");
			if (eID.length() > 0)
				setEndorsementID(eID);
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
		
		
		if(json.has("languages")){
			ArrayList<String> temp = new ArrayList<String>();
			JSONArray list = json.getJSONArray("languages");
			for(int i = 0; i <list.length(); i++){
				String languageID = list.getString(i);
				if (languageID.equals("none") == false)
					temp.add(languageID);
			}
			if (temp.size() == 0)
				temp.add("none");
			else
				temp.remove("none");
			
			setLanguages(temp);
		}

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
	
	public String getHomeCity() {
		return homeCity;
	}

	public void setHomeCity(String homeCity) {
		this.homeCity = homeCity;
	}

	public String getHomeCountry() {
		return homeCountry;
	}

	public void setHomeCountry(String homeCountry) {
		this.homeCountry = homeCountry;
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
	
	public ArrayList<String> getLanguages() {
		return languages;
	}

	public void setLanguages(ArrayList<String> languages) {
		this.languages = languages;
	}
	
	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}
	
	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
	}

	public String getEndorsementID() {
		return endorsementID;
	}

	public void setEndorsementID(String endorsementID) {
		this.endorsementID = endorsementID;
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
