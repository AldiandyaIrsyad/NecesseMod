package necesse.inventory.item.trinketItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;
import necesse.engine.GlobalData;
import necesse.engine.control.Control;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemEnchantment;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuffAbility;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.pickup.ItemPickupEntity;
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
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public abstract class TrinketItem extends Item implements Enchantable<EquipmentItemEnchant> {
   protected int enchantCost;
   public ArrayList<String> disabledBy = new ArrayList();
   public ArrayList<String> disables = new ArrayList();

   public TrinketItem(Item.Rarity var1, int var2) {
      super(1);
      this.setItemCategory(new String[]{"equipment", "trinkets"});
      this.keyWords.add("trinket");
      this.rarity = var1;
      this.enchantCost = var2;
      this.worldDrawSize = 32;
      this.incinerationTimeMillis = 30000;
   }

   public abstract TrinketBuff[] getBuffs(InventoryItem var1);

   public TrinketItem addDisabledBy(String... var1) {
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         if (!this.disabledBy.contains(var5)) {
            this.disabledBy.add(var5);
         }
      }

      return this;
   }

   public TrinketItem addDisables(String... var1) {
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         if (!this.disables.contains(var5)) {
            this.disables.add(var5);
         }
      }

      return this;
   }

   public boolean disabledBy(InventoryItem var1) {
      return this.disabledBy.stream().anyMatch((var1x) -> {
         return var1x.equals(var1.item.getStringID());
      });
   }

   public boolean disables(InventoryItem var1) {
      return this.disables.stream().anyMatch((var1x) -> {
         return var1x.equals(var1.item.getStringID());
      });
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
      var4.add(Localization.translate("itemtooltip", this.isAbilityTrinket(var1) ? "trinketabilityslot" : "trinketslot"));
      TrinketBuff[] var5 = this.getBuffs(var1);
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         TrinketBuff var8 = var5[var7];
         var4.addAll(var8.getTrinketTooltip(this, var1, var2));
      }

      return var4;
   }

   public ListGameTooltips getPostEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = new ListGameTooltips();
      if (var3.getBoolean("isAbilitySlot") && var3.getBoolean("equipped") && this.isAbilityTrinket(var1)) {
         var4.add(Localization.translate("ui", "hotkeytip", "hotkey", "[input=" + Control.TRINKET_ABILITY.id + "]"));
      }

      return var4;
   }

   public boolean isAbilityTrinket(InventoryItem var1) {
      return Arrays.stream(this.getBuffs(var1)).anyMatch((var0) -> {
         return var0 instanceof BuffAbility || var0 instanceof ActiveBuffAbility;
      });
   }

   public String getInvalidInSlotError(Container var1, ContainerSlot var2, InventoryItem var3) {
      return null;
   }

   public void addTrinketAbilityHotkeyTooltip(ListGameTooltips var1, InventoryItem var2) {
      var1.add(Localization.translate("ui", "hotkeytip", "hotkey", "[input=" + Control.TRINKET_ABILITY.id + "]"));
   }

   public String getInventoryRightClickControlTip(Container var1, InventoryItem var2, int var3, ContainerSlot var4) {
      return var3 != var1.CLIENT_TRINKET_ABILITY_SLOT && (var3 < var1.CLIENT_TRINKET_START || var3 > var1.CLIENT_TRINKET_END) ? Localization.translate("controls", "equiptip") : Localization.translate("controls", "removetip");
   }

   public Supplier<ContainerActionResult> getInventoryRightClickAction(Container var1, InventoryItem var2, int var3, ContainerSlot var4) {
      return () -> {
         if (this.isAbilityTrinket(var2) && var3 != var1.CLIENT_TRINKET_ABILITY_SLOT) {
            var1.getSlot(var1.CLIENT_TRINKET_ABILITY_SLOT).swapItems(var4);
            return new ContainerActionResult(-2081175478);
         } else {
            ArrayList var5 = new ArrayList();
            var5.add(var1.getSlot(var1.CLIENT_TRINKET_ABILITY_SLOT));

            for(int var6 = var1.CLIENT_TRINKET_START; var6 <= var1.CLIENT_TRINKET_END; ++var6) {
               var5.add(var1.getSlot(var6));
            }

            if (var5.stream().anyMatch((var1x) -> {
               return var1x.getContainerIndex() == var3;
            })) {
               ContainerTransferResult var14 = var1.transferToSlots(var4, Arrays.asList(new SlotIndexRange(var1.CLIENT_HOTBAR_START, var1.CLIENT_HOTBAR_END), new SlotIndexRange(var1.CLIENT_INVENTORY_START, var1.CLIENT_INVENTORY_END)));
               return new ContainerActionResult(-455167603, var14.error);
            } else {
               ContainerSlot var12 = null;
               Iterator var7 = var5.iterator();

               while(var7.hasNext()) {
                  ContainerSlot var8 = (ContainerSlot)var7.next();
                  if (var8.getItemInvalidError(var2) == null && !var8.isClear()) {
                     InventoryItem var9 = var8.getItem();
                     if (var9.item instanceof TrinketItem) {
                        TrinketItem var10 = (TrinketItem)var9.item;
                        TrinketItem var11 = (TrinketItem)var2.item;
                        if (var10.getID() != var11.getID() && !var10.disables(var2) && !var10.disabledBy(var2) && !var11.disables(var9) && !var11.disabledBy(var9)) {
                           continue;
                        }

                        var12 = var8;
                        break;
                     }

                     var12 = var8;
                     break;
                  }
               }

               if (var12 == null) {
                  var12 = (ContainerSlot)var5.stream().filter((var1x) -> {
                     return var1x.getItemInvalidError(var2) == null;
                  }).filter(ContainerSlot::isClear).findFirst().orElseGet(() -> {
                     return (ContainerSlot)var5.stream().filter((var1) -> {
                        return var1.getItemInvalidError(var2) == null;
                     }).findFirst().orElse((Object)null);
                  });
               }

               if (var12 != null) {
                  ItemCombineResult var13 = var12.swapItems(var4);
                  return new ContainerActionResult(417568726 + var12.getContainerIndex(), var13.error);
               } else {
                  return new ContainerActionResult(417568726);
               }
            }
         }
      };
   }

   public float getSinkingRate(ItemPickupEntity var1, float var2) {
      return super.getSinkingRate(var1, var2) / 5.0F;
   }

   public boolean canCombineItem(Level var1, PlayerMob var2, InventoryItem var3, InventoryItem var4, String var5) {
      return !super.canCombineItem(var1, var2, var3, var4, var5) ? false : this.isSameGNDData(var1, var3, var4, var5);
   }

   public boolean isSameGNDData(Level var1, InventoryItem var2, InventoryItem var3, String var4) {
      return var2.getGndData().sameKeys(var3.getGndData(), "enchantment");
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

   public EquipmentItemEnchant getRandomEnchantment(GameRandom var1, InventoryItem var2) {
      return (EquipmentItemEnchant)Enchantable.getRandomEnchantment(var1, EnchantmentRegistry.equipmentEnchantments, this.getEnchantmentID(var2), EquipmentItemEnchant.class);
   }

   public boolean isValidEnchantment(InventoryItem var1, ItemEnchantment var2) {
      return EnchantmentRegistry.equipmentEnchantments.contains(var2.getID());
   }

   public int getEnchantCost(InventoryItem var1) {
      return this.enchantCost;
   }

   public Set<Integer> getValidEnchantmentIDs(InventoryItem var1) {
      return EnchantmentRegistry.equipmentEnchantments;
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
      return super.getBrokerValue(var1) * this.getEnchantment(var1).getEnchantCostMod();
   }

   public GameMessage getLocalization(InventoryItem var1) {
      EquipmentItemEnchant var2 = this.getEnchantment(var1);
      return (GameMessage)(var2 != null && var2.getID() > 0 ? new LocalMessage("enchantment", "format", new Object[]{"enchantment", var2.getLocalization(), "item", super.getLocalization(var1)}) : super.getLocalization(var1));
   }

   public InventoryItem getDefaultLootItem(GameRandom var1, int var2) {
      InventoryItem var3 = super.getDefaultLootItem(var1, var2);
      if (this.isEnchantable(var3) && var1.getChance(0.65F)) {
         ((Enchantable)var3.item).addRandomEnchantment(var3, var1);
      }

      return var3;
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
