package Models;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;

public class Benevolent implements Strategy, Serializable {

    @Override
    public void execute(Phases p) {

        Player player = p.getCurrent_player();
        if(player == p.rival) {
            player.setNumOfDice(1);
        }
        else {


            if (p.getCurrentPhase() == 0) {
                System.out.println("benevolent phase 0");
                //TODO: phase 0
                int countryCount = 0;
                int countryTurn = 0;
                while (player.isArmyLeft()) {
                    Country currentCountry = player.getRealms().get(countryTurn);
                    try {
                        player.reinforce(currentCountry);
                        System.out.println("Benevolent: reinforce" + currentCountry.getName());
                    } catch (OutOfArmyException e) {
                        System.out.println("benevolent phase 0 out of army");
                    }
                    countryCount++;
                    countryTurn = countryCount % player.getRealms().size();
                }
                p.nextPhase();
            } else {

                //must be in phase 1
                exchangeCards(p);
                System.out.println("benevolent card exchange");
                p.phaseOneFirstStep();
                System.out.println("benevolent phase one first step, unassigned army = " + player.getUnassigned_armies());
                Comparator cp = new WeakestCountryComparator();

                while (player.isArmyLeft()) {
                    Collections.sort(player.getRealms(), cp);
                    Country weakest = player.getRealms().get(0);
                    System.out.println("weakest: " + weakest.getName());

                    try {
                        player.reinforce(weakest);
                    } catch (OutOfArmyException e) {
                        System.out.println("benevolent out of army");
                    }
                }
                p.nextPhase();
                //no attack
                //this player might not be able to attack, next phase automatically, to avoid next phase twice, check AttackingIsPossible
                p.checkAttackingIsPossible();
                if (p.getAttackingIsPossible()) {
                    p.nextPhase();
                }
                //phase 3
                int ith = player.getRealms().size() - 1;
                Collections.sort(player.getRealms(), cp);
                Country weakest = player.getRealms().get(0);
                while (true) {
                    try {
                        Country strongest = player.getRealms().get(ith);
                        int armiesToMove = (strongest.getArmy() - weakest.getArmy()) / 2;
                        player.fortify(strongest, weakest, armiesToMove);
                        System.out.println("benevolent fortify: " + strongest.getName() + " to " + weakest.getName() + " num: " + armiesToMove);
                        break;
                    } catch (CountryNotInRealms countryNotInRealms) {
                        System.out.println("benevolent not in reamls");
                    } catch (OutOfArmyException e) {
                        System.out.println("benevolent fortify out of army");
                    } catch (NoSuchPathException e) {
                        ith--;
                        continue;
                    } catch (SourceIsTargetException e) {
                        break;
                    } catch (MoveAtLeastOneArmyException e) {
                        break;
                    }
                }
                p.nextPhase();


            }
        }
        }

    public void exchangeCards(Phases p){
        Card cards = p.getCurrent_player().getCards();
        for(int i = 0; i < 3; i++){
            if(cards.cardBigger3(i)){
                p.getCurrent_player().addPlayerArmyBySameCards(i);
                System.out.println("changed 3 same cards: " + cards.showCardsName(i));
                return;
            }
        }
        if(cards.checkThreeDiffCards()){
            p.getCurrent_player().addPlayerArmyByDiffCards();
            System.out.println("changed 3 diff cards");

        }

    }
    class WeakestCountryComparator implements Comparator<Country> {
        @Override
        public int compare(Country a, Country b) {
            return a.getArmy() - b.getArmy();
        }
    }







    }








