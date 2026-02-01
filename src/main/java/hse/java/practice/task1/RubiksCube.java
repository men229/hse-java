package hse.java.practice.task1;


import java.util.Arrays;
import java.util.Collections;

/**
 * Необходимо реализовать интерфейс Cube
 * При повороте передней грани, меняются верх низ право и лево
 */
public class RubiksCube implements Cube {

    private static final int EDGES_COUNT = 6;

    private final Edge[] edges = new Edge[EDGES_COUNT];

    /**
     * Создать валидный собранный кубик
     * грани разместить по ордеру в енуме цветов
     * грань 0 -> цвет 0
     * грань 1 -> цвет 1
     * ...
     */
    public RubiksCube() {
        CubeColor[] colors = CubeColor.values();
        for (int i = 0; i < 6; i++) {
            edges[i] = new Edge(colors[i]);
        }
    }

    private int[] getAdjacentFaces(EdgePosition pos) {
        return switch (pos) {
            case UP -> new int[]{4, 2, 5, 3};
            case DOWN -> new int[]{4, 3, 5, 2};
            case LEFT -> new int[]{0, 4, 1, 5};
            case RIGHT -> new int[]{0, 5, 1, 4};
            case FRONT -> new int[]{0, 3, 1, 2};
            case BACK -> new int[]{0, 2, 1, 3};
        };
    }

    private void readRing(CubeColor[] ring, EdgePosition pos) {
        int p = 0;
        int[] adj = getAdjacentFaces(pos);
        for (int i = 0; i < 3; i++, p++) {
            ring[p] = edges[adj[0]].getParts()[i][2];
        }
        for (int i = 0; i < 3; i++, p++) {
            ring[p] = edges[adj[1]].getParts()[i][0];
        }
        for (int i = 0; i < 3; i++, p++) {
            ring[p] = edges[adj[2]].getParts()[i][0];
        }
        for (int i = 0; i < 3; i++, p++) {
            ring[p] = edges[adj[3]].getParts()[i][2];
        }
    }

    private void rotateRing(CubeColor[] ring, RotateDirection direction) {
        switch (direction) {
            case CLOCKWISE -> Collections.rotate(Arrays.asList(ring), 3);
            case COUNTERCLOCKWISE -> Collections.rotate(Arrays.asList(ring), -3);
        }
    }

    private void writeRing(CubeColor[] ring, EdgePosition pos) {
        int p = 0;
        int[] adj = getAdjacentFaces(pos);
        for (int i = 0; i < 3; i++, p++) {
            edges[adj[0]].getParts()[i][2] = ring[p];
        }
        for (int i = 0; i < 3; i++, p++) {
            edges[adj[1]].getParts()[i][0] = ring[p];
        }
        for (int i = 0; i < 3; i++, p++) {
            edges[adj[2]].getParts()[i][0] = ring[p];
        }
        for (int i = 0; i < 3; i++, p++) {
            edges[adj[3]].getParts()[i][2] = ring[p];
        }
    }

    private void rotateUpRing(RotateDirection direction) {
        int n;
        if (direction == RotateDirection.CLOCKWISE) {
            n = 3;
        } else {
            n = 1;
        }
        for (int j = 0; j < n; j++) {
            CubeColor[] temp5 = Arrays.copyOf(edges[5].getParts()[0], 3);
            edges[5].getParts()[0] = Arrays.copyOf(edges[3].getParts()[0], 3);
            edges[3].getParts()[0] = Arrays.copyOf(edges[4].getParts()[0], 3);
            edges[4].getParts()[0] = Arrays.copyOf(edges[2].getParts()[0], 3);
            edges[2].getParts()[0] = temp5;
        }
    }

    private void rotateDownRing(RotateDirection direction) {
        int n;
        if (direction == RotateDirection.CLOCKWISE) {
            n = 1;
        } else {
            n = 3;
        }
        for (int j = 0; j < n; j++) {
            CubeColor[] temp5 = Arrays.copyOf(edges[5].getParts()[2], 3);
            edges[5].getParts()[2] = Arrays.copyOf(edges[3].getParts()[2], 3);
            edges[3].getParts()[2] = Arrays.copyOf(edges[4].getParts()[2], 3);
            edges[4].getParts()[2] = Arrays.copyOf(edges[2].getParts()[2], 3);
            edges[2].getParts()[2] = temp5;
        }
    }

