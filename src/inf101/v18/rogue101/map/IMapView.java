package inf101.v18.rogue101.map;

import inf101.v18.grid.GridDirection;
import inf101.v18.grid.IArea;
import inf101.v18.grid.ILocation;
import inf101.v18.rogue101.game.IllegalMoveException;
import inf101.v18.rogue101.objects.Door;
import inf101.v18.rogue101.objects.IActor;
import inf101.v18.rogue101.objects.IItem;
import inf101.v18.rogue101.objects.Wall;
import java.util.List;

public interface IMapView {

  /**
   * Add an item to the map.
   *
   * @param loc A location
   * @param item the item
   */
  void add(ILocation loc, IItem item);

  /**
   * Check if it's legal for an IActor to go into the given location
   *
   * @param to A location
   * @return True if the location isn't already occupied
   */
  boolean canGo(ILocation to);

  /**
   * Check if it's legal for an IActor to go in the given direction from the given location
   *
   * @param from Current location
   * @param dir Direction we want to move in
   * @return True if the next location exists and isn't occupied
   */
  boolean canGo(ILocation from, GridDirection dir);

  /**
   * Get all IActors at the given location <p> The returned list either can't be modified, or
   * modifying it won't affect the map.
   *
   * @return A list of actors
   */
  List<IActor> getActors(ILocation loc);

  /**
   * Get all items (both IActors and other IItems) at the given location <p> The returned list
   * either can't be modified, or modifying it won't affect the map.
   *
   * @return A list of items
   */
  List<IItem> getAll(ILocation loc);

  /**
   * Get all non-IActor items at the given location <p> The returned list either can't be modified,
   * or modifying it won't affect the map.
   *
   * @return A list of items, non of which are instanceof IActor
   */
  List<IItem> getItems(ILocation loc);

  /**
   * @return A 2D-area defining the legal map locations
   */
  IArea getArea();

  /**
   * @return Height of the map, same as {@link #getArea()}.{@link IArea#getHeight()}
   */
  int getHeight();

  /**
   * @return Width of the map, same as {@link #getArea()}.{@link IArea#getWidth()}
   */
  int getWidth();

  /**
   * Find location of an item
   *
   * @param item The item
   * @return It's location, or <code>null</code> if it's not on the map
   */
  ILocation getLocation(IItem item);

  /**
   * Translate (x,y)-coordinates to ILocation
   *
   * @return an ILocation
   * @throws IndexOutOfBoundsException if (x,y) is outside {@link #getArea()}
   */
  ILocation getLocation(int x, int y);

  /**
   * Get the neighbouring location in the given direction
   *
   * @param from A location
   * @param dir the Direction
   * @return from's neighbour in direction dir, or null, if this would be outside the map
   */
  ILocation getNeighbour(ILocation from, GridDirection dir);

  /**
   * Compute new location of an IActor moving the given direction
   *
   * @param from Original location
   * @param dir Direction we're moving in
   * @return The new location
   * @throws IllegalMoveException if !{@link #canGo(ILocation, GridDirection)}
   */
  ILocation go(ILocation from, GridDirection dir) throws IllegalMoveException;

  /**
   * Check if an item exists at a location
   *
   * @param loc The location
   * @param target The item we're interested in
   * @return True if target would appear in {@link IMapView#getActors(ILocation loc)}
   */
  boolean has(ILocation loc, IItem target);

  /**
   * Check for actors.
   *
   * @return True if {@link IMapView#getActors(ILocation loc)} would be non-empty
   */
  boolean hasActors(ILocation loc);

  /**
   * Check for non-actors
   *
   * @return True if {@link #getItem(loc)} would be non-empty
   */
  boolean hasItems(ILocation loc);

  /**
   * Check for walls
   *
   * @return True if there is a wall at the given location ({@link IMapView#getAll()}} would contain an
   * instance of {@link Wall})
   */
  boolean hasWall(ILocation loc);

  /**
   * Check for doors
   *
   * @return True if there is a door at the given location ({@link IMapView#getAll()} would contain an
   * instance of {@link Door})
   */
  boolean hasDoor(ILocation loc);

  /**
   * Check if a neighbour exists on the map
   *
   * @param from A location
   * @param dir A direction
   * @return True if {@link ILocation#allNeighbours()} would return non-null
   */
  boolean hasNeighbour(ILocation from, GridDirection dir);

  /**
   * Get all locations within i steps from the given centre
   *
   * @return A list of locations, all at most i grid cells away from centre
   */
  List<ILocation> getNeighbourhood(ILocation centre, int dist);

  /**
   * Get the first instance of a class at a location
   */
  <T extends IItem> T find(ILocation loc, Class<T> clazz);

  /**
   * Check if there is an instance of a class at a location
   *
   * @return true if there is an instance of the class in the grid at the location
   */
  <T extends IItem> boolean has(ILocation loc, Class<T> clazz);
}
