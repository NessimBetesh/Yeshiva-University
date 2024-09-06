package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.stage6.Document;

import javax.lang.model.element.Element;
import java.util.NoSuchElementException;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {
   
    private int heapSize = 10;

    public MinHeapImpl() {
        elements = (E[]) new Comparable[10];

    }

    /**
     * @param element
     */
    @Override
    public void reHeapify(E element) {
        int index = getArrayIndex(element);
        if (index != -1) {
            upHeap(index);
            downHeap(index);
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * @param element
     * @return
     */
    @Override
    //hacrr protected
    protected int getArrayIndex(E element) {
        for (int i = 0; i < heapSize; i++) {
            if (elements[i] != null) {
                if (elements[i].equals(element)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    protected void doubleArraySize() {
        E[] newArray = (E[]) new Document[elements.length * 2];
        System.arraycopy(elements, 0, newArray, 0, elements.length);
        /*for (int i = 0; i < elements.length; i++) {
            newArray[i] = elements[i];
        }
         */
        elements = newArray;
    }
}
