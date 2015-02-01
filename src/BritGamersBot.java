import irc.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class BritGamersBot {
	private static String user;
	private static String Oauth;
	private static String channel;
	
	private static boolean debug = true;
	
	public static void main(String[] args) {
		loadUser();
		channel = "#" + user;
		IRCBot bot = new IRCBot(user, Oauth);
		bot.setVerbose(debug);
		bot.joinChannel(channel);
	}

	private static void loadUser() {
		Scanner s;
		try {
			s = new Scanner(new File("userinfo"));
			user = s.next();//Read the username
			Oauth = s.next();//Read the OAuth key for the account
			s.close();
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find userinfo file");
			e.printStackTrace();
		}
	}
}
