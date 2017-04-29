package main;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/*
 * A class that implements a rectangular pond, stores the pond's properties,
 * and can draw the pond onto a PApplet.
 */
public class Pond {
    PApplet p; // PApplet to draw objects of this class onto
    PVector pos; // position
    float w, h; // width, height

    /*
     * Constructor: Initializes this pond with the given arguments.
     */
    public Pond(PApplet p_, PVector pos_, float w_, float h_) {
        p = p_;
        pos = pos_;
        w = w_;
        h = h_;
    }

    /*
     * Draws this pond.
     */
    public void draw() {
        p.stroke(0);
        p.strokeWeight(1);
        p.fill(0, 255, 255);
        p.rectMode(PConstants.CORNER);
        p.rect(pos.x, pos.y, w, h);
    }

    /*
     * Returns whether the mouse cursor is outside of this pond.
     */
    public boolean mouseIsOutside() {
      if (p.mouseX > pos.x && p.mouseX < pos.x + w &&
          p.mouseY > pos.y && p.mouseY < pos.y + h) {
          return false;
      }
      return true;
    }
}
