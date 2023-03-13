package actions;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Basic;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class AIOpponent {

    public static Player AIStats;
    public static Card[] deployedCards = new Card[20];
    public static ArrayList<Integer> AICardNumbers = new ArrayList<Integer>();

    public static String[] AICards = {
            StaticConfFiles.c_blaze_hound,
            StaticConfFiles.c_staff_of_ykir,
            StaticConfFiles.c_staff_of_ykir,
            StaticConfFiles.c_bloodshard_golem,
            StaticConfFiles.c_entropic_decay,
            StaticConfFiles.c_hailstone_golem,
            StaticConfFiles.c_planar_scout,
            StaticConfFiles.c_pyromancer,
            StaticConfFiles.c_serpenti,
            StaticConfFiles.c_rock_pulveriser,
            
            StaticConfFiles.c_windshrike,
            StaticConfFiles.c_blaze_hound,
            StaticConfFiles.c_bloodshard_golem,
            StaticConfFiles.c_entropic_decay,
            StaticConfFiles.c_hailstone_golem,
            StaticConfFiles.c_planar_scout,
            StaticConfFiles.c_pyromancer,
            StaticConfFiles.c_serpenti,
            StaticConfFiles.c_rock_pulveriser,
            
            StaticConfFiles.c_windshrike,
    };

    public static String[] AllUnits = {
            StaticConfFiles.u_azure_herald,
            StaticConfFiles.u_azurite_lion,
            StaticConfFiles.u_blaze_hound,
            StaticConfFiles.u_bloodshard_golem,
            StaticConfFiles.u_comodo_charger,
            StaticConfFiles.u_fire_spitter,
            StaticConfFiles.u_hailstone_golem,
            StaticConfFiles.u_hailstone_golemR,
            StaticConfFiles.u_ironcliff_guardian,
            StaticConfFiles.u_planar_scout,
            StaticConfFiles.u_pureblade_enforcer,
            StaticConfFiles.u_pyromancer,
            StaticConfFiles.u_rock_pulveriser,
            StaticConfFiles.u_serpenti,
            StaticConfFiles.u_silverguard_knight,
            StaticConfFiles.u_windshrike,

    };

    public static ArrayList<Card> AIOpponentCards = new ArrayList<Card>();
    public static String[] AIHand = new String[6];
    public static int currentCardNumber = 1;
    public static ArrayList<ArrayList<Integer>> permissiblePositions = new ArrayList<ArrayList<Integer>>();
    public static int AITurn = 0;
    public static Unit currentUnit = GameState.gameBoard[7][2];
    public static int selectedUnitX = -1;
    public static int selectedUnitY = -1;
    public static int cnt=1;
    public static String readFileAsString(String file) throws Exception {
        return new String(Files.readAllBytes(Paths.get(file)));
    }

    public static Card getCard(ActorRef out, Unit unit) {
        ObjectMapper mapper = new ObjectMapper();
        for (String eachCard : AICards) {
            try {
                String json = readFileAsString(eachCard);
                Card card = mapper.readValue(json, Card.class);
                if (unit.getId() == card.getId()) {
                    return card;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("not found");

        return null;

    }

    public static void loadPlayer(ActorRef out) {
        BasicCommands.addPlayer1Notification(out, "setPlayer1Health", 2);
        AIStats = new Player(20, 2);
        BasicCommands.setPlayer2Health(out, AIStats);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Mana
        for (int m = 0; m < 2; m++) {
            // int n = m + 1;
            // BasicCommands.add(out, "setPlayer1Mana ("+n+")", 1);
            BasicCommands.setPlayer2Mana(out, AIStats);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        int playerHandCount = 0;
        for (String deck1CardFile : AICards) {
            // change to unit and upload number!
            try {

                String json = readFileAsString(deck1CardFile);
                Card card = mapper.readValue(json, Card.class);
                AICardNumbers.add(card.getId());

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (playerHandCount <= 2) {
                currentCardNumber += 1;

                AIHand[playerHandCount] = deck1CardFile;
                System.out.println("ai details " + playerHandCount + " " + AIHand[playerHandCount] + deck1CardFile);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            playerHandCount++;
        }
    }

    public static int getUnitAttack(ActorRef out, Unit unit) {
        System.out.println(unit.getId() + " we are attack");
        if (unit.getId() == 100) {
            return 2;
        } else if (unit.getId() == 200) {
            return 2;
        } else {
            Card curCard = AIOpponent.getCard(out, unit);
            System.out.println(curCard.getBigCard().getHealth() + " this is unit health!");
            return curCard.getBigCard().getAttack();
        }
    }

    public static int getUnitHealth(ActorRef out, Unit unit) {
        System.out.println(unit.getId() + " we are AI health");
        if (unit.getId() == 100) {
            return AIOpponent.AIStats.getHealth();
        } else if (unit.getId() == 200) {
            return AIOpponent.AIStats.getHealth();
        } else {
            Card curCard = AIOpponent.getCard(out, unit);
            // System.out.println(curCard.getBigCard().getHealth() + " this is unit
            // health!");
            return curCard.getBigCard().getHealth();
        }
    }

    public static void attackUnit(ActorRef out, int x, int y) {
        if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[x][y].getId())
                || GameState.gameBoard[x][y].getId() == 200) {
            // throw warning same player!
            System.out.println("this is a AI thingy");

            // remove selected unit
        } else {
            // int id = GameState.gameBoard[x][y].getId();
            int attack = getUnitAttack(out, currentUnit);
            System.out.println(attack + " this is attack!");
            BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Attack]", 2);
            BasicCommands.playUnitAnimation(out, currentUnit, UnitAnimationType.attack);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int currentHealth = getUnitHealth(out, GameState.gameBoard[x][y]);

            int updatedHealth = currentHealth - attack;
            // TODO update the Avatar health also
            if (GameState.gameBoard[x][y].getId() == 200) {
                AIOpponent.AIStats.setHealth(updatedHealth);
            }
            if (GameState.gameBoard[x][y].getId() == 100) {
                HumanPlayer.humanStats.setHealth(updatedHealth);

                // TODO increasefilverGaurd night attack power
            }
            System.out.println(updatedHealth + " " + currentHealth + " " + attack);
            for (int i = 0; i < AIOpponent.AIOpponentCards.size(); i++) {
                if (AIOpponent.AIOpponentCards.get(i).getId() == GameState.gameBoard[x][y].getId()) {
                    AIOpponent.AIOpponentCards.get(i).getBigCard().setHealth(updatedHealth);
                }
            }
            BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
            BasicCommands.setUnitHealth(out, GameState.gameBoard[x][y], updatedHealth);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // TODO
            // if opposite health is greater than 0 and if the attacking unit is in any
            // adjacent tile then attack back

            // if opposite unit is avatar - do some logic
            // if opposite unit is Human unit do some logic

        }

    }

    public static void AIlogic(ActorRef out) {
        // TODO check previous move

        // condition 1:
        // if previous move of the human player is to deploy a unit then
        // check AI hand of cars.. and run a loop to check if a card is there with that
        // can be deployed with the mana in the pool.. then deploy unit in a random tile
        // that is free
        // update the card in the AI card.
        BasicCommands.addPlayer1Notification(out, "AI turn", 2);
        System.out.println("inside AI class " + GameState.previousMove + " " + AIOpponent.AIHand.length);
        boolean isUnitDeployed = false;
        AITurn++;
        if (AITurn >= 2) {
            GameState.AIcurrentRound += 1;
            int newManaValue = GameState.AIcurrentRound + 1;
            System.out.println(newManaValue + " this is new mana value for AI");
            loadMana(out, newManaValue);
        }
        if (AITurn < 2) {
            currentUnit = GameState.gameBoard[7][2];
            // highlightPossibleMoves(out, 7, 2);
            selectedUnitX = 7;
            selectedUnitY = 2;
        }
        // highlightPossibleMoves(out, selectedUnitX, selectedUnitY);
        // else {
        // currentUnit = currentUnit;
        // }
        
        for (int i = 0; i < AIOpponent.AIHand.length; i++) {
            System.out.println("AI hand  " + AIOpponent.AIHand[i]);
            if (AIOpponent.AIHand[i] != null) {
                // check if the card can be deployed then deploy.
                // set isUnitDeployed to true
                // remove card from the hand
                // else move to next card
                ObjectMapper mapper = new ObjectMapper();
                String json;
                try {
                    json = readFileAsString(AIOpponent.AIHand[i]);
                    Card card = mapper.readValue(json, Card.class);
                    GameState.currentCard = card;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                if (GameState.currentCard.getManacost() <= AIOpponent.AIStats.getMana()) {
                    // TODO
                    // deploy unit
                    // delete the card from the AI hand.
                    if (GameState.currentCard.getId() == 1003) {
                        int max=-1;
                        int maxval=0;
                        int unitx=-1;
                        int unity=-1;
                        System.out.println("spell should be used");
                        for (int k=0;k<9;k++){
                            for (int j=0;j<5;j++){
                                if (GameState.gameBoard[k][j]!=null){
                                    System.out.println("first if running");
                                    if (HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[k][j].getId()) 
                                    && GameState.gameBoard[k][j].getId() != 100){
                                        System.out.println("second if running");
                                        System.out.println(HumanPlayer.humanCards.size());
                                        for(int x = 0; x < HumanPlayer.humanCards.size();x++){
                                            if(HumanPlayer.humanCards.get(x).getId() == GameState.gameBoard[k][j].getId()){
                                                System.out.println("third if running");
                                                if (HumanPlayer.humanCards.get(x).getBigCard().getHealth()>max){
                                                    System.out.println("fourth if running");
                                                    max=HumanPlayer.humanCards.get(x).getBigCard().getHealth();
                                                    maxval=x;
                                                    unitx=k;
                                                    unity=j;
                                                }
                                                
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        HumanPlayer.humanCards.get(maxval).getBigCard().setHealth(0);
                        BasicCommands.setUnitHealth(out, GameState.gameBoard[unitx][unity], 0);
                        EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_martyrdom);
                        Tile tile = BasicObjectBuilders.loadTile(unitx, unity);
                        BasicCommands.playEffectAnimation(out, ef, tile);
                        try{
                            Thread.sleep(100);
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                        BasicCommands.playUnitAnimation(out, GameState.gameBoard[unitx][unity], UnitAnimationType.death);
                        AIOpponent.deleteCard(out, i);
                        for (int a = 0; a < GameState.currentCard.getManacost(); a++) {
                            AIOpponent.AIStats.setMana(AIOpponent.AIStats.getMana() - 1);
                            BasicCommands.setPlayer2Mana(out, AIOpponent.AIStats);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
            
                        }
                        BasicCommands.deleteUnit(out, GameState.gameBoard[unitx][unity]);
                        GameState.gameBoard[unitx][unity] = null;
                        cnt++;
                        increaseUnitHealth(out,cnt);
                        break;
                    }
                    if (GameState.currentCard.getId()==1004){
                        for (int k=0;k<9;k++){
                            for (int j=0;j<5;j++){
                                if (GameState.gameBoard[k][j] != null){
                                    if (GameState.gameBoard[k][j].getId() == 200){
                                        System.out.println("1004 first if running");
                                        EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_martyrdom);
                                        Tile tile = BasicObjectBuilders.loadTile(k, j);
                                        BasicCommands.playEffectAnimation(out, ef, tile);
                                        try{
                                            Thread.sleep(100);
                                        }
                                        catch(Exception e){
                                            e.printStackTrace();
                                        }
                                        int att=0;
                                        BasicCommands.setUnitAttack(out, GameState.gameBoard[k][j], getUnitAttack(out, GameState.gameBoard[k][j])+2);
                                        
                                    //    if(getCard(out, GameState.gameBoard[k][j] )!= null){
                                    //     System.out.println("1004 is running");
                                    //         Card a = getCard(out, GameState.gameBoard[k][j]);
                                    //         System.out.println(a.getBigCard().getAttack() + " this is old attack");
                                    //         a.getBigCard().setAttack(a.getBigCard().getAttack()+2);
                                            
                                    //    }
                                       
                                    }
                                    
                                }
                            }
                        }
                        AIOpponent.deleteCard(out, i);
                        cnt++;
                        increaseUnitHealth(out,cnt);
                        for (int a = 0; a < GameState.currentCard.getManacost(); a++) {
                            AIOpponent.AIStats.setMana(AIOpponent.AIStats.getMana() - 1);
                            BasicCommands.setPlayer2Mana(out, AIOpponent.AIStats);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
            
                        }
                        
                        break;
                    }
                }
            }
        }
        if (GameState.previousMove == "deploy") {
            highlightPossibleMoves(out, selectedUnitX, selectedUnitY);
            for (int i = 0; i < AIOpponent.AIHand.length; i++) {
                System.out.println("AI hand  " + AIOpponent.AIHand[i]);
                if (AIOpponent.AIHand[i] != null) {
                    // check if the card can be deployed then deploy.
                    // set isUnitDeployed to true
                    // remove card from the hand
                    // else move to next card
                    ObjectMapper mapper = new ObjectMapper();
                    String json;
                    try {
                        json = readFileAsString(AIOpponent.AIHand[i]);
                        Card card = mapper.readValue(json, Card.class);
                        GameState.currentCard = card;
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (GameState.currentCard.getManacost() <= AIOpponent.AIStats.getMana()) {
                        // TODO
                        // deploy unit
                        // delete the card from the AI hand.
                        deployUnit(out, i);
                        isUnitDeployed = true;
                        break;
                    }

                }
            }

            if (isUnitDeployed == false) {
                // TODO
                // move any possible unit on the board.. by running a for loop
                // run for loop ..
                System.out.println("ai avatar id " + currentUnit.getId());
                if (currentUnit.getId() == 200) {
                    highlightPossibleMoves(out, selectedUnitX, selectedUnitY);
                } else {
                    highlightUnitTiles(out);
                }
                moveUnit(out);
            }

            // reset all the conditions and end turn automatically
        }

        // if not enough AI.. then move any one of the unit on the board if movable.

        // Condition2:
        // if previous move is attack.. then attack any nearest unit if possible.
        // else move a unit randomly on a tile if possible.

        boolean ifAttackMade = false;

        if (GameState.previousMove == "attack") {
            // run a for loop on the game board.. if not null and it is a AI Opponent check
            // if it can attack then attack/move and attack.

            if (ifAttackMade == false) {
                // run a for loop and check if you can move any unit then move a unit.
                if (currentUnit.getId() == 200) {
                    highlightPossibleMoves(out, selectedUnitX, selectedUnitY);
                } else {
                    highlightUnitTiles(out);
                }
                moveUnit(out);
            }

        }

        // condition3
        // if previous move is move .. move any unit randomly if possible

        if (GameState.previousMove == "move") {
            // run a for loop on the game board.. and move any possible Unit...
            System.out.println("ai avatar id " + currentUnit.getId());
            if (currentUnit.getId() == 200) {
                highlightPossibleMoves(out, selectedUnitX, selectedUnitY);
            } else {
                highlightUnitTiles(out);
            }
            moveUnit(out);

        }

    }
    public static void increaseUnitHealth(ActorRef out,int cnt){
        for (int k=0;k<9;k++){
            for (int j=0;j<5;j++){
                if (GameState.gameBoard[k][j] != null){
                    if (GameState.gameBoard[k][j].getId()==11){
                        if(getCard(out, GameState.gameBoard[k][j] )!= null){
                            Card a = getCard(out, GameState.gameBoard[k][j]);
                            a.getBigCard().setAttack(cnt);
                            a.getBigCard().setHealth(cnt+3);
                            
                       }
                        BasicCommands.setUnitHealth(out, GameState.gameBoard[k][j], cnt+3);
                        BasicCommands.setUnitAttack(out, GameState.gameBoard[k][j], cnt);
                    }
                }
            }
        }
    }
    public static void deployUnit(ActorRef out, int AIHandPosition) {
        boolean isSpecial = specialAbilityUnit(out, AIHandPosition);
        if (!isSpecial) {
            Random rand = new Random();
            int tilex = rand.nextInt(9);
            int tiley = rand.nextInt(5);
            boolean isPermissible = false;
            // if
            // (AIOpponent.AICardNumbers.contains(GameState.gameBoard[tilex][tiley].getId())
            // || GameState.gameBoard[7][2].getId() == 200) {
            // throw warning same player!
            // if (!AIFirstUnit) {
            // highlightPossibleMoves(out, 7, 2);
            // } else {
            highlightUnitTiles(out);
            // }
            System.out.println("inside deploy unit " + permissiblePositions.size());
            while (!isPermissible) {
                for (int i = 0; i < permissiblePositions.size(); i++) {
                    if (permissiblePositions.get(i).get(0) == tilex
                            && permissiblePositions.get(i).get(1) == tiley) {
                        isPermissible = true;
                        break;

                    }
                }
                if (!isPermissible) {
                    tilex = rand.nextInt(9);
                    tiley = rand.nextInt(5);
                }
            }
            // }
            // Tile tile = BasicObjectBuilders.loadTile(tilex, tiley);
            System.out.println("Load tile for AI");
            // TODO
            // ifselected card is a unit then deploy
            // if selected card is a spell and can deployed
            if (GameState.gameBoard[tilex][tiley] == null) {
                // TODO move this to AI player
                loadNewUnit(out, tilex, tiley, AIHandPosition);
                // } else {
                // // TODO throw not enough mana
                // System.out.println("not enough mana");

                // }

            } else if (GameState.gameBoard[tilex][tiley] != null) {
                // TODO
                // if
                // if selected card is a unit then throw error
                // if selected card is spell and current unit is player unit perform action
                // if selected card is spell and current unit is opposite perform action

            }
        }
    }

    public static void moveUnit(ActorRef out) {
        Random rand = new Random();
        int tilex = rand.nextInt(9);
        int tiley = rand.nextInt(5);
        boolean isPermissible = false;
        System.out.println("inside AI unit " + permissiblePositions.size());
        while (!isPermissible) {
            for (int i = 0; i < permissiblePositions.size(); i++) {
                if (permissiblePositions.get(i).get(0) == tilex
                        && permissiblePositions.get(i).get(1) == tiley) {
                    isPermissible = true;
                    break;

                }
            }
            if (!isPermissible) {
                tilex = rand.nextInt(9);
                tiley = rand.nextInt(5);
            }
        }
        Tile tile = BasicObjectBuilders.loadTile(tilex, tiley);
        System.out.println("Load tile for AI to move " + currentUnit + " " + GameState.gameBoard[tilex][tiley]);
        if (GameState.gameBoard[tilex][tiley] == null) {
            // move unit
            Unit unit = currentUnit;
            boolean unitMoved = false;
            for (int i = 0; i < permissiblePositions.size(); i++) {
                if (permissiblePositions.get(i).get(0) == tilex && permissiblePositions.get(i).get(1) == tiley) {
                    BasicCommands.moveUnitToTile(out, unit, tile);
                    unit.setPositionByTile(tile);
                    GameState.isUnitSelected = false;
                    GameState.gameBoard[selectedUnitX][selectedUnitY] = null;
                    GameState.gameBoard[tilex][tiley] = unit;
                    currentUnit = unit;
                    System.out.println(
                            "inside tile for AI to move " + currentUnit + " " + GameState.gameBoard[tilex][tiley]);
                    selectedUnitX = tilex;
                    selectedUnitY = tiley;
                    unitMoved = true;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!unitMoved) {
                selectedUnitX = -1;
                selectedUnitY = -1;

            }
        }
        // else if (GameState.gameBoard[tilex][tiley] != null) {
        // // opposite player attack
        // attackUnit(out, tilex, tiley);
        // try {
        // Thread.sleep(2000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
    }

    public static String getUnit(Card card) {
        ObjectMapper mapper = new ObjectMapper();
        for (String eachUnit : AllUnits) {
            try {
                String json = readFileAsString(eachUnit);
                Unit unit = mapper.readValue(json, Unit.class);
                System.out.println("Inside getunit " + unit + " " + unit.getId() + " " + card + " " + card.getId());
                if (unit.getId() == card.getId()) {
                    return eachUnit;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void highlightPossibleMoves(ActorRef out, int x, int y) {
        // System.out.println(GameState.gameBoard[x][y].getId());
        if (y - 2 >= 0) {
            highlightTile(out, x, y - 2);
            System.out.println("inside x,y-2");
        }

        if (y - 1 >= 0) {
            highlightTile(out, x, y - 1);
            System.out.println("inside x,y-1");
        }

        if (y + 1 <= 4) {
            highlightTile(out, x, y + 1);
            System.out.println("inside x,y+1");

        }
        if (y + 2 <= 4) {
            highlightTile(out, x, y + 2);
            System.out.println("inside x,y+2");

        }

        if (x - 2 >= 0) {
            highlightTile(out, x - 2, y);
            System.out.println("inside x-2,y");
        }

        if (x - 1 >= 0) {
            highlightTile(out, x - 1, y);
            System.out.println("inside x-1,y");
        }

        if (x + 1 <= 8) {
            highlightTile(out, x + 1, y);
            System.out.println("inside x+1,y");
        }

        if (x + 2 <= 8) {
            highlightTile(out, x + 2, y);
            System.out.println("inside x+2,y");
        }

        if (x - 1 >= 0 && y - 1 >= 0) {
            highlightTile(out, x - 1, y - 1);
            System.out.println("inside x-1,y-1");

        }

        if (x + 1 <= 8 && y - 1 >= 0) {
            highlightTile(out, x + 1, y - 1);
            System.out.println("inside x+1,y-1");
        }

        if (x - 1 >= 0 && y + 1 <= 4) {
            highlightTile(out, x - 1, y + 1);
            System.out.println("inside x-1,y+1");
        }

        if (x + 1 <= 8 && y + 1 <= 4) {
            highlightTile(out, x + 1, y + 1);
            System.out.println("inside x+1,y+1");

        }
    }

    public static void highlightTile(ActorRef out, int x, int y) {
        // int mode; 1003
        // System.out.println(GameState.gameBoard[x][y].getId() +" this b id");
        if (GameState.gameBoard[x][y] != null) {
            System.out.println(GameState.gameBoard[x][y].getId() + " this pos");
            if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[x][y].getId())
                    || GameState.gameBoard[x][y].getId() == 200) {
                // do not hughlight and add in highlighted list!
                System.out.println("yes AI contains");
                System.out.println(GameState.gameBoard[x][y].getId() + " this id");
            } else {
                // highlight in red and add in list
                System.out.println("inside highlightTile!");
                // Tile tile = BasicObjectBuilders.loadTile(x, y);
                BasicCommands.addPlayer1Notification(out, "Highlight enemy", 2);
                // BasicCommands.drawTile(out, tile, 2);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ArrayList<Integer> eachPermissiblePosition = new ArrayList<Integer>();
                eachPermissiblePosition.add(x);
                eachPermissiblePosition.add(y);
                permissiblePositions.add(eachPermissiblePosition);
                // try {Thread.sleep(2000);} catch (InterruptedException e)
                // {e.printStackTrace();}
            }
        } else {
            // add in list
            // Tile tile = BasicObjectBuilders.loadTile(x, y);
            BasicCommands.addPlayer1Notification(out, "Highlight", 2);
            // BasicCommands.drawTile(out, tile, 1);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<Integer> eachPermissiblePosition = new ArrayList<Integer>();
            eachPermissiblePosition.add(x);
            eachPermissiblePosition.add(y);
            permissiblePositions.add(eachPermissiblePosition);
            // try {Thread.sleep(2000);} catch (InterruptedException e)
            // {e.printStackTrace();}
            // highlight in white!
        }
    }

    public static void highlightUnitTiles(ActorRef out) {
        ///////////////// the code to know all possible tiles that can be
        ///////////////// deployed//////////
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 5; j++) {
                // j,i-1
                if (i - 1 >= 0) {
                    if (GameState.gameBoard[i - 1][j] != null) {
                        System.out.println("inside [i - 1][j] first " + GameState.gameBoard[i - 1][j].getId() + " "
                                + AIOpponent.AICardNumbers);
                        if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[i - 1][j].getId())
                                || GameState.gameBoard[i - 1][j].getId() == 200) {
                            System.out.println("inside [i - 1][j]");
                            // addUnitPositions(i + 1, j + 1, i, j);
                            ArrayList<Integer> newPosition = new ArrayList<Integer>();
                            newPosition.add(i);
                            newPosition.add(j);
                            permissiblePositions.add(newPosition);
                            System.out.println("done unit positions");
                        }
                    }
                }
                if (i + 1 <= 8) {
                    if (GameState.gameBoard[i + 1][j] != null) {
                        System.out.println("inside [i + 1][j] first " + GameState.gameBoard[i + 1][j].getId() + " "
                                + AIOpponent.AICardNumbers);
                        if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[i + 1][j].getId())
                                || GameState.gameBoard[i + 1][j].getId() == 200) {
                            System.out.println("inside [i + 1][j]");
                            // addUnitPositions(i + 1, j, i, j);
                            ArrayList<Integer> newPosition = new ArrayList<Integer>();
                            newPosition.add(i);
                            newPosition.add(j);
                            permissiblePositions.add(newPosition);
                            System.out.println("done unit positions");
                        }
                    }
                }
                if (j - 1 >= 0) {
                    if (GameState.gameBoard[i][j - 1] != null) {
                        System.out.println("inside [i ][j - 1] first " + GameState.gameBoard[i][j - 1].getId() + " "
                                + AIOpponent.AICardNumbers);
                        if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[i][j - 1].getId())
                                || GameState.gameBoard[i][j - 1].getId() == 200) {
                            // addUnitPositions(i, j - 1, i, j);
                            System.out.println("inside [i][j - 1]");
                            ArrayList<Integer> newPosition = new ArrayList<Integer>();
                            newPosition.add(i);
                            newPosition.add(j);
                            permissiblePositions.add(newPosition);
                            System.out.println("done unit positions");
                        }
                    }
                }
                if (j + 1 <= 4) {
                    if (GameState.gameBoard[i][j + 1] != null) {
                        System.out.println("inside [i ][j + 1] first " + GameState.gameBoard[i][j + 1].getId() + " "
                                + AIOpponent.AICardNumbers);
                        if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[i][j + 1].getId())
                                || GameState.gameBoard[i][j + 1].getId() == 200) {
                            System.out.println("inside [i][j + 1]");
                            // addUnitPositions(i, j + 1, i, j);
                            ArrayList<Integer> newPosition = new ArrayList<Integer>();
                            newPosition.add(i);
                            newPosition.add(j);
                            permissiblePositions.add(newPosition);
                            System.out.println("done unit positions");
                        }
                    }
                }
                if (j - 1 >= 0 && i - 1 >= 0) {
                    if (GameState.gameBoard[i - 1][j - 1] != null) {
                        System.out.println(
                                "inside [i - 1 ][j - 1] first " + GameState.gameBoard[i - 1][j - 1].getId() + " "
                                        + AIOpponent.AICardNumbers);
                        if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[i - 1][j - 1].getId())
                                || GameState.gameBoard[i - 1][j - 1].getId() == 200) {
                            System.out.println("inside [i - 1][j-1]");
                            // addUnitPositions(i - 1, j - 1, i, j);
                            ArrayList<Integer> newPosition = new ArrayList<Integer>();
                            newPosition.add(i);
                            newPosition.add(j);
                            permissiblePositions.add(newPosition);
                            System.out.println("done unit positions");
                        }
                    }
                }
                if (j + 1 <= 4 && i - 1 >= 0) {
                    if (GameState.gameBoard[i - 1][j + 1] != null) {
                        System.out.println(
                                "inside [i - 1][j - 1] first " + GameState.gameBoard[i - 1][j + 1].getId() + " "
                                        + AIOpponent.AICardNumbers);
                        if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[i - 1][j + 1].getId())
                                || GameState.gameBoard[i - 1][j + 1].getId() == 200) {
                            System.out.println("inside [i - 1][j + 1]");
                            // addUnitPositions(i - 1, j + 1, i, j);
                            ArrayList<Integer> newPosition = new ArrayList<Integer>();
                            newPosition.add(i);
                            newPosition.add(j);
                            permissiblePositions.add(newPosition);
                            System.out.println("done unit positions");
                        }
                    }

                }
                if (j - 1 >= 0 && i + 1 <= 8) {
                    if (GameState.gameBoard[i + 1][j - 1] != null) {
                        System.out.println(
                                "inside [i + 1][j - 1] first " + GameState.gameBoard[i + 1][j - 1].getId() + " "
                                        + AIOpponent.AICardNumbers);
                        if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[i + 1][j - 1].getId())
                                || GameState.gameBoard[i + 1][j - 1].getId() == 200) {
                            System.out.println("inside [i + 1][j -1]");
                            // addUnitPositions(i + 1, j - 1, i, j);
                            ArrayList<Integer> newPosition = new ArrayList<Integer>();
                            newPosition.add(i);
                            newPosition.add(j);
                            permissiblePositions.add(newPosition);
                            System.out.println("done unit positions");
                        }
                    }
                }
                if (j + 1 <= 4 && i + 1 <= 8) {
                    if (GameState.gameBoard[i + 1][j + 1] != null) {
                        System.out.println(
                                "inside [i + 1 ][j + 1] first " + GameState.gameBoard[i + 1][j + 1].getId() + " "
                                        + AIOpponent.AICardNumbers);
                        if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[i + 1][j + 1].getId())
                                || GameState.gameBoard[i + 1][j + 1].getId() == 200) {
                            System.out.println("inside [i+1][j+1]");
                            // addUnitPositions(i + 1, j + 1, i, j);
                            ArrayList<Integer> newPosition = new ArrayList<Integer>();
                            newPosition.add(i);
                            newPosition.add(j);
                            permissiblePositions.add(newPosition);
                            System.out.println("done unit positions");
                        }
                    }

                }
            }
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void loadMana(ActorRef out, int newManaValue) {

        while (AIOpponent.AIStats.getMana() <= 9 && newManaValue > 0 && newManaValue <= 9) {
            AIOpponent.AIStats.setMana(AIOpponent.AIStats.getMana() + 1);
            BasicCommands.addPlayer1Notification(out, "setPlayer2Mana", 1);
            BasicCommands.setPlayer2Mana(out, AIOpponent.AIStats);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            newManaValue = newManaValue - 1;
        }
        AIOpponent.addNewCard(out);
    }

    public static void deleteCard(ActorRef out, int AIHandPosition) {

        System.out.println("inside delete card!");

        BasicCommands.addPlayer1Notification(out, "deleteCard", 2);
        // BasicCommands.deleteCard(out, AIHandPosition);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AIHand[AIHandPosition] = null;

        // move cards
    }

    public static void addNewCard(ActorRef out) {
        System.out.println("adding a new hand");
        int position = 0;

        for (int i = 0; i < AIHand.length; i++) {

            if (AIHand[i] == null) {
                break;
            } else {
                position += 1;
            }
        }
        System.out.println(
                position + " this is position" + currentCardNumber + " " + AICards + " " + AICards[currentCardNumber]);
        if (position < 6) {
            BasicCommands.addPlayer1Notification(out, AICards[currentCardNumber], 2);
            AIHand[position] = AICards[currentCardNumber];
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentCardNumber += 1;
            System.out.println("done add new card");
        } else if (position >= 6) {
            // TODO write logic loose cards
            currentCardNumber+=1;
        }
    }

    public static boolean specialAbilityUnit(ActorRef out, int AIHandPosition) {
        if (GameState.currentCard.getId() == 10) {
            Random rand = new Random();
            int tilex = rand.nextInt(9);
            int tiley = rand.nextInt(5);
            boolean isPermissible = false;
            System.out.println("inside specialAbilityUnit planar" + permissiblePositions.size());
            while (!isPermissible) {
                if (GameState.gameBoard[tilex][tiley] == null) {
                    isPermissible = true;
                    break;

                }
                if (!isPermissible) {
                    tilex = rand.nextInt(9);
                    tiley = rand.nextInt(5);
                }
            }
            loadNewUnit(out, tilex, tiley, AIHandPosition);
            System.out.println("Load tile for AI specialAbilityUnit");
            return true;
        }
        return false;
    }

    public static void loadNewUnit(ActorRef out, int tilex, int tiley, int AIHandPosition) {
        try {
            Tile tile = BasicObjectBuilders.loadTile(tilex, tiley);
            BasicCommands.addPlayer1Notification(out, "Deploying AI Unit", 2);
            System.out.println(GameState.currentCard.getId() + " this is the id of the unit");
            Unit unit = BasicObjectBuilders.loadUnit(getUnit(GameState.currentCard),
                    GameState.currentCard.getId(), Unit.class);
            System.out.println("test before posn");
            unit.setPositionByTile(tile);
            System.out.println("after set");
            BasicCommands.drawUnit(out, unit, tile);
            System.out.println("after draw unit");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GameState.gameBoard[tilex][tiley] = unit;
            currentUnit = unit;
            System.out.println(
                    "inside deploy unit " + GameState.gameBoard[tilex][tiley] + " " + unit + " " + currentUnit);

            // Set unit Health
            BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
            BasicCommands.setUnitHealth(out, unit, GameState.currentCard.getBigCard().getHealth());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // setUnitAttack
            BasicCommands.addPlayer1Notification(out, "setUnitAttack", 2);
            BasicCommands.setUnitAttack(out, unit, GameState.currentCard.getBigCard().getAttack());
            AIOpponentCards.add(GameState.currentCard);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // delete card
            AIOpponent.deleteCard(out, AIHandPosition);
            if (GameState.currentCard.getId() == 3) {
                AIOpponent.addNewCard(out);
                HumanPlayer.addNewCard(out);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO Reduce mana here;
        for (int i = 0; i < GameState.currentCard.getManacost(); i++) {
            AIOpponent.AIStats.setMana(AIOpponent.AIStats.getMana() - 1);
            BasicCommands.setPlayer2Mana(out, AIOpponent.AIStats);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}