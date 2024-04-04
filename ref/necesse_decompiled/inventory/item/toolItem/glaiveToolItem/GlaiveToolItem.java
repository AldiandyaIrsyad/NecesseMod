package necesse.inventory.item.toolItem.glaiveToolItem;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.toolItemEvent.ToolItemEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
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

public class GlaiveToolItem extends ToolItem implements SettlerWeaponItem {
   public GlaiveToolItem(int var1) {
      super(var1);
      this.setItemCategory(new String[]{"equipment", "weapons", "meleeweapons"});
      this.setItemCategory(ItemCategory.equipmentManager, new String[]{"weapons", "meleeweapons"});
      this.keyWords.add("glaive");
      this.damageType = DamageTypeRegistry.MELEE;
      this.attackXOffset = 50;
      this.attackYOffset = 50;
      this.width = 20.0F;
      this.resilienceGain.setBaseValue(2.0F);
   }

   public GameSprite getWorldItemSprite(InventoryItem var1, PlayerMob var2) {
      return this.getItemSprite(var1, var2);
   }

   public void addStatTooltips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, boolean var5) {
      this.addAttackDamageTip(var1, var2, var3, var4, var5);
      this.addAttackSpeedTip(var1, var2, var3, var4);
      this.addResilienceGainTip(var1, var2, var3, var4, var5);
      this.addKnockbackTip(var1, var2, var3, var4);
      this.addCritChanceTip(var1, var2, var3, var4, var5);
   }

   public int getSettlerAttackRange(HumanMob var1, InventoryItem var2) {
      return this.getAttackRange(var2) / 2;
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      if (var8 == 0) {
         int var11 = this.getAttackAnimTime(var6, var4);
         ToolItemEvent var12 = new ToolItemEvent(var4, var9, var6, var2 - var4.getX(), var3 - var4.getY() + var5, var11, var11 / 2);
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
      int var8 = 0;
      Mob var9 = var2.getMount();
      if (var9 != null) {
         var8 = var9.getRiderDrawYOffset();
      }

      int var10 = this.getAttackRange(var1) / 2;
      float var11 = var5.lastHitboxProgress;
      float var12 = var2.getAttackAnimProgress();
      float var13 = (float)(Math.PI * (double)var10 * 2.0);
      float var14 = Math.max(10.0F, this.width) / var13;

      for(float var15 = var11; var15 <= var12; var15 += var14) {
         this.addHitbox(var7, var2, var15, var3, var4, var10, var8);
         if (!var6) {
            var5.lastHitboxProgress = var15;
         }
      }

      return var7;
   }

   private void addHitbox(List<Shape> var1, AttackAnimMob var2, float var3, int var4, int var5, int var6, int var7) {
      Point2D.Float var8;
      if (var4 < 0) {
         var8 = GameMath.getAngleDir(-var3 * 360.0F - 110.0F);
      } else {
         var8 = GameMath.getAngleDir(var3 * 360.0F + 110.0F);
      }

      float var9 = var8.x * (float)var6;
      float var10 = var8.y * (float)var6;
      Line2D.Float var11 = new Line2D.Float(var2.x - var9, var2.y - var10 - (float)var2.getCurrentAttackHeight() + (float)var7, var2.x + var9, var2.y + var10 - (float)var2.getCurrentAttackHeight() + (float)var7);
      if (this.width > 0.0F) {
         var1.add(new LineHitbox(var11, this.width));
      } else {
         var1.add(var11);
      }

   }

   public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions var1, InventoryItem var2, PlayerMob var3, int var4, float var5, float var6, float var7, Color var8, GameLight var9) {
      ItemAttackDrawOptions.AttackItemSprite var10 = var1.itemSprite(this.getAttackSprite(var2, var3));
      var10.itemRotatePoint(this.attackXOffset, this.attackYOffset);
      var10.itemRawCoords();
      if (var8 != null) {
         var10.itemColor(var8);
      }

      return var10.itemEnd();
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      if (var5 > 0.5F) {
         var2.armRotationOffset(135.0F);
      } else {
         var2.armRotationOffset(-45.0F);
      }

      var2.swingRotation(var5, 360.0F, 65.0F);
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
