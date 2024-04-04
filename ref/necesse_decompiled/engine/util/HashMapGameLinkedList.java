package necesse.engine.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

public class HashMapGameLinkedList<K, V> extends HashMapCollectionAbstract<K, V, GameLinkedList<V>> {
   public HashMapGameLinkedList() {
   }

   protected GameLinkedList<V> createNewList() {
      return new GameLinkedList();
   }

   protected void addToList(GameLinkedList<V> var1, V var2) {
      var1.addLast(var2);
   }

   protected void addAllToList(GameLinkedList<V> var1, Collection<V> var2) {
      var1.addAll(var2);
   }

   protected Stream<V> streamValues(GameLinkedList<V> var1) {
      return var1.stream();
   }

   protected boolean listContains(GameLinkedList<V> var1, V var2) {
      return var1.contains(var2);
   }

   protected boolean isListEmpty(GameLinkedList<V> var1) {
      return var1.isEmpty();
   }

   protected int getListSize(GameLinkedList<V> var1) {
      return var1.size();
   }

   protected boolean removeFromList(GameLinkedList<V> var1, V var2) {
      return var1.remove(var2);
   }

   public Stream<GameLinkedList<V>.Element> streamElements(K var1) {
      return ((GameLinkedList)this.get(var1)).streamElements();
   }

   public Iterator<GameLinkedList<V>.Element> elementIterator(K var1) {
      return ((GameLinkedList)this.get(var1)).elementIterator();
   }

   public Iterable<GameLinkedList<V>.Element> elements(K var1) {
      return ((GameLinkedList)this.get(var1)).elements();
   }

   public GameLinkedList<V>.Element addFirst(K var1, V var2) {
      return ((GameLinkedList)this.get(var1)).addFirst(var2);
   }

   public GameLinkedList<V>.Element addLast(K var1, V var2) {
      return ((GameLinkedList)this.get(var1)).addLast(var2);
   }

   public V getFirst(K var1) {
      return ((GameLinkedList)this.get(var1)).getFirst();
   }

   public V getLast(K var1) {
      return ((GameLinkedList)this.get(var1)).getLast();
   }

   public GameLinkedList<V>.Element getFirstElement(K var1) {
      return ((GameLinkedList)this.get(var1)).getFirstElement();
   }

   public GameLinkedList<V>.Element getLastElement(K var1) {
      return ((GameLinkedList)this.get(var1)).getLastElement();
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected boolean removeFromList(Object var1, Object var2) {
      return this.removeFromList((GameLinkedList)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected int getListSize(Object var1) {
      return this.getListSize((GameLinkedList)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected boolean isListEmpty(Object var1) {
      return this.isListEmpty((GameLinkedList)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected boolean listContains(Object var1, Object var2) {
      return this.listContains((GameLinkedList)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected Stream streamValues(Object var1) {
      return this.streamValues((GameLinkedList)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void addAllToList(Object var1, Collection var2) {
      this.addAllToList((GameLinkedList)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void addToList(Object var1, Object var2) {
      this.addToList((GameLinkedList)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected Object createNewList() {
      return this.createNewList();
   }
}
