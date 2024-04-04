package necesse.engine.util.gameAreaSearch;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class GameAreaPipeline<T_FIRST, T_IN, T_OUT> implements GameAreaStream<T_OUT> {
   private GameAreaSearch<T_FIRST> searcher;
   private final GameAreaPipeline<T_FIRST, T_FIRST, ?> firstStage;
   private final GameAreaPipeline<T_FIRST, ?, ?> prevStage;
   private GameAreaPipeline<T_FIRST, T_OUT, ?> nextStage;

   private GameAreaPipeline(GameAreaSearch<T_FIRST> var1, GameAreaPipeline<T_FIRST, T_FIRST, ?> var2, GameAreaPipeline<T_FIRST, ?, ?> var3) {
      this.searcher = var1;
      this.firstStage = var2;
      this.prevStage = var3;
   }

   public GameAreaPipeline(GameAreaSearch<T_FIRST> var1) {
      this.searcher = var1;
      this.firstStage = this;
      this.prevStage = null;
   }

   private <R> GameAreaStream<R> next(GameAreaPipeline<T_FIRST, T_OUT, R> var1) {
      this.nextStage = var1;
      return var1;
   }

   public GameAreaStream<T_OUT> filter(final Predicate<? super T_OUT> var1) {
      Objects.requireNonNull(var1);
      return this.next(new GameAreaPipeline<T_FIRST, T_OUT, T_OUT>(this.searcher, this.firstStage, this) {
         protected void handle(GameAreaSink<T_OUT> var1x, T_OUT var2) {
            if (var1.test(var2)) {
               var1x.accept(var2);
            }

         }
      });
   }

   public <R> GameAreaStream<R> map(final Function<? super T_OUT, ? extends R> var1) {
      Objects.requireNonNull(var1);
      return this.next(new GameAreaPipeline<T_FIRST, T_OUT, R>(this.searcher, this.firstStage, this) {
         protected void handle(GameAreaSink<R> var1x, T_OUT var2) {
            var1x.accept(var1.apply(var2));
         }
      });
   }

   public <R> GameAreaStream<R> flatMap(final Function<? super T_OUT, ? extends Iterable<? extends R>> var1) {
      Objects.requireNonNull(var1);
      return this.next(new GameAreaPipeline<T_FIRST, T_OUT, R>(this.searcher, this.firstStage, this) {
         protected void handle(GameAreaSink<R> var1x, T_OUT var2) {
            Iterator var3 = ((Iterable)var1.apply(var2)).iterator();

            while(var3.hasNext()) {
               Object var4 = var3.next();
               if (var1x.isCancelled()) {
                  break;
               }

               var1x.accept(var4);
            }

         }
      });
   }

   public long count() {
      AtomicLong var1 = new AtomicLong(0L);
      this.forEach((var1x) -> {
         var1.addAndGet(1L);
      });
      return var1.get();
   }

   protected void handle(GameAreaSink<T_OUT> var1, T_IN var2) {
   }

   protected void downStream(GameAreaSink<Object> var1, Object var2) {
      if (this.nextStage == null) {
         var1.accept(var2);
      } else {
         this.nextStage.next(var1, var2);
      }
   }

   protected void next(final GameAreaSink<Object> var1, Object var2) {
      this.handle(new GameAreaSink<T_OUT>() {
         public void cancel() {
            var1.cancel();
         }

         public boolean isCancelled() {
            return var1.isCancelled();
         }

         public void accept(T_OUT var1x) {
            if (GameAreaPipeline.this.nextStage != null) {
               GameAreaPipeline.this.nextStage.next(var1, var1x);
            } else {
               var1.accept(var1x);
            }

         }
      }, var2);
   }

   protected void forEachProgress(final Predicate<? super T_OUT> var1) {
      final AtomicBoolean var2 = new AtomicBoolean();

      while(!this.searcher.isDone()) {
         GameAreaSearch.FoundElement var3 = this.searcher.next();
         if (var3 != null) {
            GameAreaSink var4 = new GameAreaSink<Object>() {
               public void accept(Object var1x) {
                  if (!var1.test(var1x)) {
                     var2.set(true);
                     this.cancel();
                  }

               }
            };
            this.firstStage.downStream(var4, var3.element);
            if (var2.get()) {
               break;
            }
         }
      }

   }

   public void forEach(final Consumer<? super T_OUT> var1) {
      while(!this.searcher.isDone()) {
         GameAreaSearch.FoundElement var2 = this.searcher.next();
         if (var2 != null) {
            GameAreaSink var3 = new GameAreaSink<Object>() {
               public void accept(Object var1x) {
                  var1.accept(var1x);
               }
            };
            this.firstStage.downStream(var3, var2.element);
         }
      }

   }

   public boolean anyMatch(Predicate<? super T_OUT> var1) {
      AtomicBoolean var2 = new AtomicBoolean();
      this.forEachProgress((var2x) -> {
         if (var1.test(var2x)) {
            var2.set(true);
            return false;
         } else {
            return true;
         }
      });
      return var2.get();
   }

   public boolean allMatch(Predicate<? super T_OUT> var1) {
      AtomicBoolean var2 = new AtomicBoolean(true);
      this.forEachProgress((var2x) -> {
         if (!var1.test(var2x)) {
            var2.set(false);
            return false;
         } else {
            return true;
         }
      });
      return var2.get();
   }

   public boolean noneMatch(Predicate<? super T_OUT> var1) {
      return !this.anyMatch(var1);
   }

   public Optional<T_OUT> findFirst() {
      AtomicReference var1 = new AtomicReference(Optional.empty());
      this.forEachProgress((var1x) -> {
         var1.set(Optional.of(var1x));
         return false;
      });
      return (Optional)var1.get();
   }

   public <R, A> R findExtraDistance(final int var1, final Collector<? super T_OUT, A, R> var2) {
      final Object var3 = var2.supplier().get();
      final AtomicBoolean var4 = new AtomicBoolean(false);

      while(!this.searcher.isDone()) {
         final GameAreaSearch.FoundElement var5 = this.searcher.next();
         if (var5 != null) {
            GameAreaSink var6 = new GameAreaSink<Object>() {
               public void accept(Object var1x) {
                  var2.accumulator().accept(var3, var1x);
                  if (!var4.get()) {
                     if (var1 != Integer.MAX_VALUE) {
                        GameAreaPipeline.this.searcher.setMaxDistance(Math.min(GameAreaPipeline.this.searcher.getMaxDistance(), var5.distance + var1));
                     }

                     var4.set(true);
                  }

               }
            };
            this.firstStage.downStream(var6, var5.element);
         }
      }

      return var2.finisher().apply(var3);
   }

   public TreeSet<T_OUT> findExtraDistanceSorted(int var1, Comparator<? super T_OUT> var2) {
      return (TreeSet)this.findExtraDistance(var1, Collectors.toCollection(() -> {
         return new TreeSet(var2);
      }));
   }

   public Optional<T_OUT> findBestDistance(int var1, Comparator<? super T_OUT> var2) {
      TreeSet var3 = this.findExtraDistanceSorted(var1, var2);
      return !var3.isEmpty() ? Optional.of(var3.first()) : Optional.empty();
   }

   public <R, A> R findExtraItems(final int var1, final Collector<? super T_OUT, A, R> var2) {
      final Object var3 = var2.supplier().get();
      final AtomicInteger var4 = new AtomicInteger();

      while(!this.searcher.isDone()) {
         GameAreaSearch.FoundElement var5 = this.searcher.next();
         if (var5 != null) {
            GameAreaSink var6 = new GameAreaSink<Object>() {
               public void accept(Object var1x) {
                  var2.accumulator().accept(var3, var1x);
                  if (var4.addAndGet(1) > var1) {
                     this.cancel();
                  }

               }
            };
            this.firstStage.downStream(var6, var5.element);
         }
      }

      return var2.finisher().apply(var3);
   }

   public TreeSet<T_OUT> findExtraItemsSorted(int var1, Comparator<? super T_OUT> var2) {
      return (TreeSet)this.findExtraItems(var1, Collectors.toCollection(() -> {
         return new TreeSet(var2);
      }));
   }

   public Optional<T_OUT> findBestItems(int var1, Comparator<? super T_OUT> var2) {
      TreeSet var3 = this.findExtraItemsSorted(var1, var2);
      return !var3.isEmpty() ? Optional.of(var3.first()) : Optional.empty();
   }

   // $FF: synthetic method
   GameAreaPipeline(GameAreaSearch var1, GameAreaPipeline var2, GameAreaPipeline var3, Object var4) {
      this(var1, var2, var3);
   }
}
