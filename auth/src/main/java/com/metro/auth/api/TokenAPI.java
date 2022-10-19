package com.metro.auth.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webage.domain.Customer;
import com.webage.domain.CustomerFactory;
import com.webage.domain.Token;
import com.webage.util.JWTHelper;

@RestController
@RequestMapping("/token")
public class TokenAPI {

	//private static Key key = AuthFilter.key;	
	public static Token appUserToken;
	
	@GetMapping
	public String getAll() {
		return "jwt-fake-token-asdfasdfasfa".toString();
	}
	
	@PostMapping
	// public ResponseEntity<?> createTokenForCustomer(@RequestBody Customer customer, HttpRequest request, UriComponentsBuilder uri) {
	public ResponseEntity<?> createTokenForCustomer(@RequestBody Customer customer) {
		
		String username = customer.getName();
		String password = customer.getPassword();
		
		if (username != null && username.length() > 0 && password != null && password.length() > 0 && checkPassword(username, password)) {
			Token token = createToken(username);
			ResponseEntity<?> response = ResponseEntity.ok(token);
			return response;			
		}
		// bad request
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		
	}
	
	private boolean checkPassword(String username, String password) {
		// special case for application user
		if(username.equals("ApiClientApp") && password.equals("secret")) {
			return true;
		}
		// make call to customer service 
		Customer cust = getCustomerByNameFromCustomerAPI(username);
		
		// compare name and password
		if(cust != null && cust.getName().equals(username) && cust.getPassword().equals(password)) {
			return true;				
		}		
		return false;
		
		// local version of the above code, gets customer from repository
		/*
		Iterator<Customer> customers = repo.findAll().iterator();
		while(customers.hasNext()) {
			Customer cust = customers.next();
			if(cust.getName().equals(username) && cust.getPassword().equals(password)) {
				return true;				
			}
		}
		*/
		

	}
	
	public static Token getAppUserToken() {
		if(appUserToken == null || appUserToken.getToken() == null || appUserToken.getToken().length() == 0) {
			appUserToken = createToken("ApiClientApp");
		}
		return appUserToken;
	}
	
    private static Token createToken(String username) {
    	String scopes = "com.webage.data.apis";
    	// special case for application user
    	if( username.equalsIgnoreCase("ApiClientApp")) {
    		scopes = "com.webage.auth.apis";
    	}
    	String token_string = JWTHelper.createToken(scopes);
    	
		/*
		 * long fiveHoursInMillis = 1000 * 60 *60 * 5;
		 * 
		 * String token_string = Jwts.builder() .setSubject(username)
		 * .setIssuer("me@me.com") .claim("scopes",scopes) .setExpiration(new
		 * Date(System.currentTimeMillis() + fiveHoursInMillis)) .signWith(key)
		 * .compact();
		 */
    	
    	return new Token(token_string);
    }
    
    
	private Customer getCustomerByNameFromCustomerAPI(String username) {
		try {

			URL url = new URL("http://localhost:8080/api/customers/byname/" + username);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			Token token = getAppUserToken();
			conn.setRequestProperty("authorization", "Bearer " + token.getToken());

			if (conn.getResponseCode() != 200) {
				return null;
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output = "";
				String out = "";
				while ((out = br.readLine()) != null) {
					output += out;
				}
				conn.disconnect();
				return CustomerFactory.getCustomer(output);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;

		} catch (java.io.IOException e) {
			e.printStackTrace();
			return null;
		}

	}  	

}    
