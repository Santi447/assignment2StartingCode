
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
/**
 * @author Santiago, Kaley Wood, Asad, Dylan
 * Southern Alberta Institute of Technology: CPRG-304
 * Assignment 2: Creating ADTs, Implementing DS and an XML Parser
 * Created: 04.02.2026
 *
 * XMLParser -- reads an XML document, checks for malformed structure,
 * and reports each problematic line in file order.
 */
public class XMLParser
{
	private static final Pattern TAG_NAME_PATTERN = Pattern.compile( "[A-Za-z_][A-Za-z0-9._:-]*" );
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
			// process the file one line at a time so issues can be tied to line numbers
			while( ( line = reader.readLine() ) != null )
			{
				lineNumber++;
				processLine( line, lineNumber, state );
			}
		}
		// any tags left on the stack were opened but never closed
		while( !state.openTags.isEmpty() )
		{
			TagRecord unclosedTag = state.openTags.pop();
			addIssue( state.issues, unclosedTag.getLineNumber(), unclosedTag.getSourceLine(),
					"Opening tag is never closed." );
		}
		// a well-formed XML document must have exactly one root tag
		if( state.rootCount == 0 )
		{
			addIssue( state.issues, 0, "", "Document does not contain a root tag." );
		}
		// sort issues into file order before building the final result
		ParseIssue[] sortedIssues = state.issues.toArray( new ParseIssue[state.issues.size()] );
		Arrays.sort( sortedIssues );

		return new ParseResult( filePath.toString(), sortedIssues );
	}
	/**
	 * Scans one line of text and breaks it into tags or invalid characters
	 * that need to be reported as issues.
	 *
	 * @param line       the source line being examined
	 * @param lineNumber the current line number in the file
	 * @param state      the running parser state
	 */
	private void processLine( String line, int lineNumber, ParserState state )
	{
		int index = 0;
	// move left to right through the current line
		while( index < line.length() )
		{
			char currentCharacter = line.charAt( index );

			if( currentCharacter == '<' )
			{
				// try to find the matching closing bracket for this tag
				int closingIndex = findTagEnd( line, index );

				if( closingIndex == -1 )
				{
					addIssue( state.issues, lineNumber, line, "Tag is missing a closing bracket." );
					return;
				}

				String tagText = line.substring( index, closingIndex + 1 );
				TagToken token = buildToken( tagText, lineNumber, line, state.issues );
				// only handle the token if it was valid and not ignored
				if( token != null )
				{
					handleToken( token, state );
				}
				// skip past the entire tag we just processed
				index = closingIndex + 1;
			}
			else
			{
				// a standalone '>' outside of a tag is malformed input
				if( currentCharacter == '>' )
				{
					addIssue( state.issues, lineNumber, line, "Unexpected closing bracket found." );
				}

				index++;
			}
		}
	}
	/**
	 * Finds the closing bracket for a tag, while respecting comments, CDATA,
	 * processing instructions, and quoted attribute values.
	 *
	 * @param line       the line containing the tag
	 * @param startIndex the index where the opening '<' was found
	 * @return the index of the matching '>', or -1 if none exists
	 */
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
	/**
	 * Converts raw tag text into a token the parser can work with.
	 * Invalid tags are rejected and recorded as issues.
	 *
	 * @param tagText    the raw tag text including angle brackets
	 * @param lineNumber the line where the tag was found
	 * @param sourceLine the full source line
	 * @param issues     the issue list to append to when malformed tags appear
	 * @return a start, end, or self-closing token, or null if ignored/invalid
	 */
	private TagToken buildToken( String tagText, int lineNumber, String sourceLine, MyArrayList<ParseIssue> issues )
	{
		// declarations, comments, and other special markup are skipped
		if( tagText.startsWith( "<?" ) || tagText.startsWith( "<!" ) )
		{
			return null;
		}

		if( tagText.length() < 3 )
		{
			addIssue( issues, lineNumber, sourceLine, "Tag is malformed." );
			return null;
		}
		// handle closing tags like </book>
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
		// handle closing tags like </book>
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
	/**
	 * Finds where the tag name ends and the rest of the tag content begins.
	 *
	 * @param content the inside of an opening tag
	 * @return the ending index of the tag name
	 */
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
	/**
	 * Checks whether a closing tag name is valid.
	 *
	 * @param tagName the closing tag name to validate
	 * @return true if the name is legal for a closing tag
	 */
	private boolean isValidClosingTagName( String tagName )
	{
		return tagName.length() > 0 && !containsWhitespace( tagName ) && isValidTagName( tagName );
	}
	/**
	 * Returns true if the given string contains at least one whitespace
	 * character.
	 *
	 * @param value the string to scan
	 * @return true if whitespace is present
	 */
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
	/**
	 * Checks whether a tag name matches the allowed pattern.
	 *
	 * @param tagName the tag name to validate
	 * @return true if the name matches the accepted format
	 */
	private boolean isValidTagName( String tagName )
	{
		return TAG_NAME_PATTERN.matcher( tagName ).matches();
	}
	/**
	 * Sends a parsed token to the correct handler based on its type.
	 *
	 * @param token the token to process
	 * @param state the running parser state
	 */
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
	/**
	 * Processes a normal opening tag by counting it as a root candidate
	 * when appropriate and pushing it onto the open-tag stack.
	 *
	 * @param token the opening-tag token
	 * @param state the running parser state
	 */
	private void handleStartTag( TagToken token, ParserState state )
	{
		countRootTag( token, state );
		state.openTags.push( new TagRecord( token.getName(), token.getLineNumber(), token.getSourceLine() ) );
	}
	/**
	 * Processes a self-closing tag. It can still count as a root tag,
	 * but it does not stay on the open-tag stack.
	 *
	 * @param token the self-closing-tag token
	 * @param state the running parser state
	 */
	private void handleSelfClosingTag( TagToken token, ParserState state )
	{
		countRootTag( token, state );
	}

	/**
	 * Tracks root-tag count. A new root is only counted when there are no
	 * currently open parent tags.
	 *
	 * @param token the token being checked as a possible root
	 * @param state the running parser state
	 */
	private void countRootTag( TagToken token, ParserState state )
	{
		if( state.openTags.isEmpty() )
		{
			state.rootCount++;
			// more than one top-level tag means multiple roots
			if( state.rootCount > 1 )
			{
				addIssue( state.issues, token.getLineNumber(), token.getSourceLine(),
						"Document has more than one root tag." );
			}
		}
	}

	/**
	 * Processes a closing tag, checking whether it matches the most recent
	 * opening tag, crosses other tags, or has no valid match at all.
	 *
	 * @param token the closing-tag token
	 * @param state the running parser state
	 */
	private void handleEndTag( TagToken token, ParserState state )
	{
		// allow a previously displaced tag to be closed later without double-reporting it
		if( isExpectedOrphanedClose( token.getName(), state.orphanedTags ) )
		{
			consumeOrphanedClose( state.orphanedTags );
			return;
		}
		// no opening tags are available to match against
		if( state.openTags.isEmpty() )
		{
			addIssue( state.issues, token.getLineNumber(), token.getSourceLine(),
					"Closing tag does not have a matching opening tag." );
			return;
		}

		TagRecord currentOpenTag = state.openTags.peek();
		// normal case: the closing tag matches the most recent opening tag
		if( currentOpenTag.getName().equals( token.getName() ) )
		{
			state.openTags.pop();
			return;
		}
		// if the tag exists deeper in the stack, then the tags crossed each other
		if( stackContains( state.openTags, token.getName() ) )
		{
			addIssue( state.issues, token.getLineNumber(), token.getSourceLine(),
					"Closing tag crosses an open tag." );

			MyStack<TagRecord> displacedTags = new MyStack<>();
			// pop tags until the matching one is reached
			while( !state.openTags.isEmpty() && !state.openTags.peek().getName().equals( token.getName() ) )
			{
				displacedTags.push( state.openTags.pop() );
			}
			// remove the matching tag once found
			if( !state.openTags.isEmpty() )
			{
				state.openTags.pop();
			}
			// the displaced tags are now expected to appear later as orphaned closes
			while( !displacedTags.isEmpty() )
			{
				TagRecord displacedTag = displacedTags.pop();
				addIssue( state.issues, displacedTag.getLineNumber(), displacedTag.getSourceLine(),
						"Opening tag is closed out of order." );
				state.orphanedTags.enqueue( displacedTag );
			}

			return;
		}

		// the closing tag did not match anything currently open
		addIssue( state.issues, token.getLineNumber(), token.getSourceLine(),
				"Closing tag does not match any opening tag." );
	}

	/**
	 * Scans the open-tag stack to see whether a given tag name exists
	 * somewhere below the top.
	 *
	 * @param openTags the stack of currently open tags
	 * @param tagName  the name being searched for
	 * @return true if the tag exists in the stack
	 */
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
	/**
	 * Checks whether the next expected orphaned close matches the tag name
	 * currently being processed.
	 *
	 * @param tagName      the closing tag name being checked
	 * @param orphanedTags the queue of orphaned tags waiting to be consumed
	 * @return true if this close matches the next expected orphaned tag
	 */
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
	/**
	 * Removes the next expected orphaned close from the queue.
	 *
	 * @param orphanedTags the queue of orphaned tags
	 */
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
	/**
	 * Adds a parsing issue for a line, or appends the reason to an existing
	 * issue already recorded for that same line.
	 *
	 * @param issues     the issue list being built
	 * @param lineNumber the line number tied to the problem
	 * @param sourceLine the original source line
	 * @param reason     the explanation to record
	 */
	private void addIssue( MyArrayList<ParseIssue> issues, int lineNumber, String sourceLine, String reason )
	{
		for( int index = 0; index < issues.size(); index++ )
		{
			ParseIssue existingIssue = issues.get( index );
			// group multiple reasons onto the same line instead of duplicating entries
			if( existingIssue.getLineNumber() == lineNumber )
			{
				existingIssue.addReason( reason );
				return;
			}
		}

		issues.add( new ParseIssue( lineNumber, sourceLine, reason ) );
	}
	/**
	 * ParserState -- keeps the parser's working data together while the file
	 * is being scanned.
	 */
	private static class ParserState
	{
		private final MyStack<TagRecord> openTags;
		private final MyQueue<TagRecord> orphanedTags;
		private final MyArrayList<ParseIssue> issues;
		private int rootCount;
		
		/**
		 * Builds a fresh parser state with empty storage structures.
		 */
		public ParserState()
		{
			openTags = new MyStack<>();
			orphanedTags = new MyQueue<>();
			issues = new MyArrayList<>();
			rootCount = 0;
		}
	}
		/**
	 * TagRecord -- stores an opening tag along with the line information
	 * needed for later error reporting.
	 */
	private static class TagRecord
	{
		private final String name;
		private final int lineNumber;
		private final String sourceLine;
		/**
		 * Builds a stored record for an opening tag.
		 *
		 * @param name       the tag name
		 * @param lineNumber the line where the tag appeared
		 * @param sourceLine the full line text
		 */
		public TagRecord( String name, int lineNumber, String sourceLine )
		{
			this.name = name;
			this.lineNumber = lineNumber;
			this.sourceLine = sourceLine;
		}
		/**
		 * Returns the tag name stored in this record.
		 *
		 * @return the tag name
		 */
		public String getName()
		{
			return name;
		}
		/**
		 * Returns the source line number for this record.
		 *
		 * @return the line number
		 */
		public int getLineNumber()
		{
			return lineNumber;
		}
		/**
		 * Returns the original source line tied to this tag.
		 *
		 * @return the source line text
		 */
		public String getSourceLine()
		{
			return sourceLine;
		}
	}
	/**
	 * TagToken -- represents a parsed tag ready to be handled by the parser.
	 */
	private static class TagToken
	{
		private final TokenType type;
		private final String name;
		private final int lineNumber;
		private final String sourceLine;

		/**
		 * Builds a token from parsed tag data.
		 *
		 * @param type       the token type
		 * @param name       the tag name
		 * @param lineNumber the source line number
		 * @param sourceLine the original source line
		 */
		public TagToken( TokenType type, String name, int lineNumber, String sourceLine )
		{
			this.type = type;
			this.name = name;
			this.lineNumber = lineNumber;
			this.sourceLine = sourceLine;
		}
		/**
		 * Returns the token type.
		 *
		 * @return the token type
		 */
		public TokenType getType()
		{
			return type;
		}
		/**
		 * Returns the tag name stored in this token.
		 *
		 * @return the tag name
		 */
		public String getName()
		{
			return name;
		}
		/**
		 * Returns the source line number where this token came from.
		 *
		 * @return the line number
		 */
		public int getLineNumber()
		{
			return lineNumber;
		}
		/**
		 * Returns the original source line for this token.
		 *
		 * @return the source line text
		 */
		public String getSourceLine()
		{
			return sourceLine;
		}
	}
	/**
	 * TokenType -- identifies whether a parsed tag opens, closes,
	 * or closes itself.
	 */
	private enum TokenType
	{
		START,
		END,
		SELF
	}
	/**
	 * ParseResult -- packages the final outcome of parsing, including the
	 * file path and any issues that were found.
	 */
	public static class ParseResult
	{
		private final String filePath;
		private final ParseIssue[] issues;

		/**
		 * Builds a parse result from the file path and issue list.
		 *
		 * @param filePath the parsed file path
		 * @param issues   the issues found during parsing
		 */
		public ParseResult( String filePath, ParseIssue[] issues )
		{
			this.filePath = filePath;
			this.issues = Arrays.copyOf( issues, issues.length );
		}
		/**
		 * Returns the file path tied to this result.
		 *
		 * @return the parsed file path
		 */
		public String getFilePath()
		{
			return filePath;
		}
		/**
		 * Returns true when no parsing issues were found.
		 *
		 * @return true if the document is valid
		 */
		public boolean isValid()
		{
			return issues.length == 0;
		}
		/**
		 * Returns a copy of the issues found during parsing.
		 *
		 * @return the issue array
		 */
		public ParseIssue[] getIssues()
		{
			return Arrays.copyOf( issues, issues.length );
		}
		/**
		 * Builds a printable report describing whether the document is valid
		 * and listing each issue in file order when it is not.
		 *
		 * @return the formatted parse report
		 */
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
	/**
	 * ParseIssue -- stores one parsing problem, including its line number,
	 * original line text, and explanation.
	 */
	public static class ParseIssue implements Comparable<ParseIssue>
	{
		private final int lineNumber;
		private final String sourceLine;
		private String reason;
		/**
		 * Builds a parse issue for one detected problem.
		 *
		 * @param lineNumber the line number where the issue occurred
		 * @param sourceLine the original source line
		 * @param reason     the explanation for the issue
		 */
		public ParseIssue( int lineNumber, String sourceLine, String reason )
		{
			this.lineNumber = lineNumber;
			this.sourceLine = sourceLine;
			this.reason = reason;
		}

		/**
		 * Returns the line number for this issue.
		 *
		 * @return the line number
		 */
		public int getLineNumber()
		{
			return lineNumber;
		}

		/**
		 * Returns the original source line tied to this issue.
		 *
		 * @return the source line text
		 */
		public String getSourceLine()
		{
			return sourceLine;
		}
		/**
		 * Returns the recorded reason for this issue.
		 *
		 * @return the issue reason
		 */
		public String getReason()
		{
			return reason;
		}

		/**
		 * Appends another reason to this issue if it is not already present.
		 *
		 * @param newReason the additional reason to add
		 */
		public void addReason( String newReason )
		{
			if( reason.indexOf( newReason ) == -1 )
			{
				reason = reason + "; " + newReason;
			}
		}
		/**
		 * Compares issues so they can be sorted into file order.
		 *
		 * @param other the other issue to compare against
		 * @return a negative, zero, or positive value based on sort order
		 */
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
