import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.Test;
import org.mockito.Mockito;

public class UnitTest {

	// OBS! test random play manipulates system.out and as usual, JUnit make mess when static out is used multithreaded in execution with active log

	@Test
	public void testStayAndLose() throws IOException {
		CarlKullmanMontyHallComHem monty = new CarlKullmanMontyHallComHem();
		Random mockRandom = Mockito.mock(Random.class);
		when(mockRandom.nextInt(Mockito.anyInt())).thenReturn(0);
		monty.setRng(mockRandom);
		monty.play(Stream.of("2", "n")); // default first input (1)
		System.out.println(monty);
		assertEquals(1, monty.roundCount);
		assertEquals(0, monty.winCount);
	}

	@Test
	public void testStayAndWin() throws IOException {
		CarlKullmanMontyHallComHem monty = new CarlKullmanMontyHallComHem();
		Random mockRandom = Mockito.mock(Random.class);
		when(mockRandom.nextInt(Mockito.anyInt())).thenReturn(1);
		monty.setRng(mockRandom);
		monty.play(Stream.of("2", "n")); // default first input (1)
		System.out.println(monty);
		assertEquals(1, monty.roundCount);
		assertEquals(1, monty.winCount);
	}

	@Test
	public void testChangeAndWin() throws IOException {
		CarlKullmanMontyHallComHem monty = new CarlKullmanMontyHallComHem();
		Random mockRandom = Mockito.mock(Random.class);
		when(mockRandom.nextInt(Mockito.anyInt())).thenReturn(0);
		monty.setRng(mockRandom);
		monty.play(Stream.of("2", "y")); // default first input (1)
		System.out.println(monty);
		assertEquals(1, monty.roundCount);
		assertEquals(1, monty.winCount);
	}

	@Test
	public void testChangeAndLose() throws IOException {
		CarlKullmanMontyHallComHem monty = new CarlKullmanMontyHallComHem();
		Random mockRandom = Mockito.mock(Random.class);
		when(mockRandom.nextInt(Mockito.anyInt())).thenReturn(1);
		monty.setRng(mockRandom);
		monty.play(Stream.of("2", "y")); // default first input (1)
		System.out.println(monty);
		assertEquals(1, monty.roundCount);
		assertEquals(0, monty.winCount);
	}

	@Test
	public void testBadInput() throws IOException {
		CarlKullmanMontyHallComHem monty = new CarlKullmanMontyHallComHem();
		monty.play(Stream.of("5")); // wrong input
		assertEquals(0, monty.roundCount);
		assertFalse(monty.gameStarted);
		monty.play(Stream.of("A")); // wrong input
		assertEquals(0, monty.roundCount);
		assertFalse(monty.gameStarted);
	}

	@Test
	public void testDefaultInput() throws IOException {
		CarlKullmanMontyHallComHem monty = new CarlKullmanMontyHallComHem();
		monty.play(Stream.of("")); // default first input (0)
		assertEquals(0, monty.roundCount);
		assertTrue(monty.gameStarted);
		monty.play(Stream.of("")); // default final input (Y)
		assertEquals(1, monty.roundCount);
		assertFalse(monty.gameStarted);
	}

	// test random play with optimal strategy
	@Test
	public void test1000RoundSwitchAlways() throws IOException {
		CarlKullmanMontyHallComHem monty = new CarlKullmanMontyHallComHem();
		int count = 1000;
		// monty.setVerbose(false);
		PrintStream _out = System.out;
		System.setOut(DUMMY_OUTPUT);
		for (int i = 0; i < count; i++) {
			monty.play(Stream.of("1", "y")); // default first input (0)
		}
		assertEquals(count, monty.roundCount);
		System.setOut(_out);
		System.out.println("payout waited 66%  " + monty);
		assertTrue(0.05 > Math.abs(1D * monty.winCount / count - 2D / 3)); // 66% // +-5% confidence
	}

	// test random play with bad strategy
	@Test
	public void test1000RoundSwitchNever() throws IOException {
		CarlKullmanMontyHallComHem monty = new CarlKullmanMontyHallComHem();
		int count = 1000;
		PrintStream _out = System.out;
		System.setOut(DUMMY_OUTPUT);
		for (int i = 0; i < count; i++) {
			monty.play(Stream.of("1", "n")); // default first input (0)
		}
		System.setOut(_out);
		assertEquals(count, monty.roundCount);
		System.out.println("payout waited 33%  " + monty);
		assertTrue(0.03 > Math.abs(1D * monty.winCount / count - 1D / 3)); // 33% +-5% confidence
	}

	// test random play with RandomStrategy
	@Test
	public void test1000RoundRandomStrategy() throws IOException {
		CarlKullmanMontyHallComHem monty = new CarlKullmanMontyHallComHem();
		int count = 1000;
		PrintStream _out = System.out;
		System.setOut(DUMMY_OUTPUT);
		for (int i = 0; i < count; i++) {
			monty.play(Stream.of("1", (i % 2 == 0) ? "n" : "y")); // default first input (0)
		}
		System.setOut(_out);
		assertEquals(count, monty.roundCount);
		System.out.println("payout waited 50%  " + monty);
		assertTrue(0.03 > Math.abs(1D * monty.winCount / count - 1D / 2)); // 50% +-5% confidence
	}

	// for silent test of 1000+ rounds
	private static PrintStream DUMMY_OUTPUT = new PrintStream(new ByteArrayOutputStream() {
		public void write(int b) {
			// NO-OP
		}
	});
}
