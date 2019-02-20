package Game;
import Models.*;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Maploader1 {
//    private int globalIndex;
    private int findCountries;
    public String riskMapName;
    public String author;
    public String warn,image,wrap,scroll;
    public boolean modified;
    public ArrayList<Continent> continents;
    public int countryNum;
    public Map<String,ArrayList<Country>> countries;
    public Map<String,ArrayList<String>> adjacencyList;
    public Maploader1(String name) {
//        this.globalIndex = 1;
        this.riskMapName = name;
        this.author = "Invincible Team Four";
        this.warn = "yes";
        this.wrap = "no";
        this.image = "none";
        this.scroll = "none";
        this.modified = false;
        this.continents = new ArrayList<Continent>();
        this.countryNum = 0;
        this.countries = new HashMap<String,ArrayList<Country>>();
        this.adjacencyList = new HashMap<String,ArrayList<String>>();

    }
    public boolean loadMapFile(String mapFileName) {//mode 1-mapEditor 2-RiskGame
        BufferedReader br = null;
        String inputLine = null;
        int rowNumber = 0;
        String dateArea = "none";//none, Map, Continents, Territories;
        Map<String,ArrayList<String>> countriesList = new HashMap<String,ArrayList<String>>();
        try{
            br = new BufferedReader(new FileReader(mapFileName));
            this.riskMapName = (mapFileName.substring(mapFileName.lastIndexOf("\\")+1,mapFileName.lastIndexOf(".")));
            while ((inputLine = br.readLine()) != null){
                rowNumber++;
                inputLine = inputLine.trim();
                int index;
                if (!inputLine.isEmpty()){
                    switch (inputLine){
                        case "[Map]":
                            dateArea = "Map";
                            break;
                        case "[Continents]":
                            dateArea = "Continents";
                            break;
                        case "[Territories]":
                            dateArea = "Territories";
                            break;
                        default:
                            switch (dateArea){
                                case "Map":
                                    index = inputLine.indexOf("=");
                                    if (index!=-1){
                                        String keyword = inputLine.substring(0,index).trim().toLowerCase();
                                        String value = inputLine.substring(index+1).trim();
                                        if (!keyword.isEmpty()){
                                            switch (keyword){
                                                case "author":
                                                    if (!value.isEmpty()) this.author = value;
                                                    break;
                                                case "warn":
                                                    if (!value.isEmpty()) this.warn = value;
                                                    break;
                                                case "image":
                                                    if (!value.isEmpty()) this.image = value;
                                                    break;
                                                case "wrap":
                                                    if (!value.isEmpty()) this.wrap = value;
                                                    break;
                                                case "scroll":
                                                    if (!value.isEmpty()) this.scroll = value;
                                                    break;
                                                default:
                                                    JOptionPane.showMessageDialog(null,"Fatal error in line "+rowNumber+": Invalid format.");
                                                    return false;
                                            }
                                        }
                                    }
                                    else{
                                        JOptionPane.showMessageDialog(null,"Fatal error in line "+rowNumber+": Invalid format.");
                                        return false;
                                    }
                                    break;
                                case "Continents":
                                    index = inputLine.indexOf("=");
                                    if (index!=-1){
                                        String continentName = inputLine.substring(0,index).trim();
                                        String controlNum = inputLine.substring(index+1).trim();
                                        if (continentName==null||controlNum==null){
                                            JOptionPane.showMessageDialog(null,"Fatal error in line "+rowNumber+": Invalid format.");
                                            return false;
                                        }
                                        if (continentName.isEmpty()||controlNum.isEmpty()){
                                            JOptionPane.showMessageDialog(null,"Fatal error in line "+rowNumber+": Invalid format.");
                                            return false;
                                        }
                                        Pattern pattern = Pattern.compile("[0-9]*");
                                        if (pattern.matcher(controlNum).matches()){
//                                            if (!this.addContinent(continentName,Integer.parseInt(controlNum))) return false;
                                            System.out.println(continentName + controlNum);

                                        }
                                        else {
                                            JOptionPane.showMessageDialog(null,"Fatal error in line "+rowNumber+": Continent <"+continentName+">'s control number must be integer.");
                                            return false;
                                        }
                                        //used to new Continent
                                        addContinent(continentName,countryNum);
                                    }
                                    else{
                                        JOptionPane.showMessageDialog(null,"Fatal error in line "+rowNumber+": Invalid format.");
                                        return false;
                                    }
                                    break;

                                case "Territories":
                                    String[] countryInfo = inputLine.split(",");
                                    if (countryInfo.length>=4){
                                        String countryName = countryInfo[0].trim();
                                        String belongContinentName = countryInfo[3].trim();
                                        if (countryName.isEmpty()){
                                            JOptionPane.showMessageDialog(null,"Fatal error in line "+rowNumber+": Country name can't be empty");
                                            return false;
                                        }
                                        if (belongContinentName.isEmpty()){
                                            JOptionPane.showMessageDialog(null,"Fatal error in line "+rowNumber+": Continent name can't be empty");
                                            return false;
                                        }
                                        Pattern pattern = Pattern.compile("[0-9]*");
                                        if (!pattern.matcher(countryInfo[1].trim()).matches()||!pattern.matcher(countryInfo[2].trim()).matches()){
                                            JOptionPane.showMessageDialog(null,"Fatal error in line "+rowNumber+": Coordinates must be integer.");
                                            return false;
                                        }
//                                        if (!this.addCountry(countryName,belongContinentName,Integer.parseInt(countryInfo[1].trim()),
//                                                Integer.parseInt(countryInfo[2].trim())))
//                                            return false;
                                        countriesList.put(countryName, new ArrayList<String>());
                                        for (int i=4;i<countryInfo.length;i++){
                                            if (countriesList.get(countryName).contains(countryInfo[i].trim())){
                                                JOptionPane.showMessageDialog(null,"Fatal error in line "+rowNumber+": Duplicate record in adjacency list.");
                                                return false;
                                            }
                                            countriesList.get(countryName).add(countryInfo[i].trim());
                                        }
                                        //used to new Country
                                        addCountry(countryInfo[0],countryInfo[3],Integer.parseInt(countryInfo[1]),Integer.parseInt(countryInfo[2]));
                                    }
                                    else{
                                        JOptionPane.showMessageDialog(null,"Fatal error in line "+rowNumber+": Not enough data.");
                                        return false;
                                    }
                            }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

//        for (String loopCountry : countriesList.keySet()) {
//            ArrayList<String> neighbours = countriesList.get(loopCountry);
//            for (String loopNeighbour:neighbours){
//                if (!countriesList.containsKey(loopNeighbour)){
//                    JOptionPane.showMessageDialog(null,"Fatal error: Country <"+loopNeighbour+"> in <"+loopCountry+">'s adjacency list is not exist.");
//                    return false;
//                }
//                if (!countriesList.get(loopNeighbour).contains(loopCountry)){
//                    JOptionPane.showMessageDialog(null,"Fatal error: The connection between country <"+loopCountry+"> and <"+loopNeighbour+"> is not paired.");
//                    return false;
//                }
//                adjacencyList.get(findCountry(loopCountry).countryID).add(findCountry(loopNeighbour).countryID);
//            }
//        }

//        return checkValid(mode);
        return true;
    }
    //This method used to find country
    public Country findCountry(String countryName) {
        for (ArrayList<Country> loopList : countries.values()) {
            for (Country loopCountry:loopList){
                if (loopCountry.getName().equals(countryName)){
                    return loopCountry;
                }
            }
        }
        return null;
    }
    //This method is used to find Continent
    public Continent findContinent(String continentName) {
        for (Continent loopContinent:continents){
            if (loopContinent.getName().equals(continentName)){
                return loopContinent;
            }
        }
        return null;
    }


    //The function to add new Country in existing continent
    public boolean addCountry(String countryName,String continentName,int coordinateX, int coordinateY){

        Continent targetContinent = findContinent(continentName);
        if (targetContinent==null) {
            JOptionPane.showMessageDialog(null,"Continnet <"+continentName+"> does not exists");
            return false;
        }

        if (findCountry(countryName)!=null){
            JOptionPane.showMessageDialog(null,"Country <"+countryName+"> already exists");
            return false;
        }

        Country newCountry = new Country(coordinateX,coordinateY,countryName,targetContinent);
        countries.get(targetContinent.getName()).add(newCountry);
        adjacencyList.put(newCountry.getName(), new ArrayList<String>());
        countryNum++;
        this.modified = true;
        return true;
    }

    //This function is used to new Continent
    public boolean addContinent(String continentName, int controlNum){
        if (findContinent(continentName)!=null) {
            JOptionPane.showMessageDialog(null,"Continnet <"+continentName+"> already exists");
            return false;
        }
        Continent newContinent = new Continent(continentName,controlNum);
        continents.add(newContinent);
        countries.put(newContinent.getName(), new ArrayList<Country>());
        this.modified = true;
        return true;
    }
    //This function is used to add connection between 2 countries
    public boolean addConnection(String countryNameFrom,String countryNameTo){
        Country fromCountry = findCountry(countryNameFrom);
        if (fromCountry==null){
            JOptionPane.showMessageDialog(null,"Country  '"+countryNameFrom+"' does not exists");
            return false;
        }
        Country toCountry = findCountry(countryNameTo);
        if (toCountry==null){
            JOptionPane.showMessageDialog(null,"Country  '"+countryNameTo+"' does not exists");
            return false;
        }
        adjacencyList.get(fromCountry.getName()).add(toCountry.getName());
        adjacencyList.get(toCountry.getName()).add(fromCountry.getName());
        this.modified = true;
        return true;
    }
}
