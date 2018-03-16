package inf101.v18.rogue101.objects;

import inf101.v18.gfx.gfxmode.ITurtle;
import inf101.v18.grid.GridDirection;
import inf101.v18.grid.ILocation;
import inf101.v18.rogue101.game.IGame;
import java.util.Stack;
import javafx.scene.input.KeyCode;

public class Player implements IPlayer {

  /* Health points */
  private int hp = getMaxHealth();

  /* Stack of items the player has picked up */
  private Stack<IItem> inventory = new Stack<>();

  @Override
  public void keyPressed(IGame game, KeyCode key) {
    switch (key) {
      case UP:
        tryToMove(game, GridDirection.NORTH);
        break;
      case DOWN:
        tryToMove(game, GridDirection.SOUTH);
        break;
      case LEFT:
        tryToMove(game, GridDirection.WEST);
        break;
      case RIGHT:
        tryToMove(game, GridDirection.EAST);
        break;
      case P:
        pickUp(game);
        break;
      case D:
        drop(game);
        break;
    }
    showStatus(game);
  }

  /**
   * Pick up the first item in the list of items at the current location.
   *
   * @param game Game
   */
  public void pickUp(IGame game) {
    try {
      IItem item = game.getLocalItems().get(0);
      IItem picked = game.pickUp(item);
      if (picked != null) {
        inventory.add(picked);
        game.displayMessage("Picked up " + item.getArticle() + " " + item.getName());
      }
    } catch (IndexOutOfBoundsException e) {
      game.displayMessage("Nothing to pick up.");
    }
  }

  /**
   * Drop the item that was last picked up.
   *
   * @param game Game
   */
  public void drop(IGame game) {
    if (inventory.isEmpty()) {
      game.displayMessage("Nothing to drop");
    } else {
      IItem item = inventory.peek();
      boolean wasDropped = game.drop(item);
      if (wasDropped) {
        inventory.pop();
        game.displayMessage("Dropped " + item.getArticle() + " " + item.getName());
      }
    }
  }

  private void tryToMove(IGame game, GridDirection dir) {
    ILocation targetLoc = game.getLocation(dir);
    if (game.canGo(dir)) {
      game.move(dir);
    } else if (game.getMap().hasActors(targetLoc)) {
      IActor target = game.getMap().getActors(targetLoc).get(0);
      game.attack(dir, target);
    } else if (game.getMap().hasDoor(targetLoc)) {
      if (hasKey()) {
        game.nextRoom();
      } else {
        game.displayMessage("The door is locked.");
      }
    } else {
      game.useMovePoint();
      game.displayMessage("Ouch");
    }
  }

  private boolean hasKey() {
    for (IItem item : inventory) {
      if (item instanceof Key) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int getAttack() {
    return 100;
  }

  @Override
  public int getDamage() {
    return 100;
  }

  @Override
  public boolean draw(ITurtle painter, double w, double h) {
    return false;
  }

  @Override
  public int getCurrentHealth() {
    return hp;
  }

  @Override
  public int getDefence() {
    return 100;
  }

  @Override
  public int getMaxHealth() {
    return 1000;
  }

  @Override
  public String getName() {
    return "player";
  }

  @Override
  public int getSize() {
    return Integer.MAX_VALUE;
  }

  @Override
  public String getSymbol() {
    return "\uD83D\uDC66";
  }

  @Override
  public int handleDamage(IGame game, IItem source, int amount) {
    hp -= amount;
    return amount;
  }

  private void showStatus(IGame game) {
    game.displayStatus("HP: " + Integer.toString(hp));
  }
}
