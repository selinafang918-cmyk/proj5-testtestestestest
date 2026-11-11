package shapes;

import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Builds a world of randomly placed pluses and squares. Shapes may overlap.
 *
 * For Groups of THREE: Partner C, after both Plus and Square are committed and
 * pulled onto C's laptop.
 * 
 * For Groups of TWO: Both Partners: write this file together on one laptop, after both Plus and
 * Square are committed and pulled onto the driver's laptop.
 */
public class ShapeWorld {
    public static final int WIDTH = 40;
    public static final int HEIGHT = 30;
    public static final int ATTEMPTS = 60;

    /**
     * Returns a random Plus or Square, using only RANDOM for randomness.
     * A Plus gets an arm from 1 to 3, and a Square gets a side from 3 to 6.
     * Its (x, y) is anywhere in [0, WIDTH) x [0, HEIGHT). It may not fit in the world;
     * placeShapes checks that.
     */
    public static Shape randomShape(Random random) {
        // RandomUtils.uniform(random, a, b) returns an int in [a, b).
        int x = RandomUtils.uniform(random, 0, WIDTH);
        int y = RandomUtils.uniform(random, 0, HEIGHT);
        // TODO: Use random.nextBoolean() to choose between a Plus and a Square, then
        //  give it a random arm or side the same way x and y were picked above.
        //  Do not make a new Random here: use the one you were given.
        return null;
    }

    /**
     * Tries ATTEMPTS random shapes and returns the ones that were kept, in the order kept.
     * A shape is kept if it fits in the world.
     * The same SEED must always give the same list.
     */
    public static List<Shape> placeShapes(long seed) {
        List<Shape> placed = new ArrayList<>();
        Random random = new Random(seed);
        for (int i = 0; i < ATTEMPTS; i += 1) {
            // TODO: Get a candidate from randomShape, and add it to PLACED if it fits in
            //  the world (see fitsInWorld below).
        }
        return placed;
    }

    /** Returns true if every tile of S's box is inside the WIDTH x HEIGHT world. */
    public static boolean fitsInWorld(Shape s) {
        // TODO: Valid tiles are x in [0, WIDTH) and y in [0, HEIGHT).
        //  See Shape.java for which tiles S's box covers.
        return false;
    }

    /** Returns a WIDTH x HEIGHT world of NOTHING tiles with every shape in SHAPES drawn on it. */
    public static TETile[][] render(List<Shape> shapes) {
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        for (Shape s : shapes) {
            s.draw(world);
        }
        return world;
    }
}
