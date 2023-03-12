package structures;

import actions.*;
import java.util.*;

import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	public static boolean gameInitalised = false;

	public static int currentGameRound = 0;


	// public static  boolean something = false;

	public static String currentPlayer = "human";

	public static  boolean gameEnded = false;

    public  static boolean isCardSelected = false;
   
    public static Card currentCard = null;

	public static boolean isUnitSelected = false;

	public static int currentRound = 0;	
	
	public static int AIcurrentRound = 0;	

	//if player moves the unit - move
	// if player attacks - value - attack
	// if player deploy unit - deploy

	public static String previousMove = "nomove";


	public static Unit currentunit = null;

	public static boolean isSpellSelected = false;

	public static int selectedUnitX = -1;
	public static int selectedUnitY = -1;

	public static boolean isPlayerTurnCompleted = false;

	public static int selectedHandPosition = -1;

	public static Unit[][] gameBoard = new Unit[9][5];

	public static int playerXPosn = 1;

	public static int playerYPosn = 2;

	public static int aiXPosn = 7;
	
	public static int aiYPosn = 2;

	public static boolean checkForPlayerTurnCompleted(){
		return true;
	}
}
