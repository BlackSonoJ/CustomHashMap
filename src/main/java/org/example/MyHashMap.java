package org.example;

import java.util.*;

public class MyHashMap<K, V> implements Map<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private int size = 0;
    private float loadFactor;
    private int threshold;
    private Node<K, V>[] buckets;

    static class Node<K, V> implements Map.Entry<K, V> {
        final K key;
        V value;
        Node<K, V> next;

        public Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public  K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            var oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }

    static int hash(Object key) {
        return (key == null) ? 0 : key.hashCode() & 0x7FFFFFFF;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        threshold = threshold * 2;
        var oldBuckets = buckets;
        buckets = new Node[threshold];
        for (Node<K,V> bucket : oldBuckets) {
            var current = bucket;
            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        this.threshold = DEFAULT_INITIAL_CAPACITY;
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.buckets = new Node[this.threshold];
    }

    private int getBucketIndex(Object key) {
        return hash(key) % this.buckets.length;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        Node<K, V> current = buckets[getBucketIndex(key)];
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Node<K,V> bucket : buckets) {
            var current = bucket;
            while (current != null) {
                if (Objects.equals(current.value, value)) {
                    return true;
                }
                current = current.next;
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        var current = buckets[getBucketIndex(key)];
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (buckets[getBucketIndex(key)] == null) {
            buckets[getBucketIndex(key)] = new Node<>(key, value, null);
            size++;
            return null;
        }

        Node<K, V> prev;
        Node<K, V> current = buckets[getBucketIndex(key)];
        do {
            if (current.key.equals(key)) {
                current.setValue(value);
            }
            prev = current;
        } while ((current = current.next) != null);

        if ((float) size / buckets.length >= loadFactor) {
            resize();
        }

        prev.next = new Node<>(key, value, null);
        size++;
        return null;
    }

    @Override
    public V remove(Object key) {
        if (buckets[getBucketIndex(key)] == null) {
            return null;
        }

        Node<K, V> prev = null;
        Node<K, V> current = buckets[getBucketIndex(key)];
        do {
            if (current.key.equals(key)) {
                if  (prev == null) {
                    buckets[getBucketIndex(key)] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return current.value;
            }
            prev = current;
        } while ((current = current.next) != null);

        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        this.threshold = DEFAULT_INITIAL_CAPACITY;
        buckets = new Node[this.threshold];
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        var result = new HashSet<K>();
        for (Node<K,V> bucket : buckets) {
            var current = bucket;
            while (current != null) {
                result.add(current.key);
                current = current.next;
            }
        }
        return result;
    }

    @Override
    public Collection<V> values() {
        var result = new ArrayList<V>();
        for (Node<K,V> bucket : buckets) {
            var current = bucket;
            while (current != null) {
                result.add(current.value);
                current = current.next;
            }
        }
        return result;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        var result = new HashSet<Entry<K, V>>();
        for (Node<K,V> bucket : buckets) {
            var current = bucket;
            while (current != null) {
                result.add(current);
                current = current.next;
            }
        }
        return result;
    }
}
