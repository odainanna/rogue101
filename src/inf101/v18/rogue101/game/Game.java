package inf101.v18.rogue101.game;

import inf101.v18.gfx.Screen;
import inf101.v18.gfx.gfxmode.ITurtle;
import inf101.v18.gfx.gfxmode.TurtlePainter;
import inf101.v18.gfx.textmode.Printer;
import inf101.v18.grid.GridDirection;
import inf101.v18.grid.IGrid;
import inf101.v18.grid.ILocation;
import inf101.v18.rogue101.Main;
import inf101.v18.rogue101.enums.Status;
import inf101.v18.rogue101.map.GameMap;
import inf101.v18.rogue101.map.IGameMap;
import inf101.v18.rogue101.map.IMapView;
import inf101.v18.rogue101.map.MapReader;
import inf101.v18.rogue101.objects.Apple;
import inf101.v18.rogue101.objects.Carrot;
import inf101.v18.rogue101.objects.Door;
import inf101.v18.rogue101.objects.Dust;
import inf101.v18.rogue101.objects.DustBunny;
import inf101.v18.rogue101.objects.IActiveItem;
import inf101.v18.rogue101.objects.IActor;
import inf101.v18.rogue101.objects.IItem;
import inf101.v18.rogue101.objects.INonPlayer;
import inf101.v18.rogue101.objects.IPlayer;
import inf101.v18.rogue101.objects.Key;
import inf101.v18.rogue101.objects.MonsterRabbit;
import inf101.v18.rogue101.objects.Player;
import inf101.v18.rogue101.objects.Rabbit;
import inf101.v18.rogue101.objects.Wall;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class Game implements IGame {

  private final ITurtle painter;
  private final Printer printer;
  private Map<String, Supplier<IItem>> itemFactories = setupItemFactories();
  private Status status = Status.PLAYING;

  /**
   * All the actors that have things left to do this turn
   */
  private List<IActor> actors = Collections.synchronizedList(new ArrayList<>());
  /**
   * All the active items that have things left to do this turn. Active items are items with a
   * doTurn()-metod.
   */
  private List<IActiveItem> activeItems = Collections.synchronizedList(new ArrayList<>());
  /**
   * List of paths to available rooms.
   */
  private ArrayList<String> rooms = new ArrayList<>();
  /**
   * Messages that appear in the message board
   */
  private ArrayList<String> messages = new ArrayList<>();
  /**
   * Useful random generator
   */
  private Random random = new Random();
  /**
   * The game map. {@link IGameMap} gives us a few more details than {@link IMapView} (write access
   * to item lists); the game needs this but individual items don't.
   */
  private IGameMap map;
  /* Current room */
  private int room = 0;
  private IActor currentActor;
  private ILocation currentLocation;
  private int movePoints = 0;
  private int numPlayers = 0;

  public Game(Screen screen, ITurtle painter, Printer printer) {
    this.painter = painter;
    this.printer = printer;

    rooms.add("maps/level1.txt");
    rooms.add("maps/level2.txt");

    IGrid<String> inputGrid = MapReader.readFile(rooms.get(room));
    if (inputGrid == null) {
      System.err.println("Map not found – falling back to builtin map");
      inputGrid = MapReader.readString(Main.BUILTIN_MAP);
    }
    this.map = new GameMap(inputGrid.getArea());
    for (ILocation loc : inputGrid.locations()) {
      IItem item = createItem(inputGrid.get(loc));
      if (item != null) {
        map.add(loc, item);
      }
    }
  }

  public Game(String mapString) {
    printer = new Printer(1280, 720);
    painter = new TurtlePainter(1280, 720);
    IGrid<String> inputGrid = MapReader.readString(mapString);
    this.map = new GameMap(inputGrid.getArea());
    for (ILocation loc : inputGrid.locations()) {
      IItem item = createItem(inputGrid.get(loc));
      if (item != null) {
        map.add(loc, item);
      }
    }
  }

  private Map<String, Supplier<IItem>> setupItemFactories() {
    Map<String, Supplier<IItem>> factories = new HashMap<>();
    factories.put("@", Player::new);
    factories.put("#", Wall::new);
    factories.put(".", Dust::new);
    factories.put("A", Apple::new);
    factories.put("C", Carrot::new);
    factories.put("D", Door::new);
    factories.put("K", Key::new);
    factories.put("R", Rabbit::new);
    factories.put("M", MonsterRabbit::new);
    factories.put("B", DustBunny::new);
    return factories;
  }

  @Override
  public void nextRoom() {
    if (room > rooms.size() - 1) {
      throw new UnsupportedOperationException("There is no next level");
    }
    IGrid<String> inputGrid = MapReader.readFile(rooms.get(++room));
    if (inputGrid == null) {
      System.err.println("Map not found at " + rooms.get(room) + " can't access next room");
      return;
    }
    this.map = new GameMap(inputGrid.getArea());
    for (ILocation loc : inputGrid.locations()) {
      IItem item = createItem(inputGrid.get(loc));
      if (item != null) {
        map.add(loc, item);
      }
    }
    currentActor = null;
    currentLocation = null;
    movePoints = 0;
    numPlayers = 0;
    actors = Collections.synchronizedList(new ArrayList<>());
    activeItems = Collections.synchronizedList(new ArrayList<>());
  }

  @Override
  public void useMovePoint() {
    movePoints--;
  }

  @Override
  public void addItem(IItem item) {
    map.add(currentLocation, item);
  }

  @Override
  public void addItem(String sym) {
    IItem item = createItem(sym);
    if (item != null) {
      map.add(currentLocation, item);
    }
  }

  @Override
  public ILocation attack(GridDirection dir, IItem target) {
    ILocation loc = map.getNeighbour(currentLocation, dir);
    if (!map.has(loc, target)) {
      throw new IllegalMoveException("Target isn't there!");
    }
    boolean successful =
        currentActor.getAttack() + random.nextInt(20) + 1 >= target.getDefence() + 10;
    if (successful) {
      int damage = target.handleDamage(this, currentActor, currentActor.getAttack());
      formatMessage("%s hits %s for %d damage", currentActor.getName(), target.getName(), damage);
    } else {
      formatMessage("%s dodged the attack, no damage done", target.getName());
    }
    map.clean(loc);
    if (target.isDestroyed()) {
      displayMessage(target.getName() + " died");
      if (target instanceof IPlayer) {
        status = Status.LOST;
      } else if (target instanceof MonsterRabbit) {
        status = Status.WON;
      }
      return move(dir);
    } else {
      movePoints--;
      return currentLocation;
    }
  }

  /**
   * Begin a new game turn, or continue to the previous turn
   *
   * @return True if the game should wait for more user input
   */
  public boolean doTurn() {

    do {
      if (actors.isEmpty()) {
        // System.err.println("new turn!");

        // no one in the queue, we're starting a new turn!
        // first collect all the actors and all the active items:
        beginTurn();
      }

      //process the active items one by one by calling their doTurn-method
      while (!activeItems.isEmpty()) {
        activeItems.remove(0).doTurn(this);
      }

      // process actors one by one; for the IPlayer, we return and wait for keypresses
      while (!actors.isEmpty()) {
        // get the next player or non-player in the queue
        currentActor = actors.remove(0);
        if (currentActor.isDestroyed()) // skip if it's dead
        {
          continue;
        }
        currentLocation = map.getLocation(currentActor);
        if (currentLocation == null) {
          displayDebug("doTurn(): Whoops! Actor has disappeared from the map: " + currentActor);
        }
        movePoints = 1; // everyone gets to do one thing

        if (currentActor instanceof INonPlayer) {
          // computer-controlled players do their stuff right away
          ((INonPlayer) currentActor).doTurn(this);
          // remove any dead items from current location
          map.clean(currentLocation);
        } else if (currentActor instanceof IPlayer) {
          if (currentActor.isDestroyed()) {
            // a dead human player gets removed from the game
            map.remove(currentLocation, currentActor);
            currentActor = null;
            currentLocation = null;
          } else {
            // For the human player, we need to wait for input, so we just return.
            // Further keypresses will cause keyPressed() to be called, and once the human
            // makes a move, it'll lose its movement point and doTurn() will be called again
            //
            // NOTE: currentActor and currentLocation are set to the IPlayer (above),
            // so the game remembers who the player is whenever new keypresses occur. This
            // is also how e.g., getLocalItems() work – the game always keeps track of
            // whose turn it is.
            return true;
          }
        } else {
          displayDebug("doTurn(): Hmm, this is a very strange actor: " + currentActor);
        }
      }
    } while (numPlayers
        > 0); // we can safely repeat if we have players, since we'll return (and break out of
    // the loop) once we hit the player
    return true;
  }

  /**
   * Go through the map and collect all the actors and active items
   */
  private void beginTurn() {
    numPlayers = 0;
    // this extra fancy iteration over each map location runs *in parallel* on
    // multicore systems!
    // that makes some things more tricky, hence the "synchronized" block and
    // "Collections.synchronizedList()" in the initialization of "actors".
    // NOTE: If you want to modify this yourself, it might be a good idea to replace
    // "parallelStream()" by "stream()", because weird things can happen when many
    // things happen
    // at the same time! (or do INF214 or DAT103 to learn about locks and threading)
    map.getArea().parallelStream().forEach((loc) -> { // will do this for each location in map
      List<IItem> list = map.getAllModifiable(loc); // all items at loc
      Iterator<IItem> li = list.iterator(); // manual iterator lets us remove() items
      while (li.hasNext()) { // this is what "for(IItem item : list)" looks like on the inside
        IItem item = li.next();
        if (item.getCurrentHealth() < 0) {
          // normally, we expect these things to be removed when they are destroyed, so
          // this shouldn't happen
          synchronized (this) {
            formatDebug("beginTurn(): found and removed leftover destroyed item %s '%s' at %s%n",
                item.getName(), item.getSymbol(), loc);
          }
          li.remove();
          map.remove(loc, item); // need to do this too, to update item map
        } else if (item instanceof IActiveItem) {
          activeItems.add((IActiveItem) item);
        } else if (item instanceof IPlayer) {
          actors.add(0, (IActor) item); // we let the human player go first
          synchronized (this) {
            numPlayers++;
          }
        } else if (item instanceof IActor) {
          actors.add((IActor) item); // add other actors to the end of the list
        }
      }
    });
  }

  @Override
  public boolean canGo(GridDirection dir) {
    return map.canGo(currentLocation, dir);
  }

  @Override
  public IItem createItem(String sym) {
    Supplier<IItem> factory = itemFactories.get(sym);
    if (factory != null) {
      return factory.get();
    } else if (!sym.equals(" ")) { //if factory returns null because the symbol is unknown
      System.err.println("createItem: Don't know how to create a '" + sym + "'");
    }
    return null;
  }

  @Override
  public void displayDebug(String s) {
    printer.clearLine(Main.LINE_DEBUG);
    printer.printAt(1, Main.LINE_DEBUG, s, Color.DARKRED);
    System.err.println(s);
  }

  @Override
  public void displayMessage(String s) {
    messages.add(0, s);
    int printerY = 1;
    int printerX = Main.COLUMN_RIGHTSIDE_START;
    printer.clearRegion(printerX, printerY, getWidth(), getHeight()); //clear messages
    printer.printAt(Main.COLUMN_RIGHTSIDE_START, printerY++, "MESSAGES");
    for (int i = 0; i < messages.size() && printerY <= Main.LINE_MAP_BOTTOM; i++) {
      printer.printAt(Main.COLUMN_RIGHTSIDE_START, printerY++, messages.get(i));
    }
  }

  @Override
  public void displayStatus(String s) {
    printer.clearLine(Main.LINE_STATUS);
    printer.printAt(1, Main.LINE_STATUS, s);
    System.out.println("Status: «" + s + "»");
  }

  public void draw() {
    map.draw(painter, printer);
  }

  @Override
  public boolean drop(IItem item) {
    if (item != null) {
      map.add(currentLocation, item);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void formatDebug(String s, Object... args) {
    displayDebug(String.format(s, args));
  }

  @Override
  public void formatMessage(String s, Object... args) {
    displayMessage(String.format(s, args));
  }

  @Override
  public void formatStatus(String s, Object... args) {
    displayStatus(String.format(s, args));
  }

  @Override
  public int getHeight() {
    return map.getHeight();
  }

  @Override
  public List<IItem> getLocalItems() {
    return map.getItems(currentLocation);
  }

  @Override
  public ILocation getLocation() {
    return currentLocation;
  }

  @Override
  public ILocation getLocation(GridDirection dir) {
    if (currentLocation.canGo(dir)) {
      return currentLocation.go(dir);
    } else {
      return null;
    }
  }

  /**
   * Return the game map. {@link IGameMap} gives us a few more details than {@link IMapView} (write
   * access to item lists); the game needs this but individual items don't.
   */
  @Override
  public IMapView getMap() {
    return map;
  }

  @Override
  public List<GridDirection> getPossibleMoves() {
    List<GridDirection> possibleMoves = new ArrayList<>();
    for (GridDirection dir : GridDirection.FOUR_DIRECTIONS) {
      if (this.canGo(dir)) {
        possibleMoves.add(dir);
      }
    }
    Collections.shuffle(possibleMoves);
    return possibleMoves;
  }

  @Override
  public List<ILocation> getVisible() {
    return map.getNeighbourhood(currentLocation, currentActor.getDistanceVision(this));
  }

  @Override
  public int getWidth() {
    return map.getWidth();
  }

  public boolean keyPressed(KeyCode code) {
    // only an IPlayer/human can handle keypresses, and only if it's the human's
    // turn
    if (currentActor instanceof IPlayer) {
      ((IPlayer) currentActor).keyPressed(this, code); // do your thing
      return movePoints > 0;
    }
    return false;
  }

  @Override
  public ILocation move(GridDirection dir) {
    if (movePoints < 1) {
      throw new IllegalMoveException("You're out of moves!");
    }
    ILocation newLoc = map.go(currentLocation, dir);
    map.remove(currentLocation, currentActor);
    map.add(newLoc, currentActor);
    currentLocation = newLoc;
    movePoints--;
    return currentLocation;
  }

  @Override
  public IItem pickUp(IItem item) {
    if (item != null && map.has(currentLocation, item) && currentActor.getAttack() > item
        .getDefence()) {
      map.remove(currentLocation, item);
      return item;
    } else {
      return null;
    }
    // DONE: bruk getAttack()/getDefence() til å avgjøre om man får til å plukke opp
    // tingen
    // evt.: en IActor kan bare plukkes opp hvis den har få/ingen helsepoeng igjen
  }

  @Override
  public ILocation rangedAttack(GridDirection dir, IItem target) {
    return currentLocation;
  }

  @Override
  public ITurtle getPainter() {
    return painter;
  }

  @Override
  public Printer getPrinter() {
    return printer;
  }

  @Override
  public int[] getFreeTextAreaBounds() {
    int[] area = new int[4];
    area[0] = getWidth() + 1;
    area[1] = 1;
    area[2] = printer.getLineWidth();
    area[3] = printer.getPageHeight() - 5;
    return area;
  }

  @Override
  public void clearFreeTextArea() {
    printer.clearRegion(getWidth() + 1, 1, printer.getLineWidth() - getWidth(),
        printer.getPageHeight() - 5);
  }

  @Override
  public void clearFreeGraphicsArea() {
    painter.as(GraphicsContext.class).clearRect(getWidth() * printer.getCharWidth(), 0,
        painter.getWidth() - getWidth() * printer.getCharWidth(),
        (printer.getPageHeight() - 5) * printer.getCharHeight());
  }

  @Override
  public double[] getFreeGraphicsAreaBounds() {
    double[] area = new double[4];
    area[0] = getWidth() * printer.getCharWidth();
    area[1] = 0;
    area[2] = painter.getWidth();
    area[3] = getHeight() * printer.getCharHeight();
    return area;
  }

  @Override
  public IActor getActor() {
    return currentActor;
  }

  public ILocation setCurrent(IActor actor) {
    currentLocation = map.getLocation(actor);
    if (currentLocation != null) {
      currentActor = actor;
      movePoints = 1;
    }
    return currentLocation;
  }

  public IActor setCurrent(ILocation loc) {
    List<IActor> list = map.getActors(loc);
    if (!list.isEmpty()) {
      currentActor = list.get(0);
      currentLocation = loc;
      movePoints = 1;
    }
    return currentActor;
  }

  public IActor setCurrent(int x, int y) {
    return setCurrent(map.getLocation(x, y));
  }

  @Override
  public Random getRandom() {
    return random;
  }

  public ArrayList<String> getMessages() {
    return messages;
  }

  public <T extends IItem> T findClosestVisible(Class<T> clazz) {
    for (ILocation loc : getVisible()) {
      T item = map.find(loc, clazz);
      if (item != null) {
        return item;
      }
    }
    return null;
  }

  public <T extends IItem> T findLocally(Class<T> clazz) {
    return map.find(currentLocation, clazz);
  }

  @Override
  public Status getStatus() {
    return status;
  }

  @Override
  public void setGameStatus(Status status) {
    this.status = status;
  }

  @Override
  public void handleGameOver() {
    painter.clear();
    printer.clear();
    printer.setBackground(Color.BLACK);
    printer.setFont(Printer.FONT_LMMONO);
    int printerX = Main.COLUMN_RIGHTSIDE_START + 2;
    int printerY = printer.getPageHeight() / 2;
    String firstLine = "";
    if (getStatus() == Status.LOST) {
      firstLine = "GAME OVER";
    } else if (getStatus() == Status.WON) {
      firstLine = "YOU WON!";
    }
    printer.printAt(printerX, printerY++, firstLine, Color.WHITE);
    printer.printAt(printerX, printerY, "(press space to try again)", Color.WHITE);
    printer.setBackground(Color.WHITE);
  }
}
