package inf101.v18.grid;

import java.util.ArrayList;
import java.util.List;

public class MultiGrid<T> extends MyGrid<List<T>> implements IMultiGrid<T> {

  public MultiGrid(IArea area) {
    super(area, (l) -> new ArrayList<>());
  }

  public MultiGrid(int width, int height) {
    super(width, height, (l) -> new ArrayList<>());
  }


}
