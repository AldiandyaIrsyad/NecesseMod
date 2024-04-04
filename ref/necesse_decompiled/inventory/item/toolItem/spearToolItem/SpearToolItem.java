package necesse.inventory.item.toolItem.spearToolItem;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.toolItemEvent.ToolItemEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.SettlerWeaponItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpearToolItem extends ToolItem implements SettlerWeaponItem {
   public SpearToolItem(int var1) {
      super(var1);
      this.setItemCategory(new String[]{"equipment", "weapons", "meleeweapons"});
      this.setItemCategory(ItemCategory.equipmentManager, new String[]{"weapons", "meleeweapons"});
      this.keyWords.add("spear");
      this.damageType = DamageTypeRegistry.MELEE;
      this.attackXOffset = 26;
      this.attackYOffset = 16;
      this.width = 16.0F;
      this.resilienceGain.setBaseValue(2.0F);
      this.enchantCost.setUpgradedValue(1.0F, 2000);
   }

   public GameSprite getWorldItemSprite(InventoryItem var1, PlayerMob var2) {
      return this.getItemSprite(var1, var2);
   }

   protected ListGameTooltips getBaseTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getBaseTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "speartip"));
      return var4;
   }

   public void addStatTooltips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, boolean var5) {
      this.addAttackDamageTip(var1, var2, var3, var4, var5);
      this.addAttackSpeedTip(var1, var2, var3, var4);
      this.addResilienceGainTip(var1, var2, var3, var4, var5);
      this.addKnockbackTip(var1, var2, var3, var4);
      this.addCritChanceTip(var1, var2, var3, var4, var5);
   }

   public int getSettlerMinimumAttackRAnge(HumanMob var1, InventoryItem var2) {
      return this.getAttackRange(var2) / 2;
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      if (var8 == 0) {
         int var11 = this.getAttackAnimTime(var6, var4);
         ToolItemEvent var12 = new ToolItemEvent(var4, var9, var6, var2 - var4.getX(), var3 - var4.getY(), var11, var11 / 2);
         var1.entityManager.addLevelEventHidden(var12);
      }

      return var6;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      var2.attackItem(var3.getX(), var3.getY(), var6);
      int var7 = this.getAttackAnimTime(var6, var2);
      ToolItemEvent var8 = new ToolItemEvent(var2, var5, var6, var3.getX() - var2.getX(), var3.getY() - var2.getY() + var4, var7, var7 / 2, new HashMap());
      var1.entityManager.addLevelEventHidden(var8);
      return var6;
   }

   public ArrayList<Shape> getHitboxes(InventoryItem var1, AttackAnimMob var2, int var3, int var4, ToolItemEvent var5, boolean var6) {
      ArrayList var7 = new ArrayList();
      float var8 = (float)this.getAttackRange(var1);
      Point2D.Float var9 = GameMath.normalize((float)var3, (float)var4);
      float var10 = (float)Math.min(var2.getCurrentAttackDrawYOffset() + var2.getStartAttackHeight(), 0);
      Line2D.Float var11 = new Line2D.Float(var2.x + var9.x * var8 / 2.0F, var2.y + var9.y * var8 / 2.0F + var10, var2.x + var9.x * var8, var2.y + var9.y * var8 + var10);
      if (this.width > 0.0F) {
         var7.add(new LineHitbox(var11, this.width));
      } else {
         var7.add(var11);
      }

      return var7;
   }

   public DrawOptions getAttackDrawOptions(InventoryItem var1, Level var2, PlayerMob var3, int var4, float var5, float var6, float var7, GameSprite var8, InventoryItem var9, int var10, int var11, GameLight var12) {
      ItemAttackDrawOptions var13 = this.setupAttackDrawOptions(var1, var2, var3, var4, var5, var6, var7, var8, var9, var12);
      this.setDrawAttackRotation(var1, var13, var5, var6, var7);
      return var13.posThrust(var10, var11, var5, var6, var7);
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.pointRotation(var3, var4);
   }

   public boolean animDrawBehindHand() {
      return true;
   }

   public ToolItemEnchantment getRandomEnchantment(GameRandom var1, InventoryItem var2) {
      return (ToolItemEnchantment)Enchantable.getRandomEnchantment(var1, EnchantmentRegistry.meleeItemEnchantments, this.getEnchantmentID(var2), ToolItemEnchantment.class);
   }

   public boolean isValidEnchantment(InventoryItem var1, ItemEnchantment var2) {
      return EnchantmentRegistry.meleeItemEnchantments.contains(var2.getID());
   }

   public Set<Integer> getValidEnchantmentIDs(InventoryItem var1) {
      return EnchantmentRegistry.meleeItemEnchantments;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public ItemEnchantment getRandomEnchantment(GameRandom var1, InventoryItem var2) {
      return this.getRandomEnchantment(var1, var2);
   }
}
