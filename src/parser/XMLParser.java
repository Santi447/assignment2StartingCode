/**
 * @author Santiago, Kaley Wood, Asad, Dylan
 * Southern Alberta Institute of Technology: CPRG-304
 * Assignment 2: Creating ADTs, Implementing DS and an XML Parser
 * Created: 04.02.2026
 *
 * XMLParser -- reads an XML document, checks for malformed structure,
 * and reports each problematic line in file order.
 */
package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;

import exceptions.EmptyQueueException;
import implementations.MyArrayList;
import implementations.MyQueue;
import implementations.MyStack;
import utilities.Iterator;

public class XMLParser
{
	private static final Pattern TAG_NAME_PATTERN = Pattern.compile( "[A-Za-z_][A-Za-z0-9._:-]*" );

	/**
	 * Runs the parser from the command line.
	 *
	 * @param args the XML file path
	 */
	public static void main( String[] args )
	{
		if( args == null || args.length != 1 )
		{
			System.out.println( "Usage: java -jar Parser.jar <path-to-xml-file>" );
			return;
		}

		XMLParser parser = new XMLParser();

		try
		{
			System.out.println( parser.parse( args[0] ).buildReport() );
		}
		catch( IOException e )
		{
			System.out.println( "Unable to read XML file: " + args[0] );
			System.out.println( e.getMessage() );
		}
	}

	/**
	 * Parses an XML file from the supplied path.
	 *
	 * @param filePath the path to the XML document
	 * @return the parsing result
	 * @throws IOException if the file cannot be read
	 */
	public ParseResult parse( String filePath ) throws IOException
	{
		if( filePath == null )
		{
			throw new NullPointerException();
		}

		return parse( Paths.get( filePath ) );
	}

	/**
	 * Parses an XML file from the supplied path.
	 *
	 * @param filePath the path to the XML document
	 * @return the parsing result
	 * @throws IOException if the file cannot be read
	 */
	public ParseResult parse( Path filePath ) throws IOException
	{
		if( filePath == null )
		{
			throw new NullPointerException();
		}

		ParserState state = new ParserState();
		int lineNumber = 0;

		try( BufferedReader reader = Files.newBufferedReader( filePath, StandardCharsets.UTF_8 ) )
		{
			String line;

			while( ( line = reader.readLine() ) != null )
			{
				lineNumber++;
				processLine( line, lineNumber, state );
			}
		}

		while( !state.openTags.isEmpty() )
		{
			TagRecord unclosedTag = state.openTags.pop();
			addIssue( state.issues, unclosedTag.getLineNumber(), unclosedTag.getSourceLine(),
					"Opening tag is never closed." );
		}

		if( state.rootCount == 0 )
		{
			addIssue( state.issues, 0, "", "Document does not contain a root tag." );
		}

		ParseIssue[] sortedIssues = state.issues.toArray( new ParseIssue[state.issues.size()] );
		Arrays.sort( sortedIssues );

		return new ParseResult( filePath.toString(), sortedIssues );
	}

	private void processLine( String line, int lineNumber, ParserState state )
	{
		int index = 0;

		while( index < line.length() )
		{
			char currentCharacter = line.charAt( index );

			if( currentCharacter == '<' )
			{
				int closingIndex = findTagEnd( line, index );

				if( closingIndex == -1 )
				{
					addIssue( state.issues, lineNumber, line, "Tag is missing a closing bracket." );
					return;
				}

				String tagText = line.substring( index, closingIndex + 1 );
				TagToken token = buildToken( tagText, lineNumber, line, state.issues );

				if( token != null )
				{
					handleToken( token, state );
				}

				index = closingIndex + 1;
			}
			else
			{
				if( currentCharacter == '>' )
				{
					addIssue( state.issues, lineNumber, line, "Unexpected closing bracket found." );
				}

				index++;
			}
		}
	}

	private int findTagEnd( String line, int startIndex )
	{
		if( line.startsWith( "<!--", startIndex ) )
		{
			int commentEnd = line.indexOf( "-->", startIndex + 4 );
			return commentEnd == -1 ? -1 : commentEnd + 2;
		}

		if( line.startsWith( "<![CDATA[", startIndex ) )
		{
			int cdataEnd = line.indexOf( "]]>", startIndex + 9 );
			return cdataEnd == -1 ? -1 : cdataEnd + 2;
		}

		if( line.startsWith( "<?", startIndex ) )
		{
			int instructionEnd = line.indexOf( "?>", startIndex + 2 );
			return instructionEnd == -1 ? -1 : instructionEnd + 1;
		}

		boolean insideSingleQuotes = false;
		boolean insideDoubleQuotes = false;

		for( int index = startIndex + 1; index < line.length(); index++ )
		{
			char currentCharacter = line.charAt( index );

			if( currentCharacter == '\'' && !insideDoubleQuotes )
			{
				insideSingleQuotes = !insideSingleQuotes;
			}
			else if( currentCharacter == '"' && !insideSingleQuotes )
			{
				insideDoubleQuotes = !insideDoubleQuotes;
			}
			else if( currentCharacter == '>' && !insideSingleQuotes && !insideDoubleQuotes )
			{
				return index;
			}
		}

		return -1;
	}

