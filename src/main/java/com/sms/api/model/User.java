package com.sms.api.model;

import java.io.Serializable;
import java.util.Date;

public class User extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 2863585162742337849L;

	private String userId;
	private String password;
	private int prefix;
	private String prefixName;
	private String firstName;
	private String lastName;
	private String tokenId;
	private String activeStatus;
	private Date lastLoggedOn;
	private String role;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getPrefix() {
		return prefix;
	}
	public void setPrefix(int prefix) {
		this.prefix = prefix;
	}
	public String getPrefixName() {
		return prefixName;
	}
	public void setPrefixName(String prefixName) {
		this.prefixName = prefixName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getTokenId() {
		return tokenId;
	}
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
	public String getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(String activeStatus) {
		this.activeStatus = activeStatus;
	}
	public Date getLastLoggedOn() {
		return lastLoggedOn;
	}
	public void setLastLoggedOn(Date lastLoggedOn) {
		this.lastLoggedOn = lastLoggedOn;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
}
