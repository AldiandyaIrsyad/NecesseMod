package necesse.engine.util;

public abstract class ByteIterator {
   private int curIndex;
   private int curBitIndex;
   private int curBit;
   private boolean throwIfIndexAboveSize;

   public ByteIterator() {
   }

   public ByteIterator(ByteIterator var1) {
      this.curIndex = var1.curIndex;
      this.curBitIndex = var1.curBitIndex;
      this.curBit = var1.curBit;
   }

   public void throwIfIndexAboveSize() {
      this.throwIfIndexAboveSize = true;
   }

   public abstract int getSizeOfData();

   public boolean hasNext() {
      return this.curIndex < this.getSizeOfData();
   }

   public void resetIndex() {
      this.resetIndex(0);
   }

   public void resetIndex(int var1) {
      this.curIndex = var1;
      this.curBitIndex = var1;
      this.curBit = 0;
   }

   protected void addIndex(int var1) {
      this.curIndex += var1;
      if (this.curBit == 0) {
         this.curBitIndex = this.curIndex;
      }

      if (this.throwIfIndexAboveSize && this.curIndex > this.getSizeOfData()) {
         throw new IndexOutOfBoundsException();
      }
   }

   protected int getNextIndex() {
      if (this.throwIfIndexAboveSize && this.curIndex > this.getSizeOfData()) {
         throw new IndexOutOfBoundsException();
      } else {
         return this.curIndex;
      }
   }

   protected int getNextBitIndex() {
      return this.curBitIndex;
   }

   protected int getNextBit() {
      int var1 = this.curBit++;
      if (this.curBit == 1) {
         this.addIndex(1);
      }

      if (this.curBit > 7) {
         this.curBit = 0;
         this.curBitIndex = this.getNextIndex();
      }

      return var1;
   }
}
