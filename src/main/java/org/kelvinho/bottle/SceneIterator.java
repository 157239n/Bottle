package org.kelvinho.bottle;

import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public class SceneIterator {
    private final PApplet sketch;
    private final String folder;
    private int currentSceneIndex = 0;
    private ArrayList<String> imagePaths;
    private Scene currentScene;
    private ArrayList<Corners> listOfCorners;

    public SceneIterator(PApplet sketch, String folder) {
        this.sketch = sketch;
        this.folder = folder;
        imagePaths = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File(folder + File.separator + "original").list())));
        imagePaths.sort(Comparator.naturalOrder());
        //load labels data
        listOfCorners = new ArrayList<>(imagePaths.size());
        if (new File(folder + File.separator + "labels.txt").exists()) {
            String[] lines = sketch.loadStrings(folder + File.separator + "labels.txt");
            Stream.of(lines).filter((String a) -> !a.equals("")).forEach((String a) -> {
                Corners corners = new Corners(sketch);
                String[] splits = a.split(",");
                for (int i = 0; i < 4; i++)
                    corners.pushFromFile(Integer.parseInt(splits[1 + i * 2]), Integer.parseInt(splits[2 + i * 2]));
                listOfCorners.add(corners);
            });
        }
        load();
    }

    public void load() {
        currentSceneIndex = Math.min(Math.max(0, currentSceneIndex), imagePaths.size() - 1);
        new Thread(() -> currentScene = new Scene(sketch, folder + File.separator + "original" + File.separator + imagePaths.get(currentSceneIndex), listOfCorners.size() <= currentSceneIndex ? new Corners(sketch) : listOfCorners.get(currentSceneIndex).copy())).start();
    }

    /**
     * Saves 224x224, 1000x1000, cutout images and corners data
     */
    private void save() {
        final int currentSceneIndex = this.currentSceneIndex;
        final Scene currentScene = this.currentScene;
        new Thread(() -> {
            PImage screen = currentScene.getSquareImage();
            screen.save(folder + File.separator + "1000px" + File.separator + imagePaths.get(currentSceneIndex));
            screen.resize(224, 224);
            screen.save(folder + File.separator + "224px" + File.separator + imagePaths.get(currentSceneIndex));
            if (currentScene.getCutoutImage() != null) {
                currentScene.getCutoutImage().save(folder + File.separator + "cutout" + File.separator + imagePaths.get(currentSceneIndex));
            }
            if (listOfCorners.size() <= currentSceneIndex) {
                listOfCorners.add(currentScene.getCorners().copy());
            } else {
                listOfCorners.set(currentSceneIndex, currentScene.getCorners().copy());
            }
            saveLabels();
        }).start();
    }

    /**
     * Saves the label data.
     */
    public void saveLabels() {
        try {
            PrintWriter cout = new PrintWriter(folder + File.separator + "labels.txt");
            for (int i = 0; i < listOfCorners.size(); i++)
                cout.println(imagePaths.get(i).split("\\.")[0] + "," + listOfCorners.get(i).serialize());
            cout.close();
        } catch (FileNotFoundException ignored) {
        }
    }

    public void draw() {
        if (currentScene != null) currentScene.draw();
    }

    public void mouseClicked() {
        currentScene.mouseClicked();
    }

    public void keyPressed() {
        currentScene.keyPressed();
        switch (sketch.key) {
            case 'n':
                save();
                currentSceneIndex++;
                load();
                break;
            case 'p':
                save();
                currentSceneIndex--;
                load();
                break;
            case 'l':
                saveLabels();
            default:
        }
    }
}
