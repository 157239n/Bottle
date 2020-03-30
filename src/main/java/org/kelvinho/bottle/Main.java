package org.kelvinho.bottle;

import processing.core.PApplet;

import javax.swing.*;
import java.io.File;

public class Main extends PApplet {
    public static final float EPSILON = 1e-6f;
    private SceneIterator sceneIterator;
    private final String folder;

    public Main(String folder) {
        this.folder = folder;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(null, "You need to specify the folder where the data is", "No folder selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String folder = chooser.getSelectedFile().getAbsolutePath();
        // check whether the data file has the "original" folder
        if (!new File(folder + File.separator + "/original").exists()) {
            JOptionPane.showMessageDialog(null, "There's supposed to be a folder called \"original\" with a bunch of images under " + folder + ". Check everything and try again", "No images found", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String instructions = "This program will:\n" +
                "- Read through all images\n" +
                "- Give you a chance to label 4 corners of something of your interest\n" +
                "- Save the coordinates into labels.txt\n" +
                "- Save 2 square images of 1000 and 224 pixels wide to \"1000px\" and \"224px\" folders\n" +
                "- Save the cutout images to the \"cutout\" folder\n\n" +
                "You can press your mouse at the 4 corners sequentially. First the upper left corner, then upper right, then lower right, then lower left. If you press another time, it will change the current upper left corner\n" +
                "There are also other keyboard shortcuts available for you:\n" +
                "- n: next image. Automatically saves everything\n" +
                "- p: previous image. Automatically saves everything\n" +
                "- v: toggle between cutout and normal image\n" +
                "- d: delete all current corners and start over for a particular image";
        JOptionPane.showMessageDialog(null, instructions, "Instructions", JOptionPane.INFORMATION_MESSAGE);
        Main main = new Main(folder);
        PApplet.runSketch(new String[]{""}, main);
        main.surface.setTitle("Corner chooser");
    }

    public void settings() {
        size(1000, 1000);
    }

    public void setup() {
        background(0);
        sceneIterator = new SceneIterator(this, folder);
    }

    public void draw() {
        background(0);
        sceneIterator.draw();
        noStroke();
        fill(255, 100);
        ellipse(mouseX, mouseY, 20, 20);
    }

    public void mouseClicked() {
        sceneIterator.mouseClicked();
    }

    public void keyPressed() {
        sceneIterator.keyPressed();
    }
}
