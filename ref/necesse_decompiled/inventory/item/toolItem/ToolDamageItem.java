package necesse.inventory.item.toolItem;

import java.awt.Color;
import java.util.Arrays;
import java.util.Set;
import necesse.engine.GlobalData;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketChangeObject;
import necesse.engine.network.packet.PacketChangeTile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolDamageEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;
import necesse.inventory.item.StringItemStatTip;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public abstract class ToolDamageItem extends ToolItem {
   protected IntUpgradeValue toolDps = (new IntUpgradeValue(0, 0.2F)).setUpgradedValue(1.0F, 210);
   protected ToolType toolType;
   protected IntUpgradeValue toolTier = (new IntUpgradeValue(0, 0.0F)).setUpgradedValue(1.0F, 5);
   protected int addedRange = 0;

   public ToolDamageItem(int var1) {
      super(var1);
      this.hungerUsage = 6.6666666E-4F;
      this.changeDir = false;
   }

   protected abstract void addToolTooltips(ListGameTooltips var1);

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = new ListGameTooltips();
      this.addToolTooltips(var4);
      var4.add((Object)super.getPreEnchantmentTooltips(var1, var2, var3));
      return var4;
   }

   public void addStatTooltips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, boolean var5) {
      this.addToolDPSTip(var1, var2, var3, var4, var5);
      this.addToolTierTip(var1, var2, var3, var5 || GlobalData.debugActive());
      this.addAttackSpeedTip(var1, var2, var3, var4);
      this.addAddedRangeTip(var1, var2, var3, var4, var5);
   }

   public void addToolDPSTip(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, boolean var5) {
      int var6 = this.getToolDps(var2, var4);
      int var7 = var3 == null ? -1 : this.getToolDps(var3, var4);
      if (var6 > 0 || var7 > 0 || var5) {
         LocalMessageDoubleItemStatTip var8 = new LocalMessageDoubleItemStatTip("itemtooltip", "tooldmg", "value", (double)var6, 0);
         if (var3 != null) {
            var8.setCompareValue((double)var7);
         }

         var1.add(50, var8);
      }

      if (GlobalData.debugActive()) {
         String var11 = this.getToolHitDamageString(var2, var4);
         StringItemStatTip var9 = new StringItemStatTip(var11) {
            public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
               return new StaticMessage("Tool hit damage: " + this.getReplaceValue(var1, var2, var3, var4));
            }
         };
         if (var3 != null) {
            String var10 = this.getToolHitDamageString(var3, var4);
            var9.setCompareValue(var10, var6 == var7 ? null : var6 > var7);
         }

         var1.add(40, var9);
      }

   }

   public void addToolTierTip(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, boolean var4) {
      int var5 = this.getToolTier(var2);
      int var6 = var3 == null ? var5 : this.getToolTier(var3);
      if (var5 != var6 || var4) {
         LocalMessageDoubleItemStatTip var7 = new LocalMessageDoubleItemStatTip("itemtooltip", "tooltier", "value", (double)var5, 0);
         if (var3 != null) {
            var7.setCompareValue((double)var6);
         }

         var1.add(60, var7);
      }

   }

   public void addAddedRangeTip(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, boolean var5) {
      int var6 = this.getAddedRange(var2);
      int var7 = var3 == null ? -1 : this.getAddedRange(var3);
      if (var6 != 0 || var7 != 0 || var5) {
         LocalMessageDoubleItemStatTip var8 = new LocalMessageDoubleItemStatTip("itemtooltip", "tooladdrange", "value", (double)var6, 0);
         var8.setValueToString((var0) -> {
            return var0 >= 0.0 ? "+" + GameMath.removeDecimalIfZero(var0) : GameMath.removeDecimalIfZero(var0);
         });
         if (var3 != null) {
            var8.setCompareValue((double)var7);
         }

         var1.add(250, var8);
      }

   }

   public boolean isTileInRange(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      return var4.getDistance((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)) <= (float)this.getMiningRange(var5, var4);
   }

   public boolean canSmartMineTile(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      if (this.canDamageTile(var1, var2, var3, var4, var5)) {
         return this.toolType != ToolType.ALL && this.toolType != ToolType.SHOVEL ? var1.getObject(var2, var3).shouldSnapSmartMining(var1, var2, var3) : true;
      } else {
         return false;
      }
   }

   public boolean canDamageTile(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      return true;
   }

   public ToolType getToolType(InventoryItem var1) {
      return this.toolType;
   }

   public int getToolTier(InventoryItem var1) {
      return this.toolTier.getValue(this.getUpgradeTier(var1));
   }

   public InventoryItem runLevelDamage(Level var1, int var2, int var3, int var4, int var5, PlayerMob var6, InventoryItem var7, int var8, PacketReader var9) {
      if (this.toolType != ToolType.NONE && this.isTileInRange(var1, var4, var5, var6, var7) && this.canDamageTile(var1, var4, var5, var6, var7)) {
         int var10 = this.getToolHitDamage(var7, var8, var6);
         this.runTileDamage(var1, var2, var3, var4, var5, var6, var7, var10);
      }

      return var7;
   }

   protected void runTileDamage(Level var1, int var2, int var3, int var4, int var5, PlayerMob var6, InventoryItem var7, int var8) {
      ServerClient var9 = null;
      if (var6 != null && var6.isServerClient()) {
         var9 = var6.getServerClient();
      }

      var1.entityManager.doDamage(var4, var5, var8, this.toolType, this.getToolTier(var7), var9, true, var2, var3);
   }

   public void setupAttackContentPacket(PacketWriter var1, Level var2, int var3, int var4, PlayerMob var5, InventoryItem var6) {
      super.setupAttackContentPacket(var1, var2, var3, var4, var5, var6);
      int var7 = var3 / 32;
      int var8 = var4 / 32;
      if (this.toolType == ToolType.ALL || this.toolType == ToolType.PICKAXE || this.toolType == ToolType.AXE) {
         LevelObject var9 = GameUtils.getInteractObjectHit(var2, var3, var4, (var4x) -> {
            if (!this.isTileInRange(var2, var4x.tileX, var4x.tileY, var5, var6)) {
               return false;
            } else {
               return this.canDamageTile(var2, var4x.tileX, var4x.tileY, var5, var6);
            }
         }, (LevelObject)null);
         if (var9 != null) {
            var7 = var9.tileX;
            var8 = var9.tileY;
         }
      }

      this.setupAttackContentPacketHitTile(var1, var2, var7, var8);
   }

   public void setupAttackContentPacketHitTile(PacketWriter var1, Level var2, int var3, int var4) {
      var1.putNextShortUnsigned(var2.getTileID(var3, var4));
      int var5 = var2.getObjectID(var3, var4);
      var1.putNextShortUnsigned(var5);
      if (var5 != 0) {
         var1.putNextByteUnsigned(var2.getObjectRotation(var3, var4));
      }

      var1.putNextShortUnsigned(var3);
      var1.putNextShortUnsigned(var4);
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      int var11 = var10.getNextShortUnsigned();
      int var12 = var10.getNextShortUnsigned();
      int var13 = 0;
      if (var12 != 0) {
         var13 = var10.getNextByteUnsigned();
      }

      int var14 = var10.getNextShortUnsigned();
      int var15 = var10.getNextShortUnsigned();
      if (var4 != null && var4.isServerClient()) {
         ServerClient var16 = var4.getServerClient();
         int var17 = var1.getTileID(var14, var15);
         if (var11 != var17) {
            var16.sendPacket(new PacketChangeTile(var1, var14, var15, var17));
         }

         int var18 = var1.getObjectID(var14, var15);
         byte var19 = var18 == 0 ? 0 : var1.getObjectRotation(var14, var15);
         if (var12 != var18 || var13 != var19) {
            var16.sendPacket(new PacketChangeObject(var1, var14, var15, var18, var19));
         }
      }

      var6 = this.runLevelDamage(var1, var2, var3, var14, var15, var4, var6, var8, var10);
      var6 = super.onAttack(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      return var6;
   }

   public int getToolHitDamage(InventoryItem var1, int var2, Mob var3) {
      var2 = Math.max(0, var2);
      float var4 = this.getToolDamagePerHit(var1, var3);
      return (int)((double)(var4 + var4 * (float)var2) - Math.floor((double)(var4 * (float)var2)));
   }

   protected int[] getToolHitDamage(InventoryItem var1, Mob var2) {
      int[] var3 = new int[this.getAnimAttacks(var1)];

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var3[var4] = this.getToolHitDamage(var1, var4, var2);
      }

      return var3;
   }

   protected String getToolHitDamageString(InventoryItem var1, Mob var2) {
      return this.getToolDamagePerHit(var1, var2) + ", " + Arrays.toString(this.getToolHitDamage(var1, var2));
   }

   public int getFlatToolDps(InventoryItem var1) {
      GNDItemMap var2 = var1.getGndData();
      return var2.hasKey("toolDps") ? var2.getInt("toolDps") : this.toolDps.getValue(this.getUpgradeTier(var1));
   }

   public int getToolDps(InventoryItem var1, Mob var2) {
      int var3 = this.getFlatToolDps(var1);
      float var4 = var2 == null ? 1.0F : (Float)var2.buffManager.getModifier(BuffModifiers.TOOL_DAMAGE);
      return Math.round((float)var3 * var4 * (Float)this.getEnchantment(var1).applyModifierLimited(ToolItemModifiers.TOOL_DAMAGE, (Float)ToolItemModifiers.TOOL_DAMAGE.defaultBuffManagerValue));
   }

   public float getToolDamagePerHit(InventoryItem var1, Mob var2) {
      return (float)this.getToolDps(var1, var2) * ((float)super.getAttackAnimTime(var1, var2) * this.getMiningSpeedModifier(var1, var2) / 1000.0F) / (float)this.getAnimAttacks(var1);
   }

   public float getMiningSpeedModifier(InventoryItem var1, Mob var2) {
      return (var2 == null ? 1.0F : (Float)var2.buffManager.getModifier(BuffModifiers.MINING_SPEED)) * (Float)this.getEnchantment(var1).applyModifierLimited(ToolItemModifiers.MINING_SPEED, (Float)ToolItemModifiers.MINING_SPEED.defaultBuffManagerValue);
   }

   public float getAttackSpeedModifier(InventoryItem var1, Mob var2) {
      return super.getAttackSpeedModifier(var1, var2) * this.getMiningSpeedModifier(var1, var2);
   }

   public int getAddedRange(InventoryItem var1) {
      GNDItemMap var2 = var1.getGndData();
      return var2.hasKey("addedRange") ? var2.getInt("addedRange") : this.addedRange;
   }

   public int getMiningRange(InventoryItem var1, PlayerMob var2) {
      return (int)((3.5F + (float)this.getAddedRange(var1) + (var2 == null ? 0.0F : (Float)var2.buffManager.getModifier(BuffModifiers.MINING_RANGE))) * 32.0F);
   }

   public ToolDamageEnchantment getRandomEnchantment(GameRandom var1, InventoryItem var2) {
      return (ToolDamageEnchantment)Enchantable.getRandomEnchantment(var1, EnchantmentRegistry.toolDamageEnchantments, this.getEnchantmentID(var2), ToolDamageEnchantment.class);
   }

   public boolean isValidEnchantment(InventoryItem var1, ItemEnchantment var2) {
      return EnchantmentRegistry.toolDamageEnchantments.contains(var2.getID());
   }

   public Set<Integer> getValidEnchantmentIDs(InventoryItem var1) {
      return EnchantmentRegistry.toolDamageEnchantments;
   }

   public ToolDamageEnchantment getEnchantment(InventoryItem var1) {
      return (ToolDamageEnchantment)EnchantmentRegistry.getEnchantment(this.getEnchantmentID(var1), ToolDamageEnchantment.class, ToolDamageEnchantment.noEnchant);
   }

   public ToolType getToolType() {
      return this.toolType;
   }

   public String getCanBeUpgradedError(InventoryItem var1) {
      if (!this.toolDps.hasMoreThanOneValue()) {
         return Localization.translate("ui", "itemnotupgradable");
      } else {
         return this.getUpgradeTier(var1) >= 4.0F ? Localization.translate("ui", "itemupgradelimit") : null;
      }
   }

   protected int getNextUpgradeTier(InventoryItem var1) {
      int var2 = (int)var1.item.getUpgradeTier(var1);
      int var3 = var2 + 1;
      int var4 = this.toolDps.getValue(0.0F);
      float var5 = (float)this.toolDps.getValue((float)var3);
      if (var3 == 1 && (float)var4 < var5) {
         return var3;
      } else {
         while((float)var4 / var5 > 1.0F - this.toolDps.defaultLevelIncreaseMultiplier / 4.0F && var3 < var2 + 100) {
            ++var3;
            var5 = (float)this.toolDps.getValue((float)var3);
         }

         return var3;
      }
   }

   protected float getTier1CostPercent(InventoryItem var1) {
      return (float)this.toolDps.getValue(0.0F) / (float)this.toolDps.getValue(1.0F);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public ToolItemEnchantment getEnchantment(InventoryItem var1) {
      return this.getEnchantment(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public ToolItemEnchantment getRandomEnchantment(GameRandom var1, InventoryItem var2) {
      return this.getRandomEnchantment(var1, var2);
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
