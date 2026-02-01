package hse.java.practice.task1;

import java.util.Arrays;
import java.util.Collections;

public class Edge {

    private CubeColor[][] parts;

    public Edge(CubeColor[][] parts) {
        this.parts = parts;
    }

    public Edge(CubeColor color) {
        this.parts = new CubeColor[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                parts[i][j] = color;
            }
        }
    }

    public Edge() {
        parts = new CubeColor[3][3];
    }

    public CubeColor[][] getParts() {
        return parts;
    }

    private void transpose() {
        for (int i = 0; i < 3; i++) {
            for (int j = i + 1; j < 3; j++) {
                CubeColor temp = parts[i][j];
                parts[i][j] = parts[j][i];
                parts[j][i] = temp;
            }
        }
    }

    private void reverse() {
        for (int i = 0; i < 3; i++) {
            Collections.reverse(Arrays.asList(parts[i]));
        }
    }

    public void rotate(RotateDirection direction) {
        switch (direction) {
            case CLOCKWISE -> {
                transpose();
                reverse();
            }
            case COUNTERCLOCKWISE -> {
                reverse();
                transpose();
            }
        }
    }

    public void setParts(CubeColor[][] parts) {
        this.parts = parts;
    }

    @Override
    public String toString() {
        return Arrays.deepToString(parts);
    }
}
