package com.example.bullsandcows;

class Node {

    int value;
    Node nextNode;

    public Node(int value) {
        this.value = value;
    }
}

public class CircularLinkedList {


    private Node head = null;
    private Node tail = null;

    public Node getHead() {
        return head;
    }

    public void addNode(int value) {
        Node newNode = new Node(value);

        if (head == null) {
            head = newNode;
        } else {
            tail.nextNode = newNode;
        }
        tail = newNode;
        tail.nextNode = head;
    }

    public void traverseList() {
        Node currentNode = head;

        if (head != null) {
            do {
                currentNode = currentNode.nextNode;
            } while (currentNode != head);
        }
    }

    public Node containsNode(int searchValue) {
        Node currentNode = head;

        if (head == null) {
            return null;
        } else {
            do {
                if (currentNode.value == searchValue) {
                    return currentNode;
                }
                currentNode = currentNode.nextNode;
            } while (currentNode != head);
            return null;
        }
    }
}