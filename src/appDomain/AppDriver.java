/**
 * @author Santiago, Asad, Dylan, Kaley
 * Southern Alberta Institute of Technology: CPRG-304-B
 * Assignment 2: Creating ADTs, Implementing DS and an XML Parser
 * Created: 03.28.2026
 *
 * AppDriver — starts the XML parser from the command line.
 * It checks for a valid file path, parses the XML file, and prints
 * the finished report or an error message if the file cannot be read.
 */
package appDomain;
import java.io.IOException;
import parser.XMLParser;

public class AppDriver {
	/**
	 * Launches the parser using the XML file path passed in from the command line.
	 * If the input is invalid or the file cannot be read, a message is printed
	 * instead of a report.
	 *
	 * @param args the command-line arguments, expected to contain one XML file path
	 */
	public static void main(String[] args) {
		// make sure exactly one XML file path was provided
		if( args == null || args.length != 1 )
		{
			System.out.println( "Usage: java -jar Parser.jar <path-to-xml-file>" );
			return;
		}
		// build the parser once the input looks valid
		XMLParser parser = new XMLParser();

		try
		{
			// parse the XML file and print the completed report
			System.out.println( parser.parse( args[0] ).buildReport() );
		}
		catch( IOException e )
		{
			// if the file can't be opened or read, show a readable error instead
			System.out.println( "Unable to read XML file: " + args[0] );
			System.out.println( e.getMessage() );
		}
	}

}
