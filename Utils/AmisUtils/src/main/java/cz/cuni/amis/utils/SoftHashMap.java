package cz.cuni.amis.utils;

import java.util.*;
import java.lang.ref.*;

/**
 * The code has been obtained from site: http://www.java2s.com/Code/Java/Collections-Data-Structure/SoftHashMap.htm
 * (1.3.2008)<p><p>
 * Thank you java2s.com!
 * 
 * @author Jimmy
 */
public class SoftHashMap extends AbstractMap implements Map {

  private Set entrySet = null;
  private Map hash;
  private ReferenceQueue queue = new ReferenceQueue();

  static private class SoftKey extends SoftReference {
    private int hash;

    private SoftKey(Object k) {
      super(k);
      hash = k.hashCode();
    }
    private static SoftKey create(Object k) {
      if (k == null) {
        return null;
      } else {
        return new SoftKey(k);
      }
    }
    private SoftKey(Object k, ReferenceQueue q) {
      super(k, q);
      hash = k.hashCode();
    }
    private static SoftKey create(Object k, ReferenceQueue q) {
      if (k == null) {
        return null;
      } else {
        return new SoftKey(k, q);
      }
    }
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      } else if (!(o instanceof SoftKey)) {
        return false;
      }
      Object t = this.get();
      Object u = ((SoftKey)o).get();
      if ((t == null) || (u == null)) {
        return false;
      } else if (t == u) {
        return true;
      } else {
        return t.equals(u);
      }
    }
    public int hashCode() {
      return hash;
    }
  }

  private void processQueue() {
    SoftKey sk;
    while ((sk = (SoftKey)queue.poll()) != null) {
      hash.remove(sk);
    }
  }

  public SoftHashMap() {
    hash = new HashMap();
  }

  public SoftHashMap(Map t) {
    this(Math.max(2*t.size(), 11), 0.75f);
    putAll(t);
  }

  public SoftHashMap(int initialCapacity) {
    hash = new HashMap(initialCapacity);
  }

  public SoftHashMap(int initialCapacity, float loadFactor) {
    hash = new HashMap(initialCapacity, loadFactor);
  }

  public int size() {
    return entrySet().size();
  }

  public boolean isEmpty() {
    return entrySet().isEmpty();
  }

  public boolean containsKey(Object key) {
    return hash.containsKey(SoftKey.create(key));
  }

  public Object get(Object key) {
    return hash.get(SoftKey.create(key));
  }

  public Object put(Object key, Object value) {
    processQueue();
    return hash.put(SoftKey.create(key, queue), value);
  }

  public Object remove(Object key) {
    processQueue();
    return hash.remove(SoftKey.create(key));
  }

  public void clear() {
    processQueue();
    hash.clear();
  }

  private static class Entry implements Map.Entry {
    private Map.Entry ent;
    private Object key;

    Entry(Map.Entry ent, Object key) {
      this.ent = ent;
      this.key = key;
    }

    public Object getKey() {
      return key;
    }

    public Object getValue() {
      return ent.getValue();
    }

    public Object setValue(Object value) {
      return ent.setValue(value);
    }

    public boolean equals(Object o) {
      if (!(o instanceof Map.Entry)) {
        return false;
      } else {
        Map.Entry e = (Map.Entry)o;
        Object value = getValue();
        return (key==null ? e.getKey()==null : key.equals(e.getKey())) &&
               (value==null ? e.getValue()==null : value.equals(e.getValue()));
      }
    }

    public int hashCode() {
      Object value = getValue();
      return (((key == null) ? 0 : key.hashCode())
        ^ ((value == null) ? 0 : value.hashCode()));
    }

  }

  public Set entrySet() {
    if (entrySet == null) {
      entrySet = new EntrySet();
    }
    return entrySet;
  }

  private class EntrySet extends AbstractSet {
    Set set = hash.entrySet();

    public Iterator iterator() {

      return new Iterator() {
        Iterator iter = set.iterator();
        Entry next = null;

        public boolean hasNext() {
          while (iter.hasNext()) {
            Map.Entry ent = (Map.Entry)iter.next();
            SoftKey sk = (SoftKey)ent.getKey();
            Object k = null;
            if ((sk != null) && ((k = sk.get()) == null)) {
              /* Soft key has been cleared by GC */
              continue;
            }
            next = new Entry(ent, k);
            return true;
          }
          return false;
        }

        public Object next() {
          if ((next == null) && !hasNext()) {
            throw new NoSuchElementException();
          }
          Entry element = next;
          next = null;
          return element;
        }

        public void remove() {
          iter.remove();
        }
      };
    }

    public boolean isEmpty() {
      return !(iterator().hasNext());
    }

    public int size() {
      int size = 0;
      for (Iterator i = iterator(); i.hasNext(); i.next(), size++);
      return size;
    }

    public boolean remove(Object o) {
      processQueue();
      if (!(o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry e = (Map.Entry)o;
      Object ev = e.getValue();
      SoftKey sk = SoftKey.create(e.getKey());
      Object hv = hash.get(sk);
      if ((hv == null)
          ? ((ev == null) && hash.containsKey(sk)) : hv.equals(ev)) {
        hash.remove(sk);
        return true;
      }
      return false;
    }

    public int hashCode() {
      int h = 0;
      for (Iterator i = set.iterator(); i.hasNext();) {
        Map.Entry ent = (Map.Entry)i.next();
        SoftKey sk = (SoftKey)ent.getKey();
        Object v;
        if (sk == null) {
          continue;
        }
        h += (sk.hashCode()
          ^ (((v = ent.getValue()) == null) ? 0 : v.hashCode()));
      }
      return h;
    }
  }
}