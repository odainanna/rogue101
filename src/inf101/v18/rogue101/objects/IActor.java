package inf101.v18.rogue101.objects;

import inf101.v18.rogue101.game.IGame;

/**
 * An actor is an IItem that can also do something, either controlled by the computer (INonPlayer)
 * or the user (IPlayer).
 *
 * @author anya
 */
public interface IActor extends IItem {

  /**
   * @return This actor's attack score (used against an item's {@link #getDefence()} score to see if
   * an attack is successful)
   */
  int getAttack();

  /**
   * @return The damage this actor deals on a successful attack (used together with {@link
   * #handleDamage(inf101.v18.rogue101.game.IGame, IItem, int)} on the target)
   */
  int getDamage();

  /**
   * @return The distance this actor can see when looking for nearby items (used together with {@link
   * #getVisible()})
   */
  default int getDistanceVision(IGame game) {
    return 3;
  }

}
