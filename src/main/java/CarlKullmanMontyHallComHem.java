import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.stream.Stream;

public class CarlKullmanMontyHallComHem {
	public static void main(String[] args) throws IOException {
		p("Monty Hall problem http://en.wikipedia.org/wiki/Monty_Hall_problem");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		new CarlKullmanMontyHallComHem().play(in.lines());
	}
	// dynamic part

	int roundCount = 0;
	int winCount = 0;
	private Random rng = new Random(System.currentTimeMillis());

	int point; // 0, 1, 2 <- player points this box to begin round
	boolean win; // true when player wins
	boolean gameStarted = false; // game state

	public void play(Stream<String> in) throws IOException {
		p("Select a box 1, 2 or 3");
		in.forEach(line -> {
			// exit?
			if (line.matches("[QqXxEe].*")) System.exit(0);
			// state machine with 2 states
			if (gameStarted) {
				playRound(line);
				gameStarted = false;
				roundCount++;
				if (win) winCount++;
				p(toString());
				return;
			}
			// start round
			gameStarted = startRound(line);
		});
	}

	// first select
	private boolean startRound(String s) {
		point = (s + "1").charAt(0) - '1'; // append default "1" in case empty input line (only <RETURN>)
		// validate input
		if (point < 0 || point > 2) {
			p("select (1), 2 or 3");
			return false;
		}
		p("\nYour pointed " + (point + 1) + ". Host opens empty box. Do you want to change y/(n):");
		return true;
	}

	// change/no-change and play round - game logic here
	private void playRound(String line) {
		win = (rng.nextInt(3) == point); // box with win 33,33%
		if (line.matches("[Nn].*")) {
			p("\nNo change");
		} else {
			// when player switches then luck turns over
			p("\nYou changed to closed box");
			// your luck just turns,
			// when your first choice was win , you will lose now, other
			// when you first picked empty box and host opened another empty box, the third you switched must be WIN!
			win = !win;
		}
		p("Your " + (win ? "wan" : "loosed") + "Try again - select (1), 2 or 3");
	}

	// shortcut for console output
	static private void p(String s) {
		System.out.println(s);
	}

	// for test
	@Override
	public String toString() {
		return "CarlKullmanMontyHallComHem [winCount=" + winCount + ", roundCount=" + roundCount + ", payout=" + 1D * winCount / roundCount + "]";
	}

	// use mocked rng
	public void setRng(Random rng) {
		this.rng = rng;
	}
}
