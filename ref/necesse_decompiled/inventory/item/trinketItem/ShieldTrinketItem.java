package necesse.inventory.item.trinketItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.light.GameLight;

public class ShieldTrinketItem extends TrinketItem {
   public int armorValue;
   public float minSlowModifier;
   public int msToDepleteStamina;
   public float staminaUsageOnBlock;
   public int damageTakenPercent;
   public float angleCoverage;
   public int knockback = 100;
   public ArrayList<String> trinketStringIDs = new ArrayList();

   public ShieldTrinketItem(Item.Rarity var1, int var2, float var3, int var4, float var5, int var6, float var7, int var8) {
      super(var1, var8);
      this.armorValue = var2;
      this.minSlowModifier = var3;
      this.msToDepleteStamina = var4;
      this.staminaUsageOnBlock = var5;
      this.damageTakenPercent = GameMath.limit(var6, 0, 100);
      this.angleCoverage = var7;
   }

   public ShieldTrinketItem addCombinedTrinkets(String... var1) {
      this.trinketStringIDs.addAll(Arrays.asList(var1));
      return this;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "shieldtip"));
      var4.add(Localization.translate("itemtooltip", "shieldmodifier", "percent", 100 - this.damageTakenPercent + "%"));
      var4.add((Object)this.getExtraShieldTooltips(var1, var2, var3));
      var4.add(Localization.translate("itemtooltip", "staminausertip"));
      return var4;
   }

   public ListGameTooltips getExtraShieldTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      return new ListGameTooltips();
   }

   public boolean holdsItem(InventoryItem var1, PlayerMob var2) {
      return this.holdTexture != null && var2 != null && var2.buffManager.hasBuff(BuffRegistry.SHIELD_ACTIVE);
   }

   public boolean holdItemInFrontOfArms(InventoryItem var1, PlayerMob var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, boolean var10, GameLight var11, float var12, GameTexture var13) {
      return true;
   }

   public int getShieldArmorValue(InventoryItem var1, Mob var2) {
      return this.armorValue;
   }

   public float getShieldMinSlowModifier(InventoryItem var1, Mob var2) {
      return this.minSlowModifier;
   }

   public int getShieldMSToDepleteStamina(InventoryItem var1, Mob var2) {
      return this.msToDepleteStamina;
   }

   public float getShieldStaminaUsageOnBlock(InventoryItem var1, Mob var2) {
      return this.staminaUsageOnBlock;
   }

   public float getShieldFinalDamageMultiplier(InventoryItem var1, Mob var2) {
      return (float)this.damageTakenPercent / 100.0F;
   }

   public float getShieldAngleCoverage(InventoryItem var1, Mob var2) {
      return this.angleCoverage;
   }

   public void onShieldHit(InventoryItem var1, Mob var2, MobWasHitEvent var3) {
      if (var2.isClient()) {
         PlayerMob var4 = var2.getLevel().getClient().getPlayer();
         if (var2 == var4) {
            this.playHitSound(var1, var2, var3);
         }
      } else if (var3.attacker instanceof Mob) {
         Mob var6 = (Mob)var3.attacker;
         float var5 = var6.getKnockbackModifier();
         if (var5 != 0.0F) {
            var6.knockback(var6.x - var2.x, var6.y - var2.y, (float)this.knockback / var5);
            var6.sendMovementPacket(false);
         }
      }

   }

   public void playHitSound(InventoryItem var1, Mob var2, MobWasHitEvent var3) {
      Screen.playSound(GameResources.cling, SoundEffect.effect(var2).volume(0.8F));
   }

   public TrinketBuff[] getBuffs(InventoryItem var1) {
      TrinketBuff[] var2 = new TrinketBuff[]{(TrinketBuff)BuffRegistry.getBuff("shieldtrinket")};
      TrinketItem[] var3 = (TrinketItem[])this.streamCombinedTrinkets().toArray((var0) -> {
         return new TrinketItem[var0];
      });
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         TrinketItem var6 = var3[var5];
         var2 = (TrinketBuff[])GameUtils.concat(var2, var6.getBuffs(var1));
      }

      return var2;
   }

   public boolean disabledBy(InventoryItem var1) {
      return super.disabledBy(var1) ? true : this.streamCombinedTrinkets().anyMatch((var1x) -> {
         return var1x.disabledBy(var1);
      });
   }

   public boolean disables(InventoryItem var1) {
      if (super.disables(var1)) {
         return true;
      } else {
         return this.trinketStringIDs.stream().anyMatch((var1x) -> {
            return var1x.equals(var1.item.getStringID());
         }) ? true : this.streamCombinedTrinkets().anyMatch((var1x) -> {
            return var1x.disables(var1);
         });
      }
   }

   public Stream<TrinketItem> streamCombinedTrinkets() {
      return this.trinketStringIDs.stream().map((var0) -> {
         return (TrinketItem)ItemRegistry.getItem(var0);
      });
   }
}
