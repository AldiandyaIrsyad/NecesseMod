package necesse.level.gameObject;

import java.awt.Color;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;

public class GroundSlimeObject extends ModularCarpetObject {
   public GroundSlimeObject() {
      super("groundslime", new Color(195, 193, 35));
      this.objectHealth = 1;
      this.isGrass = true;
      this.canPlaceOnShore = true;
   }

   public ModifierValue<Float> getSlowModifier(Mob var1) {
      return !var1.isHostile && !var1.isFlying() ? new ModifierValue(BuffModifiers.SLOW, 0.5F) : super.getSpeedModifier(var1);
   }
}
