package shapes;

import com.google.common.collect.Range;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tileengine.TETile;

import java.util.List;
import java.util.Random;

import static com.google.common.truth.Truth.assertWithMessage;

/** BOTH PARTNERS: run these after you write ShapeWorld.java together. */
public class ShapeWorldTest {
    private static final int NUM_SEEDS = 100;

    /** A plain rectangle of tiles. Real shapes are square, but fitsInWorld must work for any box. */
    private static class Box implements Shape {
        private final int x;
        private final int y;
        private final int width;
        private final int height;

        Box(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public int x() {
            return x;
        }

        public int y() {
            return y;
        }

        public int width() {
            return width;
        }

        public int height() {
            return height;
        }

        public void draw(TETile[][] world) {
        }
    }

    /** Returns a short description of S, like "Plus at (3, 4), 5 wide". */
    private static String describe(Shape s) {
        if (s instanceof Box) {
            return "a box at (" + s.x() + ", " + s.y() + ") that is " + s.width() + " wide and "
                    + s.height() + " tall";
        }
        return s.getClass().getSimpleName() + " at (" + s.x() + ", " + s.y() + "), " + s.width() + " wide";
    }

    /** Returns a description of every shape in SHAPES, in order. */
    private static List<String> describe(List<Shape> shapes) {
        return shapes.stream().map(ShapeWorldTest::describe).toList();
    }

    /** Returns true if every tile of S's box is in the world, checked one tile at a time. */
    private static boolean fitsByTiles(Shape s) {
        for (int x = s.x(); x < s.x() + s.width(); x += 1) {
            for (int y = s.y(); y < s.y() + s.height(); y += 1) {
                if (x < 0 || x >= ShapeWorld.WIDTH || y < 0 || y >= ShapeWorld.HEIGHT) {
                    return false;
                }
            }
        }
        return true;
    }

    @Test
    @DisplayName("randomShape makes about as many pluses as squares, with sizes and positions in range")
    public void randomShapeRanges() {
        Random random = new Random(61);
        int pluses = 0;
        int squares = 0;
        for (int i = 0; i < 1000; i += 1) {
            Shape s = ShapeWorld.randomShape(random);
            assertWithMessage("randomShape returned null").that(s).isNotNull();
            if (s instanceof Plus) {
                pluses += 1;
                assertWithMessage("Width of " + describe(s) + " (arm 1 to 3)")
                        .that(s.width()).isIn(List.of(3, 5, 7));
            } else {
                assertWithMessage("randomShape returned a " + s.getClass().getSimpleName())
                        .that(s).isInstanceOf(Square.class);
                squares += 1;
                assertWithMessage("Width of " + describe(s) + " (side 3 to 6)")
                        .that(s.width()).isIn(Range.closed(3, 6));
            }
            assertWithMessage("x of " + describe(s)).that(s.x()).isIn(Range.closedOpen(0, ShapeWorld.WIDTH));
            assertWithMessage("y of " + describe(s)).that(s.y()).isIn(Range.closedOpen(0, ShapeWorld.HEIGHT));
        }
        assertWithMessage("Pluses in 1000 calls to randomShape. Use random.nextBoolean() to choose.")
                .that(pluses).isAtLeast(300);
        assertWithMessage("Squares in 1000 calls to randomShape. Use random.nextBoolean() to choose.")
                .that(squares).isAtLeast(300);
    }

    @Test
    @DisplayName("fitsInWorld checks all four edges of the world")
    public void fitsInWorld() {
        assertWithMessage("fitsInWorld(new Square(0, 0, 3))")
                .that(ShapeWorld.fitsInWorld(new Square(0, 0, 3))).isTrue();
        assertWithMessage("fitsInWorld(new Square(37, 27, 3)), touching the top-right corner")
                .that(ShapeWorld.fitsInWorld(new Square(37, 27, 3))).isTrue();
        assertWithMessage("fitsInWorld(new Square(38, 5, 3)), one tile past the right edge")
                .that(ShapeWorld.fitsInWorld(new Square(38, 5, 3))).isFalse();
        assertWithMessage("fitsInWorld(new Plus(5, 24, 3)), one tile past the top edge")
                .that(ShapeWorld.fitsInWorld(new Plus(5, 24, 3))).isFalse();
        assertWithMessage("fitsInWorld(new Plus(-1, 5, 1)), one tile past the left edge")
                .that(ShapeWorld.fitsInWorld(new Plus(-1, 5, 1))).isFalse();
        assertWithMessage("fitsInWorld(new Plus(5, -1, 1)), one tile past the bottom edge")
                .that(ShapeWorld.fitsInWorld(new Plus(5, -1, 1))).isFalse();
    }

