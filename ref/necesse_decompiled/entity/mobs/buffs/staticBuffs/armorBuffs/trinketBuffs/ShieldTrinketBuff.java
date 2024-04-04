package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemInventoryItem;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.ActiveBuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.HumanDrawBuff;
import necesse.entity.mobs.buffs.staticBuffs.StaminaBuff;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.CombinedTrinketItem;
import necesse.inventory.item.trinketItem.ShieldTrinketItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class ShieldTrinketBuff extends TrinketBuff implements ActiveBuffAbility, HumanDrawBuff {
   public ShieldTrinketBuff() {
   }

   public InventoryItem getTrinketInventoryItem(GNDItem var1) {
      if (var1 instanceof GNDItemInventoryItem) {
         GNDItemInventoryItem var2 = (GNDItemInventoryItem)var1;
         InventoryItem var3 = var2.invItem;
         if (var3 != null && var3.item instanceof TrinketItem) {
            return var3;
         }
      }

      return null;
   }

   public ShieldTrinketItem getShieldItem(InventoryItem var1) {
      if (var1 != null) {
         if (var1.item instanceof ShieldTrinketItem) {
            return (ShieldTrinketItem)var1.item;
         }

         if (var1.item instanceof CombinedTrinketItem) {
            CombinedTrinketItem var2 = (CombinedTrinketItem)var1.item;
            return (ShieldTrinketItem)var2.streamCombinedTrinkets().filter((var0) -> {
               return var0 instanceof ShieldTrinketItem;
            }).map((var0) -> {
               return (ShieldTrinketItem)var0;
            }).findFirst().orElse((Object)null);
         }
      }

      return null;
   }

   public <T> T runShieldGet(GNDItem var1, BiFunction<ShieldTrinketItem, InventoryItem, T> var2, T var3) {
      InventoryItem var4 = this.getTrinketInventoryItem(var1);
      if (var4 != null) {
         ShieldTrinketItem var5 = this.getShieldItem(var4);
         if (var5 != null) {
            return var2.apply(var5, var4);
         }
      }

      return var3;
   }

   public <T> T runShieldGet(GNDItem var1, BiFunction<ShieldTrinketItem, InventoryItem, T> var2) {
      return this.runShieldGet(var1, var2, (Object)null);
   }

   public boolean runShieldMethod(GNDItem var1, BiConsumer<ShieldTrinketItem, InventoryItem> var2) {
      InventoryItem var3 = this.getTrinketInventoryItem(var1);
      if (var3 != null) {
         ShieldTrinketItem var4 = this.getShieldItem(var3);
         if (var4 != null) {
            var2.accept(var4, var3);
            return true;
         }
      }

      return false;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      int var3 = (Integer)this.runShieldGet(var1.getGndData().getItem("trinketItem"), (var1x, var2x) -> {
         return var1x.getShieldArmorValue(var2x, var1.owner);
      }, 0);
      if (var3 != 0) {
         var1.setModifier(BuffModifiers.ARMOR_FLAT, var3);
      }

   }

   public void onBeforeHit(ActiveBuff var1, MobBeforeHitEvent var2) {
      super.onBeforeHit(var1, var2);
      if (var1.owner.isServer() && !var2.isPrevented() && var1.owner.buffManager.hasBuff(BuffRegistry.SHIELD_ACTIVE)) {
         float var3 = (Float)this.runShieldGet(var1.getGndData().getItem("trinketItem"), (var1x, var2x) -> {
            return var1x.getShieldAngleCoverage(var2x, var1.owner);
         }, 270.0F);
         boolean var4 = false;
         float var5;
         if (var3 >= 360.0F) {
            var4 = true;
         } else {
            var5 = GameMath.getAngle(GameMath.normalize(var2.knockbackX, var2.knockbackY)) - 90.0F;
            float var6 = 0.0F;
            if (var1.owner.dir == 0) {
               var6 = 0.0F;
            } else if (var1.owner.dir == 1) {
               var6 = 90.0F;
            } else if (var1.owner.dir == 2) {
               var6 = 180.0F;
            } else if (var1.owner.dir == 3) {
               var6 = 270.0F;
            }

            float var7 = GameMath.getAngleDifference(var5, var6);
            if (Math.abs(var7) <= var3 / 2.0F) {
               var4 = true;
            }
         }

         if (var4) {
            var5 = (Float)this.runShieldGet(var1.getGndData().getItem("trinketItem"), (var1x, var2x) -> {
               return var1x.getShieldFinalDamageMultiplier(var2x, var1.owner);
            }, 0.5F);
            if (var5 <= 0.0F) {
               var2.prevent();
               var2.showDamageTip = false;
               var2.playHitSound = false;
            } else {
               var2.damage = var2.damage.modFinalMultiplier(var5);
               var2.playHitSound = false;
            }

            var2.gndData.setItem("shieldItem", var1.getGndData().getItem("trinketItem"));
         }
      }

   }

   public void onWasHit(ActiveBuff var1, MobWasHitEvent var2) {
      super.onWasHit(var1, var2);
      this.runShieldMethod(var2.gndData.getItem("shieldItem"), (var2x, var3) -> {
         float var4 = var2x.getShieldStaminaUsageOnBlock(var3, var1.owner);
         StaminaBuff.useStaminaAndGetValid(var1.owner, var4);
         var2x.onShieldHit(var3, var1.owner, var2);
      });
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

      ActiveBuff var5 = new ActiveBuff(BuffRegistry.SHIELD_ACTIVE, var1, 1.0F, (Attacker)null);
      float var6 = (Float)this.runShieldGet(var2.getGndData().getItem("trinketItem"), (var1x, var2x) -> {
         return var1x.getShieldMinSlowModifier(var2x, var2.owner);
      }, 0.5F);
      var5.getGndData().setFloat("minSlow", var6);
      var1.buffManager.addBuff(var5, false);
   }

   public boolean tickActiveAbility(PlayerMob var1, ActiveBuff var2, boolean var3) {
      if (!var1.inLiquid() && !var1.isAttacking) {
         ActiveBuff var4 = var1.buffManager.getBuff(BuffRegistry.SHIELD_ACTIVE);
         float var6;
         if (var4 != null) {
            var4.setDurationLeftSeconds(1.0F);
         } else {
            ActiveBuff var5 = new ActiveBuff(BuffRegistry.SHIELD_ACTIVE, var1, 1.0F, (Attacker)null);
            var6 = (Float)this.runShieldGet(var2.getGndData().getItem("trinketItem"), (var1x, var2x) -> {
               return var1x.getShieldMinSlowModifier(var2x, var2.owner);
            }, 0.5F);
            var5.getGndData().setFloat("minSlow", var6);
            var1.buffManager.addBuff(var5, false);
         }

         int var7 = (Integer)this.runShieldGet(var2.getGndData().getItem("trinketItem"), (var1x, var2x) -> {
            return var1x.getShieldMSToDepleteStamina(var2x, var2.owner);
         }, 10000);
         if (var7 > 0) {
            var6 = 50.0F / (float)var7;
            if (!StaminaBuff.useStaminaAndGetValid(var1, var6)) {
               return false;
            }
         }
      } else {
         var1.buffManager.removeBuff(BuffRegistry.SHIELD_ACTIVE, false);
      }

      return !var3 || Control.TRINKET_ABILITY.isDown();
   }

   public void onActiveAbilityUpdate(PlayerMob var1, ActiveBuff var2, Packet var3) {
   }

   public void onActiveAbilityStopped(PlayerMob var1, ActiveBuff var2) {
      var1.buffManager.removeBuff(BuffRegistry.SHIELD_ACTIVE, false);
   }

   public ListGameTooltips getTrinketTooltip(TrinketItem var1, InventoryItem var2, PlayerMob var3) {
      ListGameTooltips var4 = super.getTrinketTooltip(var1, var2, var3);
      if (var1 instanceof ShieldTrinketItem) {
         ShieldTrinketItem var5 = (ShieldTrinketItem)var1;
         int var6 = var5.getShieldArmorValue(var2, var3);
         if (var6 != 0) {
            var4.add(Localization.translate("itemtooltip", "armorvalue", "value", (Object)var6));
         }
      }

      return var4;
   }

   public void addHumanDraw(ActiveBuff var1, HumanDrawOptions var2) {
      if (var1.owner.buffManager.hasBuff(BuffRegistry.SHIELD_ACTIVE)) {
         this.runShieldMethod(var1.getGndData().getItem("trinketItem"), (var1x, var2x) -> {
            var2.holdItem(var2x);
         });
      }

   }
}
