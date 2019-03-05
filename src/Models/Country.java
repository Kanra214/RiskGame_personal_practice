package Models;
import java.util.ArrayList;

import View_Components.*;

/**
 * <h1>Country</h1>
 * This class is for country object
 */
public class Country {


    private String name;
    private Phases p;



    private Continent cont;
    private Player owner;
    private int army = 0;
    private int X = 0;
    private int Y = 0;


    private ArrayList<Country> neighbours;

//public constructor because mapLoader needs this

    /**
     * Constructor
     * @param x coordinate x
     * @param y coordinate y
     * @param name name of country
     * @param continent owner to which continent
     */
    public Country(int x, int y, String name, Continent continent) {
        this.name = name;
        this.cont = continent;
        X=x;
        Y=y;

        this.neighbours = new ArrayList<>();


    }

    /**
     *
     * @param p
     */
    protected void setPhase(Phases p){
        this.p = p;
    }

    /**
     * Increase army number in panel
     */
    protected void increaseArmy(){
        this.army++;
        p.updateWindow();
    }

    /**
     * Increase army number after moving
     * @param i The number of army moved
     * @throws MoveAtLeastOneArmyException
     */
    protected void increaseArmy(int i) throws MoveAtLeastOneArmyException {
        if(i < 1){
            throw new MoveAtLeastOneArmyException(4);


        }
        else{
            this.army += i;
            p.updateWindow();
        }




    }

//whats this

    /**
     * After move army, the ary number should be decrease in previous country
     * @param i The number of army moved
     * @throws OutOfArmyException
     * @throws MoveAtLeastOneArmyException
     */
    protected void decreaseArmy(int i) throws OutOfArmyException, MoveAtLeastOneArmyException {
        if(i < 1){
            throw new MoveAtLeastOneArmyException(4);
        }
        if(this.army <= i){
            throw new OutOfArmyException(0);
        }


        else {
            this.army -= i;
            p.updateWindow();
        }


    }

    /**
     * Set owner to country
     * @param player owner is a object of player
     */
    public void setOwner(Player player) {
        this.owner = player;
        player.realms.add(this);
        p.updateWindow();
    }

//    public void sendArmy() {
//        this.army++;
//    }


    /**
     * Get country's name
     * @return name of country
     */
    public String getName(){
        return name;
    }

    /**
     * Get neighbours relationships
     * @return neighnours arraylist
     */
    public ArrayList<Country> getNeighbours(){return neighbours;}

    /**
     * Output neighbors relationships
     * @return  neighbors relationships as one string
     */
    public String printNeighbors() {
    	String reNei="";
    	for(Country nei: getNeighbours()) {
    		System.out.println(nei.getName()+" "+getNeighbours().size());
    		reNei=reNei+nei.getName()+",";

    		System.out.println(reNei);
    	}
    	return reNei;
    }
//public because MapLoader needs this

    /**
     * Add neighbour relationship
     * @param country another country object
     */
    public void addNeighbour(Country country) {

        this.neighbours.add(country);

    }

    /**
     * Get continent
     * @return continent object
     */
    public Continent getCont() {
        return cont;
    }

    /**
     * Get continent name
     * @return name of continent
     */
    public String getContName() {
        return cont.getName();
    }

    /**
     * Get country's owner
     * @return player object
     */
    public Player getOwner () {
        return owner;

    }

    /**
     * Get army number
     * @return army number
     */
    public int getArmy(){
        return army;
    }

    /**
     * Get coordinate X
     * @return X
     */
    public int getX(){
        return X;
    }

    /**
     * Get coordiante Y
     * @return Y
     */
    public int getY(){
        return Y;
    }





}

