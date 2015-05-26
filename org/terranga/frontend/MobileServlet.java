package org.terranga.frontend;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.terranga.general.Image;
import org.terranga.general.RequestInfo;

import com.google.appengine.api.datastore.DatastoreServiceFactory;


@SuppressWarnings("serial")
public class MobileServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {
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
		
		page = page.concat(".html");
		
		String html = getFileText("mobile/"+page);
		
		
		
//		html = html.replace("{{ head }}", getFileText("mobile/partials/head.html"));
//		html = html.replace("{{ nav }}", getFileText("mobile/partials/nav.html"));
//		html = html.replace("{{ footer }}", getFileText("mobile/partials/footer.html"));
//		html = html.replace("{{ scripts }}", getFileText("mobile/partials/scripts.html"));



		
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
//	    	System.out.println("File Not Found:"+filepath); 
	    	return text;
	    }
	    catch(IOException e) { e.printStackTrace(); } //route to error page, please try again
	    return text;
	}
	
	
}
