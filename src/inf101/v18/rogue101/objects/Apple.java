package inf101.v18.rogue101.objects;

import inf101.v18.gfx.gfxmode.ITurtle;
import inf101.v18.rogue101.game.IGame;
import javafx.scene.paint.Color;

public class Apple implements IEdible {

  private int hp = getMaxHealth();

  public void doTurn(IGame game) {
    hp = Math.min(hp + 1, getMaxHealth());
  }

  @Override
  public boolean draw(ITurtle painter, double w, double h) {
    painter.save();
    painter.turn(75);
    double size = ((double) hp + getMaxHealth()) / (2.0 * getMaxHealth());
    double appleHeight = size * h * .5;
    double appleWidth = size * h * .8;
    painter.jump(-appleHeight / 6);
    painter.shape().ellipse().width(appleHeight).height(appleWidth).fillPaint(Color.RED.darker())
        .fill();
    painter.jump(appleHeight / 2);
    painter.setInk(Color.FORESTGREEN);
    for (int i = -1; i < 2; i++) {
      painter.save();
      painter.turn(20 * i);
      painter.draw(appleHeight / 4);
      painter.restore();
    }
    painter.restore();
    return true;
  }

  @Override
  public int getCurrentHealth() {
    return hp;
  }

  @Override
  public int getDefence() {
    return 0;
  }

  @Override
  public double getHealthStatus() {
    return getCurrentHealth() / getMaxHealth();
  }

  @Override
  public int getMaxHealth() {
    return 5;
  }

  @Override
  public String getName() {
    return "apple";
  }

  @Override
  public int getSize() {
    return 2;
  }

  @Override
  public String getSymbol() {
    return "A";
  }

  @Override
  public String getArticle() {
    return "an";
  }

  @Override
  public int handleDamage(IGame game, IItem source, int amount) {
    hp -= amount;
    if (hp < 0) {
      // we're all eaten!
      hp = -1;
    }
    return amount;
  }

}
