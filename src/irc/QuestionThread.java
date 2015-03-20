package irc;

public class QuestionThread extends Thread {
	private boolean on;
	private boolean warned;
	private final long SLEEP_DELAY = 500; //0.5 seconds
	private long START_TIME, END_TIME;
	private IRCBot bot;

	public QuestionThread(IRCBot ircBot, int time) {
		this.bot = ircBot;
		this.START_TIME = System.currentTimeMillis();
		this.END_TIME = START_TIME + (time * 1000);
	}
	
	public void run() {
		on = true;
		warned = false;
		while(on) {
			if(System.currentTimeMillis() > END_TIME) {
				bot.endQ();
				on = false;
			}
			if(!warned && System.currentTimeMillis() > (END_TIME - 15000)) {
				bot.sendMessage("Only 15 more seconds to get your answer in! Question: " + bot.currQ);
				warned = true;
			}
			try {
				Thread.sleep(SLEEP_DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
