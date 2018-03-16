package inf101.v18.rogue101.objects;

import inf101.v18.grid.GridDirection;
import inf101.v18.grid.ILocation;
import inf101.v18.rogue101.game.IGame;
import java.util.Comparator;
import java.util.List;

public class Rabbit implements INonPlayer {

  private int food = 0;
  private int hp = getMaxHealth();

  @Override
  public void doTurn(IGame game) {

    if (food == 0) {
      hp--;
    } else {
      food--;
    }
    if (hp < 1) {
      return;
    }

    if (eatIfPossible(game)) {
      return;
    }

    if (attackIfPossible(game)) {
      return;
    }
    moveIfPossible(game);
  }

  private void moveIfPossible(IGame game) {
    List<GridDirection> possibleMoves = game.getPossibleMoves();
    if (possibleMoves.isEmpty()) {
      return;
    }
    ILocation target = game.getMap().getLocation(getTarget(game));
    if (target != null) {
      possibleMoves
          .sort(Comparator.comparingInt(dir -> game.getLocation(dir).stepDistanceTo(target)));
    }
    game.move(possibleMoves.get(0));
  }

  public IItem getTarget(IGame game) {
    return game.findClosestVisible(Carrot.class);
  }

  /**
   * Attack player if the player is a cardinal neighbour
   *
   * @return true if player was attacked, otherwise false
   */
  private boolean attackIfPossible(IGame game) {
    for (GridDirection dir : GridDirection.FOUR_DIRECTIONS) {
      IPlayer target = game.getMap().find(game.getLocation(dir), IPlayer.class);
      if (target != null) {
        game.attack(dir, target);
        return true;
      }
    }
    return false;
  }

  /**
   * Eat if there is food at the current location
   *
   * @return true if there was food, otherwise false
   */
  public boolean eatIfPossible(IGame game) {
    IItem edible = game.getMap().find(game.getLocation(), getFoodPreference());
    if (edible != null) {
      int eaten = edible.handleDamage(game, this, 5);
      food += eaten;
      System.out.println("found " + edible.getName() + " !" + "\n"
          + "ate " + edible.getName() + " worth " + eaten + "!" + "\n");
      game.displayMessage(
          getName() + " eats " + edible.getArticle() + " "
              + edible.getName());
      return true;
    }
    return false;
  }

  @Override
  public int getAttack() {
    return 10;
  }

  @Override
  public int getCurrentHealth() {
    return hp;
  }

  @Override
  public int getDamage() {
    return 10;
  }

  @Override
  public int getDefence() {
    return 100;
  }

  @Override
  public int getMaxHealth() {
    return 100;
  }

  @Override
  public String getName() {
    return "rabbit";
  }

  @Override
  public int getSize() {
    return Integer.MAX_VALUE;
  }

  @Override
  public String getSymbol() {
    return hp > 0 ? "🐇" : "¤";
  }

  @Override
  public int handleDamage(IGame game, IItem source, int amount) {
    hp -= amount;
    return amount;
  }

  public Class getFoodPreference() {
    return IEdible.class;
  }

}
