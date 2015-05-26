package org.terranga.general;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class LoginHandler {

	private Profile profile;
	private int status;

	
	public LoginHandler(HttpServletRequest req){
		setStatus(0);
		setProfile(null);
		checkLogin(req);
	}


	
	// 0 = not logged in at all, 1=logged in but not registered, 2=registered user
	public int checkLogin(HttpServletRequest req){
		HttpSession session = req.getSession();
		
		if (session.getAttribute("user")==null) { //no cookie, not logged in.
			setStatus(0);
			return 0;
		}
		
		String e = (String)session.getAttribute("expires");
		Long expiration = Long.parseLong(e);
		
		Date now = new Date();
		Long diff = expiration-now.getTime();
		double seconds = diff/1000;
		double mins = seconds/60;
		double hoursLeft = mins/60;
		
		if (hoursLeft > 24) { // expired, no longer valid.
			session.invalidate();
			setStatus(0);
			return 0;
		}

		String userId = (String)session.getAttribute("user");
        Profile profile = Profile.fetchProfile(DatastoreServiceFactory.getDatastoreService(), userId);
        if (profile == null){ // user not found
        	session.invalidate();
			setStatus(0);
			return 0;
        }
        
		setProfile(profile);
		setStatus(2);
        return 2;
	}
	
	public void logout(HttpServletRequest req){
		HttpSession session = req.getSession();
		session.invalidate();
	}
	
	public String getUserEmail(){
		return getProfile().getEmail();
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}


	public Profile getProfile() {
		return profile;
	}


	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

}
