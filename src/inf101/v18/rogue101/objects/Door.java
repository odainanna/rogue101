package inf101.v18.rogue101.objects;

import inf101.v18.gfx.textmode.BlocksAndBoxes;

public class Door extends Wall {

  @Override
  public String getSymbol() {
    return BlocksAndBoxes.BLOCK_HALF;
  }

  @Override
  public String getName() {
    return "door";
  }

}
