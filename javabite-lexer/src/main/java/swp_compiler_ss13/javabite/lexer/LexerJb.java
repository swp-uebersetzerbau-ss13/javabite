package swp_compiler_ss13.javabite.lexer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import swp_compiler_ss13.common.lexer.Lexer;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.token.BoolTokenJb;
import swp_compiler_ss13.javabite.token.NumTokenJb;
import swp_compiler_ss13.javabite.token.RealTokenJb;
import swp_compiler_ss13.javabite.token.TokenJb;

/**
 * structured along http://www.giocc.com/writing-a-lexer-in-java-1-7-using-regex-named-capturing-groups.html
 */
public class LexerJb implements Lexer {
	Pattern tokenPatterns;
	Queue<Token> tokenQueue;
	
	public LexerJb() {
		// create |-pattern for all token patterns
		StringBuilder patternBuilder = new StringBuilder();
		for (JavabiteTokenType type: JavabiteTokenType.values()) {
			if (type.getRegex() == null)
				continue;
			patternBuilder.append(String.format("|(?<%s>%s)", type.name(), type.getRegex()));
		}
		tokenPatterns = Pattern.compile(patternBuilder.substring(1));
	}
	
	@Override
	public void setSourceStream(InputStream stream) {
		if (stream == null)
			return;
		
		//convert to string for java pattern/matcher class
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(stream, writer, "UTF-8");
		} catch (IOException e) {}
		
		// we already have the complete input in memory -> direct lexing
		lex(writer.getBuffer());
	}

	@Override
	public Token getNextToken() {
		if (tokenQueue == null || tokenQueue.isEmpty())
			return new TokenJb(TokenType.EOF, "");
		
		return tokenQueue.poll();
	}
	
	private void lex(CharSequence input) {
		tokenQueue = new ArrayDeque<>(1000);
		Matcher matcher = tokenPatterns.matcher(input);
		
		int line = 1, column = 1, notATokenStart = 0, notATokenLine = 0;
		StringBuilder notAToken = new StringBuilder();
		// loop input
		while (matcher.find()) {
			for (JavabiteTokenType type:JavabiteTokenType.values()) {
				if (type.getRegex() == null || matcher.group(type.name()) == null) {
					continue;
				}
				
				TokenType tt = type.getTokenType();
				String lexem = matcher.group(type.name());
				Token token = null;
				
				if (type == JavabiteTokenType.LINEBREAK) {
					line++;	column = 1; continue;
				} else if (type == JavabiteTokenType.NOTATOKEN) {
					if (notAToken.length() == 0) {
						notATokenStart = column; notATokenLine = line;
					}
					
					notAToken.append(lexem); column++; continue;
				} else if (type == JavabiteTokenType.WHITESPACE) {
				} else if (type == JavabiteTokenType.NUM) {
					token = new NumTokenJb(tt, lexem, line, column);
				} else if (type == JavabiteTokenType.REAL) {
					token = new RealTokenJb(tt, lexem, line, column);
				} else if (type == JavabiteTokenType.TRUE || type == JavabiteTokenType.FALSE) {
					token = new BoolTokenJb(tt, lexem, line, column);
				} else {
					if (tt != null)
						token = new TokenJb(tt, lexem, line, column);
				}
				
				column += lexem.length();
				
				if (token != null) {
					// add a NOT_A_TOKEN-token if necessary
					if (notAToken.length() != 0) {
						tokenQueue.add(new TokenJb(TokenType.NOT_A_TOKEN,notAToken.toString(),notATokenLine,notATokenStart));
						notAToken = new StringBuilder();
					}
					// add the recognized token
					tokenQueue.add(token);
				}
			}
        }
		// add a NOT_A_TOKEN-token if necessary
		if (notAToken.length() != 0) {
			tokenQueue.add(new TokenJb(TokenType.NOT_A_TOKEN,notAToken.toString(),notATokenLine,notATokenStart));
		}
	}
}
