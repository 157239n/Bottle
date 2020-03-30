package org.kelvinho.bottle;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Corners implements Cloneable {
    private PApplet sketch;
    private PVector[] locations;
    private int count = 0;

    public PVector a() {
        return locations[0];
    }

    public PVector b() {
        return locations[1];
    }

    public PVector c() {
        return locations[2];
    }

    public PVector d() {
        return locations[3];
    }

    public Corners(PApplet sketch) {
        this.sketch = sketch;
        locations = new PVector[4];
        for (int i = 0; i < locations.length; i++)
            locations[i] = new PVector(0, 0);
    }

    public void draw() {
        for (int i = 0; i < locations.length; i++)
            if (locationIsSet(i))
                sketch.ellipse(locations[i].x, locations[i].y, 20, 20);
    }

    private boolean locationIsSet(int index) {
        return Math.abs(locations[index].x) + Math.abs(locations[index].y) > Main.EPSILON;
    }

    public boolean readyForCutout() {
        for (int i = 0; i < locations.length; i++)
            if (!locationIsSet(i)) return false;
        return true;
    }

    public void push(float x, float y) {
        locations[count] = new PVector(x, y);
        count++;
        count %= 4;
    }

    public void pushFromFile(int x, int y) {
        push(x * sketch.width / 1000f, y * sketch.height / 1000f);
    }

    public Corners copy() {
        Corners clone = new Corners(sketch);
        PVector[] locations = new PVector[this.locations.length];
        for (int i = 0; i < locations.length; i++) {
            locations[i] = this.locations[i].copy();
        }
        clone.locations = locations;
        return clone;
    }

    public String serialize() {
        return Stream.of(new Float[]{locations[0].x, locations[0].y, locations[1].x, locations[1].y, locations[2].x, locations[2].y, locations[3].x, locations[3].y})
                .map(number -> String.valueOf((int) (number * 1000f / sketch.width)))
                .collect(Collectors.joining(","));
    }

    public String toString() {
        return Stream.of(new Float[]{locations[0].x, locations[0].y, locations[1].x, locations[1].y, locations[2].x, locations[2].y, locations[3].x, locations[3].y}).map(String::valueOf).collect(Collectors.joining(","));
    }
}
