/**
 * <h1>Player</h1>
 * This class give the player with specific characteristics
 */
package Models;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * <h1>Player</h1>
 * This class that controls logic of the Player
 */
public class Player {
    private static Color[] ALL_COLORS = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.LIGHT_GRAY, Color.ORANGE};
    private Phases p;
    private Color playerColor;
    private int unassigned_armies;
    private int mapArmies = 0;//the total number of armies this player owns on the world map(excluding unassigned_armies)
    private int id;//this is primary key for players
    private Card cards;
    private int numOfDice;
    protected ArrayList<Integer> dice = new ArrayList<>();

    public ArrayList<Integer> getDice() {
        return dice;
    }

    /**
     * Array list of controlled territories of the player
     */
    protected ArrayList<Country> realms;

    /**
     * get all the cards of the player
     * @return cards
     */
    public Card getCards() {
        return cards;
    }

    /**
     * Constructor
     * @param id player's id
     * @param army player's army number
     * @param p player's current phase
     */
    public Player(int id, int army, Phases p) {
        this.id = id;
        this.realms = new ArrayList<>();
        this.unassigned_armies = army;
        this.playerColor = ALL_COLORS[id];
        this.p = p;
        this.cards =new Card();

    }


    /**
     * Gets all the controlled countries of the player
     * @return ArrayList of countries he controls
     */
    public ArrayList<Country> getRealms() {
        return realms;
    }


    /**
     * Gets the player color
     * @return Color of the player
     */
    public Color getPlayerColor() {
        return playerColor;
    }


    /**
     * Gets the player ID
     * @return Player ID
     */
    public int getId() {
        return id;
    }


    /**
     * Determines if the player still have unassigned armies
     * @return True if there is still some army left and False otherwise
     */
    public boolean isArmyLeft(){
        return unassigned_armies > 0;
    }

    /**
     * Set unassigned armies to the player
     * @param extra number of army give to the player
     */
    protected void getReinforcement(int extra){
        this.setUnassigned_armies(unassigned_armies + extra);
    }

    /**
     * Get unassigned army
     * @return number of army left
     */
    public int getUnassigned_armies() {
        return unassigned_armies;
    }

    /**
     * Get all armies currently on the map
     * @return number of armies currently assigned to countries owned by the player
     */
    public int getMapArmies(){
        return mapArmies;
    }


    public String showPlayerCardsName(int cardID) {
    	return cards.showCardsName(cardID);
    	
    }
    
    public int showPlayerCards(int cardID) {
    	return cards.showCardsNumber(cardID);
    	
    }
    
    public int[] showAllcards() {
    	return cards.cardAll();
    }
    
    public void addPlayerOneCard() {
    	cards.addCard();
    }
    
    public void receiveEnemyCards(int[]enemycards) {
    	
    	cards.addCard(enemycards);
    }
    
    public void addPlayerArmyByCard(int cardTurn) {
    	unassigned_armies+=cardTurn*5;
    }
    
    
    /**
     * Send army to the country
     * @param country to where army will be sent
     */
    protected void deployArmy(Country country) throws OutOfArmyException {
        if(country.getOwner() == this) {
            if(isArmyLeft()) {
                setUnassigned_armies(unassigned_armies - 1);
                incrementMapArmies();
                country.incrementArmy();
            }
            else{
                System.out.println("out of armies in deploy army");
                throw new OutOfArmyException();
            }

        }
        else{
            System.out.println("not a country of current player");
        }
    }

    private void incrementMapArmies() {
        mapArmies++;
        p.updateWindow();


    }

    private void setUnassigned_armies(int unassigned_armies) {
        this.unassigned_armies = unassigned_armies;
        p.updateWindow();

    }

    /**
     * This function is used during the fortification phase to decide whether a player can move army from one country to another;
     * @param sourceCountry The source country
     * @param targetCountry The target country
     * @return True if it is possible to transfer between given countries
     * @throws CountryNotInRealms   country does not belong to the current player
     * @throws SourceIsTargetException  source and target are the same
     */
    private boolean findPath(Country sourceCountry, Country targetCountry) throws CountryNotInRealms, SourceIsTargetException {

        if (realms.contains(sourceCountry) && realms.contains(targetCountry)) {
            if(sourceCountry != targetCountry){
                Queue<Country> queue = new LinkedList<Country>();
                HashSet<Country> set = new HashSet<>();
                queue.offer(sourceCountry);
                set.add(sourceCountry);
                while (!queue.isEmpty()) {
                    Country tempCountry = queue.poll();
                    if (tempCountry == targetCountry) {
                        return true;
                    }
                    for (Country loopCountry : realms) {
                        for (Country neighbour : tempCountry.getNeighbours()){
                            if (neighbour != loopCountry){
                                continue;
                            }
                            else{
                                if (!set.contains(neighbour)) {
                                    queue.offer(neighbour);
                                    set.add(neighbour);
                                }
                            }
                        }
                    }

                }
                return false;
            }
            else{
                throw new SourceIsTargetException();
            }
        }
        else{
            throw new CountryNotInRealms();
        }


    }


    /**
     * Transfers army from one country to another during the fortification phase
     * @param from - from which country army is being taken
     * @param to - to which country army is being send
     * @param num - number of the army to transfer
     * @throws CountryNotInRealms   country not owned by the player
     * @throws OutOfArmyException   not enough army to transfer
     * @throws NoSuchPathException  no path from owned countries between country
     * @throws SourceIsTargetException  source country and target country is the same
     * @throws MoveAtLeastOneArmyException  0 army chosen to move
     */
    protected void fortificate(Country from, Country to, int num) throws CountryNotInRealms, OutOfArmyException, NoSuchPathException, SourceIsTargetException, MoveAtLeastOneArmyException {

        if(findPath(from, to)){
            from.decreaseArmy(num);
            to.increaseArmy(num);
//            p.nextPhase();
        }
        else{
            throw new NoSuchPathException();
        }
    }
    protected void rollDice (int digits){
        numOfDice = digits;
//        ArrayList<Integer> DiceArray = new ArrayList<Integer>();
        for (int i =0; i < digits; i++){
            int Dice = (int)(Math.random()*6)+1;
            dice.add(Dice);
        }
        Collections.sort(dice, Collections.reverseOrder());
        p.updateWindow();
//        return dice;
    }
    protected void removeDice(){


        dice.remove(0);
        p.updateWindow();
    }
    protected int consumeDice(){
        int usedDice = dice.get(0);
        removeDice();
        return usedDice;
    }
    public int getNumOfDice(){
        return numOfDice;
    }

    protected void attackAssign(Country from, Country to, int num) throws MoveAtLeastOneArmyException, OutOfArmyException{
        System.out.println("attackAssign:" + num);
        from.decreaseArmy(num);
        to.increaseArmy(num);
    }

    /**
     * For checking validation in attack phase
     * @param sourceCountry source country
     * @param targetCountry target country
     * @param num number of attacking army
     * @return true for validation
     * @throws AttackMoveAtLeastOneArmy army at least one
     * @throws AttackOutOfArmy out of army number in attacking country
     * @throws AttackCountryArmyMoreThanOne the number of army in attacking country must more than one
     * @throws AttackingCountryOwner the owner of attacking country must be current player
     * @throws AttackedCountryOwner the owner of attacked country must be the enemy
     */
    private boolean countryValidation(Country sourceCountry, Country targetCountry, int num) throws AttackCountryArmyMoreThanOne, AttackingCountryOwner, AttackedCountryOwner, AttackOutOfArmy, AttackMoveAtLeastOneArmy{
        if (sourceCountry.getArmy() > 1){
            if (sourceCountry.getOwner() == this){
                if (targetCountry.getOwner() != this && sourceCountry.getNeighbours().contains(targetCountry)){
                    if (num < sourceCountry.getArmy()){
                        if (num > 0){
                            return true;
                        }else{
                            throw new AttackMoveAtLeastOneArmy();
                        }
                    }else {
                        throw new AttackOutOfArmy();
                    }
                }else {
                    throw new AttackedCountryOwner();
                }
            }else {
                throw new AttackingCountryOwner();
            }
        }else{
            throw new AttackCountryArmyMoreThanOne();
        }
    }

    /**
     * Attack phase
     * @param from source country
     * @param to target country
     * @param numAttack the number of attacking army
     * @param numDefence the number of defence army
     * @return true for conquest successful
     * @throws AttackMoveAtLeastOneArmy army at least one
     * @throws AttackOutOfArmy out of army number in attacking country
     * @throws AttackCountryArmyMoreThanOne the number of army in attacking country must more than one
     * @throws AttackingCountryOwner the owner of attacking country must be current player
     * @throws AttackedCountryOwner the owner of attacked country must be the enemy
     */
    protected boolean attack(Country from, Country to, int numAttack, int numDefence) throws AttackedCountryOwner, AttackingCountryOwner, AttackCountryArmyMoreThanOne, AttackOutOfArmy, AttackMoveAtLeastOneArmy {

        boolean conquest = false;
        ArrayList<Integer> results = new ArrayList<Integer>();
        if (countryValidation(from, to, numAttack)){
            System.out.println("Attack army:" + numAttack);
            System.out.println("Defence army: "+ numDefence);
            if (numDefence == -1){
                results = attackSimulation(to, numAttack);
                //Conquest
                if (results.get(0) > 0){
                    System.out.println("Win");
                    from.attackDecreaseArmy(numAttack - results.get(0));
                    to.setOwner(this);
                    to.attackDecreaseArmy(to.getArmy());
                    conquest = true;
                    JOptionPane.showMessageDialog(null, "Win, Dead: " + (numAttack - results.get(0))+ " army");

                } else {
                    System.out.println("Lose");
                    from.attackDecreaseArmy(numAttack);
                    to.attackDecreaseArmy(to.getArmy() - results.get(1));
                    JOptionPane.showMessageDialog(null, "Lose, Dead: " + (numAttack - results.get(0))+ " army");
                }
            }else {
                results = attackNormal(numAttack, numDefence);
                from.attackDecreaseArmy(numAttack - results.get(0));
                to.attackDecreaseArmy(numDefence - results.get(1));
                if (to.getArmy() == 0){
                    to.setOwner(this);
                    conquest = true;
                }
                if (numAttack - results.get(0) > numDefence - results.get(1)){
                    JOptionPane.showMessageDialog(null, "Lose, Kill: "+ (numDefence - results.get(1))+" army. Dead: " + (numAttack - results.get(0))+ " army");
                }
                else if(numAttack - results.get(0) == numDefence - results.get(1)) {
                    JOptionPane.showMessageDialog(null, "Equal, Kill: "+ (numDefence - results.get(1))+" army. Dead: " + (numAttack - results.get(0))+ " army");
                }else {
                    JOptionPane.showMessageDialog(null, "Win, Kill: "+ (numDefence - results.get(1))+" army. Dead: " + (numAttack - results.get(0))+ " army");
                }
            }


        }
        return conquest;
    }

    protected ArrayList<Integer> DiceArray (int digits){
        ArrayList<Integer> DiceArray = new ArrayList<Integer>();
        for (int i =0; i < digits; i++){
            int Dice = (int)(Math.random()*6)+1;
            DiceArray.add(Dice);
        }
        Collections.sort(DiceArray, Collections.reverseOrder());
        return DiceArray;
    }
    protected ArrayList<Integer> compareThreetoTwo(int liveArmy, int defenceArmy){
        ArrayList<Integer> results = new ArrayList<>();
        ArrayList<Integer> attackDice = DiceArray(3);
        ArrayList<Integer> defenceDice = DiceArray(2);
        if (attackDice.get(0) > defenceDice.get(0)) {
            defenceArmy --;
            } else {
            liveArmy --;
            }
            if (attackDice.get(1) > defenceDice.get(1)) {
            defenceArmy --;
            } else {
            liveArmy --;
            }
            results.add(liveArmy);
            results.add(defenceArmy);
        return results;
    }
    protected ArrayList<Integer> compareThreetoOne(int liveArmy, int defenceArmy){
        ArrayList<Integer> results = new ArrayList<>();
        ArrayList<Integer> attackDice = DiceArray(3);
        int defenceDice1 = (int) (Math.random() * 6) + 1;
        if (attackDice.get(0) > defenceDice1) {
            defenceArmy --;
        } else {
            liveArmy --;
        }
        results.add(liveArmy);
        results.add(defenceArmy);
        return results;
    }
    protected ArrayList<Integer> compareTwotoTwo(int liveArmy, int defenceArmy){
        ArrayList<Integer> results = new ArrayList<>();
        ArrayList<Integer> attackDice = DiceArray(2);
        ArrayList<Integer> defenceDice = DiceArray(2);
        if (attackDice.get(0) > defenceDice.get(0)) {
            defenceArmy --;
        } else {
            liveArmy --;
        }
        if (attackDice.get(1) > defenceDice.get(1)) {
            defenceArmy --;
        } else {
            liveArmy --;
        }
        results.add(liveArmy);
        results.add(defenceArmy);
        return results;
    }
    protected ArrayList<Integer> compareTwotoOne(int liveArmy, int defenceArmy){
        ArrayList<Integer> results = new ArrayList<>();
        ArrayList<Integer> attackDice = DiceArray(2);
        int defenceDice1 = (int) (Math.random() * 6) + 1;
        if (attackDice.get(0) > defenceDice1) {
            defenceArmy --;
        } else {
            liveArmy --;
        }
        results.add(liveArmy);
        results.add(defenceArmy);
        return results;
    }
    protected ArrayList<Integer> compareOnetoTwo(int liveArmy, int defenceArmy){
        ArrayList<Integer> results = new ArrayList<>();
        int attackDice1 = (int) (Math.random() * 6) + 1;
        ArrayList<Integer> defenceDice = DiceArray(2);
        if (attackDice1 > defenceDice.get(0)) {
            defenceArmy --;
        } else {
            liveArmy --;
        }
        results.add(liveArmy);
        results.add(defenceArmy);
        return results;
    }
    protected ArrayList<Integer> compareOnetoOne(int liveArmy, int defenceArmy){
        ArrayList<Integer> results = new ArrayList<>();
        int attackDice1 = (int) (Math.random() * 6) + 1;
        int defenceDice1 = (int) (Math.random() * 6) + 1;
        if (attackDice1 > defenceDice1) {
            defenceArmy --;
        } else {
            liveArmy --;
        }
        results.add(liveArmy);
        results.add(defenceArmy);
        return results;
    }
    protected ArrayList<Integer> attackNormal(int numAttack, int numDefence){
        ArrayList<Integer> results = new ArrayList<>();
        if (numAttack == 3){
            if (numDefence == 2){
                results = compareThreetoTwo(3, 2);
            }else {
                results = compareThreetoOne(3,1);
            }
        }
        if (numAttack == 2){
            if (numDefence == 2){
                results = compareTwotoTwo(2, 2);
            }else {
                results = compareTwotoOne(2,1);
            }
        }
        if (numAttack == 1){
            if (numDefence == 2){
                results = compareOnetoTwo(1, 2);
            }else {
                results = compareOnetoOne(1,1);
            }
        }
        return results;
    }
