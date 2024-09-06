package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.undo.Command;


public class StackImpl<T> implements Stack<T> {
    private Node<T> top;
    private int size;

    private class Node<T> {
        T element;
        Node<T> next;

        Node(T element) {
            this.element = element;
        }
    }

    public StackImpl() {
        this.top = null;
        this.size = 0;
    }

    /**
     * @param element object to add to the Stack
     */
    @Override
    public void push(T element) {
        Node<T> newNode = new Node<>(element);
        newNode.next = top;
        top = newNode;
        size++;
    }

    /**
     * removes and returns element at the top of the stack
     *
     * @return element at the top of the stack, null if the stack is empty
     */
    @Override
    public T pop() {
        if(top == null){
            return null;
        }
        T element = top.element;
        top = top.next;
        size--;
        return element;
    }

    /**
     * @return the element at the top of the stack without removing it
     */
    @Override
    public T peek() {
        if (top == null){
            return null;
        }
        return top.element;
    }

    /**
     * @return how many elements are currently in the stack
     */
    @Override
    public int size() {
        return size;
    }
}
