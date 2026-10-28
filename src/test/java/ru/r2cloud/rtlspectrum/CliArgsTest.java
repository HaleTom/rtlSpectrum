package ru.r2cloud.rtlspectrum;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CliArgsTest {

	@Test
	public void testNoArgs() {
		CliArgs.Parsed parsed = CliArgs.parse(new String[0]);
		assertFalse(parsed.hasError());
		assertFalse(parsed.isHelpRequested());
		assertNull(parsed.getBaseFile());
		assertTrue(parsed.getOperations().isEmpty());
	}

	@Test
	public void testSingleFileIsBase() {
		CliArgs.Parsed parsed = CliArgs.parse(new String[] { "a.csv" });
		assertFalse(parsed.hasError());
		assertNotNull(parsed.getBaseFile());
		assertEquals("a.csv", parsed.getBaseFile().getPath());
		assertTrue(parsed.getOperations().isEmpty());
	}

	@Test
	public void testMoreThanOneNakedArgIsError() {
		CliArgs.Parsed parsed = CliArgs.parse(new String[] { "a.csv", "b.csv" });
		assertTrue(parsed.hasError());
	}

	@Test
	public void testAddRequiresBaseFile() {
		CliArgs.Parsed parsed = CliArgs.parse(new String[] { "--add", "b.csv" });
		assertTrue(parsed.hasError());
	}

	@Test
	public void testSubtractRequiresBaseFile() {
		CliArgs.Parsed parsed = CliArgs.parse(new String[] { "--subtract", "b.csv" });
		assertTrue(parsed.hasError());
	}

	@Test
	public void testAddWithoutFileIsError() {
		CliArgs.Parsed parsed = CliArgs.parse(new String[] { "a.csv", "--add" });
		assertTrue(parsed.hasError());
	}

	@Test
	public void testSubtractWithoutFileIsError() {
		CliArgs.Parsed parsed = CliArgs.parse(new String[] { "a.csv", "--subtract" });
		assertTrue(parsed.hasError());
	}

	@Test
	public void testBasePlusAddInOrder() {
		CliArgs.Parsed parsed = CliArgs.parse(new String[] { "base.csv", "--add", "a.csv", "--subtract", "s.csv", "--add", "b.csv" });
		assertFalse(parsed.hasError());
		assertEquals("base.csv", parsed.getBaseFile().getPath());
		assertEquals(3, parsed.getOperations().size());
		assertEquals(CliArgs.OpType.ADD, parsed.getOperations().get(0).getType());
		assertEquals("a.csv", parsed.getOperations().get(0).getFile().getPath());
		assertEquals(CliArgs.OpType.SUBTRACT, parsed.getOperations().get(1).getType());
		assertEquals("s.csv", parsed.getOperations().get(1).getFile().getPath());
		assertEquals(CliArgs.OpType.ADD, parsed.getOperations().get(2).getType());
		assertEquals("b.csv", parsed.getOperations().get(2).getFile().getPath());
	}

	@Test
	public void testUnknownOptionIsError() {
		CliArgs.Parsed parsed = CliArgs.parse(new String[] { "a.csv", "--bogus" });
		assertTrue(parsed.hasError());
	}

	@Test
	public void testHelpShortCircuits() {
		CliArgs.Parsed parsed = CliArgs.parse(new String[] { "--help" });
		assertTrue(parsed.isHelpRequested());
		assertFalse(parsed.hasError());
	}

	@Test
	public void testHelpShortFlag() {
		CliArgs.Parsed parsed = CliArgs.parse(new String[] { "-h" });
		assertTrue(parsed.isHelpRequested());
	}

	@Test
	public void testJarFlagIsRejectedAsUnknownOption() {
		// --jar is a launcher-script flag; the app must not accept it
		CliArgs.Parsed parsed = CliArgs.parse(new String[] { "--jar", "x.jar", "a.csv" });
		assertTrue(parsed.hasError());
	}

	@Test
	public void testHelpShortCircuitsOverFileArgs() {
		CliArgs.Parsed parsed = CliArgs.parse(new String[] { "a.csv", "--help" });
		assertTrue(parsed.isHelpRequested());
		assertFalse(parsed.hasError());
	}

	@Test
	public void testMissingBaseErrorMentionsUsage() {
		CliArgs.Parsed parsed = CliArgs.parse(new String[] { "--add", "b.csv" });
		assertTrue(parsed.hasError());
		assertTrue(parsed.getError().contains("base file"));
	}

	@Test
	public void testFlagLikeValueAfterAddIsRejected() {
		CliArgs.Parsed parsed = CliArgs.parse(new String[] { "a.csv", "--add", "--subtract" });
		assertTrue(parsed.hasError());
	}

	@Test
	public void testUsageMentionsCommands() {
		String usage = CliArgs.usage();
		assertTrue(usage.contains("--add"));
		assertTrue(usage.contains("--subtract"));
		assertTrue(usage.contains("--help"));
	}
}
