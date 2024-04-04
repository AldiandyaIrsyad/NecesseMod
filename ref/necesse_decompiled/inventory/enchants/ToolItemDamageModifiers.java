package necesse.inventory.enchants;

import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierList;

/** @deprecated */
@Deprecated
public class ToolItemDamageModifiers {
   public static final ModifierList LIST;
   public static final Modifier<Float> TOOL_DAMAGE;
   public static final Modifier<Float> MINING_SPEED;

   public ToolItemDamageModifiers() {
   }

   static {
      LIST = ToolItemModifiers.LIST;
      TOOL_DAMAGE = ToolItemModifiers.TOOL_DAMAGE;
      MINING_SPEED = ToolItemModifiers.MINING_SPEED;
   }
}
