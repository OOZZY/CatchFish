package main;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/*
 * A class that implements a fishing line, stores the fishing line's
 * properties, and can draw the fishing line onto a PApplet. The fishing line's
 * position is determined by p.mouseX and p.mouseY.
 */
public class FishingLine {
    PApplet p; // PApplet to draw objects of this class onto
    PVector pos; // position
    float lineLength; // current length of fishing line
    float maxLineLength; // maximum length of fishing line
    float hookSize; // diameter of the hook at the bottom of fishing line

    // keeps track of whether this fishing line is hooked onto a fish
    boolean hooked = false;
    // references hooked fish if this fishing line is hooked onto a fish
    Fish hookedFish = null;

    /*
     * Constructor: Initializes this fishing line with the given arguments.
     */
    public FishingLine(PApplet p_, float lineLength_, float hookSize_) {
        p = p_;
        pos = new PVector(p.mouseX, p.mouseY);
        lineLength = maxLineLength = lineLength_;
        hookSize = hookSize_;
    }

    /*
     * Draws this fishing line.
     */
    public void draw() {
        p.stroke(0);
        p.strokeWeight(2);
        p.noFill();
        p.line(pos.x, pos.y, pos.x, pos.y + lineLength - hookSize / 2);
        p.arc(pos.x, pos.y + lineLength, hookSize, hookSize,
              -PConstants.PI/2, PConstants.PI);
    }

    /*
     * Updates this pond.
     */
    public void update() {
        // update position
        pos.x = p.mouseX;
        pos.y = p.mouseY;

        if (hooked) {
            // if mouse is pressed and fishing line isn't fully shrinked yet
            if (p.mousePressed && lineLength > hookSize / 2) {
                lineLength -= 5; // shrink fishing line
            }
            // update position of hooked fish
            hookedFish.pos.x = pos.x;
            hookedFish.pos.y = pos.y + lineLength + hookedFish.w / 2;
        }
    }

    /*
     * Returns whether this fish is touching another fish. Returns false if
     * this fishing line is hooked onto a fish.
     */
    public boolean touchesFish(Fish other) {
        // calculates position of the hook at the bottom of fishing line
        float hookX = pos.x;
        float hookY = pos.y + lineLength;

        if (PApplet.abs(hookX - other.pos.x) < hookSize/2 + other.w/2 &&
            PApplet.abs(hookY - other.pos.y) < hookSize/2 + other.h/2) {
            return true && !hooked;
        }
        return false;
    }

    /*
     * Updates state of this fishing line to hooked state. Saves a reference
     * to hooked fish and also updates hooked fish's state.
     */
    public void hook(Fish fish) {
        hooked = true;
        hookedFish = fish;
        hookedFish.state = Fish.State.HOOKED;
    }

    /*
     * Updates state of this fishing line to regular unhooked state. Updates
     * hooked fish's state and removes reference to hooked fish.
     */
    public void unhook() {
        hookedFish.state = Fish.State.CAUGHT;
        hookedFish = null;
        hooked = false;
        lineLength = maxLineLength;
    }
}
