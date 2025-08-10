package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {
    private class CustomLinkedList<T>{
        private Node<T> head;
        private int size;

        public CustomLinkedList(){
            this.head = null;
            this.size = 0;
        }

        public class Node<T>{
            T data;
            Node<T> next;

            public Node(T data){
                this.data = data;
            }
        }

        public void add(T data){
            Node<T> newNode = new Node<>(data);
            if (!isEmpty()) {
                newNode.next = this.head;
            }
            this.head = newNode;
            this.size++;
        }

        public T remove(){
            if (isEmpty()) {
                return null;
            }
            T data = this.head.data;
            this.head = this.head.next;
            this.size--;
            return data;
        }

        public T nextItem(){
            if (isEmpty()) {
                return null;
            }
            else{
                return this.head.data;
            }
        }

        public int size(){
            return this.size;
        }

        public boolean isEmpty(){
            return this.size == 0;
        }

    }
    private CustomLinkedList<T> stack;

    public StackImpl(){
        this.stack = new CustomLinkedList<T>();
    }

    @Override
    public void push(T element) {
        this.stack.add(element);
    }

    @Override
    public T pop() {
        return this.stack.remove();
    }

    @Override
    public T peek() {
        return stack.nextItem();
    }

    @Override
    public int size() {
        return this.stack.size();
    }
}
