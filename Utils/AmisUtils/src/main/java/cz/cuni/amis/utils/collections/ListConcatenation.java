/*
 * Copyright (C) 2013 Martin Cerny
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.cuni.amis.utils.collections;

import java.util.*;

/**
 * An unmodifiable list that combines several child lists.
 * The list represent view on the underlying lists, so if they are modified,
 * so is this list. This class is not thread-safe (i.e. parallel modification to
 * underlying lists may cause unexpected behaviour)
 * @author Martin Cerny
 */
public class ListConcatenation<T>  extends AbstractList<T> {
    
    private List<List<T>> lists;

    public static <T> List<T> concatenate(List<T> ... lists){
        return concatenate(Arrays.asList(lists));
    }
    
    /**
     * Concatenates lists into the most simple form.
     * <ul>
     * <li>Empty lists are ommitted</li>
     * <li>If there is only one non-empty list, the list is returned, instead of concatenation</li>
     * <li>If there is another ListConcatenation among parameters, it is expanded and its components are taken directly </li>
     * </ul>
     * @param <T>
     * @param lists
     * @return 
     */
    public static <T> List<T> concatenate(List<List<T>> lists){
        if(lists.isEmpty()){
            return Collections.EMPTY_LIST;
        } else {
            List<List<T>> nonEmptyBasicLists = new ArrayList<List<T>>(lists.size());
            for (List<T> list : lists) {
                if (list instanceof ListConcatenation) {
                    nonEmptyBasicLists.addAll(((ListConcatenation<T>) list).lists);
                } else {
                    if (!list.isEmpty()) {
                        nonEmptyBasicLists.add(list);
                    }
                }
            }
            if (lists.size() == 1) {
                return lists.get(0);
            } else {
                return new ListConcatenation<T>(lists);
            }
        }
    }

    public ListConcatenation(List<List<T>> lists) {
        this.lists = new ArrayList<List<T>>(lists);
    }
    
    public ListConcatenation(List<T> ... lists){
        this.lists = Arrays.asList(lists);
    }
    
    @Override
    public T get(int index) {
        int listId = 0;
        while(listId < lists.size() && index >= lists.get(listId).size()){
            index -= lists.get(listId).size();
            listId++;
        }
        if(listId > lists.size()){
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        
        return lists.get(listId).get(index);
    }

    @Override
    public int size() {
        int totalSize = 0;
        for(List<T> list : lists){
            totalSize += list.size();
        }
        return totalSize;
    }
    
    
    
}
