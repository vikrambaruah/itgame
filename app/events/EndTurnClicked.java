package events;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;

import actions.HumanPlayer;
import actions.AIOpponent;
import akka.actor.ActorRef;
import structures.GameState;
import commands.BasicCommands;
import structures.basic.Unit;
// import structures.basic.Card;
// import structures.basic.EffectAnimation;
// import structures.basic.Player;
// import structures.basic.Tile;
// import structures.basic.Unit;
// import structures.basic.UnitAnimationType;
// import utils.BasicObjectBuilders;
// import utils.StaticConfFiles;
// import java.nio.file.Files;
// import java.nio.file.Paths;
// import java.util.*;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case
 * the end-turn button.
 * 
 * {
 * messageType = “endTurnClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		TileClicked.movedUnits = new ArrayList<Unit>();
		TileClicked.attackedUnits = new ArrayList<Unit>();

		if (GameState.currentPlayer == "ai") {
			GameState.currentPlayer = "human";
			BasicCommands.addPlayer1Notification(out, "Your turn", 2);
			GameState.currentRound += 1;
			resetAIMana(out);
			int newManaValue = GameState.currentRound + 1;
			System.out.println(newManaValue + " this is new mana value");

			while (HumanPlayer.humanStats.getMana() <= 20 && newManaValue > 0) {
				HumanPlayer.humanStats.setMana(HumanPlayer.humanStats.getMana() + 1);
				BasicCommands.addPlayer1Notification(out, "setPlayer1Mana", 1);
				BasicCommands.setPlayer1Mana(out, HumanPlayer.humanStats);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				newManaValue = newManaValue - 1;
			}
			HumanPlayer.addNewCard(out);
			GameState.isPlayerTurnCompleted = false;
		}
		// Call AI Logic()
		else if (GameState.currentPlayer == "human") {
			GameState.currentPlayer = "ai";
			resetPlayerMana(out);
			AIOpponent.AIlogic(out);
			System.out.println("go to AI class");
			AIOpponent.permissiblePositions.clear();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			processEvent(out, gameState, message);
		}
	}

	public static void resetPlayerMana(ActorRef out) {
        HumanPlayer.humanStats.setMana(0);
        BasicCommands.addPlayer1Notification(out, "reset Player1Mana", 1);
        BasicCommands.setPlayer1Mana(out, HumanPlayer.humanStats);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

	public static void resetAIMana(ActorRef out) {
        AIOpponent.AIStats.setMana(0);
        BasicCommands.addPlayer1Notification(out, "reset Player2Mana", 1);
        BasicCommands.setPlayer2Mana(out, AIOpponent.AIStats);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
