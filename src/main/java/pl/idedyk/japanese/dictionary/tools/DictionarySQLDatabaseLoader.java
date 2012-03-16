package pl.idedyk.japanese.dictionary.tools;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import pl.idedyk.japanese.dictionary.dto.PolishJapaneseEntry;

/*

create table words (
	id int not null auto_increment, primary key(id),
	dictionary_entry_type varchar(20) not null, index(dictionary_entry_type),
	word_type varchar(20) not null, index(word_type),
	kanji varchar(30) null, index(kanji),
	kana_list varchar(100) not null,
	romaji_list varchar(100) not null,
	polish_translate_list varchar(100) not null,
	info varchar(60) null,
	fulltext(kanji, kana_list, romaji_list, polish_translate_list)
) default character set = utf8 collate = utf8_polish_ci;

 */

public class DictionarySQLDatabaseLoader {
	
	public static void main(String[] args) throws IOException {
		
		final String mysqlAddress = "jdbc:mysql://localhost/japdb?characterEncoding=utf8&user=japdbuser&password=japdbpasswd";
		
		loadDictionaryIntoDB(mysqlAddress, "words", "input/word.csv", "out/word.sql");
		
		
	}

	private static void loadDictionaryIntoDB(String mysqlAddress, String tableName, String inputFileName, String outputFileName) throws IOException {
		
		final String insertStatementSql = "insert into " + tableName + " values(default, ?, ?, ?, ?, ?, ?, ?)";
		
		List<PolishJapaneseEntry> polishJapaneseEntries = CsvReaderWriter.parsePolishJapaneseEntriesFromCsv(inputFileName);
		
		Connection dbConnection = null;
		PreparedStatement insertStatement = null;
		
		try {
			
			dbConnection = DriverManager.getConnection(mysqlAddress);
		
			for (PolishJapaneseEntry polishJapaneseEntry : polishJapaneseEntries) {
				
				insertStatement = dbConnection.prepareStatement(insertStatementSql);
				
				insertStatement.setString(1, polishJapaneseEntry.getDictionaryEntryType().toString());
				insertStatement.setString(2, polishJapaneseEntry.getWordType().toString());
				insertStatement.setString(3, polishJapaneseEntry.getKanji());
				insertStatement.setString(4, convertListToString(polishJapaneseEntry.getKanaList()));
				insertStatement.setString(5, convertListToString(polishJapaneseEntry.getRomajiList()));
				insertStatement.setString(6, convertListToString(polishJapaneseEntry.getPolishTranslates()));
				insertStatement.setString(7, polishJapaneseEntry.getInfo());
				
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
