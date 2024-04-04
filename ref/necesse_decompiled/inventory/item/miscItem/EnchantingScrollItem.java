package necesse.inventory.item.miscItem;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemEnchantment;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.EnchantingScrollContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.enchants.EquipmentItemEnchant;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class EnchantingScrollItem extends Item {
   private HashMap<String, GameTexture> typeTextures = new HashMap();
   public static ArrayList<EnchantScrollType> types = new ArrayList();

   public EnchantingScrollItem() {
      super(1);
      this.rarity = Item.Rarity.RARE;
      this.setItemCategory(new String[]{"misc"});
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      ItemEnchantment var5 = this.getEnchantment(var1);
      if (var5 != null && var5 != EquipmentItemEnchant.noEnchant) {
         EnchantScrollType var6 = this.getType(var5);
         if (var6 != null) {
            var4.add((GameMessage)((GameMessage)var6.itemTooltip.apply(var5)), 300);
         } else {
            var4.add("UNKNOWN_ENCHANT_TYPE");
         }

         var4.add(Localization.translate("itemtooltip", "singleuse"));
         var4.add(Localization.translate("itemtooltip", "rclickinvopentip"));
         var4.add((Object)var5.getTooltips());
      } else {
         var4.add("INVALID_ENCHANT");
      }

      return var4;
   }

   public int getTypeIndex(ItemEnchantment var1) {
      if (var1 != null) {
         for(int var2 = 0; var2 < types.size(); ++var2) {
            EnchantScrollType var3 = (EnchantScrollType)types.get(var2);
            if (var3.isPartOfThis.test(var1)) {
               return var2;
            }
         }
      }

      return -1;
   }

   public EnchantScrollType getType(ItemEnchantment var1) {
      int var2 = this.getTypeIndex(var1);
      return var2 != -1 ? (EnchantScrollType)types.get(var2) : null;
   }

   public GameSprite getItemSprite(InventoryItem var1, PlayerMob var2) {
      ItemEnchantment var3 = this.getEnchantment(var1);
      if (var3 != null && var3 != EquipmentItemEnchant.noEnchant) {
         EnchantScrollType var4 = this.getType(var3);
         if (var4 != null) {
            GameTexture var5 = (GameTexture)this.typeTextures.get(var4.stringID);
            if (var5 != null) {
               return new GameSprite(var5, 32);
            }
         }
      }

      return super.getItemSprite(var1, var2);
   }

   protected void loadItemTextures() {
      super.loadItemTextures();
      Iterator var1 = types.iterator();

      while(var1.hasNext()) {
         EnchantScrollType var2 = (EnchantScrollType)var1.next();

         try {
            GameTexture var3 = GameTexture.fromFileRaw("items/" + this.getStringID() + "_" + var2.stringID);
            this.typeTextures.put(var2.stringID, var3);
         } catch (FileNotFoundException var4) {
         }
      }

   }

   public Supplier<ContainerActionResult> getInventoryRightClickAction(Container var1, InventoryItem var2, int var3, ContainerSlot var4) {
      return () -> {
         if (var4.getInventory() == var1.getClient().playerMob.getInv().main) {
            if (var1.getClient().isServer()) {
               ServerClient var3x = var1.getClient().getServerClient();
               PacketOpenContainer var4x = new PacketOpenContainer(ContainerRegistry.ENCHANTING_SCROLL_CONTAINER, EnchantingScrollContainer.getContainerContent(var3x, var3));
               ContainerRegistry.openAndSendContainer(var3x, var4x);
            }

            return new ContainerActionResult(-1390943614);
         } else {
            return new ContainerActionResult(-1281215057, Localization.translate("itemtooltip", "rclickinvopenerror"));
         }
      };
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

   public ItemEnchantment getEnchantment(InventoryItem var1) {
      return EnchantmentRegistry.getEnchantment(this.getEnchantmentID(var1), ItemEnchantment.class, EquipmentItemEnchant.noEnchant);
   }

   public boolean isSameGNDData(Level var1, InventoryItem var2, InventoryItem var3, String var4) {
      return var2.getGndData().sameKeys(var3.getGndData(), "enchantment");
   }

   public boolean canCombineItem(Level var1, PlayerMob var2, InventoryItem var3, InventoryItem var4, String var5) {
      return !super.canCombineItem(var1, var2, var3, var4, var5) ? false : this.isSameGNDData(var1, var3, var4, var5);
   }

   public float getBrokerValue(InventoryItem var1) {
      return super.getBrokerValue(var1) * this.getEnchantment(var1).getEnchantCostMod();
   }

   public GameMessage getLocalization(InventoryItem var1) {
      ItemEnchantment var2 = this.getEnchantment(var1);
      return (GameMessage)(var2 != null && var2.getID() > 0 ? new LocalMessage("enchantment", "format", new Object[]{"enchantment", var2.getLocalization(), "item", super.getLocalization(var1)}) : super.getLocalization(var1));
   }

   public void addDefaultItems(List<InventoryItem> var1, PlayerMob var2) {
      Iterator var3 = EnchantmentRegistry.getEnchantments().iterator();

      while(var3.hasNext()) {
         ItemEnchantment var4 = (ItemEnchantment)var3.next();
         if (var4.getID() != 0) {
            InventoryItem var5 = this.getDefaultItem(var2, 1);
            this.setEnchantment(var5, var4.getID());
            var1.add(var5);
         }
      }

   }

   public int compareToSameItem(InventoryItem var1, InventoryItem var2) {
      ItemEnchantment var3 = this.getEnchantment(var1);
      ItemEnchantment var4 = this.getEnchantment(var2);
      if (var3 == var4) {
         return super.compareToSameItem(var1, var2);
      } else if (var3 != null && var3 != EquipmentItemEnchant.noEnchant) {
         if (var4 != null && var4 != EquipmentItemEnchant.noEnchant) {
            int var5 = this.getTypeIndex(var3);
            int var6 = this.getTypeIndex(var4);
            return var5 != var6 ? Integer.compare(var5, var6) : super.compareToSameItem(var1, var2);
         } else {
            return -1;
         }
      } else {
         return 1;
      }
   }

   public InventoryItem getDefaultLootItem(GameRandom var1, int var2) {
      InventoryItem var3 = super.getDefaultLootItem(var1, var2);
      TicketSystemList var4 = new TicketSystemList();
      Iterator var5 = types.iterator();

      while(var5.hasNext()) {
         EnchantScrollType var6 = (EnchantScrollType)var5.next();
         var4.addObject(var6.tickets, var6);
      }

      EnchantScrollType var7 = (EnchantScrollType)var4.getRandomObject(var1);
      ItemEnchantment var8 = (ItemEnchantment)var7.randomEnchantmentGetter.apply(var1);
      if (var8 == null) {
         this.setEnchantment(var3, 0);
      } else {
         this.setEnchantment(var3, var8.getID());
      }

      return var3;
   }

   static {
      types.add(new EnchantScrollType("equipment", 200, (var0) -> {
         return EnchantmentRegistry.equipmentEnchantments.contains(var0.getID());
      }, (var0) -> {
         return (ItemEnchantment)var0.getOneOf((Object[])((ItemEnchantment[])EnchantmentRegistry.equipmentEnchantments.stream().map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter((var0x) -> {
            return var0x.getEnchantCostMod() >= 1.0F;
         }).toArray((var0x) -> {
            return new ItemEnchantment[var0x];
         })));
      }, (var0) -> {
         return new LocalMessage("itemtooltip", "enchantingscrollequipmenttip", "enchantment", var0.getLocalization());
      }, (var0) -> {
         return new LocalMessage("ui", "enchantscrollequipment");
      }));
      types.add(new EnchantScrollType("tool", 40, (var0) -> {
         return EnchantmentRegistry.toolDamageEnchantments.contains(var0.getID());
      }, (var0) -> {
         return (ItemEnchantment)var0.getOneOf((Object[])((ItemEnchantment[])EnchantmentRegistry.toolDamageEnchantments.stream().map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter((var0x) -> {
            return var0x.getEnchantCostMod() >= 1.0F;
         }).toArray((var0x) -> {
            return new ItemEnchantment[var0x];
         })));
      }, (var0) -> {
         return new LocalMessage("itemtooltip", "enchantingscrolltooltip", "enchantment", var0.getLocalization());
      }, (var0) -> {
         return new LocalMessage("ui", "enchantscrolltool");
      }));
      types.add(new EnchantScrollType("melee", 100, (var0) -> {
         return EnchantmentRegistry.meleeItemEnchantments.contains(var0.getID());
      }, (var0) -> {
         return (ItemEnchantment)var0.getOneOf((Object[])((ItemEnchantment[])EnchantmentRegistry.meleeItemEnchantments.stream().map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter((var0x) -> {
            return var0x.getEnchantCostMod() >= 1.0F;
         }).toArray((var0x) -> {
            return new ItemEnchantment[var0x];
         })));
      }, (var0) -> {
         return new LocalMessage("itemtooltip", "enchantingscrollmeleetip", "enchantment", var0.getLocalization());
      }, (var0) -> {
         return new LocalMessage("ui", "enchantscrollmelee");
      }));
      types.add(new EnchantScrollType("ranged", 100, (var0) -> {
         return EnchantmentRegistry.rangedItemEnchantments.contains(var0.getID());
      }, (var0) -> {
         return (ItemEnchantment)var0.getOneOf((Object[])((ItemEnchantment[])EnchantmentRegistry.rangedItemEnchantments.stream().map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter((var0x) -> {
            return var0x.getEnchantCostMod() >= 1.0F;
         }).toArray((var0x) -> {
            return new ItemEnchantment[var0x];
         })));
      }, (var0) -> {
         return new LocalMessage("itemtooltip", "enchantingscrollrangedtip", "enchantment", var0.getLocalization());
      }, (var0) -> {
         return new LocalMessage("ui", "enchantscrollranged");
      }));
      types.add(new EnchantScrollType("magic", 100, (var0) -> {
         return EnchantmentRegistry.magicItemEnchantments.contains(var0.getID());
      }, (var0) -> {
         return (ItemEnchantment)var0.getOneOf((Object[])((ItemEnchantment[])EnchantmentRegistry.magicItemEnchantments.stream().map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter((var0x) -> {
            return var0x.getEnchantCostMod() >= 1.0F;
         }).toArray((var0x) -> {
            return new ItemEnchantment[var0x];
         })));
      }, (var0) -> {
         return new LocalMessage("itemtooltip", "enchantingscrollmagictip", "enchantment", var0.getLocalization());
      }, (var0) -> {
         return new LocalMessage("ui", "enchantscrollmagic");
      }));
      types.add(new EnchantScrollType("summon", 100, (var0) -> {
         return EnchantmentRegistry.summonItemEnchantments.contains(var0.getID());
      }, (var0) -> {
         return (ItemEnchantment)var0.getOneOf((Object[])((ItemEnchantment[])EnchantmentRegistry.summonItemEnchantments.stream().map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter((var0x) -> {
            return var0x.getEnchantCostMod() >= 1.0F;
         }).toArray((var0x) -> {
            return new ItemEnchantment[var0x];
         })));
      }, (var0) -> {
         return new LocalMessage("itemtooltip", "enchantingscrollsummontip", "enchantment", var0.getLocalization());
      }, (var0) -> {
         return new LocalMessage("ui", "enchantscrollsummon");
      }));
   }

   public static class EnchantScrollType {
      public String stringID;
      public int tickets;
      public Predicate<ItemEnchantment> isPartOfThis;
      public Function<GameRandom, ItemEnchantment> randomEnchantmentGetter;
      public Function<ItemEnchantment, GameMessage> itemTooltip;
      public Function<ItemEnchantment, GameMessage> enchantTip;

      public EnchantScrollType(String var1, int var2, Predicate<ItemEnchantment> var3, Function<GameRandom, ItemEnchantment> var4, Function<ItemEnchantment, GameMessage> var5, Function<ItemEnchantment, GameMessage> var6) {
         this.stringID = var1;
         this.tickets = var2;
         this.isPartOfThis = var3;
         this.randomEnchantmentGetter = var4;
         this.itemTooltip = var5;
         this.enchantTip = var6;
      }
   }
}
