package inf101.v18.rogue101.map;

import inf101.v18.gfx.gfxmode.ITurtle;
import inf101.v18.gfx.textmode.Printer;
import inf101.v18.grid.ILocation;
import inf101.v18.rogue101.objects.IItem;
import java.util.List;

/**
 * Extra map methods that are for the game class only!
 *
 * @author anya
 */
public interface IGameMap extends IMapView {

  /**
   * Draw the map
   */
  void draw(ITurtle painter, Printer printer);

  /**
   * Get a modifiable list of items
   */
  List<IItem> getAllModifiable(ILocation loc);

  /**
   * Remove any destroyed items at the given location (items where {@link IItem#isDestroyed()} is
   * true)
   */
  void clean(ILocation loc);

  /**
   * Remove an item
   */
  void remove(ILocation loc, IItem item);

}
