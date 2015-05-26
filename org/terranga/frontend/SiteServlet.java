package org.terranga.frontend;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.DatastoreServiceFactory;

import org.terranga.general.*;


public class SiteServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/html");
		
		
		RequestInfo request = new RequestInfo(req, resp);
		String page = request.getResource();
		
		if (page.equals("images")){
			String imageId = request.getResourceIdentifier();

			if (imageId==null)
				return;
			
            Image image = Image.fetchImage(DatastoreServiceFactory.getDatastoreService(), imageId);
            if (image==null)
            	return;
            
            String address = image.getAddress();
            String crop = req.getParameter("crop");
            address = (crop==null) ? address.concat("=s1024") : address.concat("=s"+crop+"-c");
            
            resp.sendRedirect(address);
            return;
		}
		
		String userAgent = req.getHeader("User-Agent").toLowerCase();
		if (userAgent.contains("iphone")==true || userAgent.contains("android")==true){
			resp.sendRedirect("/mobile/home");
			return;
		}
		

		
		page = page.concat(".html");
		String html = getFileText("site/"+page);
		
		html = html.replace("{{ head }}", getFileText("site/partials/head.html"));
		html = html.replace("{{ nav }}", getFileText("site/partials/nav.html"));
		html = html.replace("{{ scripts }}", getFileText("site/partials/scripts.html"));
		html = html.replace("{{ footer }}", getFileText("site/partials/footer.html"));
		
		
		resp.getWriter().print(html);
		return;
	}
	
	
	
	public static String getFileText(String filepath){
		String text = null;
		try {
			FileReader fileReader = new FileReader(filepath);
			BufferedReader reader = new BufferedReader(fileReader);
			String stringFromFile = "";
			while( stringFromFile != null ){
				if (text ==null){ text = ""; }
				text = text+stringFromFile+"\n";
				stringFromFile = reader.readLine();  // read next line
			}
			reader.close();
		}
	    catch(FileNotFoundException e) { //route to 404 page
	    	text = "NOT FOUND";
	    	return text;
	    }
	    catch(IOException e) { e.printStackTrace(); } //route to error page, please try again
	    return text;
	}
	
	


}
