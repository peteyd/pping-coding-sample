import com.patientping.CSVParser;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCSVParser {
    @Test
    public void testParseLine() {
        CSVParser.readHeader("id,title,author,language");
        Map<String, String> values = CSVParser.parseLine("123,Rendezvous with Rama,Arthur C. Clarke,English");

        assertEquals("123", values.get("id"));
        assertEquals("Rendezvous with Rama", values.get("title"));
        assertEquals("Arthur C. Clarke", values.get("author"));
        assertEquals("English", values.get("language"));
    }

    @Test
    public void testParseLineWithNull() {
      CSVParser.readHeader("id,title");

      Map<String, String> values = CSVParser.parseLine(null);

      assertEquals(null, values);
    }

    @Test
    public void testParseLineWithWrongNumberOfColumns() {
      CSVParser.readHeader("id,title");

      Map<String, String> values = CSVParser.parseLine("123");

      assertEquals(true, values.isEmpty());
    }
}

