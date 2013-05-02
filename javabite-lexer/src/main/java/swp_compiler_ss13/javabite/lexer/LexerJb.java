package swp_compiler_ss13.javabite.lexer;

import java.io.ByteArrayInputStream;
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

	public LexerJb() {
		StringBuffer buffer = new StringBuffer();
		for (TokenType tokenType : TokenType.values()) {
			switch (tokenType.name()) {
			case "NUM":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"[0-9]+((E|e)-?[0-9+])?"));
				break;
			case "REAL":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"[0-9]+\\.[0-9]+ ((E|e)-?[0-9+]))?"));
				break;
			case "TRUE":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"true"));
				break;
			case "FALSE":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"false"));
				break;
			case "STRING":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"\"[^\"]*\""));
				break;
			case "ID":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"[a-zA-Z][a-zA-z0-9]*"));
				break;
			case "IF":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"if"));
				break;
			case "WHILE":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"while"));
				break;
			case "DO":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"do"));
				break;
			case "BREAK":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"break"));
				break;
			case "RETURN":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"return"));
				break;
			case "PRINT":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"print"));
				break;
			case "ASSIGNOP":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"="));
				break;
			case "AND":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"&&"));
				break;
			case "OR":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"||"));
				break;
			case "EQUALS":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"=="));
				break;
			case "NOT_EQUALS":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"!="));
				break;
			case "LESS":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"<"));
				break;
			case "ESS_OR_EQUAL":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"<="));
				break;
			case "GREATER":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						">"));
				break;
			case "GREATER_EQUAL":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						">="));
				break;
			case "PLUS":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"+"));
				break;
			case "MINUS":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"-"));
				break;
			case "TIMES":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"*"));
				break;
			case "DIVIDE":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"/"));
				break;
			case "NOT":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"!"));
				break;
			case "LEFT_PARAN":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"("));
				break;
			case "RGHT_PARAN":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						")"));
				break;
			case "LEFT_BREACKET":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"["));
				break;
			case "RIGHT_BRACKET":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"]"));
				break;
			case "LEFT_BRACE":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"{"));
				break;
			case "RIGHT_BRACE":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						"}"));
				break;
			case "SEMICOLON":
				buffer.append(String.format("|(?<%s>%s)", tokenType.name(),
						";"));
				break;


			}
			// TODO: see discussion in #9
		}

		System.out.println("Pattern: " + buffer);
		tokenPatterns = Pattern.compile(new String(buffer.substring(1)));
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
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.NUM, matcher.group(TokenType.NUM
						.name()));
			}
			if (matcher.group(TokenType.TRUE.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.TRUE, matcher.group(TokenType.TRUE
						.name()));
			}
			if (matcher.group(TokenType.FALSE.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.FALSE,
						matcher.group(TokenType.FALSE.name()));
			}
			if (matcher.group(TokenType.STRING.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.STRING,
						matcher.group(TokenType.STRING.name()));
			}
			if (matcher.group(TokenType.ID.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.ID,
						matcher.group(TokenType.ID.name()));
			}
			if (matcher.group(TokenType.IF.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.IF,
						matcher.group(TokenType.IF.name()));
			}
			if (matcher.group(TokenType.WHILE.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.WHILE,
						matcher.group(TokenType.WHILE.name()));
			}
			if (matcher.group(TokenType.DO.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.DO,
						matcher.group(TokenType.DO.name()));
			}
			if (matcher.group(TokenType.BREAK.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.BREAK,
						matcher.group(TokenType.BREAK.name()));
			}
			if (matcher.group(TokenType.RETURN.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.RETURN,
						matcher.group(TokenType.RETURN.name()));
			}
			if (matcher.group(TokenType.PRINT.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.PRINT,
						matcher.group(TokenType.PRINT.name()));
			}
			if (matcher.group(TokenType.ASSIGNOP.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.ASSIGNOP,
						matcher.group(TokenType.ASSIGNOP.name()));
			}
			if (matcher.group(TokenType.AND.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.AND,
						matcher.group(TokenType.AND.name()));
			}
			if (matcher.group(TokenType.OR.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.OR,
						matcher.group(TokenType.OR.name()));
			}
			if (matcher.group(TokenType.EQUALS.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.EQUALS,
						matcher.group(TokenType.EQUALS.name()));
			}
			if (matcher.group(TokenType.NOT_EQUALS.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.NOT_EQUALS,
						matcher.group(TokenType.NOT_EQUALS.name()));
			}
			if (matcher.group(TokenType.LESS.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.LESS,
						matcher.group(TokenType.LESS.name()));
			}
			if (matcher.group(TokenType.LESS_OR_EQUAL.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.LESS_OR_EQUAL,
						matcher.group(TokenType.LESS_OR_EQUAL.name()));
			}
			if (matcher.group(TokenType.GREATER.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.GREATER,
						matcher.group(TokenType.GREATER.name()));
			}
			if (matcher.group(TokenType.GREATER_EQUAL.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.GREATER_EQUAL,
						matcher.group(TokenType.GREATER_EQUAL.name()));
			}
			if (matcher.group(TokenType.PLUS.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.PLUS,
						matcher.group(TokenType.PLUS.name()));
			}
			if (matcher.group(TokenType.MINUS.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.MINUS,
						matcher.group(TokenType.MINUS.name()));
			}
			if (matcher.group(TokenType.TIMES.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.TIMES,
						matcher.group(TokenType.TIMES.name()));
			}
			if (matcher.group(TokenType.DIVIDE.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.DIVIDE,
						matcher.group(TokenType.DIVIDE.name()));
			}
			if (matcher.group(TokenType.NOT.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.NOT,
						matcher.group(TokenType.NOT.name()));
			}
			if (matcher.group(TokenType.LEFT_PARAN.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.LEFT_PARAN,
						matcher.group(TokenType.LEFT_PARAN.name()));
			}
			if (matcher.group(TokenType.RIGHT_PARAN.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.RIGHT_PARAN,
						matcher.group(TokenType.RIGHT_PARAN.name()));
			}
			if (matcher.group(TokenType.LEFT_BRACKET.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.LEFT_BRACKET,
						matcher.group(TokenType.LEFT_BRACKET.name()));
			}
			if (matcher.group(TokenType.RIGHT_BRACKET.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.RIGHT_BRACKET,
						matcher.group(TokenType.RIGHT_BRACKET.name()));
			}
			if (matcher.group(TokenType.LEFT_BRACE.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.LEFT_BRACE,
						matcher.group(TokenType.LEFT_BRACE.name()));
			}
			if (matcher.group(TokenType.RIGHT_BRACE.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.RIGHT_BRACE,
						matcher.group(TokenType.RIGHT_BRACE.name()));
			}
			if (matcher.group(TokenType.SEMICOLON.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.SEMICOLON,
						matcher.group(TokenType.SEMICOLON.name()));
			}
			if (matcher.group(TokenType.FALSE.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.FALSE,
						matcher.group(TokenType.FALSE.name()));
			}
			if (matcher.group(TokenType.FALSE.name()) != null) {
				System.out.println(matcher.group() + " @ " + matcher.start()
						+ ", " + matcher.end());
				return new TokenJb(TokenType.FALSE,
						matcher.group(TokenType.FALSE.name()));
			}
			
			return new TokenJb(TokenType.EOF,
						matcher.group(TokenType.EOF.name()));
			
			
		}
		return null;
	}

	public static void main(String[] args) {
		String myString = "4 false 3\ntrue55\nfalse";
		System.out.println("Input: \"" + myString + "\"");
		byte[] bytes = myString.getBytes();
		InputStream is = new ByteArrayInputStream(bytes);
		LexerJb lexer = new LexerJb();
		lexer.setSourceStream(is);
		Token t = lexer.getNextToken();
		while (t != null) {
			t = lexer.getNextToken();
		}
	}
}
