package inf101.v18.rogue101.objects;

import inf101.v18.rogue101.game.IGame;

/**
 * The monster rabbit differs from the rabbit in that it has unlimited distance vision, and relentlessly
 * pursues the players instead of edible items.
 */
public class MonsterRabbit extends Rabbit {

  private final int IMPROVEMENT_FACTOR = 10;

  @Override
  public IItem getTarget(IGame game) {
    return game.findClosestVisible(IPlayer.class);
  }

  @Override
  public String getSymbol() {
    return "🐰";
  }

  @Override
  public int getAttack() {
    return super.getAttack() * IMPROVEMENT_FACTOR;
  }

  @Override
  public int getMaxHealth() {
    return super.getMaxHealth() * IMPROVEMENT_FACTOR;
  }

  @Override
  public String getName() {
    return "monster" + super.getName();
  }

  @Override
  public int getDistanceVision(IGame game) {
    return game.getMap().getLocation(0, 0)
        .gridDistanceTo(game.getMap().getLocation(game.getWidth() - 1, game.getHeight() - 1));
  }
}
