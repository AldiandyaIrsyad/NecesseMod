package necesse.inventory.container;

public enum ContainerAction {
   LEFT_CLICK,
   RIGHT_CLICK,
   QUICK_MOVE,
   QUICK_TRASH,
   QUICK_TRASH_ONE,
   QUICK_DROP,
   QUICK_DROP_ONE,
   TOGGLE_LOCKED,
   TAKE_ONE,
   QUICK_MOVE_ONE,
   QUICK_GET_ONE,
   RIGHT_CLICK_ACTION;

   private ContainerAction() {
   }

   public int getID() {
      return this.ordinal();
   }

   public static ContainerAction getContainerAction(int var0) {
      ContainerAction[] var1 = values();
      return var0 >= 0 && var0 < var1.length ? var1[var0] : null;
   }

   // $FF: synthetic method
   private static ContainerAction[] $values() {
      return new ContainerAction[]{LEFT_CLICK, RIGHT_CLICK, QUICK_MOVE, QUICK_TRASH, QUICK_TRASH_ONE, QUICK_DROP, QUICK_DROP_ONE, TOGGLE_LOCKED, TAKE_ONE, QUICK_MOVE_ONE, QUICK_GET_ONE, RIGHT_CLICK_ACTION};
   }
}
