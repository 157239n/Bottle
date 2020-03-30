package org.kelvinho.bottle;

import processing.core.*;
import javax.annotation.*;

public class Scene {
    private final PApplet sketch;
    private final PImage image;
    private PImage cutoutImage = null;
    private boolean viewingCutout = false;
    private boolean cornersChanged = true;
    private Corners corners;
    private PVector offsets;

    public Scene(PApplet sketch, String imagePath, Corners corners) {
        this.sketch = sketch;
        image = sketch.loadImage(imagePath);
        scaleImage(image);
        offsets = new PVector((sketch.width - image.width) / 2f, (sketch.height - image.height) / 2f);
        this.corners = corners;
    }

    public void draw() {
        if (viewingCutout) {
            if (cutoutImage == null) prepareCutOut();
            if (cornersChanged) {
                cornersChanged = false;
                prepareCutOut();
            }
            if (cutoutImage != null) sketch.image(cutoutImage, 0, 0);
        } else {
            sketch.image(image, offsets.x, offsets.y);
            corners.draw();
        }
    }

    public PImage getSquareImage() {
        PGraphics graphics = sketch.createGraphics(sketch.width, sketch.height);
        graphics.beginDraw();
        graphics.background(0);
        graphics.image(image, offsets.x, offsets.y);
        graphics.endDraw();
        return graphics.get();
    }

    @Nullable
    public PImage getCutoutImage() {
        if (cornersChanged) {
            cornersChanged = false;
            prepareCutOut();
        }
        return cutoutImage;
    }

    private void scaleImage(PImage image) {
        int max = Math.max(image.width, image.height);
        image.resize(sketch.width * image.width / max, sketch.height * image.height / max);
    }

    public void mouseClicked() {
        corners.push(sketch.mouseX, sketch.mouseY);
        cornersChanged = true;
    }

    public void keyPressed() {
        switch (sketch.key) {
            case 'd': // delete every recorded locations
                corners = new Corners(sketch);
                break;
            case 'v':
                viewingCutout = !viewingCutout;
                break;
            default:
        }
    }

    private void prepareCutOut() {
        if (!corners.readyForCutout()) return;
        PVector a = PVector.sub(corners.a(), offsets);
        PVector b = PVector.sub(corners.b(), offsets);
        PVector c = PVector.sub(corners.c(), offsets);
        PVector d = PVector.sub(corners.d(), offsets);
        PVector ab = PVector.sub(b, a);
        PVector ad = PVector.sub(d, a);
        PVector bc = PVector.sub(c, b);
        int width = (int) ab.mag();
        int height = (int) ad.mag();
        cutoutImage = sketch.createImage(width, height, PConstants.RGB);
        cutoutImage.loadPixels();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                PVector ae = PVector.mult(ad, j * 1f / height);
                PVector bf = PVector.mult(bc, j * 1f / height);
                PVector eg = PVector.add(ab, bf).sub(ae).mult(i * 1f / width);
                PVector g = PVector.add(a, ae).add(eg);
                cutoutImage.pixels[j * width + i] = image.pixels[((int) g.y) * image.width + ((int) g.x)];
            }
        }
        cutoutImage.updatePixels();
    }

    public Corners getCorners() {
        return corners;
    }
}
