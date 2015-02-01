package irc;

import org.jibble.pircbot.*;

public class IRCBot extends PircBot{
	private String user;
	private String Oauth;
	private String channel;

	private long lastMessage = 0;
	
	public IRCBot(String user, String Oauth) {
		this.user = user;
		this.Oauth = Oauth;
		this.setName(user);
		
	}

	public void sendMessage(String message) {
		long newMessage = System.currentTimeMillis();
		System.out.println(user + ": " + message);
		if(newMessage - lastMessage > 1500) super.sendMessage(this.channel, message);
		else System.out.println("Withholding message: " + message);
		lastMessage = newMessage;
	}
	
	public void joinChannel(String channel){
		this.channel = channel;
		super.joinChannel(channel);
	}
	
	public int getNumUsers() {
		return getUsers(this.channel).length;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getKey() {
		return Oauth;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public void setKey(String Oauth) {
		this.Oauth = Oauth;
	}
}
