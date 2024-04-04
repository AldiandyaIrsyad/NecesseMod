package necesse.inventory.item.toolItem.swordToolItem;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;
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

public class SwordToolItem extends ToolItem implements SettlerWeaponItem {
   public float animSwingAngleOffset;
   public float animSwingAngle = 150.0F;

   public SwordToolItem(int var1) {
      super(var1);
      this.setItemCategory(new String[]{"equipment", "weapons", "meleeweapons"});
      this.setItemCategory(ItemCategory.equipmentManager, new String[]{"weapons", "meleeweapons"});
      this.keyWords.add("sword");
      this.damageType = DamageTypeRegistry.MELEE;
      this.width = 15.0F;
      this.showAttackAllDirections = true;
      this.resilienceGain.setBaseValue(2.0F);
      this.enchantCost.setUpgradedValue(1.0F, 2000);
   }

   public void addStatTooltips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, boolean var5) {
      this.addAttackDamageTip(var1, var2, var3, var4, var5);
      this.addAttackSpeedTip(var1, var2, var3, var4);
      this.addResilienceGainTip(var1, var2, var3, var4, var5);
      this.addKnockbackTip(var1, var2, var3, var4);
      this.addCritChanceTip(var1, var2, var3, var4, var5);
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.rotation(this.getSwingRotation(var1, var2.dir, var5) - 90.0F);
   }

   public float getSwingRotation(InventoryItem var1, int var2, float var3) {
      float var4 = 0.0F;
      if (var2 == 1) {
         var4 = 30.0F;
      } else if (var2 == 3) {
         var4 = 30.0F;
      }

      return this.animInverted ? ItemAttackDrawOptions.getSwingRotationInv(var3, 150.0F, var4) : ItemAttackDrawOptions.getSwingRotation(var3, 150.0F, var4);
   }

   public float animSwingAngle(int var1) {
      return this.animSwingAngle;
   }

   public float animSwingAngleOffset(int var1) {
      return this.animSwingAngleOffset;
   }

   public Function<Float, Float> getSwingDirection(AttackAnimMob var1) {
      int var3 = var1.dir;
      float var4 = this.animSwingAngle(var3);
      float var5 = this.animSwingAngleOffset(var3);
      Function var2;
      if (var3 == 0) {
         if (this.animInverted) {
            var2 = (var2x) -> {
               return -var2x * var4 - var5;
            };
         } else {
            var2 = (var2x) -> {
               return 180.0F + var2x * var4 + var5;
            };
         }
      } else if (var3 == 1) {
         if (this.animInverted) {
            var2 = (var2x) -> {
               return 90.0F - var2x * var4 - var5;
            };
         } else {
            var2 = (var2x) -> {
               return 270.0F + var2x * var4 + var5;
            };
         }
      } else if (var3 == 2) {
         if (this.animInverted) {
            var2 = (var2x) -> {
               return 180.0F - var2x * var4 - var5;
            };
         } else {
            var2 = (var2x) -> {
               return var2x * var4 + var5;
            };
         }
      } else if (this.animInverted) {
         var2 = (var2x) -> {
            return 90.0F + var2x * var4 + var5;
         };
      } else {
         var2 = (var2x) -> {
            return 270.0F - var2x * var4 - var5;
         };
      }

      return var2;
   }

   public ArrayList<Shape> getHitboxes(InventoryItem var1, AttackAnimMob var2, int var3, int var4, ToolItemEvent var5, boolean var6) {
      ArrayList var7 = new ArrayList();
      int var8 = this.getAttackRange(var1);
      float var9 = var5.lastHitboxProgress;
      float var10 = var2.getAttackAnimProgress();
      float var11 = (float)(Math.PI * (double)var8);
      float var12 = Math.max(10.0F, this.width) / var11;
      Point2D.Float var13 = new Point2D.Float(var2.x, var2.y);
      int var14 = var2.dir;
      if (var14 == 0) {
         var13.x += 8.0F;
      } else if (var14 == 2) {
         var13.x -= 8.0F;
      }

      for(float var15 = var9; var15 <= var10; var15 += var12) {
         float var16 = (Float)this.getSwingDirection(var2).apply(var15);
         Point2D.Float var17 = GameMath.getAngleDir(var16);
         Line2D.Float var18 = new Line2D.Float(var13.x, var13.y, var17.x * (float)var8 + var2.x, var17.y * (float)var8 + var2.y);
         if (this.width > 0.0F) {
            var7.add(new LineHitbox(var18, this.width));
         } else {
            var7.add(var18);
         }

         if (!var6) {
            var5.lastHitboxProgress = var15;
         }
      }

      return var7;
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      if (var8 == 0) {
         int var11 = this.getAttackAnimTime(var6, var4);
         ToolItemEvent var12 = new ToolItemEvent(var4, var9, var6, var2 - var4.getX(), var3 - var4.getY() + var5, var11, var11);
         var1.entityManager.addLevelEventHidden(var12);
      }

      return var6;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      var2.attackItem(var3.getX(), var3.getY(), var6);
      int var7 = this.getAttackAnimTime(var6, var3);
      ToolItemEvent var8 = new ToolItemEvent(var2, GameRandom.globalRandom.nextInt(), var6, var3.getX() - var2.getX(), var3.getY() - var2.getY() + var4, var7, var7, new HashMap());
      var1.entityManager.addLevelEventHidden(var8);
      return var6;
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
