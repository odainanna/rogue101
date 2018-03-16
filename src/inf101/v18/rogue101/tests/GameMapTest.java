package inf101.v18.rogue101.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import inf101.v18.grid.ILocation;
import inf101.v18.rogue101.objects.Apple;
import inf101.v18.rogue101.objects.Carrot;
import inf101.v18.rogue101.examples.ExampleItem;
import inf101.v18.rogue101.map.GameMap;
import inf101.v18.rogue101.objects.Dust;
import inf101.v18.rogue101.objects.IItem;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class GameMapTest {

  @Test
  void testSortedAdd() {
    GameMap gameMap = new GameMap(20, 20);
    ILocation location = gameMap.getLocation(10, 10);
    //add a lot of items to a location on the map
    for (int i = 0; i < 1000; i++) {
      gameMap.add(location, randomItem());
    }
    //test that they are all correctly sorted
    List<IItem> items = gameMap.getItems(location);
    for (int i = 1; i < items.size(); i++) {
      assertTrue((items.get(i - 1).compareTo(gameMap.getItems(location).get(i))) >= 0);
    }
  }

  private IItem randomItem() {
    Random random = new Random();
    final int DUST = 0;
    final int CARROT = 1;
    final int APPLE = 2;
    int toMake = random.nextInt(4);
    switch (toMake) {
      case DUST:
        return new Dust();
      case CARROT:
        return new Carrot();
      case APPLE:
        return new Apple();
      default:
        return new ExampleItem();
    }
  }

  @Test
  void testIllegalCentreArgument() {
    GameMap gameMap = new GameMap(10, 10);
    try {
      List<ILocation> neighbours = gameMap.getNeighbourhood(null, 2);
      fail("Failed to throw exception.");
    } catch (IllegalArgumentException ignored) {
    }
  }

  @Test
  void testIllegalDistanceArgument() {
    int width = 10;
    int height = 10;
    GameMap gameMap = new GameMap(10, 10);
    ILocation centre = gameMap.getLocation(width / 2, height / 2);
    try {
      List<ILocation> neighbours = gameMap.getNeighbourhood(centre, -2);
      fail("Failed to throw exception.");
    } catch (IllegalArgumentException ignored) {
    }
  }

  @Test
  void testGetClosestNeighbours() {
    GameMap gameMap = new GameMap(10, 10);
    ILocation centre = gameMap.getLocation(5, 5);
    int distance = 1;
    List<ILocation> neighbours = gameMap.getNeighbourhood(centre, distance);

    assertEquals(neighbours.size(), 8);
    for (ILocation neighbour : neighbours) {
      int d = centre.gridDistanceTo(neighbour);
      assertTrue(centre.gridDistanceTo(neighbour) == distance);
    }
  }

  @Test
  void testNeighboursOutsideOfMap() {
    int width = 10;
    int height = 10;
    GameMap gameMap = new GameMap(width, height);
    ILocation centre = gameMap.getLocation(width / 2, height / 2);
    int distance = 7; //outside of the map on all sides
    List<ILocation> neighbours = gameMap.getNeighbourhood(centre, distance);

    assertEquals(neighbours.size(), (width * height) - 1); //enough neighbours?

    for (ILocation neighbour : neighbours) {
      int d = centre.gridDistanceTo(neighbour);
      assertTrue(centre.gridDistanceTo(neighbour) <= distance);
    }
  }

  private void assertEquals(int size, int i) {
  }

  @Test
  void testNeighboursWhenCentreIsInCorner() {
    GameMap gameMap = new GameMap(10, 10);
    ILocation centre = gameMap.getLocation(0, 0);
    int distance = 2;
    List<ILocation> neighbours = gameMap.getNeighbourhood(centre, distance);
    assertEquals(8, neighbours.size());
    for (ILocation neighbour : neighbours) {
      assertTrue(centre.gridDistanceTo(neighbour) <= distance);
    }
  }

  @Test
  void testNeighbourSortingByDistanceToCentre() {
    GameMap gameMap = new GameMap(10, 10);
    ILocation centre = gameMap.getLocation(5, 5);
    int distance = 5;
    List<ILocation> neighbours = gameMap.getNeighbourhood(centre, distance);
    for (int i = 1; i < neighbours.size(); i++) {
      assertTrue(
          neighbours.get(i - 1).gridDistanceTo(centre) <= neighbours.get(i).gridDistanceTo(centre));
    }
  }

}