	private TagToken buildToken( String tagText, int lineNumber, String sourceLine, MyArrayList<ParseIssue> issues )
	{
		if( tagText.startsWith( "<?" ) || tagText.startsWith( "<!" ) )
		{
			return null;
		}

		if( tagText.length() < 3 )
		{
			addIssue( issues, lineNumber, sourceLine, "Tag is malformed." );
			return null;
		}

		if( tagText.startsWith( "</" ) )
		{
			String tagName = tagText.substring( 2, tagText.length() - 1 ).trim();

			if( !isValidClosingTagName( tagName ) )
			{
				addIssue( issues, lineNumber, sourceLine, "Closing tag is malformed." );
				return null;
			}

			return new TagToken( TokenType.END, tagName, lineNumber, sourceLine );
		}

		boolean selfClosing = tagText.endsWith( "/>" );
		int endTrim = selfClosing ? 2 : 1;
		String rawContent = tagText.substring( 1, tagText.length() - endTrim ).trim();

		if( rawContent.length() == 0 )
		{
			addIssue( issues, lineNumber, sourceLine, "Opening tag is malformed." );
			return null;
		}

		int nameEndIndex = findNameEnd( rawContent );
		String tagName = rawContent.substring( 0, nameEndIndex );

		if( !isValidTagName( tagName ) )
		{
			addIssue( issues, lineNumber, sourceLine, "Opening tag is malformed." );
			return null;
		}

		return new TagToken( selfClosing ? TokenType.SELF : TokenType.START, tagName, lineNumber, sourceLine );
	}

	private int findNameEnd( String content )
	{
		for( int index = 0; index < content.length(); index++ )
		{
			if( Character.isWhitespace( content.charAt( index ) ) )
			{
				return index;
			}
		}

		return content.length();
	}

	private boolean isValidClosingTagName( String tagName )
	{
		return tagName.length() > 0 && !containsWhitespace( tagName ) && isValidTagName( tagName );
	}

	private boolean containsWhitespace( String value )
	{
		for( int index = 0; index < value.length(); index++ )
		{
			if( Character.isWhitespace( value.charAt( index ) ) )
			{
				return true;
			}
		}

		return false;
	}

	private boolean isValidTagName( String tagName )
	{
		return TAG_NAME_PATTERN.matcher( tagName ).matches();
	}

	private void handleToken( TagToken token, ParserState state )
	{
		switch( token.getType() )
		{
			case START:
				handleStartTag( token, state );
				break;
			case SELF:
				handleSelfClosingTag( token, state );
				break;
			case END:
				handleEndTag( token, state );
				break;
			default:
				break;
		}
	}

	private void handleStartTag( TagToken token, ParserState state )
	{
		countRootTag( token, state );
		state.openTags.push( new TagRecord( token.getName(), token.getLineNumber(), token.getSourceLine() ) );
	}

	private void handleSelfClosingTag( TagToken token, ParserState state )
	{
		countRootTag( token, state );
	}

	private void countRootTag( TagToken token, ParserState state )
	{
		if( state.openTags.isEmpty() )
		{
			state.rootCount++;

			if( state.rootCount > 1 )
			{
				addIssue( state.issues, token.getLineNumber(), token.getSourceLine(),
						"Document has more than one root tag." );
			}
		}
	}

	private void handleEndTag( TagToken token, ParserState state )
	{
		if( isExpectedOrphanedClose( token.getName(), state.orphanedTags ) )
		{
			consumeOrphanedClose( state.orphanedTags );
			return;
		}

		if( state.openTags.isEmpty() )
		{
			addIssue( state.issues, token.getLineNumber(), token.getSourceLine(),
					"Closing tag does not have a matching opening tag." );
			return;
		}

		TagRecord currentOpenTag = state.openTags.peek();

		if( currentOpenTag.getName().equals( token.getName() ) )
		{
			state.openTags.pop();
			return;
		}

		if( stackContains( state.openTags, token.getName() ) )
		{
			addIssue( state.issues, token.getLineNumber(), token.getSourceLine(),
					"Closing tag crosses an open tag." );

			MyStack<TagRecord> displacedTags = new MyStack<>();

			while( !state.openTags.isEmpty() && !state.openTags.peek().getName().equals( token.getName() ) )
			{
				displacedTags.push( state.openTags.pop() );
			}

			if( !state.openTags.isEmpty() )
			{
				state.openTags.pop();
			}

			while( !displacedTags.isEmpty() )
			{
				TagRecord displacedTag = displacedTags.pop();
				addIssue( state.issues, displacedTag.getLineNumber(), displacedTag.getSourceLine(),
						"Opening tag is closed out of order." );
				state.orphanedTags.enqueue( displacedTag );
			}

			return;
		}

		addIssue( state.issues, token.getLineNumber(), token.getSourceLine(),
				"Closing tag does not match any opening tag." );
	}

