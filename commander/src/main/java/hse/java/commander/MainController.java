package hse.java.commander;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntBinaryOperator;
import java.util.stream.Stream;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

public class MainController {

    @FXML
    public Button copy;
    @FXML
    public Button move;
    @FXML
    public Button delete;
    boolean focused = false;

    Path leftDir = null;
    Path rightDir = null;
    AnchorPane pane = new AnchorPane();
    Label viewPathLeft = new Label("");
    Label viewPathRight = new Label("");

    @FXML
    public ListView<Path> left;

    @FXML
    public ListView<Path> right;

    private void updateView(ListView<Path> target, Path dir) {
        target.getItems().clear();
        Path parentDir = dir.getParent();
        target.getItems().add(parentDir);

        try (Stream<Path> paths = Files.list(dir)) {
            paths.forEach(path -> target.getItems().add(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void customList(ListView<Path> target, Path dir) {
        target.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        target.setCellFactory(p -> new ListCell<Path>() {
            @Override
            protected void updateItem(Path path, boolean empty) {
                super.updateItem(path, empty);

                if (empty || path == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Path currentDir = (target == left) ? leftDir : rightDir;

                Path parent = currentDir == null ? null : currentDir.getParent();

                if (path.equals(parent)) {
                    setText("...");
                } else {
                    setText(path.getFileName().toString());
                }

            }
        });
    }

    private void setCustomClick(ListView<Path> left1, ListView<Path> right1, boolean chanceFocus) {
        left1.setOnMouseClicked(event -> {
            right1.getSelectionModel().clearSelection();
            focused = chanceFocus;
            if (event.getClickCount() == 2) {
                int index = left1.getSelectionModel().getSelectedIndex();
                if (index >= 0 && Files.isDirectory(left1.getItems().get(index))) {
                    if (!chanceFocus) {
                        leftDir = left1.getItems().get(index);
                    } else {
                        rightDir = right.getItems().get(index);
                    }
                    left1.getItems().clear();
                    updateView(left1, !chanceFocus ? leftDir : rightDir);
                }
            }
        });
    }

    @FXML
    public void setInitialDirs(Path leftStart, Path rightStart) {
        this.leftDir = leftStart;
        this.rightDir = rightStart;

        updateView(left, leftDir);
        updateView(right, rightDir);

        customList(left, leftDir);
        customList(right, rightDir);

        setCustomClick(left, right, false);
        setCustomClick(right, left, true);
    }

    private void clearSelect() {
        left.getSelectionModel().clearSelection();
        right.getSelectionModel().clearSelection();
    }

    public void handleMove(ActionEvent actionEvent) {
        System.out.println(focused);

        ListView<Path> nowSelected = !focused ? left : right;
        ListView<Path> target = focused ? left : right;
        Path targetDir = focused ? leftDir : rightDir;

        ObservableList<Path> selectionItem = nowSelected.getSelectionModel().getSelectedItems();

        for (Path sourcePath : List.copyOf(selectionItem))
            try {
                System.out.println("Перемещение: " + sourcePath + " : " + sourcePath.getFileName());
                Files.move(sourcePath, targetDir.resolve(sourcePath.getFileName()));
//                target.getItems().add(targetDir.resolve(sourcePath.getFileName()));
//                nowSelected.getItems().remove(sourcePath);
            } catch (IOException e) {
                System.err.println("Ошибка перемещения");
                e.printStackTrace();

            }
        updateView(left, leftDir);
        updateView(right, rightDir);
        clearSelect();
    }

    public void handleCopy(ActionEvent actionEvent) {
        ListView<Path> nowSelected = !focused ? left : right;
        ListView<Path> target = focused ? left : right;
        Path targetDir = focused ? leftDir : rightDir;

        ObservableList<Path> selectionItem = nowSelected.getSelectionModel().getSelectedItems();

        for (Path sourcePath : List.copyOf(selectionItem)) {
            try (Stream<Path> currentPath = Files.walk(sourcePath)) {
                currentPath.forEach(source -> {
                    try {
                        Path relativePath = sourcePath.relativize(source);
                        Path newPath = targetDir.resolve(sourcePath.getFileName()).resolve(relativePath);
                        if (Files.isDirectory(source)) {
                            Files.createDirectories(newPath);
                        } else {
                            Files.copy(source, newPath);
                        }
                        if (newPath.getParent().equals(targetDir)) {
//                            target.getItems().add(newPath);
                        }
                    } catch (IOException e) {
                        System.err.println("Ошибка копирования: " + source);
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                System.err.println("Ошибка рекурсивного копирования!");
            }
        }
        updateView(left, leftDir);
        updateView(right, rightDir);
        clearSelect();
    }

    public void handleDelete(ActionEvent actionEvent) {
        ListView<Path> nowSelected = !focused ? left : right;
        Path targetDir = !focused ? leftDir : rightDir;

        ObservableList<Path> selectionItem = nowSelected.getSelectionModel().getSelectedItems();

        for (Path sourcePath : List.copyOf(selectionItem)) {
            try (Stream<Path> currentPath = Files.walk(sourcePath).sorted(Comparator.reverseOrder())) {
                currentPath.forEach(source -> {
                    try {
                        Files.delete(source);
                        if (source.getParent().equals(targetDir)) {
//                            nowSelected.getItems().remove(source);
                        }
                    } catch (IOException e) {
                        System.err.println("Ошибка копирования: " + source);
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                System.err.println("Ошибка рекурсивного копирования!");
            }
        }
        updateView(left, leftDir);
        updateView(right, rightDir);
        clearSelect();
    }
}