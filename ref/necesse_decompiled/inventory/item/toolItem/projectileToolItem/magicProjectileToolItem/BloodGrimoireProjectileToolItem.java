package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ReturnLifeOnHitLevelEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MouseBeamAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.BloodGrimoireRightClickProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.ItemStatTipList;
import necesse.level.maps.Level;

public class BloodGrimoireProjectileToolItem extends MagicProjectileToolItem implements ItemInteractAction {
   public BloodGrimoireProjectileToolItem() {
      super(1750);
      this.rarity = Item.Rarity.EPIC;
      this.attackAnimTime.setBaseValue(2000);
      this.attackDamage.setBaseValue(84.0F).setUpgradedValue(1.0F, 90.0F);
      this.knockback.setBaseValue(10);
      this.velocity.setBaseValue(120);
      this.attackCooldownTime.setBaseValue(500);
      this.attackRange.setBaseValue(225);
      this.attackXOffset = 20;
      this.attackYOffset = 20;
      this.manaCost.setBaseValue(0.0F);
      this.lifeCost.setBaseValue(10);
      this.lifeSteal.setBaseValue(3);
      this.resilienceGain.setBaseValue(0.75F);
   }

   public float getAttackSpeedModifier(InventoryItem var1, Mob var2) {
      return 1.0F;
   }

   public boolean animDrawBehindHand() {
      return false;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.magicbolt1, SoundEffect.effect(var4).volume(0.2F).pitch(GameRandom.globalRandom.getFloatBetween(1.5F, 1.6F)));
      }

   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add((String)Localization.translate("itemtooltip", "bloodgrimoiretip"), 400);
      var4.add((String)Localization.translate("itemtooltip", "bloodgrimoiresecondarytip"), 400);
      return var4;
   }

   public void addStatTooltips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, boolean var5) {
      this.addAttackDamageTip(var1, var2, var3, var4, var5);
      this.addResilienceGainTip(var1, var2, var3, var4, var5);
      this.addCritChanceTip(var1, var2, var3, var4, var5);
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      float var11 = (Float)this.getEnchantment(var6).applyModifierLimited(ToolItemModifiers.ATTACK_SPEED, (Float)ToolItemModifiers.ATTACK_SPEED.defaultBuffValue);
      ReturnLifeOnHitLevelEvent var12 = new ReturnLifeOnHitLevelEvent(var4, var2, var3, var9, 50.0F, (float)this.getAttackRange(var6), this.getAttackDamage(var6), this.getKnockback(var6, var4), 500, var11, 0, this.getResilienceGain(var6), new Color(147, 16, 45), this.getLifeSteal(var6));
      var1.entityManager.addLevelEventHidden(var12);
      byte var13 = 75;
      int var14 = (int)((float)this.getFlatLifeCost(var6) / (500.0F / (float)var13));
      var4.startAttackHandler(new MouseBeamAttackHandler(var4, var7, var13, var9, var12, var14));
      if (var1.isServer()) {
         ServerClient var15 = var4.getServerClient();
         var1.getServer().network.sendToClientsAtExcept(new PacketLevelEvent(var12), (ServerClient)var15, var15);
      }

      return var6;
   }

   public boolean canLevelInteract(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      return !var4.buffManager.hasBuff(BuffRegistry.Debuffs.BLOOD_GRIMOIRE_COOLDOWN);
   }

   public InventoryItem onLevelInteract(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, PacketReader var9) {
      var4.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.BLOOD_GRIMOIRE_COOLDOWN, var4, 5.0F, (Attacker)null), false);
      if (var1.isServer()) {
         BloodGrimoireRightClickProjectile var10 = new BloodGrimoireRightClickProjectile(var1, var4, var4.x, var4.y, (float)var2, (float)var3, 80.0F, this.getAttackRange(var6), this.getAttackDamage(var6), this.getKnockback(var6, var4));
         var1.entityManager.projectiles.add(var10);
      }

      return var6;
   }

   public int getLevelInteractAttackAnimTime(InventoryItem var1, PlayerMob var2) {
      return 400;
   }

   public float getItemCooldownPercent(InventoryItem var1, PlayerMob var2) {
      return var2.buffManager.getBuffDurationLeftSeconds(BuffRegistry.Debuffs.BLOOD_GRIMOIRE_COOLDOWN) / 5.0F;
   }

   public ItemControllerInteract getControllerInteract(Level var1, PlayerMob var2, InventoryItem var3, boolean var4, int var5, LinkedList<Rectangle> var6, LinkedList<Rectangle> var7) {
      Point2D.Float var8 = var2.getControllerAimDir();
      Point var9 = this.getControllerAttackLevelPos(var1, var8.x, var8.y, var2, var3);
      return new ItemControllerInteract(var9.x, var9.y) {
         public DrawOptions getDrawOptions(GameCamera var1) {
            return null;
         }

         public void onCurrentlyFocused(GameCamera var1) {
         }
      };
   }
}
