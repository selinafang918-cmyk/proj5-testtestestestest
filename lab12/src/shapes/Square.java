package shapes;

import tileengine.TETile;
import tileengine.Tileset;

/**
 * A hollow square outline made of WALL tiles. (x, y) is its bottom-left corner, and each
 * side is SIDE tiles long. The tiles inside the outline are left unchanged.
 *
 * For example, new Square(2, 3, 4) covers x = 2..5 and y = 3..6, and draws:
 *
 *   # # # #
 *   #     #
 *   #     #
 *   # # # #
 *
 * PARTNER B: this file is yours. Partner A should not edit it.
 */
public class Square implements Shape {
    public static final TETile TILE = Tileset.WALL;

    private final int x;
    private final int y;
    private final int side;

    public Square(int x, int y, int side) {
        this.x = x;
        this.y = y;
        this.side = side;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public int width() {
        // TODO: A square is SIDE tiles wide.
        return 0;
    }

    @Override
    public int height() {
        // TODO: A square is as tall as it is wide.
        return 0;
    }

    @Override
    public void draw(TETile[][] world) {
        // TODO: Set world[..][..] = TILE along the four edges of the square.
        //  The bottom edge is y, the top edge is y + side - 1,
        //  the left edge is x, and the right edge is x + side - 1.
        //  Do not change any tile inside the outline.
    }
}
