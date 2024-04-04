package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.awt.Color;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MouseBeamLevelEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MouseBeamAttackHandler;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemStatTipList;
import necesse.level.maps.Level;

public class DragonLanceProjectileToolItem extends MagicProjectileToolItem {
   public DragonLanceProjectileToolItem() {
      super(1500);
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(2000);
      this.attackDamage.setBaseValue(60.0F).setUpgradedValue(1.0F, 70.0F);
      this.knockback.setBaseValue(10);
      this.velocity.setBaseValue(120);
      this.attackCooldownTime.setBaseValue(500);
      this.attackRange.setBaseValue(400);
      this.attackXOffset = 20;
      this.attackYOffset = 20;
      this.manaCost.setBaseValue(5.0F).setUpgradedValue(1.0F, 5.5F);
      this.resilienceGain.setBaseValue(1.0F);
   }

   public float getFinalAttackMovementMod(InventoryItem var1, PlayerMob var2) {
      return 0.0F;
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
      var4.add(Localization.translate("itemtooltip", "dragonlancetip"));
      return var4;
   }

   public void addStatTooltips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, boolean var5) {
      this.addAttackDamageTip(var1, var2, var3, var4, var5);
      this.addResilienceGainTip(var1, var2, var3, var4, var5);
      this.addCritChanceTip(var1, var2, var3, var4, var5);
      this.addManaCostTip(var1, var2, var3, var4);
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      float var11 = (Float)this.getEnchantment(var6).applyModifierLimited(ToolItemModifiers.ATTACK_SPEED, (Float)ToolItemModifiers.ATTACK_SPEED.defaultBuffValue);
      MouseBeamLevelEvent var12 = new MouseBeamLevelEvent(var4, var2, var3, var9, 50.0F, (float)this.getAttackRange(var6), this.getAttackDamage(var6), this.getKnockback(var6, var4), 250, var11, 0, this.getResilienceGain(var6), new Color(212, 75, 41));
      var1.entityManager.addLevelEventHidden(var12);
      byte var13 = 75;
      float var14 = this.getManaCost(var6) / (500.0F / (float)var13);
      float var15 = this.getManaCost(var6);
      if (var15 > 0.0F) {
         var4.useMana(var15, var4.isServerClient() ? var4.getServerClient() : null);
      }

      var4.startAttackHandler(new MouseBeamAttackHandler(var4, var7, var13, var9, var12, var14));
      if (var1.isServer()) {
         ServerClient var16 = var4.getServerClient();
         var1.getServer().network.sendToClientsWithEntityExcept(new PacketLevelEvent(var12), var12, var16);
      }

      return var6;
   }
}