//Results: first: rest of attacking second: rest of attacked
    protected ArrayList<Integer> attackSimulation(Country to, int num){
        ArrayList<Integer> results = new ArrayList<>();
        int liveArmy = num;
        int defenceArmy = to.getArmy();

        while (liveArmy != 0 && defenceArmy !=0){
            if (liveArmy > 2) {
                if (defenceArmy >= 2) {
                    System.out.println("1");
                    ArrayList<Integer> compare = compareThreetoTwo(liveArmy, defenceArmy);
                    liveArmy = compare.get(0);
                    defenceArmy = compare.get(1);
                } else {
                    System.out.println("2");
                    ArrayList<Integer> compare = compareThreetoOne(liveArmy, defenceArmy);
                    liveArmy = compare.get(0);
                    defenceArmy = compare.get(1);
                }
            } else if (liveArmy == 2) {
                if (defenceArmy >= 2) {
                    System.out.println("3");
                    ArrayList<Integer> compare = compareTwotoTwo(liveArmy, defenceArmy);
                    liveArmy = compare.get(0);
                    defenceArmy = compare.get(1);
                } else {
                    System.out.println("4");
                    ArrayList<Integer> compare = compareTwotoOne(liveArmy, defenceArmy);
                    liveArmy = compare.get(0);
                    defenceArmy = compare.get(1);
                }
            } else if (liveArmy == 1) {
                if (defenceArmy >= 2) {
                    System.out.println("5");
                    ArrayList<Integer> compare = compareOnetoTwo(liveArmy, defenceArmy);
                    liveArmy = compare.get(0);
                    defenceArmy = compare.get(1);
                } else {
                    System.out.println("6");
                    ArrayList<Integer> compare = compareOnetoOne(liveArmy, defenceArmy);
                    liveArmy = compare.get(0);
                    defenceArmy = compare.get(1);
                }
            }
        }
        results.add(liveArmy);
        results.add(defenceArmy);
        System.out.println("attack:"+liveArmy);
        System.out.println("defence:"+defenceArmy);
        return results;
    }
    protected void loseArmy(Country country) throws OutOfArmyException {
        mapArmies--;

        country.decrementArmy();


//        p.updateWindow();



    }


}
