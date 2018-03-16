package inf101.v18.rogue101.objects;

import inf101.v18.rogue101.game.IGame;

/**
 * An active item is an item with a doTurn-method
 *
 * @author oda
 */
public interface IActiveItem extends IItem {

  /**
   * Method for items that do something each round
   */
  void doTurn(IGame game);

}