    private void rotateRightRing(RotateDirection direction) {
        int n;
        if (direction == RotateDirection.CLOCKWISE) {
            n = 1;
        } else {
            n = 3;
        }
        for (int j = 0; j < n; j++) {
            CubeColor[] temp5 = new CubeColor[3];
            for (int i = 0; i < 3; i++) {
                temp5[i] = edges[5].getParts()[i][0];
            }

            for (int i = 0; i < 3; i++) {
                edges[5].getParts()[i][0] = edges[0].getParts()[i][2];
            }
            for (int i = 0; i < 3; i++) {
                edges[0].getParts()[i][2] = edges[4].getParts()[2 - i][2];
            }
            for (int i = 0; i < 3; i++) {
                edges[4].getParts()[i][2] = edges[1].getParts()[i][2];
            }
            for (int i = 0; i < 3; i++) {
                edges[1].getParts()[i][2] = temp5[2 - i];
            }
        }
    }

    private void rotateLeftRing(RotateDirection direction) {
        int n;
        if (direction == RotateDirection.CLOCKWISE) {
            n = 1;
        } else {
            n = 3;
        }
        for (int j = 0; j < n; j++) {
            CubeColor[] temp4 = new CubeColor[3];
            for (int i = 0; i < 3; i++) {
                temp4[i] = edges[4].getParts()[i][0];
            }

            for (int i = 0; i < 3; i++) {
                edges[4].getParts()[i][0] = edges[0].getParts()[i][0];
            }
            for (int i = 0; i < 3; i++) {
                edges[0].getParts()[i][0] = edges[5].getParts()[2 - i][2];
            }
            for (int i = 0; i < 3; i++) {
                edges[5].getParts()[i][2] = edges[1].getParts()[i][0];
            }
            for (int i = 0; i < 3; i++) {
                edges[1].getParts()[i][0] = temp4[2 - i];
            }
        }
    }

    private void rotateBackRing(RotateDirection direction) {
        int n;
        if (direction == RotateDirection.CLOCKWISE) {
            n = 1;
        } else {
            n = 3;
        }
        for (int j = 0; j < n; j++) {
            CubeColor[] temp2 = new CubeColor[3];
            for (int i = 0; i < 3; i++) {
                temp2[i] = edges[3].getParts()[i][2];
            }

            for (int i = 0; i < 3; i++) {
                edges[3].getParts()[i][2] = edges[1].getParts()[2][2 - i];
            }
            for (int i = 0; i < 3; i++) {
                edges[1].getParts()[2][i] = edges[2].getParts()[i][0];
            }
            for (int i = 0; i < 3; i++) {
                edges[2].getParts()[i][0] = edges[0].getParts()[0][2 - i];
            }
            for (int i = 0; i < 3; i++) {
                edges[0].getParts()[0][i] = temp2[i];
            }
        }
    }

    private void rotateFrontRing(RotateDirection direction){
        int n;
        if (direction == RotateDirection.CLOCKWISE) {
            n = 1;
        } else {
            n = 3;
        }
        for (int j = 0; j < n; j++) {
            CubeColor[] temp3 = new CubeColor[3];
            for (int i = 0; i < 3; i++) {
                temp3[i] = edges[3].getParts()[i][0];
            }

            for (int i = 0; i < 3; i++) {
                edges[3].getParts()[i][0] = edges[0].getParts()[2][i];
            }
            for (int i = 0; i < 3; i++) {
                edges[0].getParts()[2][i] = edges[2].getParts()[i][2];
            }
            for (int i = 0; i < 3; i++) {
                edges[2].getParts()[i][2] = edges[1].getParts()[0][2 - i];
            }
            for (int i = 0; i < 3; i++) {
                edges[1].getParts()[0][i] = temp3[2 - i];
            }
        }
    }

    @Override
    public void up(RotateDirection direction) {
        edges[0].rotate(direction);
        rotateUpRing(direction);
    }

    @Override
    public void down(RotateDirection direction) {
        edges[1].rotate(direction);
        rotateDownRing(direction);

    }

    @Override
    public void left(RotateDirection direction) {
        edges[2].rotate(direction);
        rotateLeftRing(direction);
    }

    @Override
    public void right(RotateDirection direction) {
        edges[3].rotate(direction);
        rotateRightRing(direction);
    }

    public void front(RotateDirection direction) {
        edges[4].rotate(direction);
        rotateFrontRing(direction);
    }

    @Override
    public void back(RotateDirection direction) {
        edges[5].rotate(direction);
        rotateBackRing(direction);
    }

    public Edge[] getEdges() {
        return edges;
    }

    @Override
    public String toString() {
        return Arrays.toString(edges);
    }
}
