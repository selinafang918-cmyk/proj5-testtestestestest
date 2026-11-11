package shapes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.Set;
import java.util.TreeSet;

import static com.google.common.truth.Truth.assertWithMessage;

/** PARTNER A: run these before you commit Plus.java. */
public class PlusTest {

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

    /** Returns the tiles of the plus whose box starts at (X0, Y0), as "x,y" strings. */
    private static Set<String> expectedTiles(int x0, int y0, int arm) {
        Set<String> expected = new TreeSet<>();
        for (int i = 0; i <= 2 * arm; i += 1) {
            expected.add((x0 + i) + "," + (y0 + arm));
            expected.add((x0 + arm) + "," + (y0 + i));
        }
        return expected;
    }

    @Test
    @DisplayName("width() and height() are 2 * arm + 1")
    public void widthAndHeight() {
        for (int arm = 1; arm <= 5; arm += 1) {
            for (int x = 0; x < 4; x += 1) {
                int y = x + 2;
                String name = "new Plus(" + x + ", " + y + ", " + arm + ")";
                Plus plus = new Plus(x, y, arm);
                assertWithMessage(name + ".x()").that(plus.x()).isEqualTo(x);
                assertWithMessage(name + ".y()").that(plus.y()).isEqualTo(y);
                assertWithMessage(name + ".width()").that(plus.width()).isEqualTo(2 * arm + 1);
                assertWithMessage(name + ".height()").that(plus.height()).isEqualTo(2 * arm + 1);
            }
        }
    }

    @Test
    @DisplayName("A plus with arm 1 draws 5 tiles")
    public void drawArm1() {
        TETile[][] world = filledWorld(10, 10, Tileset.NOTHING);
        new Plus(4, 4, 1).draw(world);
        assertWithMessage("Tiles drawn by new Plus(4, 4, 1), centered at (5, 5)")
                .that(drawnTiles(world, Tileset.NOTHING))
                .containsExactly("4,5", "5,5", "6,5", "5,4", "5,6");
        assertWithMessage("Tile at the center").that(world[5][5]).isEqualTo(Plus.TILE);
    }

    @Test
    @DisplayName("A plus with arm 2 draws 9 tiles")
    public void drawArm2() {
        TETile[][] world = filledWorld(10, 10, Tileset.NOTHING);
        new Plus(2, 4, 2).draw(world);
        assertWithMessage("Tiles drawn by new Plus(2, 4, 2), centered at (4, 6)")
                .that(drawnTiles(world, Tileset.NOTHING))
                .containsExactly("2,6", "3,6", "4,6", "5,6", "6,6",
                        "4,4", "4,5", "4,7", "4,8");
    }

    @Test
    @DisplayName("A plus draws exactly its tiles, all with Plus.TILE, and changes nothing else")
    public void drawExact() {
        for (int arm = 1; arm <= 5; arm += 1) {
            for (int x0 : new int[]{0, 3}) {
                int y0 = 1;
                String name = "new Plus(" + x0 + ", " + y0 + ", " + arm + ")";
                TETile[][] world = filledWorld(2 * arm + 6, 2 * arm + 6, Tileset.FLOOR);
                new Plus(x0, y0, arm).draw(world);
                assertWithMessage("Tiles changed by " + name)
                        .that(drawnTiles(world, Tileset.FLOOR))
                        .containsExactlyElementsIn(expectedTiles(x0, y0, arm));
                for (String pos : expectedTiles(x0, y0, arm)) {
                    int x = Integer.parseInt(pos.split(",")[0]);
                    int y = Integer.parseInt(pos.split(",")[1]);
                    assertWithMessage("Tile at (" + pos + ") drawn by " + name)
                            .that(world[x][y]).isEqualTo(Plus.TILE);
                }
            }
        }
    }

    @Test
    @DisplayName("A plus at (0, 0) reaches every edge of a world exactly its size")
    public void fillsBoxExactly() {
        for (int arm = 1; arm <= 5; arm += 1) {
            int size = 2 * arm + 1;
            TETile[][] world = filledWorld(size, size, Tileset.NOTHING);
            new Plus(0, 0, arm).draw(world);
            assertWithMessage("Number of tiles drawn by new Plus(0, 0, " + arm + ")")
                    .that(drawnTiles(world, Tileset.NOTHING)).hasSize(4 * arm + 1);
        }
    }
}
