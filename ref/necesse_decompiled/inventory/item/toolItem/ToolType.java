package necesse.inventory.item.toolItem;

public enum ToolType {
   UNBREAKABLE,
   NONE,
   ALL,
   AXE,
   PICKAXE,
   SHOVEL;

   private ToolType() {
   }

   public boolean canDealDamageTo(ToolType var1) {
      return var1 != UNBREAKABLE && (var1 == this || this == ALL || var1 == ALL);
   }

   // $FF: synthetic method
   private static ToolType[] $values() {
      return new ToolType[]{UNBREAKABLE, NONE, ALL, AXE, PICKAXE, SHOVEL};
   }
}
