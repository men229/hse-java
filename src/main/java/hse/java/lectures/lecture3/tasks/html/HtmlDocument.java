package hse.java.lectures.lecture3.tasks.html;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

public class HtmlDocument {

    public HtmlDocument(String filePath) {
        this(Path.of(filePath));
    }

    public HtmlDocument(Path filePath) {
        String content = readFile(filePath);
        validate(content);
    }

    private String readFile(Path filePath) {
        try {
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + filePath, e);
        }
    }

    private void validate(String content) {
        Deque<String> deque_part = new ArrayDeque<>();
        int[] used = new int[3];
        Set<String> set = Set.of("html", "head", "body", "div", "p");
        boolean open_head = false;
        boolean open_body = false;

        for (int i = 0; i < content.length(); i++) {
            int flag = 0;
            StringBuilder str = new StringBuilder();
            char c = content.charAt(i);
            if (c == '<') {
                i++;
                if (content.charAt(i) == '/') {
                    i += 1;
                    flag = 1;
                }
                c = content.charAt(i);
                while (c != '>' && c != ' ') {
                    str.append(c);
                    i++;
                    c = content.charAt(i);
                }
                if (!set.contains(str.toString().toLowerCase())) {
                    throw new UnsupportedTagException("Invalid command");
                }
            }
            if (str.toString().isEmpty() && !set.contains(str.toString())) {
                continue;
            }
            if (str.toString().equalsIgnoreCase("html")) {
                if (used[0] >= 2) {
                    throw new InvalidStructureException("Invalid struct");
                }
                used[0] += 1;
            } else if (str.toString().equalsIgnoreCase("head")) {
                open_head = true;
                if(used[0] == 0 || used[2] > 0){
                    throw new InvalidStructureException("Invalid struct");
                }
                if (used[1] >= 2) {
                    throw new InvalidStructureException("Invalid struct");
                }
                used[1] += 1;
            }else if (str.toString().equalsIgnoreCase("body")) {
                open_body = true;
                if (used[2] >= 2) {
                    throw new InvalidStructureException("Invalid struct");
                }
                used[2] += 1;
            }

            if (flag == 0) {

                if (c == ' ') {
                    while (c != '>') {
                        i++;
                        c = content.charAt(i);
                    }
                }
                deque_part.offerFirst(str.toString().toLowerCase());
            } else {
                if (deque_part.isEmpty()) {
                    throw new UnexpectedClosingTagException("Unexpected tag");
                }
                if (deque_part.getFirst().contentEquals(str.toString().toLowerCase())) {
                    deque_part.removeFirst();
                } else {
                    throw new MismatchedClosingTagException("Mismatched tag");
                }
            }
            if(!open_head && !open_body && !str.toString().equalsIgnoreCase("html")){
                throw new InvalidStructureException("Invalid struct");
            }
        }
        if (!deque_part.isEmpty()) {
            throw new UnclosedTagException("Unclosed tag");
        }
    }
}
