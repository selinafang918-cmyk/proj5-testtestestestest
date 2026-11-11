package shapes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.Set;
import java.util.TreeSet;

import static com.google.common.truth.Truth.assertWithMessage;

/** PARTNER B: run these before you commit Square.java. */
public class SquareTest {

    /** Returns a WIDTH x HEIGHT world filled with FILL. */
    private static TETile[][] filledWorld(int width, int height, TETile fill) {
        TETile[][] world = new TETile[width][height];
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = fill;
            }
        }
        return world;
    }

    /** Returns the positions in WORLD that are not FILL, as "x,y" strings. */
    private static Set<String> drawnTiles(TETile[][] world, TETile fill) {
        Set<String> drawn = new TreeSet<>();
        for (int x = 0; x < world.length; x += 1) {
            for (int y = 0; y < world[0].length; y += 1) {
                if (world[x][y] != fill) {
                    drawn.add(x + "," + y);
                }
            }
        }
        return drawn;
    }

    /** Returns the outline tiles of the square whose box starts at (X0, Y0), as "x,y" strings. */
    private static Set<String> expectedTiles(int x0, int y0, int side) {
        Set<String> expected = new TreeSet<>();
        for (int x = x0; x < x0 + side; x += 1) {
            for (int y = y0; y < y0 + side; y += 1) {
                if (x == x0 || x == x0 + side - 1 || y == y0 || y == y0 + side - 1) {
                    expected.add(x + "," + y);
                }
            }
        }
        return expected;
    }

    @Test
    @DisplayName("width() and height() are both side")
    public void widthAndHeight() {
        for (int side = 3; side <= 8; side += 1) {
            for (int x = 0; x < 4; x += 1) {
                int y = 3 - x;
                String name = "new Square(" + x + ", " + y + ", " + side + ")";
                Square square = new Square(x, y, side);
                assertWithMessage(name + ".x()").that(square.x()).isEqualTo(x);
                assertWithMessage(name + ".y()").that(square.y()).isEqualTo(y);
                assertWithMessage(name + ".width()").that(square.width()).isEqualTo(side);
                assertWithMessage(name + ".height()").that(square.height()).isEqualTo(side);
            }
        }
    }

    @Test
    @DisplayName("A square with side 3 draws 8 tiles around an empty center")
    public void drawSide3() {
        TETile[][] world = filledWorld(10, 10, Tileset.NOTHING);
        new Square(1, 2, 3).draw(world);
        assertWithMessage("Tiles drawn by new Square(1, 2, 3)")
                .that(drawnTiles(world, Tileset.NOTHING))
                .containsExactly("1,2", "2,2", "3,2",
                        "1,3", "3,3",
                        "1,4", "2,4", "3,4");
        assertWithMessage("Tile at a corner").that(world[1][2]).isEqualTo(Square.TILE);
    }

    @Test
    @DisplayName("A square leaves the tiles inside it unchanged")
    public void hollow() {
        TETile[][] world = filledWorld(8, 8, Tileset.FLOOR);
        new Square(1, 1, 5).draw(world);
        for (int x = 2; x <= 4; x += 1) {
            for (int y = 2; y <= 4; y += 1) {
                assertWithMessage("Tile at (" + x + ", " + y + "), inside new Square(1, 1, 5)")
                        .that(world[x][y]).isEqualTo(Tileset.FLOOR);
            }
        }
    }

    @Test
    @DisplayName("A square draws exactly its outline, all with Square.TILE, and changes nothing else")
    public void drawExact() {
        for (int side = 3; side <= 8; side += 1) {
            for (int x0 : new int[]{0, 3}) {
                int y0 = 3 - x0;
                String name = "new Square(" + x0 + ", " + y0 + ", " + side + ")";
                TETile[][] world = filledWorld(side + 4, side + 4, Tileset.FLOOR);
                new Square(x0, y0, side).draw(world);
                assertWithMessage("Tiles changed by " + name)
                        .that(drawnTiles(world, Tileset.FLOOR))
                        .containsExactlyElementsIn(expectedTiles(x0, y0, side));
                for (String pos : expectedTiles(x0, y0, side)) {
                    int x = Integer.parseInt(pos.split(",")[0]);
                    int y = Integer.parseInt(pos.split(",")[1]);
                    assertWithMessage("Tile at (" + pos + ") drawn by " + name)
                            .that(world[x][y]).isEqualTo(Square.TILE);
                }
            }
        }
    }

    @Test
    @DisplayName("A square at (0, 0) draws 4s - 4 tiles in a world exactly its size")
    public void fillsBoxExactly() {
        for (int side = 3; side <= 8; side += 1) {
            TETile[][] world = filledWorld(side, side, Tileset.NOTHING);
            new Square(0, 0, side).draw(world);
            assertWithMessage("Number of tiles drawn by new Square(0, 0, " + side + ")")
                    .that(drawnTiles(world, Tileset.NOTHING)).hasSize(4 * side - 4);
        }
    }
}
