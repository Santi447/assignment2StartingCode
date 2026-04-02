package appDomain;

import java.io.IOException;

import parser.XMLParser;
public class AppDriver {

	public static void main(String[] args) {
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

}
