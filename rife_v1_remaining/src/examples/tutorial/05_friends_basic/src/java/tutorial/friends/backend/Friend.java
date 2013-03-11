/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Friend.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package tutorial.friends.backend;

public class Friend {
	private String	firstname;
	private String	lastname;
	private String	description;
	private String	url;
	
	public Friend(String firstname, String lastname, String description, String url) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.description = description;
		this.url = url;
	}
	
	public Friend() {
	}
	
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	public String getFirstname() {
		return firstname;
	}
	
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public String getLastname() {
		return lastname;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
}
