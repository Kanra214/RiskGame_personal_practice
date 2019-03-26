package Models;
//import View_Components.CardExchangeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
/**
 * <h1>Phase</h1>
 * This class that controls logic of the game
 *
 */
public class Phases extends Observable {
    private int numOfPlayers = 1;
    private ArrayList<Player> players;
    private ArrayList<Country> graph;
    private ArrayList<Continent> worldmap;
    private Player current_player;
    private Player rival;//the player being attacked in the attack phase
    private int currentPhase = 0;
    private int currentTurn = -1;
    private boolean viewIsConnected = false;
    private boolean at_least_once = false;//used to determine the current player is qualified  to receive a card
    public boolean gameOver = false;
    public boolean inBattle = false;//used to enable complete button, when during the dice consuming battle, player can't go to the next phase
    private boolean attackingIsPossible = true;//if false, the game automatically skip the attack phase
    public boolean cardCancelTrigger = false;
    public int CardTurn=1;//flag for how many times players change cards



    /**
     * Constructor
     * @param graph    List of countries on the map
     * @param worldMap List of Continent on the map
     */
    public Phases(ArrayList<Country> graph, ArrayList<Continent> worldMap) {
        this.graph = graph;
        this.worldmap = worldMap;
        players = new ArrayList<>();
        //give each country access to this so they can update the view
        for (Country country : graph) {
            country.setPhase(this);
        }
    }

    /**
     * Give each player initial armies of which the number is depended on the number of players
     * @param number the number of players
     * @return the number of initial armies
     */
    private int getInitialArmyCount(int number) {
        switch (number) {
            case 6:
                return 20;
            case 5:
                return 25;
            case 4:
                return 30;
            case 3:
                return 35;
            case 2:
                return 10;//TODO 45
            default:
                return 100;
        }
    }

    /**
     * Set up new game: instantiate players, determine the order of players, randomly assign countries and start start up phase
     * @param numOfPlayers the number of players
     */
    public void gameSetUp(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
        for (int i = 0; i < numOfPlayers; i++) {
            players.add(new Player(i, getInitialArmyCount(numOfPlayers), this));
        }
        determineOrder();
        countryAssignment();
        nextPhase();
    }

    /**
     * Gets list of countries
     *
     * @return ArrayList of country
     */
    public ArrayList<Country> getGraph() {
        return graph;
    }


    /**
     * Gets list of Players
     *
     * @return ArrayList of players
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }


    /**
     * Gets reinforcements from all continents that the player owns
     *
     * @param player which player to check
     * @return int Reinforcements that the player gets from owning the whole continent
     */
    public int extraArmyFromContinent(Player player) {
        int reinforcement = 0;
        for (Continent c : worldmap) {
            if (checkContinentOwner(c, player)) {
                reinforcement += c.getControl_value();
            }
        }
        return reinforcement;
    }


    /**
     * Get reinforcements according to the number of countries and continents that the player owns
     *
     * @param player which player to check
     * @return int  number of all countries devided by 3 plus army from continent
     */
    public int reinforcementArmy(Player player) {
    	
    	if(player.getUnassigned_armies()>3) {
    		System.out.println(player.getUnassigned_armies());
    		System.out.println("return 0");
    		return 0;
    	}
        int reinforcement = player.realms.size() / 3 + extraArmyFromContinent(player);
        if (reinforcement < 3) reinforcement = 3;
        return reinforcement;
    }


    /**
     * get current Phase
     *
     * @return current phase
     */
    public int getCurrentPhase() {
        
    	return currentPhase;
    }

    /**
     * next player's turn
     */
    private void nextTurn() {

        currentTurn++;
        if (at_least_once){
            System.out.println("got a card");
            current_player.addPlayerOneCard();
        }
        at_least_once = false;
        current_player = players.get(currentTurn % numOfPlayers);//first player is players[0]
        if (current_player.getRealms().size() == 0) {//if the player is ruled out of the game
            nextTurn();

        }


        if (currentPhase == 1) {
        	//TODO
       	 System.out.println("p"+getCurrentPhase());
         System.out.println("p"+getCurrentTurn());
        	// phaseOneFirstStep();
        	 cardCancelTrigger=false;
           
        }

    }


    /**
     * Gets current player
     *
     * @return current player
     */
    public Player getCurrent_player() {
    	
    	return current_player;
    }

    /**
     * First step of phase one where amount of the reinforcement army is being determined where min he gets is 3
     */
    public void phaseOneFirstStep() {
        int reinforce = reinforcementArmy(current_player);
        if (reinforce == -1) {
            current_player.getReinforcement(3);
        } else {
            current_player.getReinforcement(reinforce);
        }

    }


    /**
     * Move to the next phase and updates the window
     */
    public void nextPhase() {

        switch (currentPhase) {
            case 0:
                if (currentTurn >= numOfPlayers - 1) {
                    currentPhase = 1;
                }
                nextTurn();
                break;
            case 1:
//                cardView.setVisible(false);
                currentPhase = 2;
                checkAttackingIsPossible();//every beggining of phase two needs to be checked

                break;
            case 2:

                currentPhase = 3;
//                cardView.setVisible(true);

                break;
            case 3:
                if (current_player.getRealms().size() == 0) {
                    nextPhase();
                }
                else {
                    currentPhase = 1;
                    nextTurn();
                }
                break;
        }
        updateWindow();
    }


