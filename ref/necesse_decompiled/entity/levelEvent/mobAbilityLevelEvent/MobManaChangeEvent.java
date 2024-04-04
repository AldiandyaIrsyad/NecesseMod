package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.level.maps.hudManager.floatText.DamageText;

public class MobManaChangeEvent extends MobAbilityLevelEvent {
   private float finalMana;
   private float change;

   public MobManaChangeEvent() {
   }

   public MobManaChangeEvent(Mob var1, float var2, float var3) {
      super(var1, GameRandom.globalRandom);
      if (var3 == 0.0F) {
         throw new IllegalArgumentException("Cannot send a mana change event with 0 change.");
      } else {
         this.finalMana = var2;
         this.change = var3;
      }
   }

   public MobManaChangeEvent(Mob var1, float var2) {
      this(var1, Math.max(0.0F, var1.getMana() + var2), var2);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.finalMana = var1.getNextFloat();
      this.change = var1.getNextFloat();
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.finalMana);
      var1.putNextFloat(this.change);
   }

   public void init() {
      super.init();
      if (this.owner != null) {
         this.owner.setManaHidden(this.finalMana);
         if (this.isClient() && Math.abs(this.change) >= 1.0F) {
            int var1 = (int)this.change;
            this.level.hudManager.addElement(new DamageText(this.owner, var1, var1 > 0 ? new Color(51, 133, 224) : new Color(133, 51, 224), GameRandom.globalRandom.getIntBetween(30, 40)));
         }
      }

   }
}
