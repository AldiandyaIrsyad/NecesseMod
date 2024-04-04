package necesse.engine.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GameLinkedList<T> implements Collection<T> {
   public final Object lock = new Object();
   private int size;
   private GameLinkedList<T>.Element first;
   private GameLinkedList<T>.Element last;

   public GameLinkedList() {
   }

   public GameLinkedList<T>.Element addFirst(T var1) {
      synchronized(this.lock) {
         if (this.first == null) {
            Element var3 = new Element(var1);
            this.first = var3;
            this.last = var3;
            this.size = 1;
            this.onAdded(var3);
            return var3;
         } else {
            return this.first.insertBefore(var1);
         }
      }
   }

   public GameLinkedList<T>.Element addLast(T var1) {
      synchronized(this.lock) {
         if (this.last == null) {
            Element var3 = new Element(var1);
            this.first = var3;
            this.last = var3;
            this.size = 1;
            this.onAdded(var3);
            return var3;
         } else {
            return this.last.insertAfter(var1);
         }
      }
   }

   public T getFirst() {
      synchronized(this.lock) {
         return this.first == null ? null : this.first.object;
      }
   }

   public GameLinkedList<T>.Element getFirstElement() {
      synchronized(this.lock) {
         return this.first;
      }
   }

   public T getLast() {
      synchronized(this.lock) {
         return this.last == null ? null : this.last.object;
      }
   }

   public GameLinkedList<T>.Element getLastElement() {
      synchronized(this.lock) {
         return this.last;
      }
   }

   public T removeFirst() {
      synchronized(this.lock) {
         Element var2 = this.getFirstElement();
         var2.remove();
         return var2.object;
      }
   }

   public T removeLast() {
      synchronized(this.lock) {
         Element var2 = this.getLastElement();
         var2.remove();
         return var2.object;
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public void clear() {
      synchronized(this.lock) {
         this.size = 0;
         this.first = null;
         this.last = null;
      }
   }

   public boolean contains(Object var1) {
      synchronized(this.lock) {
         for(Element var3 = this.getFirstElement(); var3 != null; var3 = var3.next()) {
            if (Objects.equals(var3.object, var1)) {
               return true;
            }
         }

         return false;
      }
   }

   public Object[] toArray() {
      synchronized(this.lock) {
         Object[] var2 = new Object[this.size];
         Element var3 = this.getFirstElement();

         for(int var4 = 0; var4 < this.size; ++var4) {
            var2[var4] = var3.object;
            var3 = var3.next();
         }

         return var2;
      }
   }

   public <T1> T1[] toArray(T1[] var1) {
      synchronized(this.lock) {
         if (var1.length < this.size) {
            return Arrays.copyOf(this.toArray(), this.size, var1.getClass());
         } else {
            Element var3 = this.getFirstElement();

            for(int var4 = 0; var4 < this.size; ++var4) {
               var1[var4] = var3 == null ? null : var3.object;
               if (var3 != null) {
                  var3 = var3.next();
               }
            }

            return var1;
         }
      }
   }

   public boolean add(T var1) {
      this.addLast(var1);
      return true;
   }

   public boolean remove(Object var1) {
      synchronized(this.lock) {
         for(Element var3 = this.getFirstElement(); var3 != null; var3 = var3.next()) {
            if (Objects.equals(var3.object, var1)) {
               var3.remove();
               return true;
            }
         }

         return false;
      }
   }

   public boolean containsAll(Collection<?> var1) {
      synchronized(this.lock) {
         Iterator var3 = var1.iterator();

         Object var4;
         do {
            if (!var3.hasNext()) {
               return true;
            }

            var4 = var3.next();
         } while(this.contains(var4));

         return false;
      }
   }

   public boolean addAll(Collection<? extends T> var1) {
      synchronized(this.lock) {
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Object var4 = var3.next();
            this.add(var4);
         }

         return true;
      }
   }

   public boolean removeAll(Collection<?> var1) {
      synchronized(this.lock) {
         boolean var3 = false;
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            Object var5 = var4.next();
            if (this.remove(var5)) {
               var3 = true;
            }
         }

         return var3;
      }
   }

   public boolean retainAll(Collection<?> var1) {
      synchronized(this.lock) {
         return this.removeIf((var1x) -> {
            return !var1.contains(var1x);
         });
      }
   }

   public boolean removeIf(Predicate<? super T> var1) {
      synchronized(this.lock) {
         Objects.requireNonNull(var1);
         boolean var3 = false;
         Element var4 = this.getFirstElement();

         while(var4 != null) {
            if (var1.test(var4.object)) {
               Element var5 = var4;
               var4 = var4.next();
               var5.remove();
               var3 = true;
            } else {
               var4 = var4.next();
            }
         }

         return var3;
      }
   }

   public void sort(Comparator<? super T> var1) {
      synchronized(this.lock) {
         Object[] var3 = this.toArray();
         Arrays.sort(var3, var1);
         Element var4 = this.getFirstElement();
         Object[] var5 = var3;
         int var6 = var3.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Object var8 = var5[var7];
            var4 = var4.replace(var8).next();
         }

      }
   }

   public Iterator<T> iterator() {
      return new GameLinkedListIterator((var0) -> {
         return var0.object;
      });
   }

   public Iterator<GameLinkedList<T>.Element> elementIterator() {
      return new GameLinkedListIterator((var0) -> {
         return var0;
      });
   }

   public ListIterator<T> listIterator() {
      return new GameListIterator((var0) -> {
         return var0.object;
      }, true);
   }

   public GameLinkedList<T>.GameListIterator<GameLinkedList<T>.Element> elementListIterator() {
      return new GameListIterator((var0) -> {
         return var0;
      }, false);
   }

   public Iterable<GameLinkedList<T>.Element> elements() {
      return new ElementIterable();
   }

   public Stream<GameLinkedList<T>.Element> streamElements() {
      return StreamSupport.stream(this.elements().spliterator(), false);
   }

   public void printDebug() {
      synchronized(this.lock) {
         int var2 = 0;
         System.out.println("Size: " + this.size + ", first: " + this.hexString(this.first) + ", last: " + this.hexString(this.last));

         for(Iterator var3 = this.elements().iterator(); var3.hasNext(); ++var2) {
            Element var4 = (Element)var3.next();
            System.out.println(var2 + ": " + this.hexString(var4) + " (" + var4.object + "), prev: " + this.hexString(var4.prev) + ", next: " + this.hexString(var4.next));
         }

      }
   }

   public String toString() {
      Iterator var1 = this.iterator();
      if (!var1.hasNext()) {
         return "[]";
      } else {
         StringBuilder var2 = new StringBuilder();
         var2.append('[');

         while(true) {
            Object var3 = var1.next();
            var2.append(var3 == this ? "(this GameLinkedList)" : var3);
            if (!var1.hasNext()) {
               return var2.append(']').toString();
            }

            var2.append(", ");
         }
      }
   }

   private String hexString(Object var1) {
      return var1 == null ? null : Integer.toHexString(var1.hashCode());
   }

   public void onAdded(GameLinkedList<T>.Element var1) {
   }

   public void onRemoved(GameLinkedList<T>.Element var1) {
   }

   public class Element {
      private boolean removed;
      private GameLinkedList<T>.Element prev;
      private GameLinkedList<T>.Element next;
      public final T object;

      private Element(T var2) {
         this.object = var2;
      }

      public GameLinkedList<T> getList() {
         return GameLinkedList.this;
      }

      public boolean hasNext() {
         synchronized(GameLinkedList.this.lock) {
            return this.next != null;
         }
      }

      public GameLinkedList<T>.Element next() {
         synchronized(GameLinkedList.this.lock) {
            if (this.removed) {
               throw new IllegalStateException("Cannot perform actions on removed element");
            } else {
               return this.next;
            }
         }
      }

      public GameLinkedList<T>.Element nextWrap() {
         synchronized(GameLinkedList.this.lock) {
            if (this.removed) {
               throw new IllegalStateException("Cannot perform actions on removed element");
            } else {
               return this.next == null ? GameLinkedList.this.first : this.next;
            }
         }
      }

      public boolean hasPrev() {
         synchronized(GameLinkedList.this.lock) {
            return this.prev != null;
         }
      }

      public GameLinkedList<T>.Element prev() {
         synchronized(GameLinkedList.this.lock) {
            if (this.removed) {
               throw new IllegalStateException("Cannot perform actions on removed element");
            } else {
               return this.prev;
            }
         }
      }

      public GameLinkedList<T>.Element prevWrap() {
         synchronized(GameLinkedList.this.lock) {
            if (this.removed) {
               throw new IllegalStateException("Cannot perform actions on removed element");
            } else {
               return this.prev == null ? GameLinkedList.this.last : this.prev;
            }
         }
      }

      public GameLinkedList<T>.Element insertAfter(T var1) {
         synchronized(GameLinkedList.this.lock) {
            if (this.removed) {
               throw new IllegalStateException("Cannot perform actions on removed element");
            } else {
               Element var3 = GameLinkedList.this.new Element(var1);
               var3.prev = this;
               var3.next = this.next;
               if (this.next != null) {
                  this.next.prev = var3;
               } else {
                  GameLinkedList.this.last = var3;
               }

               this.next = var3;
               GameLinkedList.this.size++;
               GameLinkedList.this.onAdded(var3);
               return var3;
            }
         }
      }

      public GameLinkedList<T>.Element insertBefore(T var1) {
         synchronized(GameLinkedList.this.lock) {
            if (this.removed) {
               throw new IllegalStateException("Cannot perform actions on removed element");
            } else {
               Element var3 = GameLinkedList.this.new Element(var1);
               var3.next = this;
               var3.prev = this.prev;
               if (this.prev != null) {
                  this.prev.next = var3;
               } else {
                  GameLinkedList.this.first = var3;
               }

               this.prev = var3;
               GameLinkedList.this.size++;
               GameLinkedList.this.onAdded(var3);
               return var3;
            }
         }
      }

      public GameLinkedList<T>.Element replace(T var1) {
         synchronized(GameLinkedList.this.lock) {
            if (this.removed) {
               throw new IllegalStateException("Cannot perform actions on removed element");
            } else {
               Element var3 = GameLinkedList.this.new Element(var1);
               var3.prev = this.prev;
               var3.next = this.next;
               if (this.prev != null) {
                  this.prev.next = var3;
               } else {
                  GameLinkedList.this.first = var3;
               }

               if (this.next != null) {
                  this.next.prev = var3;
               } else {
                  GameLinkedList.this.last = var3;
               }

               this.removed = true;
               GameLinkedList.this.onRemoved(this);
               GameLinkedList.this.onAdded(var3);
               return var3;
            }
         }
      }

      public void remove() {
         synchronized(GameLinkedList.this.lock) {
            if (this.removed) {
               throw new IllegalStateException("Cannot perform actions on removed element");
            } else {
               if (this.prev != null) {
                  this.prev.next = this.next;
               } else {
                  GameLinkedList.this.first = this.next;
               }

               if (this.next != null) {
                  this.next.prev = this.prev;
               } else {
                  GameLinkedList.this.last = this.prev;
               }

               GameLinkedList.this.size--;
               this.removed = true;
               GameLinkedList.this.onRemoved(this);
            }
         }
      }

      public void removePrev() {
         synchronized(GameLinkedList.this.lock) {
            if (this.prev != null) {
               this.prev.remove();
            }

         }
      }

      public void removePrevWrap() {
         synchronized(GameLinkedList.this.lock) {
            Element var2 = this.prevWrap();
            if (var2 != null) {
               var2.remove();
            }

         }
      }

      public void removeNext() {
         synchronized(GameLinkedList.this.lock) {
            if (this.next != null) {
               this.next.remove();
            }

         }
      }

      public void removeNextWrap() {
         synchronized(GameLinkedList.this.lock) {
            Element var2 = this.nextWrap();
            if (var2 != null) {
               var2.remove();
            }

         }
      }

      public boolean isRemoved() {
         synchronized(GameLinkedList.this.lock) {
            return this.removed;
         }
      }

      // $FF: synthetic method
      Element(Object var2, Object var3) {
         this(var2);
      }
   }

   private class GameLinkedListIterator<E> implements Iterator<E> {
      private GameLinkedList<T>.Element next;
      private Function<GameLinkedList<T>.Element, E> extractor;

      public GameLinkedListIterator(Function<GameLinkedList<T>.Element, E> var2) {
         this.extractor = var2;
         this.next = GameLinkedList.this.first;
      }

      public boolean hasNext() {
         synchronized(GameLinkedList.this.lock) {
            return this.next != null;
         }
      }

      public E next() {
         synchronized(GameLinkedList.this.lock) {
            Object var2 = this.extractor.apply(this.next);
            this.next = this.next.next;
            return var2;
         }
      }
   }

   public class GameListIterator<E> implements ListIterator<E> {
      private GameLinkedList<T>.Element current;
      private GameLinkedList<T>.Element next;
      private Function<GameLinkedList<T>.Element, E> extractor;
      private boolean isListObjectIterator;

      public GameListIterator(Function<GameLinkedList<T>.Element, E> var2, boolean var3) {
         this.extractor = var2;
         this.isListObjectIterator = var3;
         this.next = GameLinkedList.this.first;
      }

      public boolean hasNext() {
         synchronized(GameLinkedList.this.lock) {
            return this.next != null;
         }
      }

      public E next() {
         synchronized(GameLinkedList.this.lock) {
            if (this.next == null) {
               if (GameLinkedList.this.first == null) {
                  throw new NoSuchElementException("List is empty");
               } else {
                  throw new NoSuchElementException("We are at the end of the list");
               }
            } else {
               Object var2 = this.extractor.apply(this.next);
               this.current = this.next;
               this.next = this.next.next;
               return var2;
            }
         }
      }

      public boolean hasPrevious() {
         synchronized(GameLinkedList.this.lock) {
            return this.next != null && this.next.prev != null || GameLinkedList.this.last != null;
         }
      }

      public E previous() {
         synchronized(GameLinkedList.this.lock) {
            Object var2;
            if (this.next != null) {
               if (this.next.prev == null) {
                  throw new NoSuchElementException("We are at the beginning of the list");
               } else {
                  var2 = this.extractor.apply(this.next.prev);
                  this.current = this.next.prev;
                  this.next = this.next.prev;
                  return var2;
               }
            } else if (GameLinkedList.this.last == null) {
               throw new NoSuchElementException("List is empty");
            } else {
               var2 = this.extractor.apply(GameLinkedList.this.last);
               this.current = GameLinkedList.this.last;
               this.next = GameLinkedList.this.last;
               return var2;
            }
         }
      }

      public int nextIndex() {
         throw new UnsupportedOperationException("Indexes not supported in GameLinkedLists");
      }

      public int previousIndex() {
         throw new UnsupportedOperationException("Indexes not supported in GameLinkedLists");
      }

      public void remove() {
         if (this.current != null) {
            this.current.remove();
            this.current = null;
         } else {
            throw new IllegalStateException("No element has been returned");
         }
      }

      public void set(E var1) {
         if (!this.isListObjectIterator) {
            throw new UnsupportedOperationException("List iterator is not for list objects. Use GameLinkedListListIterator.setObject(T) instead");
         } else {
            this.setObject(var1);
         }
      }

      public void setObject(T var1) {
         if (this.current != null) {
            this.current.replace(var1);
         } else {
            throw new IllegalStateException("No element has been returned");
         }
      }

      public void add(E var1) {
         if (!this.isListObjectIterator) {
            throw new UnsupportedOperationException("List iterator is not for list objects. Use GameLinkedListListIterator.setObject(T) instead");
         } else {
            this.addObject(var1);
         }
      }

      public void addObject(T var1) {
         if (this.next == null) {
            GameLinkedList.this.addLast(var1);
         } else {
            this.next.insertBefore(var1);
         }

      }
   }

   private class ElementIterable implements Iterable<GameLinkedList<T>.Element> {
      private ElementIterable() {
      }

      public Iterator<GameLinkedList<T>.Element> iterator() {
         return GameLinkedList.this.new GameLinkedListIterator((var0) -> {
            return var0;
         });
      }

      // $FF: synthetic method
      ElementIterable(Object var2) {
         this();
      }
   }
}
