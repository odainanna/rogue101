package inf101.v18.rogue101.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import inf101.v18.grid.GridDirection;
import inf101.v18.grid.ILocation;
import inf101.v18.rogue101.game.Game;
import inf101.v18.rogue101.map.IMapView;
import inf101.v18.rogue101.objects.IItem;
import inf101.v18.rogue101.objects.IPlayer;
import inf101.v18.rogue101.objects.Player;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;


class PlayerTest {


  public static String TEST_MAP_1 = "40 5\n" //
      + "########################################\n" //
      + "#...... ..C.R ......R.R......... ..R...#\n" //
      + "#.R@R...... ..........RC..R...... ... .#\n" //
      + "#... ..R........R......R. R........R.RR#\n" //
      + "########################################\n" //
      ;

  public static String TEST_MAP_2 = "40 5\n" //
      + "########################################\n" //
      + "#...... ..C.R ......R.R......... ..R...#\n" //
      + "#..@....... ..........RC..R...... ... .#\n" //
      + "#... ..R........R......R. R........R.RR#\n" //
      + "########################################\n" //
      ;

  @Test
  void testMovingNorth() {
    // new game with our test map
    Game game = new Game(TEST_MAP_1);
    // pick (3,2) as the "current" position; this is where the player is on the
    // test map, so it'll set up the player and return it
    IPlayer player = (IPlayer) game.setCurrent(3, 2);

    // find players location
    ILocation loc = game.getLocation();
    // press "UP" key
    player.keyPressed(game, KeyCode.UP);
    // see that we moved north
    assertEquals(loc.go(GridDirection.NORTH), game.getLocation());
  }

  @Test
  void testMovingSouth() {
    Game game = new Game(TEST_MAP_1);
    IPlayer player = (IPlayer) game.setCurrent(3, 2);
    ILocation loc = game.getLocation();
    player.keyPressed(game, KeyCode.DOWN);
    assertEquals(loc.go(GridDirection.SOUTH), game.getLocation());
  }

  @Test
  void testMovingEast() {
    Game game = new Game(TEST_MAP_2);
    IPlayer player = (IPlayer) game.setCurrent(3, 2);
    ILocation loc = game.getLocation();
    player.keyPressed(game, KeyCode.RIGHT);
    assertEquals(loc.go(GridDirection.EAST), game.getLocation());
  }

  @Test
  void testMovingWest() {
    Game game = new Game(TEST_MAP_2);
    IPlayer player = (IPlayer) game.setCurrent(3, 2);
    ILocation loc = game.getLocation();
    player.keyPressed(game, KeyCode.LEFT);
    assertEquals(loc.go(GridDirection.WEST), game.getLocation());
  }

  @Test
  void testMovingIntoWall() {
    Game game = new Game(TEST_MAP_2);
    IPlayer player = (IPlayer) game.setCurrent(3, 2);
    ILocation loc = game.getLocation();
    player.keyPressed(game, KeyCode.UP); //move up
    player.keyPressed(game, KeyCode.UP); //try to walk through wall
    assertEquals(loc.go(GridDirection.NORTH), game.getLocation()); //check that it didn't work
  }

  @Test
  void testPickupAndDrop() {
    Game game = new Game(TEST_MAP_1);
    IPlayer player = (Player) game.setCurrent(3, 2);
    ILocation loc = game.getLocation();
    IMapView map = game.getMap();
    IItem carrot = game.createItem("C"); //create a carrot
    game.getMap().add(loc, carrot); //place the carrot at player's location
    player.keyPressed(game, KeyCode.P); //pick up the carrot
    try {
      IItem item = map.getItems(loc).get(0); //look for carrot at the current location
      fail("Failed to throw exception. if the carrot was successfully picked up there "
          + "should be no items at the location and an exception should be thrown");
    } catch (IndexOutOfBoundsException e) {
      //if the carrot was successfully picked up there is no item at the location
      //this should cause an IndexOutOfBoundsException
    }
    player.keyPressed(game, KeyCode.D); //try to drop the carrot
    assertTrue(map.getItems(loc).get(0) == carrot); //check that the carrot is back
  }

}
