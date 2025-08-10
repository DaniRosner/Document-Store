package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;
import java.util.NoSuchElementException;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {
    public MinHeapImpl() {
        elements = (E[]) new Comparable[5];
    }

    @Override
    public void reHeapify(E element) {
        upHeap(getArrayIndex(element));
        downHeap(getArrayIndex(element));
    }

    @Override
    protected int getArrayIndex(E element) {
        for(int i = 1; i < elements.length; i++){
            if(elements[i].equals(element)){
                return i;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    protected void doubleArraySize() {
        E[] temp = (E[]) new Comparable[elements.length * 2];
        for(int i = 0; i < elements.length; i++){
            temp[i] = elements[i];
        }
        elements = temp;
    }
}
