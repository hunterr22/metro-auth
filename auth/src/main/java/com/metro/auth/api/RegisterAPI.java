package com.metro.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.webage.domain.Customer;
import com.webage.domain.CustomerFactory;
import com.webage.domain.Token;

@RestController
@RequestMapping("/register")
public class RegisterAPI {

	@PostMapping
	public ResponseEntity<?> registerCustomer(@RequestBody Customer newCustomer, UriComponentsBuilder uri) {
		if (newCustomer.getId() != 0 || newCustomer.getName() == null || newCustomer.getEmail() == null) {
			// Reject we'll assign the customer id
			return ResponseEntity.badRequest().build();
		}
		
		String json_string = CustomerFactory.getCustomerAsJSONString(newCustomer);
		
		postNewCustomerToCustomerAPI(json_string);
		
		// old code that calls repository directly
		// newCustomer = repo.save(newCustomer);
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(newCustomer.getId()).toUri();
		ResponseEntity<?> response = ResponseEntity.created(location).build();
		return response;
	}

	private void postNewCustomerToCustomerAPI(String json_string) {
		try {

			URL url = new URL("http://localhost:8080/api/customers");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
	  		Token token = TokenAPI.getAppUserToken();
	  		conn.setRequestProperty("authorization", "Bearer " + token.getToken());
	  		// conn.setRequestProperty("tokencheck", "false");

			OutputStream os = conn.getOutputStream();
			os.write(json_string.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

}
Footer
© 2022 GitHub, Inc.
Footer navigation