	private boolean stackContains( MyStack<TagRecord> openTags, String tagName )
	{
		Iterator<TagRecord> iterator = openTags.iterator();

		while( iterator.hasNext() )
		{
			if( iterator.next().getName().equals( tagName ) )
			{
				return true;
			}
		}

		return false;
	}

	private boolean isExpectedOrphanedClose( String tagName, MyQueue<TagRecord> orphanedTags )
	{
		if( orphanedTags.isEmpty() )
		{
			return false;
		}

		try
		{
			return orphanedTags.peek().getName().equals( tagName );
		}
		catch( EmptyQueueException e )
		{
			return false;
		}
	}

	private void consumeOrphanedClose( MyQueue<TagRecord> orphanedTags )
	{
		try
		{
			orphanedTags.dequeue();
		}
		catch( EmptyQueueException e )
		{
			throw new IllegalStateException( e );
		}
	}

	private void addIssue( MyArrayList<ParseIssue> issues, int lineNumber, String sourceLine, String reason )
	{
		for( int index = 0; index < issues.size(); index++ )
		{
			ParseIssue existingIssue = issues.get( index );

			if( existingIssue.getLineNumber() == lineNumber )
			{
				existingIssue.addReason( reason );
				return;
			}
		}

		issues.add( new ParseIssue( lineNumber, sourceLine, reason ) );
	}

	private static class ParserState
	{
		private final MyStack<TagRecord> openTags;
		private final MyQueue<TagRecord> orphanedTags;
		private final MyArrayList<ParseIssue> issues;
		private int rootCount;

		public ParserState()
		{
			openTags = new MyStack<>();
			orphanedTags = new MyQueue<>();
			issues = new MyArrayList<>();
			rootCount = 0;
		}
	}

	private static class TagRecord
	{
		private final String name;
		private final int lineNumber;
		private final String sourceLine;

		public TagRecord( String name, int lineNumber, String sourceLine )
		{
			this.name = name;
			this.lineNumber = lineNumber;
			this.sourceLine = sourceLine;
		}

		public String getName()
		{
			return name;
		}

		public int getLineNumber()
		{
			return lineNumber;
		}

		public String getSourceLine()
		{
			return sourceLine;
		}
	}

	private static class TagToken
	{
		private final TokenType type;
		private final String name;
		private final int lineNumber;
		private final String sourceLine;

		public TagToken( TokenType type, String name, int lineNumber, String sourceLine )
		{
			this.type = type;
			this.name = name;
			this.lineNumber = lineNumber;
			this.sourceLine = sourceLine;
		}

		public TokenType getType()
		{
			return type;
		}

		public String getName()
		{
			return name;
		}

		public int getLineNumber()
		{
			return lineNumber;
		}

		public String getSourceLine()
		{
			return sourceLine;
		}
	}

	private enum TokenType
	{
		START,
		END,
		SELF
	}

	public static class ParseResult
	{
		private final String filePath;
		private final ParseIssue[] issues;

		public ParseResult( String filePath, ParseIssue[] issues )
		{
			this.filePath = filePath;
			this.issues = Arrays.copyOf( issues, issues.length );
		}

		public String getFilePath()
		{
			return filePath;
		}

		public boolean isValid()
		{
			return issues.length == 0;
		}

		public ParseIssue[] getIssues()
		{
			return Arrays.copyOf( issues, issues.length );
		}

		public String buildReport()
		{
			StringBuilder report = new StringBuilder();
			report.append( "File: " ).append( filePath ).append( System.lineSeparator() );

			if( isValid() )
			{
				report.append( "XML document is properly constructed." );
				return report.toString();
			}

			report.append( "XML document is not properly constructed." ).append( System.lineSeparator() );

			for( ParseIssue issue : issues )
			{
				if( issue.getLineNumber() > 0 )
				{
					report.append( "Line " ).append( issue.getLineNumber() ).append( ": " )
							.append( issue.getSourceLine() ).append( System.lineSeparator() );
				}
				else
				{
					report.append( "General: " ).append( issue.getReason() ).append( System.lineSeparator() );
					continue;
				}

				report.append( "Reason: " ).append( issue.getReason() ).append( System.lineSeparator() );
			}

			return report.toString().trim();
		}
	}

	public static class ParseIssue implements Comparable<ParseIssue>
	{
		private final int lineNumber;
		private final String sourceLine;
		private String reason;

		public ParseIssue( int lineNumber, String sourceLine, String reason )
		{
			this.lineNumber = lineNumber;
			this.sourceLine = sourceLine;
			this.reason = reason;
		}

		public int getLineNumber()
		{
			return lineNumber;
		}

		public String getSourceLine()
		{
			return sourceLine;
		}

		public String getReason()
		{
			return reason;
		}

		public void addReason( String newReason )
		{
			if( reason.indexOf( newReason ) == -1 )
			{
				reason = reason + "; " + newReason;
			}
		}

		@Override
		public int compareTo( ParseIssue other )
		{
			if( lineNumber != other.lineNumber )
			{
				return Integer.compare( lineNumber, other.lineNumber );
			}

			return sourceLine.compareTo( other.sourceLine );
		}
	}
}
