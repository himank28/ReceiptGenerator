package com.navkar.billGeneratorPojo;

public class Credentials {
	private String userName;
	private String password;
	
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
		
	public Credentials(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}
	

	public String toString() {
		return "Credentials [userName=" + userName + ", password=" + password + "]";
	}
				
}
