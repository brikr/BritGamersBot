import irc.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/* Expected files:
 * mods - a newline-seperated file of twitch accounts that can use the !question command
 * questions - a newline-seperated file of questions to ask, in the format question|answer
 * 
 */
public class BritGamersBot {
	private static String user;
	private static String Oauth;
	private static String channel;
	
	private static boolean debug = false;
	private static boolean on = true;
	
	public static void main(String[] args) throws Exception{
		loadUser();
//		channel = "#" + user;
		channel = "#minikori";
		IRCBot bot = new IRCBot(user, Oauth);
		bot.setVerbose(debug);
		bot.connect("irc.twitch.tv.", 6667, Oauth);
		bot.joinChannel(channel);
		while(on) {
			Thread.sleep(500);
		}
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
