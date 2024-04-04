package necesse.inventory;

import necesse.engine.util.ComparableSequence;

public class SlotPriority {
   public final int slot;
   public final ComparableSequence<Integer> comparable;

   public SlotPriority(int var1, ComparableSequence<Integer> var2) {
      this.slot = var1;
      this.comparable = var2;
   }
}
