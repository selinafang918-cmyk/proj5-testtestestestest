package shapes;

import tileengine.TETile;
import tileengine.Tileset;

/**
 * A plus sign made of FLOWER tiles, with arms ARM tiles long. (x, y) is the bottom-left
 * corner of its box, so its center is (x + arm, y + arm).
 *
 * For example, new Plus(3, 3, 2) is centered at (5, 5). It covers (3, 5) through (7, 5)
 * horizontally and (5, 3) through (5, 7) vertically:
 *
 *       ❀
 *       ❀
 *   ❀ ❀ ❀ ❀ ❀
 *       ❀
 *       ❀
 *
 * PARTNER A: this file is yours. Partner B should not edit it.
 */
public class Plus implements Shape {
    public static final TETile TILE = Tileset.FLOWER;

    private final int x;
    private final int y;
    private final int arm;

    public Plus(int x, int y, int arm) {
        this.x = x;
        this.y = y;
        this.arm = arm;
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
        // TODO: A plus is 2 * arm + 1 tiles wide.
        return 0;
    }

    @Override
    public int height() {
        // TODO: A plus is as tall as it is wide.
        return 0;
    }

    @Override
    public void draw(TETile[][] world) {
        // TODO: Set world[..][..] = TILE for every tile of the plus.
        //  The horizontal bar is row y + arm, from column x to x + 2 * arm.
        //  The vertical bar is column x + arm, from row y to y + 2 * arm.
    }
}
