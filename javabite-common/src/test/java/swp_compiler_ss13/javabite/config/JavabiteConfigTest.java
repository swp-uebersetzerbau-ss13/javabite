package swp_compiler_ss13.javabite.config;

import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class JavabiteConfigTest {
	JavabiteConfig config;
	
	@Before
	public void setUp() throws Exception {
		config = new JavabiteConfig();
	}

	@Test
	public void testGetConfigCategories() {
		Set<ConfigCategory> categories = config.getConfigCategories();
		
		assertEquals(3, categories.size());
		Iterator<ConfigCategory> it = categories.iterator();
		ConfigCategory cc = it.next();
		assertEquals("syntaxHighlighting", cc.getName());
		assertEquals("Syntax Highlighting", cc.toString());
		cc = it.next();
		assertEquals("compiler", cc.getName());
		assertEquals("Compiler", cc.toString());
		cc = it.next();
		assertEquals("", cc.getName());
		assertEquals("Misc", cc.toString());
	}
	
	@Test
	public void testGetConfigKeys() {
		Set<ConfigKey> keys = config.getConfigKeys("syntaxHighlighting");
		assertEquals(2, keys.size());
		Iterator<ConfigKey> it = keys.iterator();
		ConfigKey ck = it.next();
		assertEquals("syntaxHighlighting.num", ck.getName());
		assertEquals("Num", ck.toString());
		ck = it.next();
		assertEquals("syntaxHighlighting.string", ck.getName());
		assertEquals("String", ck.toString());
		
		keys = config.getConfigKeys("compiler");
		assertEquals(2, keys.size());
		it = keys.iterator();
		ck = it.next();
		assertEquals("compiler.lexer", ck.getName());
		assertEquals("Lexer", ck.toString());
		ck = it.next();
		assertEquals("compiler.parser", ck.getName());
		assertEquals("Parser", ck.toString());
		
		keys = config.getConfigKeys("");
		assertEquals(1, keys.size());
		it = keys.iterator();
		ck = it.next();
		assertEquals("other", ck.getName());
		assertEquals("Other", ck.toString());
	}
}
