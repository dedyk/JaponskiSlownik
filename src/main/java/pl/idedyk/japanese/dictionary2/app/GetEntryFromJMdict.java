package pl.idedyk.japanese.dictionary2.app;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class GetEntryFromJMdict {

	public static void main(String[] args) throws Exception {
		
		// parser lini polecen
		CommandLineParser commandLineParser = new DefaultParser();
		
		// opcje
		Options options = new Options();
		
		options.addOption("e", "entry-ids", true, "Entry ids");
		options.addOption("gewaeipd", "get-entry-which-also-exists-in-polish-dictionary", false, "Get entry which also exists in polish dictionary");
		options.addOption("h", "help", false, "Help");
		
		// parsowanie opcji		
		CommandLine commandLine = null;
		
		try {
			commandLine = commandLineParser.parse(options, args);
			
		} catch (Exception e) {
			
			System.out.println(e.getMessage() + "\n");
			
			printHelp(options);
			
			System.exit(1);
		}
		
		if (commandLine.hasOption("help") == true) {

			printHelp(options);
			
			System.exit(1);
		}
		
		//
		
		Set<Integer> entryIds = new HashSet<>();
		
		boolean getEntryWhichAlsoExistsInPolishDictionary = false;
		
		//
		
		if (commandLine.hasOption("entry-ids") == true) {
			
			String entryIdsValue = commandLine.getOptionValue("entry-ids");
			
			String[] entryIdsValueSplited = entryIdsValue.split(",");
			
			for (String currentEntryId : entryIdsValueSplited) {
				entryIds.add(new Integer(currentEntryId));
			}
			
		} else {
			
			printHelp(options);
			
			System.exit(1);			
		}
		
		if (commandLine.hasOption("get-entry-which-also-exists-in-polish-dictionary") == true) {
			getEntryWhichAlsoExistsInPolishDictionary = true;			
		}
		
		


		int fixme = 1;
		// 1152890,1343800,2098030,2373430,2827753,1851980
	}
	
	private static void printHelp(Options options) {
		
		HelpFormatter formatter = new HelpFormatter();
		
		formatter.printHelp(GenerateMissingWordListApp.class.getSimpleName(), options);
	}
}
