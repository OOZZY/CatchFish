package main;

import processing.core.PApplet;
import processing.core.PVector;

/*
 * Creates a shoal of fish in a pond and allows user to fish using mouse input.
 */
public class Main extends PApplet {
    Pond pond;
    FishingLine fishingLine;
    Fish[] fishes = new Fish[15]; // fishes to draw/animate

    /*
     * Returns whether the mouse cursor is inside the window.
     */
    public boolean mouseIsInsideWindow() {
        if (mouseX > 5 && mouseX < width - 5 &&
            mouseY > 5 && mouseY < height - 5) {
            return true;
        }
        return false;
    }

    public void setup() {
        size(1200, 700);
        background(255);
        smooth();

        pond = new Pond(this, new PVector(10, height / 2),
                        width - 20, height / 2 - 10);
        fishingLine = new FishingLine(this, pond.h, 20);

        for (int i = 0; i < fishes.length; ++i) {
            // random size
            float fishW = random(75, 125);
            float fishH = fishW / 2;
            // random position
            float fishX = random(pond.pos.x + fishW / 2,
                                 pond.pos.x + pond.w - fishW / 2);
            float fishY = random(pond.pos.y + fishH / 2,
                                 pond.pos.y + pond.h - fishH / 2);
            // random velocity
            float fishVelX = random(1, 5);
            float fishVelY = 1;
            if ((int)random(100) % 2 == 0) { fishVelX *= -1; }
            if ((int)random(100) % 2 == 0) { fishVelY *= -1; }

            PVector fishPos = new PVector(fishX, fishY);
            PVector fishVel = new PVector(fishVelX, fishVelY);
            fishes[i] = new Fish(this, fishPos, fishW, fishH, fishVel);

        }
    }

    public void draw() {
        background(255);
        pond.draw();

        // update fishes
        for (int i = 0; i < fishes.length; ++i) {
            // move fishes away from pond boundaries
            fishes[i].detectBoundary(pond);
            // move fishes away from each other
            for (int j = i + 1; j < fishes.length; ++j) {
                fishes[i].detectCollision(fishes[j]);
            }
            // move fishes away from fishing line
            fishes[i].detectCollision(fishingLine);

            fishes[i].update();
            fishes[i].draw();
        }

        // make fishing line hook fishes
        for (int i = 0; i < fishes.length; ++i) {
            if (mousePressed && fishingLine.touchesFish(fishes[i]) &&
                fishes[i].state == Fish.State.NORMAL) {
                fishingLine.hook(fishes[i]);
            }
        }

        fishingLine.update();

        // draw fishing line conditionally
        if (!fishingLine.hooked) {
            // only draw if mouse is inside window and outside pond
            if (mouseIsInsideWindow() && pond.mouseIsOutside()) {
                fishingLine.draw();
            }
        } else {
            fishingLine.draw(); // always draw if fishing line is hooked
        }
    }

    public void mouseReleased() {
        // if mouse is released and fishing line is fully shrinked, unhook
        // fishing line
        if (fishingLine.lineLength <= fishingLine.hookSize / 2) {
            fishingLine.unhook();
        }
    }
}
