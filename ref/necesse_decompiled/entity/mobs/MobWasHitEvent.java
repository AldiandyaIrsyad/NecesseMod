package necesse.entity.mobs;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.gameDamageType.DamageType;

public class MobWasHitEvent {
   public final Mob target;
   public final Attacker attacker;
   public final int beforeHealth;
   public final DamageType damageType;
   public final int damage;
   public final float knockbackX;
   public final float knockbackY;
   public final float knockbackAmount;
   public final boolean isCrit;
   public final GNDItemMap gndData;
   public final boolean showDamageTip;
   public final boolean playHitSound;
   public final boolean wasPrevented;

   public MobWasHitEvent(MobBeforeHitCalculatedEvent var1) {
      this.target = var1.target;
      this.attacker = var1.attacker;
      this.beforeHealth = this.target.getHealth();
      this.damageType = var1.damageType;
      this.damage = var1.damage;
      this.knockbackX = var1.knockbackX;
      this.knockbackY = var1.knockbackY;
      this.knockbackAmount = var1.knockbackAmount;
      this.isCrit = var1.isCrit;
      this.showDamageTip = var1.showDamageTip;
      this.playHitSound = var1.playHitSound;
      this.wasPrevented = var1.isPrevented();
      this.gndData = var1.gndData;
   }

   public MobWasHitEvent(Mob var1, Attacker var2, PacketReader var3) {
      this.target = var1;
      this.attacker = var2;
      this.beforeHealth = var1.getHealth();
      DamageType var4 = DamageTypeRegistry.getDamageType(var3.getNextByteUnsigned());
      if (var4 == null) {
         var4 = DamageTypeRegistry.NORMAL;
      }

      this.damageType = var4;
      this.damage = var3.getNextInt();
      this.knockbackX = var3.getNextFloat();
      this.knockbackY = var3.getNextFloat();
      this.knockbackAmount = var3.getNextFloat();
      this.isCrit = var3.getNextBoolean();
      this.gndData = new GNDItemMap(var3);
      this.showDamageTip = var3.getNextBoolean();
      this.playHitSound = var3.getNextBoolean();
      this.wasPrevented = var3.getNextBoolean();
   }

   public void writePacket(PacketWriter var1) {
      var1.putNextByteUnsigned(this.damageType.getID());
      var1.putNextInt(this.damage);
      var1.putNextFloat(this.knockbackX);
      var1.putNextFloat(this.knockbackY);
      var1.putNextFloat(this.knockbackAmount);
      var1.putNextBoolean(this.isCrit);
      this.gndData.writePacket(var1);
      var1.putNextBoolean(this.showDamageTip);
      var1.putNextBoolean(this.playHitSound);
      var1.putNextBoolean(this.wasPrevented);
   }
}
