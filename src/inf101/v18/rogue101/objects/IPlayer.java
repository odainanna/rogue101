package inf101.v18.rogue101.objects;

import inf101.v18.rogue101.game.IGame;
import javafx.scene.input.KeyCode;

public interface IPlayer extends IActor {

  /**
   * Send key presses from the human player to the player object. <p> The player object should
   * interpret the key presses, and then perform its moves or whatever, according to the game's
   * rules and the player's instructions. <p> This IPlayer will be the game's current actor ({@link
   * IGame#getActor()}) and be at {@link IGame#getLocation()}, when this method is called. <p> This
   * method may be called many times in a single turn; the turn ends {@link #keyPressed(IGame,
   * KeyCode)} returns and the player has used its movement points (e.g., by calling {@link
   * IGame#move(inf101.v18.grid.GridDirection)}).
   *
   * @param game Game, for interacting with the world
   */
  void keyPressed(IGame game, KeyCode key);

  /**
   * Try to pick up the first item in the list of items at the player's location.
   *
   * @param game Game
   */
  void pickUp(IGame game);

  /**
   * Try to drop the first item in the players stack of items at the player's location.
   *
   * @param game Game
   */
  void drop(IGame game);


}
