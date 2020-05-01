package com.navkar.billGenerator;

import com.navkar.billGeneratorPojo.Credentials;

public class LoginController {
	
	public Credentials getAdminCredentials(){
		return new Credentials("mayank", "pass123");
	}
}
