package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.ActiveBuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.StaminaBuff;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.ItemStatTip;

public class MyceliumHoodSetBonusBuff extends SetBonusBuff implements ActiveBuffAbility {
   public MyceliumHoodSetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public void onItemAttacked(ActiveBuff var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8) {
      super.onItemAttacked(var1, var2, var3, var4, var5, var6, var7, var8);
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

      var1.buffManager.addBuff(new ActiveBuff(BuffRegistry.MYCELIUM_HOOD_ACTIVE, var1, 1.0F, (Attacker)null), false);
   }

   public boolean tickActiveAbility(PlayerMob var1, ActiveBuff var2, boolean var3) {
      if (var1.inLiquid()) {
         var1.buffManager.removeBuff(BuffRegistry.MYCELIUM_HOOD_ACTIVE, false);
      } else {
         ActiveBuff var4 = var1.buffManager.getBuff(BuffRegistry.MYCELIUM_HOOD_ACTIVE);
         if (var4 != null) {
            var4.setDurationLeftSeconds(1.0F);
         } else {
            var1.buffManager.addBuff(new ActiveBuff(BuffRegistry.MYCELIUM_HOOD_ACTIVE, var1, 1.0F, (Attacker)null), false);
         }

         long var5 = 4000L;
         float var7 = 50.0F / (float)var5;
         if (!StaminaBuff.useStaminaAndGetValid(var1, var7)) {
            return false;
         }
      }

      return !var3 || Control.SET_ABILITY.isDown();
   }

   public void onActiveAbilityUpdate(PlayerMob var1, ActiveBuff var2, Packet var3) {
   }

   public void onActiveAbilityStopped(PlayerMob var1, ActiveBuff var2) {
      var1.buffManager.removeBuff(BuffRegistry.MYCELIUM_HOOD_ACTIVE, false);
   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add(Localization.translate("itemtooltip", "myceliumhoodset"));
      return var3;
   }

   public void addStatTooltips(LinkedList<ItemStatTip> var1, ActiveBuff var2, ActiveBuff var3) {
      super.addStatTooltips(var1, var2, var3);
      var2.getModifierTooltipsBuilder(true, true).addLastValues(var3).buildToStatList(var1);
   }
}
