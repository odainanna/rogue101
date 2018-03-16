package inf101.v18.rogue101.objects;

import inf101.v18.gfx.textmode.AnsiColors;

public class DustBunny extends Rabbit {

  @Override
  public String getName() {
    return "dustbunny";
  }

  @Override
  public String getSymbol() {
    return AnsiColors.ANSI_BLUE + super.getSymbol() + AnsiColors.ANSI_RESET;
  }

  @Override
  public Class getFoodPreference() {
    return Dust.class;
  }

}
