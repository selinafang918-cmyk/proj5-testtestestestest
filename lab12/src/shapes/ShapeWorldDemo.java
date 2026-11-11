package shapes;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;

import java.awt.Color;
import java.util.List;

/**
 * Opens a window showing the world that ShapeWorld.placeShapes makes for a seed.
 * Pass a seed as a program argument to try other worlds. You do not need to modify this file.
 */
public class ShapeWorldDemo {
    private static final long DEFAULT_SEED = 61;

    public static void main(String[] args) {
        long seed = DEFAULT_SEED;
        if (args.length > 0) {
            seed = Long.parseLong(args[0]);
        }

        List<Shape> shapes = ShapeWorld.placeShapes(seed);
        TETile[][] world = ShapeWorld.render(shapes);
        System.out.println("Seed " + seed + " placed " + shapes.size() + " shapes.");

        TERenderer ter = new TERenderer();
        ter.initialize(ShapeWorld.WIDTH, ShapeWorld.HEIGHT);
        StdDraw.clear(new Color(0, 0, 0));
        ter.drawTiles(world);
        StdDraw.show();
    }
}