    @Test
    @DisplayName("fitsInWorld is right for boxes that are not square, at and past every edge")
    public void fitsInWorldRectangles() {
        for (int w = 3; w <= 7; w += 1) {
            int h = 10 - w;
            for (int x = -2; x <= ShapeWorld.WIDTH; x += 1) {
                for (int y : new int[]{-2, -1, 0, 1, ShapeWorld.HEIGHT - h, ShapeWorld.HEIGHT - h + 1}) {
                    Shape s = new Box(x, y, w, h);
                    assertWithMessage("fitsInWorld(" + describe(s) + "). Use width() for x and "
                            + "height() for y.")
                            .that(ShapeWorld.fitsInWorld(s)).isEqualTo(fitsByTiles(s));
                }
            }
        }
    }

    @Test
    @DisplayName("Placed shapes fit in the world")
    public void placedShapesFit() {
        for (long seed = 0; seed < NUM_SEEDS; seed += 1) {
            List<Shape> shapes = ShapeWorld.placeShapes(seed);
            assertWithMessage("Seed " + seed + ": placeShapes returned no shapes").that(shapes).isNotEmpty();
            for (Shape s : shapes) {
                assertWithMessage("Seed " + seed + ": " + describe(s) + " fits in the world")
                        .that(fitsByTiles(s)).isTrue();
            }
        }
    }

    @Test
    @DisplayName("Each world has at least 8 shapes, of both kinds")
    public void enoughShapes() {
        for (long seed = 0; seed < NUM_SEEDS; seed += 1) {
            List<Shape> shapes = ShapeWorld.placeShapes(seed);
            assertWithMessage("Seed " + seed + ": number of shapes placed")
                    .that(shapes.size()).isAtLeast(8);
            assertWithMessage("Seed " + seed + ": some shape is a Plus")
                    .that(shapes.stream().anyMatch(s -> s instanceof Plus)).isTrue();
            assertWithMessage("Seed " + seed + ": some shape is a Square")
                    .that(shapes.stream().anyMatch(s -> s instanceof Square)).isTrue();
        }
    }

    @Test
    @DisplayName("The same seed always makes the same world, even after other seeds")
    public void sameSeedSameWorld() {
        for (long seed = 0; seed < NUM_SEEDS; seed += 1) {
            List<Shape> first = ShapeWorld.placeShapes(seed);
            assertWithMessage("Seed " + seed + ": placeShapes returned no shapes").that(first).isNotEmpty();
            ShapeWorld.placeShapes(seed + 1000);
            List<Shape> again = ShapeWorld.placeShapes(seed);
            assertWithMessage("Seed " + seed + " gave different shapes on two calls. "
                    + "Did you use new Random() without the seed, Math.random(), or a static Random?")
                    .that(describe(again)).isEqualTo(describe(first));
            assertWithMessage("Seed " + seed + " rendered differently on two calls")
                    .that(TETile.toString(ShapeWorld.render(again)))
                    .isEqualTo(TETile.toString(ShapeWorld.render(first)));
        }
    }

    @Test
    @DisplayName("Different seeds make different worlds")
    public void differentSeedsDiffer() {
        for (long seed = 0; seed < NUM_SEEDS; seed += 1) {
            assertWithMessage("Seeds " + seed + " and " + (seed + 1) + " gave the same shapes. "
                    + "Is placeShapes ignoring its seed?")
                    .that(describe(ShapeWorld.placeShapes(seed + 1)))
                    .isNotEqualTo(describe(ShapeWorld.placeShapes(seed)));
        }
    }
}
