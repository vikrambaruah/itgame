package events;


import com.fasterxml.jackson.databind.JsonNode;

import actions.HumanPlayer;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import utils.BasicObjectBuilders;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * 
 * { 
 *   messageType = “cardClicked”
 *   position = <hand index position [1-6]>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor{



	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		int handPosition = message.get("position").asInt();

		System.out.println(handPosition + " this is handPositon!");

		if(GameState.isCardSelected){
			System.out.println("iniside here! 123");
			// drawCard [1] Highlight
			BasicCommands.addPlayer1Notification(out, GameState.currentCard+" highlight remove", 2);
			BasicCommands.drawCard(out, GameState.currentCard, GameState.selectedHandPosition, 0);
			TileClicked.removeHighlight(out);

		    // BasicCommands.drawCard(out, entropic_decay, 1, 0);
		    // try {Thread.sleep(000);} catch (InterruptedException e) {e.printStackTrace();}

		}
		
		//change to static later!
		// gameState.currentCard = HumanPlayer.PlayerHand[handPosition];
			ObjectMapper mapper = new ObjectMapper();
		    try{

    				String json = readFileAsString(HumanPlayer.PlayerHand[handPosition]);
    				Card card=mapper.readValue(json,Card.class);
					GameState.currentCard = card;

				if(GameState.currentCard.getBigCard().getAttack() == -1 && GameState.currentCard.getBigCard().getHealth() == -1){
					// GameState.isSpellSelected = true;
					// it is a spell
					if(GameState.currentCard.getId() == 1002){
						//TODO //highlight all possible enemy units
						//reduce damage to enemy units
						for(int i =0;i <9;i++){
							for(int j = 0; j <5;j++){

							if(GameState.gameBoard[i][j] != null){

						

								if(HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[i][j].getId()) || GameState.gameBoard[i][j].getId() == 100 ){
									///don nothing
								}else { 
									Tile tile = BasicObjectBuilders.loadTile(i, j);
								 	BasicCommands.addPlayer1Notification(out, "Highlight", 2);
		       					 	BasicCommands.drawTile(out, tile, 2);
									ArrayList<Integer> newPosition = new ArrayList<Integer>();
									newPosition.add(i);
									newPosition.add(j);
									TileClicked.permissiblePositions.add(newPosition);
								}
							}

							}
						}
						
					}
					if(GameState.currentCard.getId() == 1001){
						//TODO highlight 
						for(int i =0;i <9;i++){
							for(int j = 0; j <5;j++){

							if(GameState.gameBoard[i][j] != null){
								if(HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[i][j].getId()) || GameState.gameBoard[i][j].getId() == 100 ){
								
									Tile tile = BasicObjectBuilders.loadTile(i, j);
								 	BasicCommands.addPlayer1Notification(out, "Highlight", 2);
		       					 	BasicCommands.drawTile(out, tile, 1);
									ArrayList<Integer> newPosition = new ArrayList<Integer>();
									newPosition.add(i);
									newPosition.add(j);
									TileClicked.permissiblePositions.add(newPosition);
								}
							}

							}
						}
						
					}
				}else { 
					// GameState.isUnitSelected = true;
	
					System.out.println("this is here asdfsaffas!!");

					//TODO if the selected card is IronCliff - then highlight all possible tiles


					/////////////////the code to know all possible tiles that can be deployed//////////

					for(int i =0;i<9;i++){
						for(int j = 0; j < 5;j++){
							//j,i-1
							if(i-1>=0){
								if(GameState.gameBoard[i-1][j] != null){

								if(HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[i-1][j].getId()) || GameState.gameBoard[i-1][j].getId() == 100){
									if(GameState.gameBoard[i][j] == null){
									Tile tile = BasicObjectBuilders.loadTile(i, j);
								 	BasicCommands.addPlayer1Notification(out, "Highlight", 2);
		       					 	BasicCommands.drawTile(out, tile, 1);									
									ArrayList<Integer> newPosition = new ArrayList<Integer>();
									newPosition.add(i);
									newPosition.add(j);
									TileClicked.permissiblePositions.add(newPosition);

									}

								}
								}
							}
							//j,i+1
							if(i+1 <=8){
								if(GameState.gameBoard[i+1][j] != null){

									if(HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[i+1][j].getId()) || GameState.gameBoard[i+1][j].getId() == 100){
																
									if(GameState.gameBoard[i][j] == null){
										System.out.println(i+1 + " this is i");
										System.out.println(j + " this is j");
										System.out.println(GameState.gameBoard[i+1][j] );	
										Tile tile = BasicObjectBuilders.loadTile(i, j);
								 		BasicCommands.addPlayer1Notification(out, "Highlight", 2);
		       					 		BasicCommands.drawTile(out, tile, 1);									
										ArrayList<Integer> newPosition = new ArrayList<Integer>();
										newPosition.add(i);
										newPosition.add(j);
										TileClicked.permissiblePositions.add(newPosition);
									}
									}
							}
							}

							//j-1,y
							if(j-1 >=0){
								if(GameState.gameBoard[i][j-1] != null){
							
									if(HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[i][j-1].getId()) || GameState.gameBoard[i][j-1].getId() == 100){

									
									if(GameState.gameBoard[i][j] == null){
									Tile tile = BasicObjectBuilders.loadTile(i, j);
								 	BasicCommands.addPlayer1Notification(out, "Highlight", 2);
		       					 	BasicCommands.drawTile(out, tile, 1);									
									ArrayList<Integer> newPosition = new ArrayList<Integer>();
									newPosition.add(i);
									newPosition.add(j);
									TileClicked.permissiblePositions.add(newPosition);

									}
									}
							}
							}

							//j+1,i

							if (j+1<=4) {
								if(GameState.gameBoard[i][j+1]!= null){
								
									if(HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[i][j+1].getId()) || GameState.gameBoard[i][j+1].getId() == 100){
									if(GameState.gameBoard[i][j] == null){
									System.out.println(i + " this is i");
									System.out.println(j+1 + " this is j");	
									Tile tile = BasicObjectBuilders.loadTile(i, j);
								 	BasicCommands.addPlayer1Notification(out, "Highlight", 2);
		       					 	BasicCommands.drawTile(out, tile, 1);									
									ArrayList<Integer> newPosition = new ArrayList<Integer>();
									newPosition.add(i);
									newPosition.add(j);
									TileClicked.permissiblePositions.add(newPosition);

									}
									}
							}

							}

							//j-1,i-1

							if(j-1 >=0 && i-1>=0){
								if(GameState.gameBoard[i-1][j-1] != null){
											
									if(HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[i-1][j-1].getId()) || GameState.gameBoard[i-1][j-1].getId() == 100){
									if(GameState.gameBoard[i][j] == null){
									Tile tile = BasicObjectBuilders.loadTile(i, j);
								 	BasicCommands.addPlayer1Notification(out, "Highlight", 2);
		       					 	BasicCommands.drawTile(out, tile, 1);									
									ArrayList<Integer> newPosition = new ArrayList<Integer>();
									newPosition.add(i);
									newPosition.add(j);
									TileClicked.permissiblePositions.add(newPosition);

									}
									}
							}

							}

							//j+1,i-1

							if (j+1<=4 && i-1 >=0)
							{
								if(GameState.gameBoard[i-1][j+1] != null){
						
									if(HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[i-1][j+1].getId()) || GameState.gameBoard[i-1][j+1].getId() == 100){
									if(GameState.gameBoard[i][j] == null){
									Tile tile = BasicObjectBuilders.loadTile(i, j);
								 	BasicCommands.addPlayer1Notification(out, "Highlight", 2);
		       					 	BasicCommands.drawTile(out, tile, 1);									
									ArrayList<Integer> newPosition = new ArrayList<Integer>();
									newPosition.add(i);
									newPosition.add(j);
									TileClicked.permissiblePositions.add(newPosition);

									}
									}
							}

							 }

							//j-1,i+1

							if( j-1 >=0 && i+1 <=8) { 
								if(GameState.gameBoard[i+1][j-1] != null){
					
									if(HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[i+1][j-1].getId()) || GameState.gameBoard[i+1][j-1].getId() == 100){
									if(GameState.gameBoard[i][j] == null){
									Tile tile = BasicObjectBuilders.loadTile(i, j);
								 	BasicCommands.addPlayer1Notification(out, "Highlight", 2);
		       					 	BasicCommands.drawTile(out, tile, 1);									
									ArrayList<Integer> newPosition = new ArrayList<Integer>();
									newPosition.add(i);
									newPosition.add(j);
									TileClicked.permissiblePositions.add(newPosition);

									}
									}
							}							
							}



							//j+1,i+1
							if(j+1 <= 4 && i+1 <=8){
								if(GameState.gameBoard[i+1][j+1] != null){
																	
									if(HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[i+1][j+1].getId()) || GameState.gameBoard[i+1][j+1].getId() == 100){
									if(GameState.gameBoard[i][j] == null){
									Tile tile = BasicObjectBuilders.loadTile(i, j);
								 	BasicCommands.addPlayer1Notification(out, "Highlight", 2);
		       					 	BasicCommands.drawTile(out, tile, 1);									
									ArrayList<Integer> newPosition = new ArrayList<Integer>();
									newPosition.add(i);
									newPosition.add(j);
									TileClicked.permissiblePositions.add(newPosition);

									}
									}
							}

							}
						}
					}
				}

    			}
    			catch(Exception e){
    				e.printStackTrace();
    			}


		GameState.selectedHandPosition = handPosition;
		
		//change to static later!s
		GameState.isCardSelected = true;

		// drawCard [1] Highlight
		BasicCommands.addPlayer1Notification(out, GameState.currentCard+" Highlight", 2);
		BasicCommands.drawCard(out, GameState.currentCard, handPosition, 1);
		
	

		
		
		
	}
	public static String readFileAsString(String file)throws Exception
    {
        return new String(Files.readAllBytes(Paths.get(file)));
    }


}
