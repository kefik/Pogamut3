/*
 * Copyright (C) 2013 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
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
package cz.cuni.amis.utils;

/**
 * An object that may be used to combine two keys in a map.
 * Added benefit over {@link NKey} is that it is generic.
 * @author Martin Cerny
 * @see NKey
 */
public class PairKey<FIRST, SECOND> {
    private final FIRST first;
    private final SECOND second;

    public PairKey(FIRST first, SECOND second) {
        this.first = first;
        this.second = second;
    }

    public FIRST getFirst() {
        return first;
    }

    public SECOND getSecond() {
        return second;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.first != null ? this.first.hashCode() : 0);
        hash = 97 * hash + (this.second != null ? this.second.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PairKey<FIRST, SECOND> other = (PairKey<FIRST, SECOND>) obj;
        if (this.first != other.first && (this.first == null || !this.first.equals(other.first))) {
            return false;
        }
        if (this.second != other.second && (this.second == null || !this.second.equals(other.second))) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "Pair<" + first.toString() + ", " + second.toString();
    }
    
    
}
