package com.patientping;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class CSVParser {
  private static String[] csvHeader;

  public static void readHeader(String header) {
    csvHeader = header.split(",");
  }

  // TODO: throw exception if not the right number of columns
  public static Map<String, String> parseLine(String line) {
    if (line == null) {
      return null;
    }

    Map<String, String> valueMap = new HashMap();

    String[] columnValues = line.split(",");

    if (columnValues.length == csvHeader.length) {
      for (int i = 0; i < columnValues.length; ++i) {
        valueMap.put(csvHeader[i], columnValues[i]);
      }
    }

    return valueMap;
  }
}
