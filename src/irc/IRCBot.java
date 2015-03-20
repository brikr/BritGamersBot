package irc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.TimeZone;

import org.jibble.pircbot.*;

public class IRCBot extends PircBot{
	private String user, Oauth, channel;
	public String currQ, currA;
	
	private Random rand;
	
	private ArrayList<String> mods, ogQuestions, currQuestions;
	private HashSet<String> entries;
	
	private boolean inQ;
	private QuestionThread qt;
	
	private long lastMessage = 0;
	
	public IRCBot(String user, String Oauth) {
		this.rand = new Random();
		this.entries = null;
		
		this.user = user;
		this.Oauth = Oauth;
		this.setName(user);
		
		loadMods();
		loadQuestions();
	}

	public void sendMessage(String message) {
		long newMessage = System.currentTimeMillis();
		System.out.println(user + ": " + message);
		super.sendMessage(this.channel, message);
		lastMessage = newMessage;
	}
	
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		System.out.println(sender + ": " + message);
		if(isMod(sender) && message.startsWith("!question")) startQ(message);
		if(inQ && message.toLowerCase().equals(currA)) {
			System.out.println(sender + " has answered correctly.");
			entries.add(sender);
		}
	}
	
	private void startQ(String m) {
		if(inQ) {
			this.sendMessage("There is already an active question!");
			return;
		}
		
		if(currQuestions.isEmpty()) currQuestions = (ArrayList<String>) ogQuestions;
		
		int time;
		try {
			time = Integer.parseInt(m.split(" ")[1]);
		} catch (NumberFormatException | IndexOutOfBoundsException e) {
			time = 120;
		}
		String[] input = currQuestions.remove(rand.nextInt(currQuestions.size())).split("\\|");
		this.currQ = input[0];
		this.currA = input[1].toLowerCase();
		
		System.out.println("New question started: " + this.currQ + "\n\t" + this.currA);
		this.sendMessage("New question: " + this.currQ);
		this.sendMessage("Type your response in chat to be entered into the drawing. Make sure to use proper spelling!");
		
		entries = new HashSet<String>();
		qt = new QuestionThread(this, time);
		qt.start();
		inQ = true;
	}

	public void joinChannel(String channel){
		this.channel = channel;
//		this.sendMessage("I have joined the channel.");
		super.joinChannel(channel);
	}
	
	public boolean isMod(String user) {
		return mods.contains(user.toLowerCase());
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
	
	public void loadMods() {
		Scanner s;
		mods = new ArrayList<String>();
		try {
			s = new Scanner(new File("mods"));
			while(s.hasNext())
				mods.add(s.next().toLowerCase());
			s.close();
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find mods file");
			e.printStackTrace();
		}
		System.out.println("Loaded mods:");
		for(String mod : mods)
			System.out.println("\t" + mod);
	}

	@SuppressWarnings("unchecked")
	private void loadQuestions() {
		Scanner s;
		ogQuestions = new ArrayList<String>();
		currQuestions = new ArrayList<String>();
		try {
			s = new Scanner(new File("questions"));
			while(s.hasNextLine())
				ogQuestions.add(s.nextLine());
			s.close();
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find mods file");
			e.printStackTrace();
		}
		currQuestions = (ArrayList<String>) ogQuestions.clone();
		System.out.println("Loaded " + ogQuestions.size() + " questions.");
	}

	public void endQ() {
		this.sendMessage("The question has ended after receiving " + entries.size() + " correct answers! The correct answer to \"" + this.currQ + "\" was \"" + this.currA + "\"");
		String winner = (String) entries.toArray()[rand.nextInt(entries.size())];
		this.sendMessage("And the winner is... " + winner + "!");
		
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(new File("winners"), true));
			TimeZone tz = TimeZone.getTimeZone("UTC");
		    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		    df.setTimeZone(tz);
		    String time = df.format(new Date());
			br.write(winner + "; " + time + "\n");
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.entries = null;
		inQ = false;
	}
}
