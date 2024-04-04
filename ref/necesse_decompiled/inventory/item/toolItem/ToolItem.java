package necesse.inventory.item.toolItem;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemEnchantment;
import necesse.engine.network.gameNetworkData.GNDItemGameDamage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.toolItemEvent.ToolItemEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameColor;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.inventory.item.upgradeUtils.SalvageableItem;
import necesse.inventory.item.upgradeUtils.UpgradableItem;
import necesse.inventory.item.upgradeUtils.UpgradedItem;
import necesse.inventory.recipe.Ingredient;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public class ToolItem extends Item implements Enchantable<ItemEnchantment>, UpgradableItem, SalvageableItem {
   protected DamageType damageType;
   protected FloatUpgradeValue attackDamage;
   protected FloatUpgradeValue resilienceGain;
   protected IntUpgradeValue attackRange;
   protected IntUpgradeValue knockback;
   protected boolean animInverted;
   protected IntUpgradeValue enchantCost;
   protected float width;
   protected FloatUpgradeValue manaCost;
   protected IntUpgradeValue lifeSteal;
   protected IntUpgradeValue lifeCost;
   /** @deprecated */
   @Deprecated
   protected GameDamage attackDmg;

   public ToolItem(int var1) {
      super(1);
      this.damageType = DamageTypeRegistry.NORMAL;
      this.attackDamage = new FloatUpgradeValue(0.0F, 0.2F);
      this.resilienceGain = new FloatUpgradeValue(1.0F, 0.0F);
      this.attackRange = new IntUpgradeValue(50, 0.0F);
      this.knockback = new IntUpgradeValue(0, 0.0F);
      this.enchantCost = new IntUpgradeValue(0, 0.1F);
      this.manaCost = new FloatUpgradeValue(2.5F, 0.0F);
      this.lifeSteal = new IntUpgradeValue(0, 0.0F);
      this.lifeCost = new IntUpgradeValue(0, 0.0F);
      this.enchantCost.setBaseValue(var1);
      this.setItemCategory(new String[]{"equipment"});
      this.keyWords.add("toolitem");
      this.changeDir = true;
      this.width = 0.0F;
      this.attackXOffset = 4;
      this.attackYOffset = 4;
      this.worldDrawSize = 40;
      this.incinerationTimeMillis = 30000;
   }

   public void onItemRegistryClosed() {
      super.onItemRegistryClosed();
      if (this.attackDmg != null && this.attackDamage.isEmpty()) {
         this.attackDamage.setBaseValue(this.attackDmg.damage);
         this.damageType = this.attackDmg.type;
      }

   }

   public final ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add((Object)this.getPreEnchantmentTooltips(var1, var2, var3));
      var4.add((Object)this.getEnchantmentTooltips(var1));
      var4.add((Object)this.getPostEnchantmentTooltips(var1, var2, var3));
      return var4;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = new ListGameTooltips();
      this.addStatTooltips(var4, var1, (InventoryItem)var3.get(InventoryItem.class, "compareItem"), var3.getBoolean("showDifference"), (Mob)var3.get(Mob.class, "perspective", var2));
      return var4;
   }

   public ListGameTooltips getPostEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      return new ListGameTooltips();
   }

   public final void addStatTooltips(ListGameTooltips var1, InventoryItem var2, InventoryItem var3, boolean var4, Mob var5) {
      ItemStatTipList var6 = new ItemStatTipList();
      this.addStatTooltips(var6, var2, var3, var5, false);
      Iterator var7 = var6.iterator();

      while(var7.hasNext()) {
         ItemStatTip var8 = (ItemStatTip)var7.next();
         var1.add((Object)var8.toTooltip((Color)GameColor.GREEN.color.get(), (Color)GameColor.RED.color.get(), (Color)GameColor.YELLOW.color.get(), var4));
      }

   }

   public void addStatTooltips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, boolean var5) {
   }

   public int getWorldDrawSize(InventoryItem var1, PlayerMob var2) {
      GameSprite var3 = this.getWorldItemSprite(var1, var2);
      return Math.min(this.worldDrawSize, Math.max(var3.width, var3.height));
   }

   public GameSprite getWorldItemSprite(InventoryItem var1, PlayerMob var2) {
      GameSprite var3 = this.getAttackSprite(var1, var2);
      return var3 != null && Math.max(var3.width, var3.height) >= 32 ? var3 : this.getItemSprite(var1, var2);
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      if (var8 == 0) {
         int var11 = this.getAttackAnimTime(var6, var4);
         ToolItemEvent var12 = new ToolItemEvent(var4, var9, var6, var2 - var4.getX(), var3 - var4.getY() + var5, var11, var11);
         var1.entityManager.addLevelEventHidden(var12);
      }

      return var6;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.swing2, SoundEffect.effect(var4));
      }

   }

   public ArrayList<Shape> getHitboxes(InventoryItem var1, AttackAnimMob var2, int var3, int var4, ToolItemEvent var5, boolean var6) {
      ArrayList var7 = new ArrayList();
      int var8 = this.getAttackRange(var1);
      Point2D.Float var9 = GameMath.normalize((float)var3, (float)var4);
      Line2D.Float var10 = new Line2D.Float(var2.x, var2.y, var9.x * (float)var8 + var2.x, var9.y * (float)var8 + var2.y);
      if (this.width > 0.0F) {
         var7.add(new LineHitbox(var10, this.width));
      } else {
         var7.add(var10);
      }

      return var7;
   }

   public boolean getConstantUse(InventoryItem var1) {
      return true;
   }

   public float getAttackMovementMod(InventoryItem var1) {
      return 0.5F;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      if (this.animInverted) {
         var2.swingRotationInv(var5);
      } else {
         var2.swingRotation(var5);
      }

   }

   public DamageType getDamageType(InventoryItem var1) {
      return this.damageType;
   }

   public GameDamage getFlatAttackDamage(InventoryItem var1) {
      GNDItemMap var2 = var1.getGndData();
      if (var2.hasKey("damage")) {
         GNDItem var3 = var2.getItem("damage");
         if (var3 instanceof GNDItemGameDamage) {
            return ((GNDItemGameDamage)var3).damage;
         }

         if (var3 instanceof GNDItem.GNDPrimitive) {
            float var4 = ((GNDItem.GNDPrimitive)var3).getFloat();
            return new GameDamage(this.getDamageType(var1), var4);
         }
      }

      return new GameDamage(this.getDamageType(var1), this.attackDamage.getValue(this.getUpgradeTier(var1)));
   }

   public GameDamage getAttackDamage(InventoryItem var1) {
      return this.getFlatAttackDamage(var1).enchantedDamage(this.getEnchantment(var1), ToolItemModifiers.DAMAGE, ToolItemModifiers.ARMOR_PEN, ToolItemModifiers.CRIT_CHANCE);
   }

   /** @deprecated */
   @Deprecated
   public GameDamage getDamage(InventoryItem var1) {
      return this.getAttackDamage(var1);
   }

   public void addAttackDamageTip(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Attacker var4, boolean var5) {
      int var6 = this.getAttackDamageValue(var2, var4);
      int var7 = var3 == null ? -1 : this.getAttackDamageValue(var3, var4);
      if (var6 > 0 || var7 > 0 || var5) {
         DoubleItemStatTip var8 = this.getDamageType(var2).getDamageTip(var6);
         if (var3 != null) {
            var8.setCompareValue((double)var7);
         }

         var1.add(100, var8);
      }

   }

   public int getAttackDamageValue(InventoryItem var1, Attacker var2) {
      return Math.round(this.getFlatAttackDamage(var1).getBuffedDamage(var2) * (Float)this.getEnchantment(var1).applyModifierLimited(ToolItemModifiers.DAMAGE, (Float)ToolItemModifiers.DAMAGE.defaultBuffManagerValue));
   }

   public void addAttackSpeedTip(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4) {
      int var5 = Math.max(this.getAttackAnimTime(var2, var4), this.getAttackCooldownTime(var2, var4));
      LocalMessageDoubleItemStatTip var6 = new LocalMessageDoubleItemStatTip("itemtooltip", "attackspeedtip", "value", this.toAttacksPerSecond(var5), 1);
      if (var3 != null) {
         int var7 = Math.max(this.getAttackAnimTime(var3, var4), this.getAttackCooldownTime(var3, var4));
         var6.setCompareValue(this.toAttacksPerSecond(var7));
      }

      var1.add(200, var6);
   }

   public void addResilienceGainTip(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, boolean var5) {
      if (var4 != null && var4.getMaxResilience() > 0 || var5) {
         float var6 = var4 == null ? (Float)BuffModifiers.RESILIENCE_GAIN.defaultBuffManagerValue : (Float)var4.buffManager.getModifier(BuffModifiers.RESILIENCE_GAIN);
         float var7 = this.getResilienceGain(var2) * var6;
         LocalMessageDoubleItemStatTip var8 = new LocalMessageDoubleItemStatTip("itemtooltip", "resiliencegaintip", "value", (double)var7, 1);
         if (var3 != null) {
            var8.setCompareValue((double)(this.getResilienceGain(var3) * var6));
         }

         var1.add(300, var8);
      }

   }

   public float getResilienceGain(InventoryItem var1) {
      return this.resilienceGain.getValue((float)this.getUpgradeLevel(var1)) * (Float)this.getEnchantment(var1).applyModifierLimited(ToolItemModifiers.RESILIENCE_GAIN, (Float)ToolItemModifiers.RESILIENCE_GAIN.defaultBuffManagerValue);
   }

   public void addCritChanceTip(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Attacker var4, boolean var5) {
      float var6 = this.getCritChance(var2, var4);
      float var7 = var3 == null ? 0.0F : this.getCritChance(var2, var4);
      if (var6 > 0.0F || var7 > 0.0F || var5) {
         LocalMessageDoubleItemStatTip var8 = new LocalMessageDoubleItemStatTip("itemtooltip", "crittip", "value", (double)(var6 * 100.0F), 0);
         if (var3 != null) {
            var8.setCompareValue((double)(var7 * 100.0F));
         }

         var1.add(400, var8);
      }

   }

   public float getCritChance(InventoryItem var1, Attacker var2) {
      return GameMath.limit(this.getFlatAttackDamage(var1).getBuffedCritChance(var2) + (Float)this.getEnchantment(var1).applyModifierLimited(ToolItemModifiers.CRIT_CHANCE, (Float)ToolItemModifiers.CRIT_CHANCE.defaultBuffManagerValue), 0.0F, 1.0F);
   }

   public void addAttackRangeTip(ItemStatTipList var1, InventoryItem var2, InventoryItem var3) {
      LocalMessageDoubleItemStatTip var4 = new LocalMessageDoubleItemStatTip("itemtooltip", "knockbacktip", "value", (double)this.getAttackRange(var2), 0);
      if (var3 != null) {
         var4.setCompareValue((double)this.getAttackRange(var3));
      }

      var1.add(500, var4);
   }

   public void addKnockbackTip(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Attacker var4) {
      LocalMessageDoubleItemStatTip var5 = new LocalMessageDoubleItemStatTip("itemtooltip", "knockbacktip", "value", (double)this.getKnockback(var2, var4), 0);
      if (var3 != null) {
         var5.setCompareValue((double)this.getKnockback(var3, var4));
      }

      var1.add(600, var5);
   }

   public int getFlatKnockback(InventoryItem var1) {
      GNDItemMap var2 = var1.getGndData();
      return var2.hasKey("knockback") ? var2.getInt("knockback") : this.knockback.getValue(this.getUpgradeTier(var1));
   }

   public int getKnockback(InventoryItem var1, Attacker var2) {
      int var3 = this.getFlatKnockback(var1);
      float var4 = 1.0F;
      Mob var5 = var2 != null ? var2.getAttackOwner() : null;
      if (var5 != null) {
         var4 = (Float)var5.buffManager.getModifier(BuffModifiers.KNOCKBACK_OUT);
      }

      return Math.round((float)var3 * (Float)this.getEnchantment(var1).applyModifierLimited(ToolItemModifiers.KNOCKBACK, (Float)ToolItemModifiers.KNOCKBACK.defaultBuffManagerValue) * var4);
   }

   public void addManaCostTip(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4) {
      float var5 = var4 == null ? (Float)BuffModifiers.MANA_USAGE.defaultBuffManagerValue : (Float)var4.buffManager.getModifier(BuffModifiers.MANA_USAGE);
      float var6 = this.getManaCost(var2) * var5;
      LocalMessageDoubleItemStatTip var7 = new LocalMessageDoubleItemStatTip("itemtooltip", "manacosttip", "value", (double)var6, 1);
      if (var3 != null) {
         var7.setCompareValue((double)(this.getManaCost(var3) * var5), false);
      }

      var1.add(1000, var7);
   }

   public float getFlatManaCost(InventoryItem var1) {
      GNDItemMap var2 = var1.getGndData();
      return var2.hasKey("manaCost") ? var2.getFloat("manaCost") : this.manaCost.getValue(this.getUpgradeTier(var1));
   }

   public float getManaCost(InventoryItem var1) {
      return this.getFlatManaCost(var1) * this.getManaUsageModifier(var1);
   }

   public float getManaUsageModifier(InventoryItem var1) {
      return (Float)this.getEnchantment(var1).applyModifierLimited(ToolItemModifiers.MANA_USAGE, (Float)ToolItemModifiers.MANA_USAGE.defaultBuffManagerValue);
   }

   public void consumeMana(PlayerMob var1, InventoryItem var2) {
      float var3 = this.getManaCost(var2);
      if (var3 > 0.0F) {
         var1.useMana(var3, var1.isServerClient() ? var1.getServerClient() : null);
      }

   }

   public int getFlatLifeCost(InventoryItem var1) {
      GNDItemMap var2 = var1.getGndData();
      return var2.hasKey("lifeCost") ? var2.getInt("lifeCost") : this.lifeCost.getValue(this.getUpgradeTier(var1));
   }

   public int getLifeSteal(InventoryItem var1) {
      GNDItemMap var2 = var1.getGndData();
      return var2.hasKey("lifeSteal") ? var2.getInt("lifeSteal") : this.lifeSteal.getValue(this.getUpgradeTier(var1));
   }

   public void addLifeCostTip(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4) {
      float var5 = (float)this.getFlatLifeCost(var2);
      LocalMessageDoubleItemStatTip var6 = new LocalMessageDoubleItemStatTip("itemtooltip", "lifecosttip", "value", (double)var5, 1);
      if (var3 != null) {
         var6.setCompareValue((double)this.getFlatLifeCost(var3), false);
      }

      var1.add(1100, var6);
   }

   public int getFlatAttackRange(InventoryItem var1) {
      GNDItemMap var2 = var1.getGndData();
      return var2.hasKey("attackRange") ? var2.getInt("attackRange") : this.attackRange.getValue(this.getUpgradeTier(var1));
   }

   public int getAttackRange(InventoryItem var1) {
      int var2 = this.getFlatAttackRange(var1);
      return Math.round((float)var2 * (Float)this.getEnchantment(var1).applyModifierLimited(ToolItemModifiers.RANGE, (Float)ToolItemModifiers.RANGE.defaultBuffManagerValue));
   }

   public float getAttackSpeedModifier(InventoryItem var1, Mob var2) {
      DamageType var3 = this.getDamageType(var1);
      return var3.calculateTotalAttackSpeedModifier(var2, (Float)this.getEnchantment(var1).applyModifierUnlimited(ToolItemModifiers.ATTACK_SPEED, (Float)ToolItemModifiers.ATTACK_SPEED.defaultBuffValue));
   }

   public boolean matchesSearch(InventoryItem var1, PlayerMob var2, String var3) {
      return super.matchesSearch(var1, var2, var3) ? true : this.getFlatAttackDamage(var1).type.getStringID().toLowerCase().contains(var3);
   }

   public boolean canCombineItem(Level var1, PlayerMob var2, InventoryItem var3, InventoryItem var4, String var5) {
      return !super.canCombineItem(var1, var2, var3, var4, var5) ? false : this.isSameGNDData(var1, var3, var4, var5);
   }

   public boolean isSameGNDData(Level var1, InventoryItem var2, InventoryItem var3, String var4) {
      return var2.getGndData().sameKeys(var3.getGndData(), "enchantment", "upgradeLevel");
   }

   public boolean canHitMob(Mob var1, ToolItemEvent var2) {
      return var1.canBeHit(var2);
   }

   public void hitMob(InventoryItem var1, ToolItemEvent var2, Level var3, Mob var4, Mob var5) {
      if (!var2.hasResilienceApplied && var4.canGiveResilience(var5)) {
         var5.addResilience(this.getResilienceGain(var1));
         var2.hasResilienceApplied = true;
      }

      var4.isServerHit(this.getAttackDamage(var1), var4.x - var5.x, var4.y - var5.y, (float)this.getKnockback(var1, var5), var5);
   }

   public boolean canHitObject(LevelObject var1) {
      return var1.object.attackThrough;
   }

   public void hitObject(InventoryItem var1, LevelObject var2, Mob var3) {
      var2.attackThrough(this.getAttackDamage(var1), var3);
   }

   public CollisionFilter getAttackThroughFilter() {
      return (new CollisionFilter()).attackThroughCollision((var1) -> {
         return this.canHitObject(var1.object());
      });
   }

   public float getSinkingRate(ItemPickupEntity var1, float var2) {
      return super.getSinkingRate(var1, var2) / 5.0F;
   }

   public boolean isEnchantable(InventoryItem var1) {
      return true;
   }

   public void setEnchantment(InventoryItem var1, int var2) {
      var1.getGndData().setItem("enchantment", new GNDItemEnchantment(var2));
   }

   public int getEnchantmentID(InventoryItem var1) {
      GNDItem var2 = var1.getGndData().getItem("enchantment");
      GNDItemEnchantment var3 = GNDItemEnchantment.convertEnchantmentID(var2);
      var1.getGndData().setItem("enchantment", var3);
      return var3.getRegistryID();
   }

   public void clearEnchantment(InventoryItem var1) {
      var1.getGndData().setItem("enchantment", (GNDItem)null);
   }

   public ToolItemEnchantment getRandomEnchantment(GameRandom var1, InventoryItem var2) {
      return null;
   }

   public boolean isValidEnchantment(InventoryItem var1, ItemEnchantment var2) {
      return false;
   }

   public int getEnchantCost(InventoryItem var1) {
      return this.enchantCost.getValue(var1.item.getUpgradeTier(var1));
   }

   public ToolItemEnchantment getEnchantment(InventoryItem var1) {
      return (ToolItemEnchantment)EnchantmentRegistry.getEnchantment(this.getEnchantmentID(var1), ToolItemEnchantment.class, ToolItemEnchantment.noEnchant);
   }

   public Set<Integer> getValidEnchantmentIDs(InventoryItem var1) {
      return EnchantmentRegistry.toolDamageEnchantments;
   }

   public GameTooltips getEnchantmentTooltips(InventoryItem var1) {
      if (this.getEnchantmentID(var1) > 0) {
         ListGameTooltips var2 = new ListGameTooltips(this.getEnchantment(var1).getTooltips());
         if (GlobalData.debugActive()) {
            var2.addFirst("Enchantment id " + this.getEnchantmentID(var1));
         }

         return var2;
      } else {
         return new StringTooltips();
      }
   }

   public float getBrokerValue(InventoryItem var1) {
      int var2 = 0;
      float var3 = var1.item.getUpgradeTier(var1);
      if (var3 > 0.0F) {
         byte var4 = 8;
         int var5 = 20 * ((int)var3 - 1) * var4;
         int var6 = Math.max((int)((1.0F - this.getTier1CostPercent(var1)) * 40.0F), 1) * var4;
         var2 = var6 + var5;
      }

      return super.getBrokerValue(var1) * this.getEnchantment(var1).getEnchantCostMod() + (float)var2;
   }

   protected ListGameTooltips getDisplayNameTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getDisplayNameTooltips(var1, var2, var3);
      int var5 = this.getUpgradeLevel(var1);
      if (var5 > 0) {
         int var7 = var5 / 100;
         String var6;
         if ((float)var7 == (float)var5 / 100.0F) {
            var6 = String.valueOf(var7);
         } else {
            int var8 = var5 - var7 * 100;
            var6 = var7 + " +" + var8 + "%";
         }

         var4.add((Object)(new StringTooltips(Localization.translate("item", "tier", "tiernumber", var6), new Color(133, 49, 168))));
      }

      return var4;
   }

   public Item.Rarity getRarity(InventoryItem var1) {
      Item.Rarity var2 = super.getRarity(var1);
      return this.getUpgradeTier(var1) >= 1.0F ? var2.getNext(Item.Rarity.EPIC) : var2;
   }

   public GameMessage getLocalization(InventoryItem var1) {
      Object var2 = super.getLocalization(var1);
      ToolItemEnchantment var3 = this.getEnchantment(var1);
      if (var3 != null && var3.getID() > 0) {
         var2 = new LocalMessage("enchantment", "format", new Object[]{"enchantment", var3.getLocalization(), "item", var2});
      }

      return (GameMessage)var2;
   }

   public InventoryItem getDefaultLootItem(GameRandom var1, int var2) {
      InventoryItem var3 = super.getDefaultLootItem(var1, var2);
      if (this.isEnchantable(var3) && var1.getChance(0.65F)) {
         ((Enchantable)var3.item).addRandomEnchantment(var3, var1);
      }

      return var3;
   }

   public String getCanBeUpgradedError(InventoryItem var1) {
      if (!this.attackDamage.hasMoreThanOneValue()) {
         return Localization.translate("ui", "itemnotupgradable");
      } else {
         return this.getUpgradeTier(var1) >= 4.0F ? Localization.translate("ui", "itemupgradelimit") : null;
      }
   }

   public void addUpgradeStatTips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, Mob var5) {
      DoubleItemStatTip var6 = (new LocalMessageDoubleItemStatTip("item", "tier", "tiernumber", (double)this.getUpgradeTier(var3), 2)).setCompareValue((double)this.getUpgradeTier(var2)).setToString((var0) -> {
         int var2 = (int)var0;
         double var3 = var0 - (double)var2;
         return var3 != 0.0 ? var2 + " (+" + (int)(var3 * 100.0) + "%)" : String.valueOf(var2);
      });
      var1.add(Integer.MIN_VALUE, var6);
      this.addStatTooltips(var1, var3, var2, var4, true);
   }

   protected int getNextUpgradeTier(InventoryItem var1) {
      int var2 = (int)var1.item.getUpgradeTier(var1);
      int var3 = var2 + 1;
      float var4 = this.attackDamage.getValue(0.0F);
      float var5 = this.attackDamage.getValue((float)var3);
      if (var3 == 1 && var4 < var5) {
         return var3;
      } else {
         while(var4 / var5 > 1.0F - this.attackDamage.defaultLevelIncreaseMultiplier / 4.0F && var3 < var2 + 100) {
            ++var3;
            var5 = this.attackDamage.getValue((float)var3);
         }

         return var3;
      }
   }

   protected float getTier1CostPercent(InventoryItem var1) {
      return this.attackDamage.getValue(0.0F) / this.attackDamage.getValue(1.0F);
   }

   public UpgradedItem getUpgradedItem(InventoryItem var1) {
      int var2 = this.getNextUpgradeTier(var1);
      InventoryItem var3 = var1.copy();
      var3.item.setUpgradeTier(var3, (float)var2);
      int var4;
      if (var2 <= 1) {
         var4 = Math.max((int)((1.0F - this.getTier1CostPercent(var1)) * 40.0F), 1);
      } else {
         var4 = var2 * 20;
      }

      return new UpgradedItem(var1, var3, new Ingredient[]{new Ingredient("upgradeshard", var4)});
   }

   public Collection<InventoryItem> getSalvageRewards(InventoryItem var1) {
      int var2 = (int)((float)var1.getAmount() * this.getUpgradeTier(var1) * 10.0F);
      return Collections.singleton(new InventoryItem("upgradeshard", var2));
   }

   // $FF: synthetic method
   // $FF: bridge method
   public ItemEnchantment getRandomEnchantment(GameRandom var1, InventoryItem var2) {
      return this.getRandomEnchantment(var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public ItemEnchantment getEnchantment(InventoryItem var1) {
      return this.getEnchantment(var1);
   }
}
