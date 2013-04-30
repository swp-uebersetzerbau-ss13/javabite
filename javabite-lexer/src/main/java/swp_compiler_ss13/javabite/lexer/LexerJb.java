package swp_compiler_ss13.javabite.lexer;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import swp_compiler_ss13.common.lexer.Lexer;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.token.TokenJb;

public class LexerJb implements Lexer {
	
	Pattern tokenPatterns;
	Matcher matcher;
	
	LexerJb(InputStream stream) {
		StringBuffer buffer = new StringBuffer();
		for (TokenType tokenType : TokenType.values()) {
			buffer.append(String.format("|(?<%s>%s)", tokenType.name(), ""));
			// TODO: second parameter would still tokenType.regExpr, discussion in #9
		}
		
		tokenPatterns = Pattern.compile(new String(buffer.substring(1)));
		setSourceStream(stream);
	}
	
	@Override
	public void setSourceStream(InputStream stream) {
		if (stream != null) {
			// extract string from stream
			Scanner scanner = new Scanner(stream, "UTF-8");
			String sourcecode = scanner.useDelimiter("\\A").next();
			scanner.close();
			
			// matcher should work on the new string
			matcher = tokenPatterns.matcher(sourcecode);
		}
	}

	@Override
	public Token getNextToken() {
		if (matcher.find()) {
			if (matcher.group(TokenType.NUM.name()) != null) {
				return new TokenJb(TokenType.NUM, matcher.group(TokenType.NUM.name()));
			}
		}
		return null;
	}
}
