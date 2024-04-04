package necesse.engine.util;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class AreaSpliterator<T> implements Spliterator<T> {
   protected int startX;
   protected int lastX;
   protected int lastY;
   protected int cX;
   protected int cY;
   protected boolean valid;

   public AreaSpliterator(int var1, int var2, int var3, int var4) {
      this.reset(var1, var2, var3, var4);
   }

   protected AreaSpliterator() {
   }

   protected void reset(int var1, int var2, int var3, int var4) {
      this.valid = var1 < var3 && var2 < var4;
      this.startX = var1;
      this.lastX = var3;
      this.lastY = var4;
      this.cX = var1;
      this.cY = var2;
   }

   public boolean tryAdvance(Consumer<? super T> var1) {
      if (this.cX >= this.lastX) {
         this.cX = this.startX;
         ++this.cY;
      }

      if (this.cY < this.lastY) {
         var1.accept(this.getPos(this.cX, this.cY));
         ++this.cX;
         return true;
      } else {
         return false;
      }
   }

   protected abstract T getPos(int var1, int var2);

   public Spliterator<T> trySplit() {
      if (!this.valid) {
         return null;
      } else {
         int var1 = this.lastY - this.cY - 1;
         int var2;
         if (var1 <= 0) {
            var2 = this.lastX - this.cX - 1;
            if (var2 > 0) {
               int var3 = this.cX + var2 / 2 + 1;
               AreaSpliterator var5 = new AreaSpliterator<T>(var3, this.cY, this.lastX, this.lastY) {
                  protected T getPos(int var1, int var2) {
                     return AreaSpliterator.this.getPos(var1, var2);
                  }
               };
               this.lastX = var3;
               return var5;
            } else {
               return null;
            }
         } else {
            var2 = this.cY + var1 / 2 + 1;
            AreaSpliterator var4 = new AreaSpliterator<T>(this.startX, var2, this.lastX, this.lastY) {
               protected T getPos(int var1, int var2) {
                  return AreaSpliterator.this.getPos(var1, var2);
               }
            };
            this.lastY = var2;
            return var4;
         }
      }
   }

   public long estimateSize() {
      if (!this.valid) {
         return 0L;
      } else {
         int var1 = this.lastX - this.cX;
         int var2 = this.lastX - this.startX;
         int var3 = this.lastY - this.cY - 1;
         return (long)var1 + (long)var3 * (long)var2;
      }
   }

   public int characteristics() {
      return 17489;
   }

   public static <T> Stream<T> stream(Spliterator<T> var0, boolean var1) {
      return StreamSupport.stream(var0, var1);
   }

   public static <T> Stream<T> stream(Spliterator<T> var0) {
      return stream(var0, false);
   }

   public Stream<T> stream(boolean var1) {
      return stream(this, var1);
   }

   public Stream<T> stream() {
      return stream(this);
   }
}
