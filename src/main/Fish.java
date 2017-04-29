package main;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/*
 * A class that implements a fish, stores the fish's properties, and can
 * draw the fish onto a PApplet. The fish can detect collision against pond
 * boundaries, other fishes, and fishing lines. The fish has wavy movement.
 */
public class Fish {
    PApplet p; // PApplet to draw objects of this class onto
    PVector pos, vel; // position, velocity
    float w, h; // width, height
    float theta; // variable used for tail animation/oscillation
    int[] colors = new int[4]; // colors for tail, fins, body, head
    float wave, waveSpeed, amp; // used for wavy movement

    /*
     * Fish is in normal state when it is swimming in a pond.
     * fish is in hooked state when it is hooked onto a fishing line.
     * fish is in caught state when it is caught and invisible.
     */
    enum State {NORMAL, HOOKED, CAUGHT}
    State state = State.NORMAL;

    /*
     * Constructor. Initializes position, width, height, and velocity with
     * given arguments.
     */
    public Fish(PApplet p_, PVector pos_, float w_, float h_, PVector vel_) {
        p = p_;
        pos = pos_;
        w = w_;
        h = h_;
        vel = vel_;
        theta = p.random(PConstants.TWO_PI);
        // generate random colors
        for (int i = 0; i < colors.length; ++i) {
            colors[i] = p.color((int)p.random(50, 200),
                                (int)p.random(50, 200),
                                (int)p.random(50, 200));
        }
        wave = p.random(PConstants.TWO_PI);
        waveSpeed = 0.1f;
        amp = 1;
    }

    /*
     * Draws fish.
     */
    public void draw() {
        if (state != State.CAUGHT) { // if fish's state is normal or hooked
            p.pushMatrix();

            p.translate(pos.x, pos.y);
            // scale to width and height specified in constructor
            p.scale(w / 200, h / 100);
            if (vel.x < 0) { // if moving left
                p.scale(-1, 1); // flip horizontally
            }

            if (state == State.HOOKED) {
                // make fish face upwards. works regardless of whether fish is
                // facing left or right because of the flip above
                p.rotate(PApplet.radians(-90));

                // rotate fish using perlin noise to make fish look like it's
                // struggling and wobbling
                p.rotate(PApplet.radians(p.noise(theta, theta) * 32 - 16));
            }

            p.stroke(0);
            p.strokeWeight(1);
            float sinTheta = PApplet.sin(theta); // a float between -1, 1

            // tail
            p.fill(colors[0]);
            for (int i = 0; i < 4; ++i) {
                // top half
                p.triangle(-100, -32 + 8*i + sinTheta*3, -75, 0, -40, 0);
                // bottom half
                p.triangle(-100, 32 - 8*i - sinTheta*3, -75, 0, -40, 0);
            }

            // body fins
            p.fill(colors[1]);
            // top left
            p.curve(100, 100, -40, -20, -10, -30, 100, 100);
            p.curve(50, 50, -40, -20, -10, -30, 50, 50);
            // top right
            p.curve(-80, 130, -10, -30, 50, -30, -80, 130);
            p.curve(-40, 65, -10, -30, 50, -30, -40, 65);
            // bottom left
            p.curve(100, -100, -40, 20, -10, 30, 100, -100);
            p.curve(50, -50, -40, 20, -10, 30, 50, -50);
            // bottom right
            p.curve(175, -125, 10, 30, 40, 30, 175, -125);
            p.curve(87.5f, -62.5f, 10, 30, 40, 30, 87.5f, -62.5f);

            // body
            p.fill(colors[2]);
            p.curve(0, 260, -60, 1, 100, 1, 40, 260); // top half
            p.curve(0, -260, -60, -1, 100, -1, 40, -260); // bottom half

            // body scales
            for (int j = 0; j < 2; ++j) {
                p.pushMatrix();
                p.translate(-10 + j * 28, 0); // move center of grid
                p.rotate(PConstants.PI / 4); // rotate around center of grid
                // draw grid
                for (int i = 0; i < 40; i += 10) {
                    p.line(i - 15, -20, i - 15, 20);
                    p.line(-20, i - 15, 20, i - 15);
                }
                p.popMatrix();
            }

            // head
            p.fill(colors[3]);
            p.curve(-250, -50, 60, -28, 60, 28, -250, 50); // right curve
            p.curve(150, -50, 60, -28, 60, 28, 150, 50); // left curve (gill)
            p.noFill();

            // mouth and eyes
            if (state == State.HOOKED) {
                p.line(75, 0, 100, 0); // no smile
                // make eyes look like an X
                p.line(65, -15, 75, -5);
                p.line(65, -5, 75, -15);
            } else { // if fish's state is normal
                p.curve(75, -30, 75, 0, 100, 0, 100, -30); // mouth/smile
                p.fill(0);
                p.ellipse(70, -10, 10, 10); // eye
                p.fill(255);
                p.ellipse(72, -11, 4, 4); // eye glint
            }

            p.popMatrix();
        }
    }

