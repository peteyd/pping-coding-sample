package com.patientping;

import java.util.Map;
import java.util.HashMap;

public class CSVParser {
  private static String[] csvHeader;

  public static void readHeader(String header) {
    csvHeader = header.split(",");
  }

  public static Map<String, String> parseLine(String line) {
    if (line == null) {
      return null;
    }

    Map<String, String> valueMap = new HashMap();

    String[] columnValues = line.split(",");

    // if the column value list and the header list are the same length,
    // iterate through both arrays and create a value map indexed by
    // column name
    if (columnValues.length == csvHeader.length) {
      for (int i = 0; i < columnValues.length; ++i) {
        valueMap.put(csvHeader[i], columnValues[i]);
      }
    }

    return valueMap;
  }
}
