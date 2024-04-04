package necesse.inventory.item.armorItem;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import necesse.engine.GlobalData;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierTooltip;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemEnchantment;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobGenericEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.ArmorBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameColor;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.DefaultColoredGameTooltips;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.ItemCombineResult;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.ContainerTransferResult;
import necesse.inventory.container.SlotIndexRange;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.EquipmentItemEnchant;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.inventory.item.upgradeUtils.SalvageableItem;
import necesse.inventory.item.upgradeUtils.UpgradableItem;
import necesse.inventory.item.upgradeUtils.UpgradedItem;
import necesse.inventory.recipe.Ingredient;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ArmorItem extends Item implements Enchantable<EquipmentItemEnchant>, UpgradableItem, SalvageableItem {
   protected IntUpgradeValue armorValue = new IntUpgradeValue(0, 0.25F);
   protected float setterEquipmentValueModifier = 1.0F;
   protected IntUpgradeValue enchantCost = new IntUpgradeValue(0, 0.1F);
   public ArmorType armorType;
   public HairDrawMode hairDrawOptions;
   public final String textureName;
   public GameTexture armorTexture;
   public boolean drawBodyPart;

   public ArmorItem(ArmorType var1, int var2, int var3, String var4) {
      super(1);
      this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HAIR;
      this.drawBodyPart = true;
      this.textureName = var4;
      this.keyWords.add("armor");
      this.armorType = var1;
      this.armorValue.setBaseValue(var2);
      this.enchantCost.setBaseValue(var3).setUpgradedValue(1.0F, 1500);
      switch (var1) {
         case HEAD:
            if (var2 > 0) {
               this.armorValue.setUpgradedValue(1.0F, 24);
               if (var2 > 24) {
                  this.armorValue.setBaseValue(24);
               }
            }

            this.keyWords.add("helmet");
            this.keyWords.add("head");
            this.keyWords.add("hat");
            break;
         case CHEST:
            if (var2 > 0) {
               this.armorValue.setUpgradedValue(1.0F, 30);
               if (var2 > 30) {
                  this.armorValue.setBaseValue(30);
               }
            }

            this.keyWords.add("chest");
            this.keyWords.add("body");
            break;
         case FEET:
            if (var2 > 0) {
               this.armorValue.setUpgradedValue(1.0F, 18);
               if (var2 > 18) {
                  this.armorValue.setBaseValue(18);
               }
            }

            this.keyWords.add("feet");
            this.keyWords.add("boots");
      }

      if (var2 == 0) {
         this.setItemCategory(new String[]{"equipment", "cosmetics"});
         this.keyWords.add("cosmetic");
      } else {
         this.setItemCategory(new String[]{"equipment", "armor"});
         this.setItemCategory(ItemCategory.equipmentManager, new String[]{"armor"});
      }

      this.worldDrawSize = 32;
      this.incinerationTimeMillis = 30000;
   }

   public ArmorItem hairDrawMode(HairDrawMode var1) {
      this.hairDrawOptions = var1;
      return this;
   }

   public ArmorItem drawBodyPart(boolean var1) {
      this.drawBodyPart = var1;
      return this;
   }

   public void onItemRegistryClosed() {
      super.onItemRegistryClosed();
      if (this.armorValue.hasMoreThanOneValue() && this.armorValue.getValue(0.0F) > this.armorValue.getValue(1.0F)) {
         this.armorValue.setBaseValue(this.armorValue.getValue(1.0F));
      }

   }

   public final ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add((Object)this.getPreEnchantmentTooltips(var1, var2, var3));
      if (!var3.getBoolean("isCosmeticSlot")) {
         var4.add((Object)this.getEnchantmentTooltips(var1));
      }

      var4.add((Object)this.getPostEnchantmentTooltips(var1, var2, var3));
      return var4;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = new ListGameTooltips();
      if (this.armorType == ArmorItem.ArmorType.HEAD) {
         var4.add(Localization.translate("itemtooltip", "headslot"));
      }

      if (this.armorType == ArmorItem.ArmorType.CHEST) {
         var4.add(Localization.translate("itemtooltip", "chestslot"));
      }

      if (this.armorType == ArmorItem.ArmorType.FEET) {
         var4.add(Localization.translate("itemtooltip", "feetslot"));
      }

      int var5 = this.getFlatArmorValue(var1);
      if (var5 == 0) {
         var4.add(Localization.translate("itemtooltip", "cosmetic"));
      } else if (var3.getBoolean("isCosmeticSlot")) {
         var4.add(GameColor.RED.getColorCode() + Localization.translate("itemtooltip", "cosmeticslot"));
      } else {
         this.addStatTooltips(var4, var1, (InventoryItem)var3.get(InventoryItem.class, "compareItem"), var3.getBoolean("showDifference"), (Mob)var3.get(Mob.class, "perspective", var2));
      }

      return var4;
   }

   public ListGameTooltips getPostEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = new ListGameTooltips();
      if (!var3.getBoolean("isCosmeticSlot")) {
         GameTooltips var5 = (GameTooltips)var3.get(GameTooltips.class, "setBonus");
         if (var5 != null) {
            var4.add(Localization.translate("itemtooltip", "setbonus"));
            var4.add((Object)var5);
         } else if (var2 != null) {
            SetBonusBuff var6 = this.getSetBuff(var1, var2, false);
            if (var6 != null) {
               var4.add((Object)(new StringTooltips(Localization.translate("itemtooltip", "setbonus"), GameColor.GRAY)));
               GameBlackboard var7 = new GameBlackboard();
               InventoryItem var8 = (InventoryItem)var3.get(InventoryItem.class, "compareItem");
               ActiveBuff var9;
               if (var8 != null) {
                  var9 = new ActiveBuff(var6, var2, 1, (Attacker)null);
                  this.setupSetBuff(var8, (InventoryItem)null, (InventoryItem)null, var2, var9, false);
                  var9.init(new BuffEventSubscriber() {
                     public <T extends MobGenericEvent> void subscribeEvent(Class<T> var1, Consumer<T> var2) {
                     }
                  });
                  var7.set("compareValues", var9);
               }

               var9 = new ActiveBuff(var6, var2, 1, (Attacker)null);
               this.setupSetBuff(var1, (InventoryItem)null, (InventoryItem)null, var2, var9, false);
               var9.init(new BuffEventSubscriber() {
                  public <T extends MobGenericEvent> void subscribeEvent(Class<T> var1, Consumer<T> var2) {
                  }
               });
               var4.add((Object)(new DefaultColoredGameTooltips(var6.getTooltip(var9, var7), GameColor.GRAY)));
            }
         }
      }

      return var4;
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
      this.addArmorTooltip(var1, var2, var3, var4);
      this.addModifierTooltips(var1, var2, var3, var4);
   }

   public String getInventoryRightClickControlTip(Container var1, InventoryItem var2, int var3, ContainerSlot var4) {
      if (var3 != var1.CLIENT_HELMET_SLOT && var3 != var1.CLIENT_CHEST_SLOT && var3 != var1.CLIENT_FEET_SLOT) {
         if (var3 != var1.CLIENT_COSM_HELMET_SLOT && var3 != var1.CLIENT_COSM_CHEST_SLOT && var3 != var1.CLIENT_COSM_FEET_SLOT) {
            return Localization.translate("controls", "equiptip");
         } else {
            return this.getFlatArmorValue(var2) > 0 ? Localization.translate("controls", "equiptip") : Localization.translate("controls", "removetip");
         }
      } else {
         return Localization.translate("controls", "removetip");
      }
   }

   public Supplier<ContainerActionResult> getInventoryRightClickAction(Container var1, InventoryItem var2, int var3, ContainerSlot var4) {
      return () -> {
         ContainerTransferResult var5;
         if (var3 != var1.CLIENT_HELMET_SLOT && var3 != var1.CLIENT_CHEST_SLOT && var3 != var1.CLIENT_FEET_SLOT) {
            ItemCombineResult var6;
            if (var3 != var1.CLIENT_COSM_HELMET_SLOT && var3 != var1.CLIENT_COSM_CHEST_SLOT && var3 != var1.CLIENT_COSM_FEET_SLOT) {
               if (this.getFlatArmorValue(var2) <= 0) {
                  if (this.armorType == ArmorItem.ArmorType.HEAD) {
                     var6 = var1.getSlot(var1.CLIENT_COSM_HELMET_SLOT).swapItems(var4);
                     return new ContainerActionResult(-2004484079, var6.error);
                  } else if (this.armorType == ArmorItem.ArmorType.CHEST) {
                     var6 = var1.getSlot(var1.CLIENT_COSM_CHEST_SLOT).swapItems(var4);
                     return new ContainerActionResult(-2004484078, var6.error);
                  } else if (this.armorType == ArmorItem.ArmorType.FEET) {
                     var6 = var1.getSlot(var1.CLIENT_COSM_FEET_SLOT).swapItems(var4);
                     return new ContainerActionResult(-2004484077, var6.error);
                  } else {
                     return new ContainerActionResult(-2004484080);
                  }
               } else if (this.armorType == ArmorItem.ArmorType.HEAD) {
                  var6 = var1.getSlot(var1.CLIENT_HELMET_SLOT).swapItems(var4);
                  return new ContainerActionResult(674304817, var6.error);
               } else if (this.armorType == ArmorItem.ArmorType.CHEST) {
                  var6 = var1.getSlot(var1.CLIENT_CHEST_SLOT).swapItems(var4);
                  return new ContainerActionResult(674304818, var6.error);
               } else if (this.armorType == ArmorItem.ArmorType.FEET) {
                  var6 = var1.getSlot(var1.CLIENT_FEET_SLOT).swapItems(var4);
                  return new ContainerActionResult(674304819, var6.error);
               } else {
                  return new ContainerActionResult(674304816);
               }
            } else if (this.getFlatArmorValue(var2) > 0) {
               if (this.armorType == ArmorItem.ArmorType.HEAD) {
                  var6 = var1.getSlot(var1.CLIENT_HELMET_SLOT).swapItems(var4);
                  return new ContainerActionResult(-2094192454, var6.error);
               } else if (this.armorType == ArmorItem.ArmorType.CHEST) {
                  var6 = var1.getSlot(var1.CLIENT_CHEST_SLOT).swapItems(var4);
                  return new ContainerActionResult(-2094192453, var6.error);
               } else if (this.armorType == ArmorItem.ArmorType.FEET) {
                  var6 = var1.getSlot(var1.CLIENT_FEET_SLOT).swapItems(var4);
                  return new ContainerActionResult(-2094192452, var6.error);
               } else {
                  return new ContainerActionResult(-2094192455);
               }
            } else {
               var5 = var1.transferToSlots(var4, Arrays.asList(new SlotIndexRange(var1.CLIENT_HOTBAR_START, var1.CLIENT_HOTBAR_END), new SlotIndexRange(var1.CLIENT_INVENTORY_START, var1.CLIENT_INVENTORY_END)));
               return new ContainerActionResult(1314787361, var5.error);
            }
         } else {
            var5 = var1.transferToSlots(var4, Arrays.asList(new SlotIndexRange(var1.CLIENT_HOTBAR_START, var1.CLIENT_HOTBAR_END), new SlotIndexRange(var1.CLIENT_INVENTORY_START, var1.CLIENT_INVENTORY_END)));
            return new ContainerActionResult(843241958, var5.error);
         }
      };
   }

   public void addArmorTooltip(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4) {
      LocalMessageDoubleItemStatTip var5 = new LocalMessageDoubleItemStatTip("itemtooltip", "armorvalue", "value", (double)this.getTotalArmorValue(var2, var4), 0);
      if (var3 != null) {
         var5.setCompareValue((double)this.getTotalArmorValue(var3, var4));
      }

      var1.add(100, var5);
   }

   public void addModifierTooltips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4) {
      ArmorModifiers var5 = this.getArmorModifiers(var2, var4);
      if (var5 != null) {
         Iterator var6 = var5.getModifierTooltips(var3 == null ? null : this.getArmorModifiers(var3, var4)).iterator();

         while(var6.hasNext()) {
            ModifierTooltip var7 = (ModifierTooltip)var6.next();
            var1.add(200, var7.tip);
         }
      }

   }

   public void loadTextures() {
      super.loadTextures();
      this.loadArmorTexture();
   }

   public int getFlatArmorValue(InventoryItem var1) {
      GNDItemMap var2 = var1.getGndData();
      return var2.hasKey("armor") ? var2.getInt("armorValue") : this.armorValue.getValue(this.getUpgradeTier(var1));
   }

   public int getTotalArmorValue(InventoryItem var1, Mob var2) {
      return this.getFlatArmorValue(var1);
   }

   public ArmorItem setSettlerEquipmentValueModifier(float var1) {
      this.setterEquipmentValueModifier = var1;
      return this;
   }

   public float getSettlerEquipmentValue(InventoryItem var1, HumanMob var2) {
      EquipmentItemEnchant var3 = this.getEnchantment(var1);
      return (float)this.getTotalArmorValue(var1, var2) * (var3 == null ? 1.0F : var3.getEnchantCostMod()) * this.setterEquipmentValueModifier;
   }

   public boolean drawBodyPart(InventoryItem var1, PlayerMob var2) {
      return this.drawBodyPart;
   }

   protected void loadArmorTexture() {
      if (this.textureName != null) {
         this.armorTexture = GameTexture.fromFile("player/armor/" + this.textureName);
      }

   }

   public GameTexture getArmorTexture(InventoryItem var1, PlayerMob var2) {
      return this.armorTexture;
   }

   public DrawOptions getArmorDrawOptions(InventoryItem var1, Level var2, PlayerMob var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, boolean var11, boolean var12, GameLight var13, float var14, GameTexture var15) {
      GameTexture var16 = this.getArmorTexture(var1, var3);
      Color var17 = this.getDrawColor(var1, var3);
      return (DrawOptions)(var16 != null ? var16.initDraw().sprite(var4, var5, var6).colorLight(var17, var13).alpha(var14).size(var9, var10).mirror(var11, var12).addShaderTextureFit(var15, 1).pos(var7, var8) : () -> {
      });
   }

   public final DrawOptions getArmorDrawOptions(InventoryItem var1, Level var2, PlayerMob var3, int var4, int var5, int var6, int var7, int var8, boolean var9, boolean var10, GameLight var11, float var12, GameTexture var13) {
      return this.getArmorDrawOptions(var1, var2, var3, var4, var5, var6, var7, var8, 64, 64, var9, var10, var11, var12, var13);
   }

   /** @deprecated */
   @Deprecated
   public DrawOptions getArmorDrawOptions(InventoryItem var1, PlayerMob var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, boolean var10, boolean var11, GameLight var12, float var13, GameTexture var14) {
      return this.getArmorDrawOptions(var1, var2 == null ? null : var2.getLevel(), var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
   }

   /** @deprecated */
   @Deprecated
   public final DrawOptions getArmorDrawOptions(InventoryItem var1, PlayerMob var2, int var3, int var4, int var5, int var6, int var7, boolean var8, boolean var9, GameLight var10, float var11, GameTexture var12) {
      return this.getArmorDrawOptions(var1, var2, var3, var4, var5, var6, var7, 64, 64, var8, var9, var10, var11, var12);
   }

   public void addExtraDrawOptions(HumanDrawOptions var1, InventoryItem var2) {
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return null;
   }

   public boolean hasSet(InventoryItem var1, InventoryItem var2, InventoryItem var3) {
      return this.hasSet(var1.item, var2.item, var3.item);
   }

   /** @deprecated */
   @Deprecated
   public boolean hasSet(Item var1, Item var2, Item var3) {
      return false;
   }

   public SetBonusBuff getSetBuff(InventoryItem var1, Mob var2, boolean var3) {
      return this.getSetBuff(var2, var3);
   }

   public void setupSetBuff(InventoryItem var1, InventoryItem var2, InventoryItem var3, Mob var4, ActiveBuff var5, boolean var6) {
      int var7 = 0;
      int var8 = 0;
      if (var1 != null) {
         var7 += this.getUpgradeLevel(var1);
         ++var8;
      }

      if (var2 != null) {
         var7 += this.getUpgradeLevel(var2);
         ++var8;
      }

      if (var3 != null) {
         var7 += this.getUpgradeLevel(var3);
         ++var8;
      }

      var5.getGndData().setInt("upgradeLevel", Math.round((float)var7 / (float)var8));
   }

   /** @deprecated */
   @Deprecated
   public SetBonusBuff getSetBuff(Mob var1, boolean var2) {
      return null;
   }

   public ArmorBuff[] getBuffs(InventoryItem var1) {
      return new ArmorBuff[0];
   }

   public float getSinkingRate(ItemPickupEntity var1, float var2) {
      return super.getSinkingRate(var1, var2) / 5.0F;
   }

   public boolean isSameGNDData(Level var1, InventoryItem var2, InventoryItem var3, String var4) {
      return var2.getGndData().sameKeys(var3.getGndData(), "enchantment", "upgradeLevel");
   }

   public boolean isEnchantable(InventoryItem var1) {
      return this.getEnchantCost(var1) > 0;
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

   public Set<Integer> getValidEnchantmentIDs(InventoryItem var1) {
      return EnchantmentRegistry.equipmentEnchantments;
   }

   public EquipmentItemEnchant getRandomEnchantment(GameRandom var1, InventoryItem var2) {
      return (EquipmentItemEnchant)Enchantable.getRandomEnchantment(var1, EnchantmentRegistry.equipmentEnchantments, this.getEnchantmentID(var2), EquipmentItemEnchant.class);
   }

   public boolean isValidEnchantment(InventoryItem var1, ItemEnchantment var2) {
      return EnchantmentRegistry.equipmentEnchantments.contains(var2.getID());
   }

   public int getEnchantCost(InventoryItem var1) {
      return this.enchantCost.getValue(var1.item.getUpgradeTier(var1));
   }

   public EquipmentItemEnchant getEnchantment(InventoryItem var1) {
      return (EquipmentItemEnchant)EnchantmentRegistry.getEnchantment(this.getEnchantmentID(var1), EquipmentItemEnchant.class, EquipmentItemEnchant.noEnchant);
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
         int var5 = 10 * ((int)var3 - 1) * var4;
         int var6 = Math.max((int)((1.0F - this.getTier1CostPercent(var1)) * 20.0F), 1) * var4;
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
            var6 = var7 + " (+" + var8 + "%)";
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
      EquipmentItemEnchant var3 = this.getEnchantment(var1);
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
      if (this.getFlatArmorValue(var1) > 0 && this.armorValue.hasMoreThanOneValue()) {
         return this.getUpgradeTier(var1) >= 4.0F ? Localization.translate("ui", "itemupgradelimit") : null;
      } else {
         return Localization.translate("ui", "itemnotupgradable");
      }
   }

   public void addUpgradeStatTips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, Mob var5) {
      DoubleItemStatTip var6 = (new LocalMessageDoubleItemStatTip("item", "tier", "tiernumber", (double)this.getUpgradeTier(var3), 2)).setCompareValue((double)this.getUpgradeTier(var2)).setToString((var0) -> {
         int var2 = (int)var0;
         double var3 = var0 - (double)var2;
         return var3 != 0.0 ? var2 + " (+" + (int)(var3 * 100.0) + "%)" : String.valueOf(var2);
      });
      var1.add(Integer.MIN_VALUE, var6);
      this.addStatTooltips(var1, var3, var2, var5, true);
      SetBonusBuff var7 = this.getSetBuff(var3, var4, false);
      if (var7 != null) {
         ItemStatTipList var8 = new ItemStatTipList();
         var8.add(Integer.MIN_VALUE, new ItemStatTip() {
            public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
               return new LocalMessage("itemtooltip", "setbonus");
            }
         });
         GameBlackboard var9 = new GameBlackboard();
         ActiveBuff var10 = new ActiveBuff(var7, var4, 1, (Attacker)null);
         this.setupSetBuff(var2, (InventoryItem)null, (InventoryItem)null, var4, var10, false);
         var10.init(new BuffEventSubscriber() {
            public <T extends MobGenericEvent> void subscribeEvent(Class<T> var1, Consumer<T> var2) {
            }
         });
         var9.set("compareValues", var10);
         ActiveBuff var11 = new ActiveBuff(var7, var4, 1, (Attacker)null);
         this.setupSetBuff(var3, (InventoryItem)null, (InventoryItem)null, var4, var11, false);
         var11.init(new BuffEventSubscriber() {
            public <T extends MobGenericEvent> void subscribeEvent(Class<T> var1, Consumer<T> var2) {
            }
         });
         LinkedList var12 = new LinkedList();
         var7.addStatTooltips(var12, var11, var10);
         int var13 = 0;
         Iterator var14 = var12.iterator();

         while(var14.hasNext()) {
            ItemStatTip var15 = (ItemStatTip)var14.next();
            var8.add(var13++, var15);
         }

         if (var13 > 0) {
            var1.add(10000, var8);
         }
      }

   }

   protected int getNextUpgradeTier(InventoryItem var1) {
      int var2 = (int)var1.item.getUpgradeTier(var1);
      int var3 = var2 + 1;
      float var4 = (float)this.armorValue.getValue(0.0F);
      float var5 = (float)this.armorValue.getValue((float)var3);
      if (var3 == 1 && var4 < var5) {
         return var3;
      } else {
         while(var4 / var5 > 1.0F - this.armorValue.defaultLevelIncreaseMultiplier / 4.0F && var3 < var2 + 100) {
            ++var3;
            var5 = (float)this.armorValue.getValue((float)var3);
         }

         return var3;
      }
   }

   protected float getTier1CostPercent(InventoryItem var1) {
      return (float)this.armorValue.getValue(0.0F) / (float)this.armorValue.getValue(1.0F);
   }

   public UpgradedItem getUpgradedItem(InventoryItem var1) {
      int var2 = this.getNextUpgradeTier(var1);
      InventoryItem var3 = var1.copy();
      var3.item.setUpgradeTier(var3, (float)var2);
      int var4;
      if (var2 <= 1) {
         var4 = Math.max((int)((1.0F - this.getTier1CostPercent(var1)) * 20.0F), 1);
      } else {
         var4 = var2 * 10;
      }

      return new UpgradedItem(var1, var3, new Ingredient[]{new Ingredient("upgradeshard", var4)});
   }

   public Collection<InventoryItem> getSalvageRewards(InventoryItem var1) {
      int var2 = (int)((float)var1.getAmount() * this.getUpgradeTier(var1) * 5.0F);
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

   public static enum HairDrawMode {
      NO_HAIR,
      UNDER_HAIR,
      OVER_HAIR;

      private HairDrawMode() {
      }

      // $FF: synthetic method
      private static HairDrawMode[] $values() {
         return new HairDrawMode[]{NO_HAIR, UNDER_HAIR, OVER_HAIR};
      }
   }

   public static enum ArmorType {
      HEAD,
      CHEST,
      FEET;

      private ArmorType() {
      }

      // $FF: synthetic method
      private static ArmorType[] $values() {
         return new ArmorType[]{HEAD, CHEST, FEET};
      }
   }
}
