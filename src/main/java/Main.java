import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Cell {
  private String oem;
  private String model;
  private Integer launchAnnounced;
  private Object launchStatus; // Can be Integer or String
  private String bodyDimensions;
  private Float bodyWeight;
  private String bodySim;
  private String displayType;
  private Float displaySize;
  private String displayResolution;
  private String featuresSensors;
  private String platformOs;

  public Cell(String oem, String model, String launchAnnounced, String launchStatus,
      String bodyDimensions, String bodyWeight, String bodySim, String displayType,
      String displaySize, String displayResolution, String featuresSensors, String platformOs) {
    this.oem = oem;
    this.model = model;
    this.launchAnnounced = parseYear(launchAnnounced);
    this.launchStatus = parseLaunchStatus(launchStatus);
    this.bodyDimensions = parseBodyDimensions(bodyDimensions);
    this.bodyWeight = parseBodyWeight(bodyWeight);
    this.bodySim = parseBodySim(bodySim);
    this.displayType = parseDisplayType(displayType);
    this.displaySize = parseDisplaySize(displaySize);
    this.displayResolution = parseDisplayResolution(displayResolution);
    this.featuresSensors = parseFeaturesSensors(featuresSensors);
    this.platformOs = parsePlatformOs(platformOs);
  }

  // Getters for cell attributes
  public String getOem() {
    return oem;
  }

  public String getModel() {
    return model;
  }

  public Integer getLaunchAnnounced() {
    return launchAnnounced;
  }

  public Object getLaunchStatus() {
    return launchStatus;
  }

  public String getBodyDimensions() {
    return bodyDimensions;
  }

  public Float getBodyWeight() {
    return bodyWeight;
  }

  public String getBodySim() {
    return bodySim;
  }

  public String getDisplayType() {
    return displayType;
  }

  public Float getDisplaySize() {
    return displaySize;
  }

  public String getDisplayResolution() {
    return displayResolution;
  }

  public String getFeaturesSensors() {
    return featuresSensors;
  }

  public String getPlatformOs() {
    return platformOs;
  }

  @Override
  public String toString() {
    return String.format("OEM: %s, Model: %s, Launch Announced: %s, Launch Status: %s, " +
        "Body Dimensions: %s, Body Weight: %.2f grams, Body SIM: %s, " +
        "Display Type: %s, Display Size: %.2f inches, Display Resolution: %s, " +
        "Features Sensors: %s, Platform OS: %s",
        oem, model, launchAnnounced, launchStatus, bodyDimensions, bodyWeight, bodySim,
        displayType, displaySize, displayResolution, featuresSensors, platformOs);
  }

  // Helper methods for parsing attributes
  private Integer parseYear(String value) {
    Pattern pattern = Pattern.compile("\\b(\\d{4})\\b");
    Matcher matcher = pattern.matcher(value);
    if (matcher.find()) {
      return Integer.parseInt(matcher.group(1));
    }
    return null;
  }

  private Object parseLaunchStatus(String value) {
    String valueLower = value.trim().toLowerCase();
    if (valueLower.equals("discontinued") || valueLower.equals("cancelled")) {
      return valueLower;
    } else {
      Integer year = parseYear(value);
      return (year != null) ? year : null;
    }
  }

  private String parseBodyDimensions(String value) {
    if (value != null) {
      Pattern pattern = Pattern.compile("(\\d+\\.\\d+ x \\d+\\.\\d+ x \\d+\\.\\d+ mm)");
      Matcher matcher = pattern.matcher(value);
      return matcher.find() ? matcher.group(0) : null;
    }
    return null;
  }

  private Float parseBodyWeight(String value) {
    if (value != null) {
      String normalizedWeight = value.trim().toLowerCase();
      if (normalizedWeight.equals("no") || normalizedWeight.equals("yes")) {
        return null;
      } else {
        Pattern pattern = Pattern.compile("(\\d+)\\s*g\\b");
        Matcher matcher = pattern.matcher(value);
        return matcher.find() ? Float.parseFloat(matcher.group(1)) : null;
      }
    }
    return null;
  }

  private String parseBodySim(String value) {
    if (value != null) {
      String normalizedSim = value.trim().toLowerCase();
      return (normalizedSim.equals("no") || normalizedSim.equals("yes")) ? null : value.trim();
    }
    return null;
  }

  private String parseDisplayType(String value) {
    return (value != null) ? value.trim() : null;
  }

  private Float parseDisplaySize(String value) {
    if (value != null) {
      Pattern pattern = Pattern.compile("(\\d+\\.\\d+|\\d+)\\s*inches");
      Matcher matcher = pattern.matcher(value.trim().toLowerCase());
      return matcher.find() ? Float.parseFloat(matcher.group(1)) : null;
    }
    return null;
  }

  private String parseDisplayResolution(String value) {
    return (value != null) ? value.trim() : null;
  }

  private String parseFeaturesSensors(String value) {
    if (value != null) {
      String normalizedFeatures = value.trim().toLowerCase();
      if (normalizedFeatures.equals("v1") || normalizedFeatures.matches(".*[a-zA-Z0-9].*")) {
        return value.trim();
      }
    }
    return null;
  }

  private String parsePlatformOs(String value) {
    if (value != null) {
      Matcher matcher = Pattern.compile("^([^,]+)").matcher(value.trim());
      return matcher.find() ? matcher.group(1) : null;
    }
    return null;
  }
}

