package events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import actions.AIOpponent;
import actions.GameBoard;
import actions.HumanPlayer;

import java.nio.file.Files;
import java.nio.file.Paths;

import akka.actor.ActorRef;
import akka.actor.typed.ActorRef.ActorRefOps;
import akka.protobufv3.internal.Api;
import structures.GameState;
import commands.BasicCommands;
import scala.concurrent.java8.FuturesConvertersImpl.P;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import java.util.*;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case a tile. The event returns the x (horizontal) and y (vertical) indices of
 * the tile that was clicked. Tile indices start at 1.
 * 
 * { messageType = “tileClicked” tilex = <x index of the tile> tiley = <y index
 * of the tile> }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor {
	int storedX = 0;
	int storedY = 0;
	// public static boolean highlighted[][] = new boolean[100][100];


	// public static int highlightedTiles[][] = new int[12][2];

public static ArrayList<ArrayList<Integer>> permissiblePositions = new ArrayList<ArrayList<Integer>>();
// public static DummyTell altTell = null;

public static ArrayList<Unit> movedUnits = new ArrayList<Unit> ();
public static ArrayList<Unit> attackedUnits = new ArrayList<Unit>();


	
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
    
    
	public static String readFileAsString(String file)throws Exception
    {
        return new String(Files.readAllBytes(Paths.get(file)));
    }

	public String getUnit(Card card) {
		ObjectMapper mapper = new ObjectMapper();
		for (String eachUnit : AllUnits) {
			try{
				String json = readFileAsString(eachUnit);
				Unit unit=mapper.readValue(json,Unit.class);
				if(unit.getId() == card.getId()) {
					return eachUnit;
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}

	public void highlightTile(ActorRef out,int x, int y){
		// int mode;
		// System.out.println(GameState.gameBoard[x][y].getId() +" this b id"); 
		if(GameState.gameBoard[x][y] != null){

			System.out.println(GameState.gameBoard[x][y].getId() +" this pos"); 
			if(HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[x][y].getId()) || GameState.gameBoard[x][y].getId() == 100 ){
					//do not hughlight and add in highlighted list!
					System.out.println("yes human contains");
					System.out.println(GameState.gameBoard[x][y].getId() +" this id"); 
			}else  { 
				   // highlight in red and add in list
				System.out.println("inside here!");
				Tile tile = BasicObjectBuilders.loadTile(x, y);
				BasicCommands.addPlayer1Notification(out, "Highlight enemy", 2);
		        BasicCommands.drawTile(out, tile, 2);
				try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
				ArrayList<Integer> eachPermissiblePosition = new ArrayList<Integer>();
				eachPermissiblePosition.add(x);
				eachPermissiblePosition.add(y);
				permissiblePositions.add(eachPermissiblePosition);
		        // try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}


			}
		}else { 
			   //add in list
				Tile tile = BasicObjectBuilders.loadTile(x, y);
				BasicCommands.addPlayer1Notification(out, "Highlight", 2);
		        BasicCommands.drawTile(out, tile, 1);
				try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
				ArrayList<Integer> eachPermissiblePosition = new ArrayList<Integer>();
				eachPermissiblePosition.add(x);
				eachPermissiblePosition.add(y);
				permissiblePositions.add(eachPermissiblePosition);
		        // try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			   //highlight in white!
		}
	}



	public void highlightPossibleMoves(ActorRef out, int x, int y){
		System.out.println(GameState.gameBoard[x][y].getId());
		if(y-2 >= 0){
			highlightTile(out,x,y-2);
			System.out.println("isnide x,y-2");
		}

		if(y-1>=0){
			highlightTile(out,x,y-1);
			System.out.println("isnide x,y-1");
		}

		if(y+1 <= 4){
			highlightTile(out,x,y+1);
			System.out.println("isnide x,y+1");
		
		}
		if(y+2 <=4){
			highlightTile(out,x,y+2);
			System.out.println("isnide x,y+2");

		}

		if(x-2 >=0){
			highlightTile(out,x-2,y);
			System.out.println("isnide x-2,y");
		}

		if(x-1>=0){
			    highlightTile(out,x-1,y);
				System.out.println("isnide x-1,y");
		}

		if(x+1<=8){
				highlightTile(out,x+1,y);
				System.out.println("isnide x+1,y");
		}
		
		if(x+2<=8){
				highlightTile(out,x+2,y);
				System.out.println("isnide x+2,y");
		}

		
		if(x-1>=0 && y-1 >=0){
			    highlightTile(out,x-1,y-1);
				System.out.println("isnide x-1,y-1");
				
		}

		if(x+1<=8 && y-1 >=0){
				highlightTile(out,x+1,y-1);
				System.out.println("isnide x+1,y-1");
		}

		if(x-1>=0 && y+1 <=4){
			   highlightTile(out,x-1,y+1);
			   System.out.println("isnide x-1,y+1");
		}

		if(x+1<=8 && y+1 <=4){
				highlightTile(out,x+1,y+1);
				System.out.println("isnide x+1,y+1");

		}
		
			
	

		// setUnitSelected = true
		
	
	}

	public static void removeHighlight(ActorRef out){
		for(int i =0;i<permissiblePositions.size();i++){
				Tile tile = BasicObjectBuilders.loadTile(permissiblePositions.get(i).get(0), permissiblePositions.get(i).get(1));
				BasicCommands.addPlayer1Notification(out, "drawTile Highlight", 2);
				BasicCommands.drawTile(out, tile, 0);
				try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}

	public void moveUnit(){
			
	}




	public static int getUnitAttack(ActorRef out, Unit unit){
		System.out.println(unit.getId() + " we are attack");
		if(unit.getId() == 100){
			return 2;
		}else if(unit.getId() == 200){
			return 2;
		}else {
			Card curCard = HumanPlayer.getCard(out, unit);
			System.out.println(curCard.getBigCard().getHealth() + " this is unit health!");
			return curCard.getBigCard().getAttack();
		}
	}


	public static int getUnitHealth(ActorRef out, Unit unit){
		System.out.println(unit.getId() + " we are health");
		if(unit.getId() == 100){
			// HumanPlayer.humanStats.getHealth();
			return HumanPlayer.humanStats.getHealth();
		}else if(unit.getId() == 200){
			return AIOpponent.AIStats.getHealth();
		}
		else{
			System.out.println("befor cur card");
			Card curCard = AIOpponent.getCard(out, unit);
			if(curCard == null){
				curCard = HumanPlayer.getCard(out, unit);
			}

			System.out.println("this is curCard!" + curCard.getId());
			// System.out.println(curCard.getBigCard().getHealth() + " this is unit health!");
			return curCard.getBigCard().getHealth();
		}
	

	}


	public static boolean canCounterAttack(ActorRef out) { 
		//TODO check if any adjacent tile is provoke then if the counter attack is a provoke then attack else don't attack
		//check if the opposite unit is an adjacent unit
		return true;
	}


	public static void checkProvoke(){

	}




	public static void counterAttack(ActorRef out, int x, int y){
				System.out.println("inside counter attack!");
				if(canCounterAttack(out)){
					// int reattack =	 AIOpponent.getUnitAttackg(out,GameState.gameBoard[x][y]);
					int reattack = AIOpponent.getUnitAttack(out, GameState.gameBoard[x][y]);
					System.out.println(reattack + " this is re-attack!");
					BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Attack]", 2);
					BasicCommands.playUnitAnimation(out, GameState.gameBoard[x][y], UnitAnimationType.attack);
					try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
					//run a for loop for current health
					int reAttackCurrenttHealth = getUnitHealth(out,GameState.currentunit);
					if(GameState.currentunit.getId() == 100){
						reAttackCurrenttHealth = HumanPlayer.humanStats.getHealth();
					}
					for(int i = 0; i < HumanPlayer.humanCards.size();i++){
						if(HumanPlayer.humanCards.get(i).getId() == GameState.currentunit.getId()){
							reAttackCurrenttHealth = HumanPlayer.humanCards.get(i).getBigCard().getHealth();
							break;
						}
					}
					
					int reAttackUpdatedHealth = reAttackCurrenttHealth - reattack;

					System.out.println(reAttackCurrenttHealth + " this is reattach current health");

					System.out.println(reAttackUpdatedHealth + " this is reattack updated health");

					//TODO
					//if opposite player is human
					if(GameState.currentunit.getId() == 100){

						System.out.println("opposite player human");
						HumanPlayer.humanStats.setHealth(reAttackUpdatedHealth);
						BasicCommands.setPlayer2Health(out, HumanPlayer.humanStats);
						BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
		    			BasicCommands.setUnitHealth(out, GameState.currentunit, reAttackUpdatedHealth);
						try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
						//death animation of avatar -- game ends here
						BasicCommands.addPlayer1Notification(out, "Game Ended! Player won!", 3);
						BasicCommands.playUnitAnimation(out, GameState.currentunit, UnitAnimationType.death);
				
						try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
					}else {

					System.out.println("opposite not human player but human unit");

					for(int i = 0; i < HumanPlayer.humanCards.size();i++){
						System.out.println("inside herre " + HumanPlayer.humanCards.get(i).getId() + " " + GameState.currentunit.getId());
						if(HumanPlayer.humanCards.get(i).getId() == GameState.currentunit.getId()){
							System.out.println("check point 1!");
							if(reAttackUpdatedHealth <=0){
								System.out.println("check point 2!");
								//will die
								HumanPlayer.humanCards.get(i).getBigCard().setHealth(0);
								BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
		    					BasicCommands.setUnitHealth(out, GameState.currentunit, 0);
								try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
								BasicCommands.addPlayer1Notification(out, "Unit dead!", 3);
								BasicCommands.playUnitAnimation(out, GameState.currentunit, UnitAnimationType.death);
								//TODORemove unit

								try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
								return;
							}
							System.out.println("check point 3!");
							System.out.println(HumanPlayer.humanCards.get(i).getBigCard().getHealth() + " this is here health");
							HumanPlayer.humanCards.get(i).getBigCard().setHealth(reAttackUpdatedHealth);
							BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
		    				BasicCommands.setUnitHealth(out, GameState.currentunit, reAttackUpdatedHealth);
							try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
						
					}
				}						


			}
					//else do this

		}

	}

	public void attackUnit(ActorRef out, int x, int y){

		// if()

		if(HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[x][y].getId()) || GameState.gameBoard[x][y].getId() == 100){
			//throw warning same player!
			System.out.println("this is a human thingy");
			
			
			//remove selected unit
		}
		else { 
			// int id = GameState.gameBoard[x][y].getId();
			int attack =	getUnitAttack(out,GameState.currentunit);
			System.out.println(attack + " this is attack!");
			BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Attack]", 2);
			BasicCommands.playUnitAnimation(out, GameState.currentunit, UnitAnimationType.attack);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

			int currentHealth = getUnitHealth(out,GameState.gameBoard[x][y]);
			
			int updatedHealth = currentHealth - attack;

			
			if(GameState.gameBoard[x][y].getId() == 200){
				if(updatedHealth <=0){
					//death animation of the opposite player
					AIOpponent.AIStats.setHealth(0);
					BasicCommands.setPlayer2Health(out, AIOpponent.AIStats);
					BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
		    		BasicCommands.setUnitHealth(out, GameState.gameBoard[x][y], updatedHealth);
					try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
					//death animation of avatar -- game ends here
					BasicCommands.addPlayer1Notification(out, "Game Ended! Player won!", 3);
					BasicCommands.playUnitAnimation(out, GameState.gameBoard[x][y], UnitAnimationType.death);
				
					try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
					return;

				}
				AIOpponent.AIStats.setHealth(updatedHealth);
				BasicCommands.setPlayer2Health(out, AIOpponent.AIStats);
				BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
		    	BasicCommands.setUnitHealth(out, GameState.gameBoard[x][y], updatedHealth);
				try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

				//counter attack
				//method to check if can be counter attacked
				//If can be counter attacked then attack.. else don't attack.. once counter attacked if died then play death animation
				//counterAttack

				counterAttack(out, x, y);

			}else { 
				for(int i = 0; i < AIOpponent.AIOpponentCards.size();i++){
				if(AIOpponent.AIOpponentCards.get(i).getId() == GameState.gameBoard[x][y].getId()){
					if(updatedHealth <=0){
						//will die
						AIOpponent.AIOpponentCards.get(i).getBigCard().setHealth(0);
						BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
		    			BasicCommands.setUnitHealth(out, GameState.gameBoard[x][y], 0);
						try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
						BasicCommands.addPlayer1Notification(out, "Unit dead!", 3);
						BasicCommands.playUnitAnimation(out, GameState.gameBoard[x][y], UnitAnimationType.death);
						//TODORemove unit 

						try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
						return;
					}
					AIOpponent.AIOpponentCards.get(i).getBigCard().setHealth(updatedHealth);
					BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
		    		BasicCommands.setUnitHealth(out, GameState.gameBoard[x][y], updatedHealth);
					try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
					counterAttack(out, x, y);
				}
				}
			}
			// System.out.println(updatedHealth + " " + currentHealth + " " + attack);

		}

	}


	public static void applyAbilities(ActorRef out){

		if(GameState.currentCard.getId() == 1){
			//AZureherald - increase avatar health of 3. till 20.
		}else if(GameState.currentCard.getId() == 2){

		} else if ( GameState.currentCard.getId() ==  3) {
				//Azurite lion - allow one or more attack;
		} else if (GameState.currentCard.getId() ==  4 ) {

				
		}else if (GameState.currentCard.getId() ==  6 ) { 
			// can attack any enemy on the board
		}


	}

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
	

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		Tile tile = BasicObjectBuilders.loadTile(tilex, tiley);

		System.out.println(GameState.isPlayerTurnCompleted);

		System.out.println(GameState.gameBoard[tilex][tiley]);


		if(GameState.isPlayerTurnCompleted != true){

		/*
		 * New game logic
		 */

		if(GameState.isUnitSelected){	
			if(GameState.gameBoard[tilex][tiley] == null){
				    //move unit
			        Unit unit = GameState.currentunit;

					boolean unitMoved = false; 
					//TODO
					//check if provoked then cannot move 

					for(int i =0;i<permissiblePositions.size();i++){
						if(permissiblePositions.get(i).get(0) == tilex &&permissiblePositions.get(i).get(1) == tiley){
							    //TODO Checkprovoke if provoked cant move.


								
								if(movedUnits.contains(unit) || attackedUnits.contains(unit)){
									BasicCommands.addPlayer1Notification(out, "Unit already moved", 2);
									try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
									removeHighlight(out);
									System.out.println("unit already moved this turn");
									return;
								}

							
								BasicCommands.moveUnitToTile(out, unit, tile);
								unit.setPositionByTile(tile);
								GameState.isUnitSelected = false;
								GameState.gameBoard[GameState.selectedUnitX][GameState.selectedUnitY] = null;
								GameState.gameBoard[tilex][tiley] = unit; 
								removeHighlight(out);
								unitMoved = true;
				    			//unhighligh all tiles.
								try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
								GameState.previousMove = "move";
								movedUnits.add(unit);
								// GameState.isPlayerTurnCompleted = true;

						}
					}

					if(!unitMoved){
						GameState.isUnitSelected = false;
						GameState.selectedUnitX = -1;
						GameState.selectedUnitY = -1;
						// GameState.gameBoard[GameState.selectedUnitX][GameState.selectedUnitY] = null;
						// GameState.gameBoard[tilex][tiley] = unit; 
						removeHighlight(out);
						try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
					}
			
			}else if(GameState.gameBoard[tilex][tiley] != null) { 

				//TODO current unit is two steps ahead? then move one adjacent tile then attack

				
				attackUnit(out,tilex,tiley); 
				GameState.isUnitSelected = false;
				
				try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
				GameState.previousMove = "attack";
				// GameState.isPlayerTurnCompleted = true;

				removeHighlight(out);
			
			}
		}else if(GameState.isCardSelected){
			System.out.println("inside here 123");

			if(GameState.currentCard.getBigCard().getAttack() == -1 && GameState.currentCard.getBigCard().getHealth() == -1 && GameState.gameBoard[tilex][tiley] != null ){
				if(GameState.gameBoard[tilex][tiley] == null){
					//throw error
				}else  { 
				int manaToReduce = GameState.currentCard.getManacost();
				if(HumanPlayer.humanStats.getMana() >= manaToReduce){
						                    // GameState.isSpellSelected = true;
                    // it is a spell
                    System.out.println("this is a spell shit!!");
                    if(GameState.currentCard.getId() == 1002){
                        if( GameState.gameBoard[tilex][tiley] != null){
                                if(HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[tilex][tiley].getId()) || GameState.gameBoard[tilex][tiley].getId() == 100 ){

                                }else  { 
                                    int currentHealth = 0;
									if(GameState.gameBoard[tilex][tiley].getId() == 200){
										currentHealth = AIOpponent.AIStats.getHealth();
										System.out.println("inisde AI health");
									}else { 
									for(int i = 0; i < HumanPlayer.humanCards.size();i++){
										if(HumanPlayer.humanCards.get(i).getId() == GameState.gameBoard[tilex][tiley].getId()){
											currentHealth = HumanPlayer.humanCards.get(i).getBigCard().getHealth();
											System.out.println("not here");
										}
									}
            
									}

                                    int updatedHealth = currentHealth - 2;
									System.out.println(updatedHealth + " " + currentHealth);
                                    BasicCommands.addPlayer1Notification(out, StaticConfFiles.f1_inmolation, 2);
                                    EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation);
                                    BasicCommands.playEffectAnimation(out, ef, tile);
                                    try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
                                    //////////////////
                                    if(GameState.gameBoard[tilex][tiley].getId() == 200){
                                        AIOpponent.AIStats.setHealth(updatedHealth);
										System.out.println("inside updating health");
										BasicCommands.setUnitHealth(out, GameState.gameBoard[tilex][tiley], updatedHealth);
										BasicCommands.setPlayer2Health(out, AIOpponent.AIStats);
										try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                                    }else { 
                                        System.out.println(updatedHealth + " " + currentHealth + " " + 2);
                                        BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
                                        BasicCommands.setUnitHealth(out, GameState.gameBoard[tilex][tiley], updatedHealth);
									for(int i = 0; i < HumanPlayer.humanCards.size();i++){
										if(HumanPlayer.humanCards.get(i).getId() == GameState.gameBoard[tilex][tiley].getId()){
											HumanPlayer.humanCards.get(i).getBigCard().setHealth(updatedHealth);
										}
									}
                                        try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

                                    }

                                    
                                }

                        }


                        
                    }else if (GameState.currentCard.getId() == 1001){
                            
							if(HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[tilex][tiley].getId()) || GameState.gameBoard[tilex][tiley].getId() == 100){
									int maximumHealth = getUnitHealth(out,GameState.gameBoard[tilex][tiley]);
									int healthCanBeIncreased = 5;
									int actualHealth;
									int currentHealth = 0;
									int updatedHealth = 0;
									for(int i =0; i < HumanPlayer.humanCards.size();i++){
										if(HumanPlayer.humanCards.get(i).getId() == GameState.gameBoard[tilex][tiley].getId()){
											currentHealth = HumanPlayer.humanCards.get(i).getBigCard().getHealth();
											actualHealth = HumanPlayer.humanCards.get(i).getBigCard().getHealth();
										}
									}

									for(int i = currentHealth ; i < maximumHealth ;i++){
										if( healthCanBeIncreased > 0 ){
											healthCanBeIncreased -= 1;
											updatedHealth += 1;
										}
									}
									System.out.println(maximumHealth + " this is current health");
									// if(maximumHealth)
									// GameState.gameBoard[tilex][tiley]
									BasicCommands.setUnitHealth(out, GameState.gameBoard[tilex][tiley], currentHealth + updatedHealth);
                                    try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
							}else { 
								System.out.println("you cannot use it for opposite player value");
							}

                    }

						// checkForAbilitiesAndMakeitHappen
                        GameState.isCardSelected = false;
                        GameState.isUnitSelected = false;
                        GameState.currentCard = null;


						
					
						HumanPlayer.deleteCard(out);
						removeHighlight(out);
						//decrease mana
					for(int i = 0; i< manaToReduce;i++){
						HumanPlayer.humanStats.setMana(HumanPlayer.humanStats.getMana()-1);
						BasicCommands.setPlayer1Mana(out, HumanPlayer.humanStats);
						try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

					}
					
					// for(int i =0)
						// remove all highlights

				
					
				}else  { 
					System.out.println("not enough mana");
				}

				}


			}
			
		    else if(GameState.gameBoard[tilex][tiley] == null){

				
				int manaToReduce = GameState.currentCard.getManacost();
				if(HumanPlayer.humanStats.getMana() >= manaToReduce){
					

			    ObjectMapper mapper = new ObjectMapper();
		        try{

    				String json = readFileAsString(HumanPlayer.PlayerHand[GameState.selectedHandPosition]);
    				Card card=mapper.readValue(json,Card.class);
					GameState.currentCard = card;

				if(GameState.currentCard.getBigCard().getAttack() == -1 && GameState.currentCard.getBigCard().getHealth() == -1){
						//through error as it is a spell
				}else { 
				
				System.out.println("inside here not a spell! its a unit");

				boolean isDeployed = false;

				for(int i =0; i <permissiblePositions.size();i++){
					if(permissiblePositions.get(i).get(0) == tilex && permissiblePositions.get(i).get(1 ) == tiley){
				BasicCommands.addPlayer1Notification(out, "Deploying Unit", 2);

				System.out.println(GameState.currentCard.getId() + " this is the id of the unit");
				HumanPlayer.humanCards.add(GameState.currentCard);
			    Unit unit = BasicObjectBuilders.loadUnit(getUnit(GameState.currentCard), GameState.currentCard.getId(), Unit.class);
			    unit.setPositionByTile(tile); 
			    BasicCommands.drawUnit(out, unit, tile);
				try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
				GameState.gameBoard[tilex][tiley] = unit;

				
				//Set unit Health
			    BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
		        BasicCommands.setUnitHealth(out, unit,GameState.currentCard.getBigCard().getHealth());
		        try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

						
			    // setUnitAttack
		        BasicCommands.addPlayer1Notification(out, "setUnitAttack", 2);
		        BasicCommands.setUnitAttack(out, unit, GameState.currentCard.getBigCard().getAttack());
		        try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
				


				//delete card
				HumanPlayer.deleteCard(out);
				
				//Add new Card! if new card is null then don't add card // add player card over true!

				// GameState.isPlayerTurnCompleted = true;
				GameState.isCardSelected = false;
				GameState.isUnitSelected = false;
				GameState.currentCard = null;
				removeHighlight(out);
				isDeployed = true;
				for(int j = 0; j< manaToReduce;j++){
					HumanPlayer.humanStats.setMana(HumanPlayer.humanStats.getMana()-1);
					BasicCommands.setPlayer1Mana(out, HumanPlayer.humanStats);
					try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

				}
				GameState.previousMove = "deploy";
				// GameState.isPlayerTurnCompleted = true;				
				}
				}

				if(!isDeployed){
					//throw error
					System.out.println("cannot deploy unit here!");

					BasicCommands.addPlayer1Notification(out, GameState.currentCard+" highlight remove", 2);
					BasicCommands.drawCard(out, GameState.currentCard, GameState.selectedHandPosition, 0);
					GameState.isCardSelected = false;
					GameState.isUnitSelected = false;
					GameState.currentCard = null;
					removeHighlight(out);
				}
				
			   

				}

    			}
    			catch(Exception e){
    				e.printStackTrace();
    			}

				



				}else  { 
					//TODO tthrow not enough mana
					System.out.println("not enough mana");

					BasicCommands.addPlayer1Notification(out, GameState.currentCard+" highlight remove", 2);
					BasicCommands.drawCard(out, GameState.currentCard, GameState.selectedHandPosition, 0);
					GameState.isCardSelected = false;
					GameState.isUnitSelected = false;
					GameState.currentCard = null;
					removeHighlight(out);
				}

	
			 }else if(GameState.gameBoard[tilex][tiley] != null){
					//TODO
					//This condition never comes
					//if selected card is a unit then throw error
					// if selected card is spell and current unit is player unit perform action
					// if selected card is spell and current unit is opposite perform action
			 }
		}else if(GameState.gameBoard[tilex][tiley] != null){


				
		if(HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[tilex][tiley].getId()) || GameState.gameBoard[tilex][tiley].getId() == 100){
				//throw warning same player!

				//TODO if provoked.. highlight only provoked tiles else do below
				highlightPossibleMoves(out,tilex,tiley);

			    GameState.isUnitSelected = true;
				GameState.currentunit = GameState.gameBoard[tilex][tiley];
				GameState.selectedUnitX = tilex;
				GameState.selectedUnitY = tiley;
		
			
			//remove selected unit
		}else { 
			//TODO throw error - you cannot select opposite player

		}

				
		}else {
					//do nothing
		}


		//Setting up mana

		// int currentPlayerMana = HumanPlayer.humanStats.getMana();



		

		// end turn// 
		}

	
		else  { 
			//throw error
			System.out.println("not human player turn");
		}	
	}


}


