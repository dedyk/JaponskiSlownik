import java.io.FileReader;
import java.io.FileWriter;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;


public class TestCsv {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CsvReader csvReader = new CsvReader(new FileReader("test.csv"), ',');
		
		while(csvReader.readRecord()) {
			int columns = csvReader.getColumnCount();
			
			for (int idx = 0; idx < columns; ++idx) {
				System.out.println(csvReader.get(idx));
			}			
		}		
		
		csvReader.close();
		
		CsvWriter csvWriter = new CsvWriter(new FileWriter("test2.csv"), ',');
		
		csvWriter.write("aaaa");
		csvWriter.write("bbbb");
		csvWriter.write("cccc\ndddd");
		
		csvWriter.endRecord();
		
		csvWriter.write("aaaa2");
		csvWriter.write("bbbb2");
		csvWriter.write("cccc2\ndddd2");
		
		csvWriter.close();
	}
}