    /**
     * Determine the round robin order for players by changing the order in the players field
     */
    private void determineOrder() {
        Collections.shuffle(players);

    }

    /**
     * Assign countries to players randomly in a round robin
     */
    private void countryAssignment() {
        Collections.shuffle(this.graph);
        int turnReference = 0;
        int turn = 0;
        for (Country country : this.graph) {
            Player player = players.get(turn);
            country.setOwner(player);
            try {
                player.deployArmy(country);
            } catch (OutOfArmyException e) {
                System.out.println("Not possible");
            }
            checkContinentOwner(country.getCont(),player);
            turnReference++;
            turn = turnReference % players.size();
        }

    }

    public void updatePhase() {
    	updateWindow();
    }

    /**
     * Notifies connected observers
     */
    protected void updateWindow() {
        if (viewIsConnected) {
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Notifies connected observers
     */
    protected void updateWindow(Object o) {
        if (viewIsConnected) {
            setChanged();
            notifyObservers(o);
        }
    }


    /**
     * Connect view
     */
    public void connectView() {
        viewIsConnected = true;
        updateWindow();
    }


    /**
     * Sends 1 army from current player to chosen Country during start up phase
     *
     * @param chosen Country where to send army to
     */
    public void startUpPhase(Country chosen) {
        try {
            current_player.deployArmy(chosen);
        } catch (RiskGameException e) {
            System.out.println("out of army in start phase");

        }
    }


    /**
     * Sends 1 army from current player to chosen Country during reinforcement phase
     *
     * @param chosen Country where to send army to
     */
    public void reinforcementPhase(Country chosen) {


        try {
            current_player.deployArmy(chosen);
        } catch (RiskGameException e) {
            System.out.println("Not possible");
        }


    }


    /**
     * Attack phase
     */
    public boolean attackPhase(Country from, Country to) throws AttackingCountryOwner, AttackedCountryOwner, WrongDiceNumber, AttackCountryArmyMoreThanOne, TargetCountryNotAdjacent {

        boolean validated = false;//only first validateAttack() will throw exceptions to controller, after that, exceptions thrown by validateAttack() will be caught


        while (true) {
            try {
                int attackDice = Math.min(from.getArmy() - 1, 3);
                int defendDice = Math.min(to.getArmy(), 2);

                if (attackPhase(from, to, attackDice, defendDice)) {
                    at_least_once = true;


                    return true;
                }
                validated = true;//any exception after this will be caught and break the while


            } catch (RiskGameException e) {
                if (validated) {
                    at_least_once = false;
                    return false;
                } else {
                    throw e;
                }

            }


        }


    }

    /**
     * Attack phase
     *
     * @param from       Country from where army will attacking
     * @param to         Country from where army will be attacked
     * @param attackDice int number of dice to roll for attacker
     * @return true if country is conquered, false otherwise
     * @throws AttackCountryArmyMoreThanOne the number of army in attacking country must more than one
     * @throws AttackingCountryOwner        the owner of attacking country must be current player
     * @throws AttackedCountryOwner         the owner of attacked country must be the enemy
     */
    public boolean attackPhase(Country from, Country to, int attackDice, int defendDice) throws AttackingCountryOwner, AttackedCountryOwner, WrongDiceNumber, AttackCountryArmyMoreThanOne, TargetCountryNotAdjacent {
//        current_player.attack(from, to, attackDice, defendDice);
//        if(current_player.attackValidation(from, to, num)) {
//            attackSimulation(from, to, num);
//        }


        try {
            if (attackValidation(from, to, attackDice, defendDice)) {

                attackSimulation(from, to, attackDice, defendDice);

            }
        } catch (OutOfArmyException e) {
            to.swapOwnership(rival, current_player);


            if (checkWinner()) {//this attacker conquered all the countries
                gameOver = true;

            }
            at_least_once = true;
            checkContinentOwner(to.getCont(),current_player);//check if this player gets control of the continent
            if(rival.getRealms().size() == 0){
                current_player.receiveEnemyCards(rival);
            }

            return true;


        }
        checkAttackingIsPossible();
        at_least_once = false;

        return false;


    }


    protected void attackSimulation(Country from, Country to, int attackDice, int defendDice) throws OutOfArmyException {

        inBattle(true);

        rival = to.getOwner();
        current_player.rollDice(attackDice);
        rival.rollDice(defendDice);

        while (current_player.dice.size() > 0 && rival.dice.size() > 0) {


            try {


                if (current_player.dice.get(0) > rival.dice.get(0)) {

                    losesAnArmy(rival, to);
                } else {
                    losesAnArmy(current_player, from);
                }
                current_player.dice.remove(0);
                rival.dice.remove(0);

            } catch (OutOfArmyException e) {
                inBattle(false);
                current_player.dice.clear();
                rival.dice.clear();

                throw e;
            }

        }
        current_player.dice.clear();
        rival.dice.clear();
        inBattle(false);


    }


    /**
     * Sends army from one country to another
     *
     * @param from Country from where army will be deducted
     * @param to   Country from where army will be sent
     * @param num  int number of armies to send
     * @throws CountryNotInRealms          country not owned by the player
     * @throws OutOfArmyException          not enough army to transfer
     * @throws NoSuchPathException         no path from owned countries between country
     * @throws SourceIsTargetException     source country and target country is the same
     * @throws MoveAtLeastOneArmyException 0 army chosen to move
     */
    public void fortificationsPhase(Country from, Country to, int num) throws SourceIsTargetException, MoveAtLeastOneArmyException, CountryNotInRealms, OutOfArmyException, NoSuchPathException {
        current_player.fortificate(from, to, num);
        nextPhase();
    }

    public boolean checkWinner() {//check if current player win the whole game
        return current_player.realms.size() == graph.size();


    }

    public void checkAttackingIsPossible() {
        //check if the current player is still possible to continue attacking phase
        //return false if no such possibility

        for (Country attack : current_player.realms) {
            if (attack.getArmy() > 1) {
                for (Country defend : attack.getNeighbours()) {
                    if (defend.getOwner() != current_player) {
                        attackingIsPossible = true;
                        return;

                    }
                }
            }

        }

        attackingIsPossible = false;
        updateWindow();
        nextPhase();

    }


    protected boolean checkAttack(Player player) {
        boolean val = true;
        int count_army = 0;
        int count_owner = 0;
        for (Country country : player.getRealms()) {
            if (country.getArmy() == 1) {
                count_army++;
            }
            for (Country nei : country.getNeighbours()) {
                if (nei.getOwner() != player) {
                    count_owner++;
                }
            }
        }
        return val;
    }

    /**
     * For checking validation in attack phase
     *
     * @param sourceCountry source country
     * @param targetCountry target country
     * @param attackDice    number of attacker's dice
     * @param defendDice    number of defender's dice
     * @return true for validation
     * @throws AttackCountryArmyMoreThanOne the number of army in attacking country must more than one
     * @throws AttackingCountryOwner        the owner of attacking country must be current player
     * @throws AttackedCountryOwner         the owner of attacked country must be the enemy
     */
    public boolean attackValidation(Country sourceCountry, Country targetCountry, int attackDice, int defendDice) throws AttackingCountryOwner, AttackedCountryOwner, WrongDiceNumber, AttackCountryArmyMoreThanOne, TargetCountryNotAdjacent {
        if (sourceCountry.getArmy() >= 2) {
            if (sourceCountry.getOwner() == current_player) {
                if (targetCountry.getOwner() != current_player) {
                    if (sourceCountry.getNeighbours().contains(targetCountry)) {
                        if (attackDice <= sourceCountry.getArmy() - 1 && attackDice <= 3 && attackDice > 0) {
                            if (defendDice <= targetCountry.getArmy() && defendDice <= 2 && defendDice > 0) {
                                return true;


                            } else {
                                throw new WrongDiceNumber(targetCountry.getOwner());

                            }
                        } else {
                            throw new WrongDiceNumber(current_player);
                        }
                    } else {
                        throw new TargetCountryNotAdjacent();
                    }
                } else {
                    throw new AttackedCountryOwner();
                }
            } else {
                throw new AttackingCountryOwner();
            }
        } else {
            throw new AttackCountryArmyMoreThanOne();
        }

    }


    public boolean deploymentAfterConquer(Country from, Country to, int num) throws MustBeEqualOrMoreThanNumOfDice, SourceIsTargetException, NoSuchPathException, CountryNotInRealms, OutOfArmyException, MoveAtLeastOneArmyException {


        if (num >= current_player.getNumOfDice()) {
            current_player.fortificate(from, to, num);
            checkAttackingIsPossible();
            return true;
        } else {

            throw new MustBeEqualOrMoreThanNumOfDice();

        }
    }

    public ArrayList<Continent> getWorldmap(){
        return worldmap;
    }

    private void losesAnArmy(Player player, Country country) throws OutOfArmyException {
        updateWindow(player);
        try {
            player.loseArmy(country);


        } catch (OutOfArmyException e) {

            throw e;
        }

    }

    private void inBattle(boolean flag) {
        inBattle = flag;
        updateWindow();
    }

    public boolean getInBattle() {
        return inBattle;
    }

    public Player getRival() {
        return rival;
    }

    public boolean getAttackingIsPossible() {
        return attackingIsPossible;
    }
    public int getCurrentTurn(){return currentTurn;
    }
    public int getNumOfPlayers(){return numOfPlayers;}
    private boolean checkContinentOwner(Continent cont, Player player){
        boolean flag = cont.checkOwnership(player);
        updateWindow();
        return flag;
    }

    public void setCurrent_player(Player current_player) {
        this.current_player = current_player;
    }
}