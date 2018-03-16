package inf101.v18.rogue101.events;

import inf101.v18.rogue101.game.IGame;
import inf101.v18.rogue101.objects.IItem;

public class GameOverEvent<T> implements IEvent<T> {

  @Override
  public T getData() {
    return null;
  }

  @Override
  public void setData(T value) {

  }

  @Override
  public String getEventName() {
    return "Game Over";
  }

  @Override
  public IGame getGame() {
    return null;
  }

  @Override
  public IItem getSource() {
    return null;
  }

  @Override
  public IItem getTarget() {
    return null;
  }
}