    /*
     * Moves fish.
     */
    public void update() {
        if (state == State.NORMAL) {
            // wavy movement
            wave += waveSpeed;
            // update position
            pos.x += vel.x;
            pos.y += vel.y + PApplet.sin(wave) * amp;
        }
        if (state != State.CAUGHT) {
            theta += 0.08;
        }
    }

    /*
     * Modifies fish's velocity if fish reaches pond's edges. If fish
     * exceeds left and right edges, reverse horizontal velocity. If fish
     * exceeds top and bottom edges, reverse vertical velocity
     */
    public void detectBoundary(Pond pond) {
        if (state == State.NORMAL) {
            // left edge
            if (pos.x < pond.pos.x + w/2) {
                pos.x = pond.pos.x + w/2;
                vel.x *= -1;
            }
            // right edge
            if (pos.x > pond.pos.x + pond.w - w/2) {
                pos.x = pond.pos.x + pond.w - w/2;
                vel.x *= -1;
            }
            // top edge
            if (pos.y < pond.pos.y + h/2) {
                pos.y = pond.pos.y + h/2;
                vel.y *= -1;
            }
            // bottom edge
            if (pos.y > pond.pos.y + pond.h - h/2) {
                pos.y = pond.pos.y + pond.h - h/2;
                vel.y *= -1;
            }
        }
    }

    /*
     * Modifies fish's velocity if fish collides with another fish.
     */
    public void detectCollision(Fish other) {
        if (state == State.NORMAL && other.state == State.NORMAL) {
            if (PApplet.abs(pos.x - other.pos.x) < w/2 + other.w/2 &&
                PApplet.abs(pos.y - other.pos.y) < h/2 + other.h/2) {
                float angle = PApplet.atan2(pos.y - other.pos.y,
                                            pos.x - other.pos.x);
                vel.x = p.random(1, 5) * PApplet.cos(angle);
                vel.y = PApplet.sin(angle);
                other.vel.x = p.random(1, 5) *
                              PApplet.cos(angle - PConstants.PI);
                other.vel.y = PApplet.sin(angle - PConstants.PI);
            }
        }
    }

    /*
     * Modifies fish's velocity if fish gets close to the given fishing line.
     */
    public void detectCollision(FishingLine other) {
        if (state == State.NORMAL && !other.hooked) {
            // calculates position of the hook at the bottom of fishing line
            float hookX = other.pos.x;
            float hookY = other.pos.y + other.lineLength;

            if (PApplet.abs(pos.x - hookX) < w/2 + other.hookSize*2 &&
                PApplet.abs(pos.y - hookY) < h/2 + other.hookSize*2) {
                float angle = PApplet.atan2(pos.y - hookY, pos.x - hookX);
                vel.x = p.random(1, 5) * PApplet.cos(angle);
                vel.y = PApplet.sin(angle);
            }
        }
    }
}
