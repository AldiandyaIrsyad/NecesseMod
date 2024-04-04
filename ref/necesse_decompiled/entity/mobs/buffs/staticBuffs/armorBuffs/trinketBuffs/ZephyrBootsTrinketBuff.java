package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.ActiveBuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.StaminaBuff;
import necesse.gfx.camera.GameCamera;

public class ZephyrBootsTrinketBuff extends TrinketBuff implements ActiveBuffAbility {
   public ZephyrBootsTrinketBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public Packet getStartAbilityContent(PlayerMob var1, ActiveBuff var2, GameCamera var3) {
      return this.getRunningAbilityContent(var1, var2);
   }

   public Packet getRunningAbilityContent(PlayerMob var1, ActiveBuff var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      StaminaBuff.writeStaminaData(var1, var4);
      return var3;
   }

   public boolean canRunAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
      if (var2.owner.isRiding()) {
         return false;
      } else {
         return var1.isServer() && Settings.giveClientsPower ? true : StaminaBuff.canStartStaminaUsage(var2.owner);
      }
   }

   public void onActiveAbilityStarted(PlayerMob var1, ActiveBuff var2, Packet var3) {
      PacketReader var4 = new PacketReader(var3);
      if (!var1.isServer() || Settings.giveClientsPower) {
         StaminaBuff.readStaminaData(var1, var4);
      }

      var1.buffManager.addBuff(new ActiveBuff(BuffRegistry.ZEPHYR_BOOTS_ACTIVE, var1, 1.0F, (Attacker)null), false);
   }

   public boolean tickActiveAbility(PlayerMob var1, ActiveBuff var2, boolean var3) {
      if (var1.inLiquid()) {
         var1.buffManager.removeBuff(BuffRegistry.ZEPHYR_BOOTS_ACTIVE, false);
      } else {
         ActiveBuff var4 = var1.buffManager.getBuff(BuffRegistry.ZEPHYR_BOOTS_ACTIVE);
         if (var4 != null) {
            var4.setDurationLeftSeconds(1.0F);
         } else {
            var1.buffManager.addBuff(new ActiveBuff(BuffRegistry.ZEPHYR_BOOTS_ACTIVE, var1, 1.0F, (Attacker)null), false);
         }

         if ((var1.moveX != 0.0F || var1.moveY != 0.0F) && (var1.dx != 0.0F || var1.dy != 0.0F)) {
            long var5 = 4000L;
            float var7 = 50.0F / (float)var5;
            if (!StaminaBuff.useStaminaAndGetValid(var1, var7)) {
               return false;
            }
         }
      }

      return !var3 || Control.TRINKET_ABILITY.isDown();
   }

   public void onActiveAbilityUpdate(PlayerMob var1, ActiveBuff var2, Packet var3) {
   }

   public void onActiveAbilityStopped(PlayerMob var1, ActiveBuff var2) {
      var1.buffManager.removeBuff(BuffRegistry.ZEPHYR_BOOTS_ACTIVE, false);
   }
}
