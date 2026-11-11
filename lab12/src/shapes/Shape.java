package shapes;

import tileengine.TETile;

/**
 * Something that can be drawn into a world of tiles. You do not need to modify this file.
 *
 * Every shape sits in a box of tiles whose bottom-left tile is (x(), y()). The box covers
 * x() through x() + width() - 1 across and y() through y() + height() - 1 up.
 */
public interface Shape {

    /** Returns the x coordinate of the bottom-left tile of this shape's box. */
    int x();

    /** Returns the y coordinate of the bottom-left tile of this shape's box. */
    int y();

    /** Returns how many tiles wide this shape's box is. */
    int width();

    /** Returns how many tiles tall this shape's box is. */
    int height();

    /**
     * Draws this shape into WORLD. Only changes tiles inside this shape's box.
     * Assumes the box fits inside WORLD.
     */
    void draw(TETile[][] world);
}
