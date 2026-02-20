package hse.java.lectures.lecture3.practice.randomSet;

import java.util.Random;

public class RandomSet<T extends Comparable<T>> {

    private static class Node<V extends Comparable<V>> {
        int priority;
        V value;
        int size = 1;
        int index;
        Node<V> left = null;
        Node<V> right = null;
    }

    private final Random rand = new Random();

    private Node<T> root = null;

    private Object[] randomArray = new Object[1024];

    private int size = 0;

    private void add_array(T x) {
        if (size * 2 > randomArray.length) {
            int newLen = randomArray.length * 2;
            Object[] newArr = new Object[newLen];
            System.arraycopy(randomArray, 0, newArr, 0, randomArray.length);
            randomArray = newArr;
        }
        randomArray[size] = x;
        size += 1;
    }

    private int nodeSize(Node<T> n) {
        return n == null ? 0 : n.size;
    }

    private Node<T> descent(Node<T> root, T value) {
        if (root == null) {
            return null;
        }

        if (root.value.compareTo(value) > 0) {
            return descent(root.left, value);
        }

        if (root.value.compareTo(value) < 0) {
            return descent(root.right, value);
        }
        return root;
    }


    private Node<T>[] split(Node<T> root, T x) {
        if (root == null) {
            return new Node[]{null, null};
        } else if (x.compareTo(root.value) > 0) {
            Node<T>[] res = split(root.right, x);
            root.right = res[0];
            root.size = 1 + nodeSize(res[0]) + nodeSize(res[1]);
            return new Node[]{root, res[1]};
        }
        Node<T>[] res = split(root.left, x);
        root.left = res[1];
        root.size = 1 + nodeSize(res[0]) + nodeSize(res[1]);
        return new Node[]{res[0], root};
    }

    private Node<T> merge(Node<T> left, Node<T> right) {
        if (left == null) {
            return right;
        }

        if (right == null) {
            return left;
        }

        if (left.priority > right.priority) {
            left.right = merge(left.right, right);
            left.size = 1 + nodeSize(left.left) + nodeSize(left.right);
            return left;
        }
        right.left = merge(left, right.left);
        right.size = 1 + nodeSize(right.left) + nodeSize(right.right);
        return right;
    }

    public boolean insert(T value) {
        if (descent(root, value) != null) {
            return false;
        }
        Node<T> new_node = new Node<T>();
        new_node.value = value;
        new_node.priority = rand.nextInt();
        new_node.index = size;

        add_array(value);

        Node<T>[] res = split(root, value);
        Node<T> l1 = merge(res[0], new_node);

        root = merge(l1, res[1]);
        return true;
    }

    private Node<T> removeMin(Node<T> node) {
        if (node.left == null) {
            return node.right;
        }
        node.left = removeMin(node.left);
        node.size = 1 + nodeSize(node.left) + nodeSize(node.right);
        return node;
    }

    public boolean remove(T value) {
        Node<T> target = descent(root, value);
        if (target == null) {
            return false;
        }

        Node<T>[] res = split(root, value);
        Node<T> left = res[0];
        Node<T> right = res[1];

        right = removeMin(right);
        root = merge(left, right);

        int id_val = target.index;
        int id_end = size - 1;


        size -= 1;
        if (id_val != id_end) {
            randomArray[id_val] = randomArray[id_end];
            Node<T> end_node = descent(root, (T) randomArray[id_val]);
            end_node.index = id_val;
        }
        randomArray[id_end] = null;

        return true;
    }

    public boolean contains(T value) {
        return descent(root, value) != null;
    }

    public T getRandom() {
        if (size == 0) {
            throw new EmptySetException("Owch");
        }
        int n = rand.nextInt(size);
        return (T) randomArray[n];
    }
}