public class Main {
  public static void main(String[] args) {
    String csvFile = "cells.csv";
    List<Cell> cells = readCSV(csvFile);

    // Print out the details of each cell
    for (Cell cell : cells) {
      System.out.println(cell);
      System.out.println();
    }

    // Additional functionalities
    Map<String, Float> averageWeights = calculateAverageWeightByOEM(cells);
    System.out.println("Average Weight of Phone Body by OEM:");
    for (Map.Entry<String, Float> entry : averageWeights.entrySet()) {
      System.out.println(entry.getKey() + ": " + entry.getValue() + " grams");
    }

    List<Cell> mismatchedPhones = findPhonesWithDifferentAnnounceAndRelease(cells);
    System.out.println("\nPhones Announced in One Year and Released in Another:");
    for (Cell cell : mismatchedPhones) {
      System.out.println(cell.getOem() + " " + cell.getModel());
    }

    int countV1FeatureSensors = countPhonesWithOneFeatureSensor(cells);
    System.out.println("\nNumber of Phones with Only One Feature Sensor: " + countV1FeatureSensors);

    int maxLaunchYear = findYearWithMostLaunches(cells);
    System.out.println("\nYear with Most Phones Launched (After 1999): " + maxLaunchYear);
  }

  // Method to read CSV file and create Cell objects
  private static List<Cell> readCSV(String csvFile) {
    List<Cell> cells = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
      String line;
      br.readLine(); // Skip header line
      while ((line = br.readLine()) != null) {
        String[] row = line.split(",");
        Cell cell = createCellFromCsvRow(row);
        if (cell != null) {
          cells.add(cell);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return cells;
  }

  // Method to create Cell object from CSV row
  private static Cell createCellFromCsvRow(String[] row) {
    if (row.length < 12) { // Ensure correct number of columns
      return null;
    }
    String oem = row[0].trim();
    String model = row[1].trim();
    String launchAnnounced = row[2].trim();
    String launchStatus = row[3].trim();
    String bodyDimensions = row[4].trim();
    String bodyWeight = row[5].trim();
    String bodySim = row[6].trim();
    String displayType = row[7].trim();
    String displaySize = row[8].trim();
    String displayResolution = row[9].trim();
    String featuresSensors = row[10].trim();
    String platformOs = row[11].trim();

    return new Cell(oem, model, launchAnnounced, launchStatus, bodyDimensions,
        bodyWeight, bodySim, displayType, displaySize, displayResolution,
        featuresSensors, platformOs);
  }

  // Additional functionalities

  // Method to calculate average weight of phone body by OEM
  private static Map<String, Float> calculateAverageWeightByOEM(List<Cell> cells) {
    Map<String, Float> averageWeights = new HashMap<>();
    Map<String, Float> sumWeights = new HashMap<>();
    Map<String, Integer> countWeights = new HashMap<>();

    for (Cell cell : cells) {
      String oem = cell.getOem();
      Float weight = cell.getBodyWeight();

      if (oem != null && weight != null) {
        sumWeights.put(oem, sumWeights.getOrDefault(oem, 0f) + weight);
        countWeights.put(oem, countWeights.getOrDefault(oem, 0) + 1);
      }
    }

    for (String oem : sumWeights.keySet()) {
      averageWeights.put(oem, sumWeights.get(oem) / countWeights.get(oem));
    }

    return averageWeights;
  }

  // Method to find phones announced in one year and released in another
  private static List<Cell> findPhonesWithDifferentAnnounceAndRelease(List<Cell> cells) {
    List<Cell> mismatchedPhones = new ArrayList<>();

    for (Cell cell : cells) {
      Integer announceYear = cell.getLaunchAnnounced();
      Object status = cell.getLaunchStatus();

      if (announceYear != null && status instanceof Integer) {
        Integer releaseYear = (Integer) status;
        if (!announceYear.equals(releaseYear)) {
          mismatchedPhones.add(cell);
        }
      }
    }

    return mismatchedPhones;
  }

  // Method to count phones with only one feature sensor
  private static int countPhonesWithOneFeatureSensor(List<Cell> cells) {
    int count = 0;

    for (Cell cell : cells) {
      String sensors = cell.getFeaturesSensors();
      if (sensors != null && sensors.equalsIgnoreCase("v1")) {
        count++;
      }
    }

    return count;
  }

  // Method to find year with most phones launched
  private static int findYearWithMostLaunches(List<Cell> cells) {
    Map<Integer, Integer> yearCounts = new HashMap<>();

    for (Cell cell : cells) {
      Integer year = cell.getLaunchAnnounced();
      if (year != null && year > 1999) {
        yearCounts.put(year, yearCounts.getOrDefault(year, 0) + 1);
      }
    }

    int maxCount = 0;
    int maxYear = 0;

    for (Map.Entry<Integer, Integer> entry : yearCounts.entrySet()) {
      if (entry.getValue() > maxCount) {
        maxCount = entry.getValue();
        maxYear = entry.getKey();
      }
    }

    return maxYear;
  }
}
