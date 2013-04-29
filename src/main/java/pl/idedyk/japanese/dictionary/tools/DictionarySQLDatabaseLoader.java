package pl.idedyk.japanese.dictionary.tools;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;
import pl.idedyk.japanese.dictionary.exception.JapaneseDictionaryException;

/*

create table words (
	id int not null auto_increment, primary key(id),
	dictionary_entry_type varchar(30) not null, index(dictionary_entry_type),
	word_type varchar(20) not null, index(word_type),
	prefix varchar(10) null,
	kanji varchar(30) null, index(kanji),
	kana_list varchar(100) not null,
	prefix_romaji varchar(10) null,
	romaji_list varchar(100) not null,
	polish_translate_list varchar(100) not null,
	info varchar(100) null,
	use_entry varchar(10) null
) default character set = utf8 collate = utf8_polish_ci;

 */

public class DictionarySQLDatabaseLoader {
	
	public static void main(String[] args) throws IOException, JapaneseDictionaryException {
		
		
		final String mysqlAddress = "jdbc:mysql://localhost/japdb?characterEncoding=utf8&user=japdbuser&password=japdbpasswd";
		
		//loadDictionaryIntoDB(mysqlAddress, "words", "input/word.csv");
		loadDictionaryIntoDB(mysqlAddress, "words", "output/word.csv");
	}

	private static void loadDictionaryIntoDB(String mysqlAddress, String tableName, String inputFileName) throws IOException, JapaneseDictionaryException {
		
		final String insertStatementSql = "insert into " + tableName + " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(inputFileName);
		
		Connection dbConnection = null;
		PreparedStatement insertStatement = null;
		
		try {
			
			dbConnection = DriverManager.getConnection(mysqlAddress);
		
			for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
				
				insertStatement = dbConnection.prepareStatement(insertStatementSql);
				
				insertStatement.setInt(1, polishJapaneseEntry.getId());
				insertStatement.setString(3, polishJapaneseEntry.getDictionaryEntryType().toString());
				insertStatement.setString(4, polishJapaneseEntry.getWordType().toString());
				insertStatement.setString(5, polishJapaneseEntry.getPrefixKana());
				insertStatement.setString(6, polishJapaneseEntry.getKanji());
				insertStatement.setString(7, convertListToString(polishJapaneseEntry.getKanaList()));
				insertStatement.setString(8, polishJapaneseEntry.getPrefixRomaji());
				insertStatement.setString(9, convertListToString(polishJapaneseEntry.getRomajiList()));
				insertStatement.setString(10, convertListToString(polishJapaneseEntry.getPolishTranslates()));
				insertStatement.setString(11, polishJapaneseEntry.getInfo());
				insertStatement.setString(12, polishJapaneseEntry.isUseEntry() == true ? "" : "NO");
				
				insertStatement.execute();
				
				insertStatement.close();			
			}
		} catch (SQLException e) {
			System.err.println("SQL Error: " + e);
		} finally {
			if (insertStatement != null) {
				try {
					insertStatement.close();
				} catch (SQLException e) {
					// ignore
				}
			}
			
			if (dbConnection != null) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					// ignore
				}
			}
		}
		
	}
	
	private static String convertListToString(List<String> list) {
		StringBuffer sb = new StringBuffer();
		
		for (int idx = 0; idx < list.size(); ++idx) {
			sb.append(list.get(idx));
			
			if (idx != list.size() - 1) {
				sb.append(",");
			}
		}
		
		return sb.toString();
	}
}
