package necesse.inventory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerInventory;
import necesse.engine.network.packet.PacketPlayerInventoryPart;
import necesse.engine.network.packet.PacketPlayerInventorySlot;
import necesse.engine.network.packet.PacketShowPickupText;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.Performance;
import necesse.entity.Entity;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameResources;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ShirtArmorItem;
import necesse.inventory.item.armorItem.ShoesArmorItem;
import necesse.level.maps.hudManager.floatText.ItemPickupText;

public class PlayerInventoryManager {
   public static final int EQUIPMENT_MOUNT_SLOT = 0;
   public static final int EQUIPMENT_TRINKET_ABILITY_SLOT = 1;
   protected final ArrayList<PlayerInventory> inventories = new ArrayList();
   public final PlayerInventory drag;
   public final PlayerInventory main;
   public final PlayerInventory armor;
   public final PlayerInventory cosmetic;
   public final PlayerInventory equipment;
   public final PlayerInventory trinkets;
   public final PlayerInventory trash;
   public final PlayerInventory cloud;
   public final PlayerInventory party;
   protected boolean updatePartySize;
   protected boolean hasPartyItems;
   public static int MAX_PARTY_INVENTORY_SIZE = 20;
   private final ArrayList<PlayerTempInventory> tempInvs;
   public final PlayerMob player;

   public PlayerInventoryManager(PlayerMob var1) {
      this.player = var1;
      this.drag = new PlayerInventory(this.inventories, var1, 1, false, false, true);
      this.main = new PlayerInventory(this.inventories, var1, 50, false, true, true) {
         public void updateSlot(int var1) {
            super.updateSlot(var1);
            if (!this.isSlotClear(var1) && this.player.getLevel() != null && this.player.isServer()) {
               ServerClient var2 = this.player.getServerClient();
               if (var2.achievementsLoaded() && !var2.achievements().HOARDER.isCompleted()) {
                  HashSet var3 = new HashSet();
                  boolean var4 = true;

                  for(int var5 = 0; var5 < this.getSize(); ++var5) {
                     if (this.isSlotClear(var5)) {
                        var4 = false;
                        break;
                     }

                     int var6 = this.getItemID(var5);
                     if (var3.contains(var6)) {
                        var4 = false;
                        break;
                     }

                     var3.add(var6);
                  }

                  if (var4) {
                     var2.achievements().HOARDER.markCompleted(var2);
                  }
               }
            }

         }
      };
      this.armor = new PlayerInventory(this.inventories, var1, 3, false, false, true) {
         public void updateSlot(int var1) {
            super.updateSlot(var1);
            this.player.equipmentBuffManager.updateArmorBuffs();
            if (this.player.getLevel() != null && this.player.isServer()) {
               ServerClient var2 = this.player.getServerClient();
               if (var2.achievementsLoaded() && !var2.achievements().SELF_PROCLAIMED.isCompleted() && !this.isSlotClear(var1) && this.getItemID(var1) == ItemRegistry.getItemID("goldcrown")) {
                  var2.achievements().SELF_PROCLAIMED.markCompleted(var2);
               }
            }

         }
      };
      this.cosmetic = new PlayerInventory(this.inventories, var1, 3, false, false, true) {
         public void updateSlot(int var1) {
            super.updateSlot(var1);
            this.player.equipmentBuffManager.updateCosmeticSetBonus();
         }
      };
      this.equipment = new PlayerInventory(this.inventories, var1, 2, false, false, true) {
         public void updateSlot(int var1) {
            super.updateSlot(var1);
            if (var1 == 1) {
               this.player.equipmentBuffManager.updateTrinketBuffs();
               if (this.player.getLevel() != null && this.player.isServer() && this.player.getNetworkClient() != null && !this.isSlotClear(var1)) {
                  Item var2 = this.getItemSlot(var1);
                  ServerClient var3 = (ServerClient)this.player.getNetworkClient();
                  var3.newStats.trinkets_worn.addTrinketWorn(var2);
                  if (var2.isTrinketItem() && var3.achievementsLoaded()) {
                     var3.achievements().EQUIP_ABILITY.markCompleted(var3);
                  }
               }
            }

         }
      };
      this.trinkets = new PlayerInventory(this.inventories, var1, 4, true, false, true) {
         public void updateSlot(int var1) {
            super.updateSlot(var1);
            this.player.equipmentBuffManager.updateTrinketBuffs();
            if (this.player.getLevel() != null && this.player.isServer() && this.player.getNetworkClient() != null && !this.isSlotClear(var1)) {
               ((ServerClient)this.player.getNetworkClient()).newStats.trinkets_worn.addTrinketWorn(this.getItemSlot(var1));
            }

         }
      };
      this.trash = new PlayerInventory(this.inventories, var1, 1, false, false, false);
      this.cloud = new PlayerInventory(this.inventories, var1, 0, true, false, true);
      this.party = new PlayerInventory(this.inventories, var1, 5, true, false, false) {
         public void updateSlot(int var1) {
            super.updateSlot(var1);
            PlayerInventoryManager.this.updatePartySize = true;
         }
      };
      this.tempInvs = new ArrayList();
   }

   public PlayerInventory getInventoryByID(int var1) {
      if (var1 < this.inventories.size()) {
         return (PlayerInventory)this.inventories.get(var1);
      } else {
         Iterator var2 = this.tempInvs.iterator();

         PlayerInventory var3;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            var3 = (PlayerInventory)var2.next();
         } while(var3.getInventoryID() != var1);

         return var3;
      }
   }

   public void tick() {
      Performance.record(this.player.getLevel().tickManager(), "tickItems", (Runnable)(() -> {
         Iterator var1 = this.inventories.iterator();

         while(var1.hasNext()) {
            PlayerInventory var2 = (PlayerInventory)var1.next();
            var2.tickItems(this.player);
         }

         var1 = this.tempInvs.iterator();

         while(var1.hasNext()) {
            PlayerTempInventory var3 = (PlayerTempInventory)var1.next();
            if (!var3.shouldDispose()) {
               var3.tickItems(this.player);
            }
         }

      }));
      if (this.updatePartySize) {
         if (this.adjustPartyInventorySize()) {
         }

         this.updatePartySize = false;
         this.hasPartyItems = false;

         for(int var1 = 0; var1 < this.party.getSize(); ++var1) {
            if (!this.party.isSlotClear(var1)) {
               this.hasPartyItems = true;
               break;
            }
         }
      }

      boolean var5 = this.player.isServer();
      if (var5) {
         boolean var2 = this.inventories.stream().allMatch(Inventory::isFullDirty);
         if (var2) {
            this.player.getLevel().getServer().network.sendToAllClients(new PacketPlayerInventory(this.player.getServerClient()));
            this.clean();
         } else {
            Iterator var3 = this.inventories.iterator();

            while(var3.hasNext()) {
               PlayerInventory var4 = (PlayerInventory)var3.next();
               this.tickSync(var4);
            }
         }
      }

      for(int var6 = 0; var6 < this.tempInvs.size(); ++var6) {
         PlayerTempInventory var7 = (PlayerTempInventory)this.tempInvs.get(var6);
         if (var5) {
            this.tickSync(var7);
         }

         if (var7.shouldDispose()) {
            var7.dispose();
            this.tempInvs.remove(var6);
            --var6;
         }
      }

   }

   private void tickSync(PlayerInventory var1) {
      if (var1.isFullDirty()) {
         this.player.getLevel().getServer().network.sendToAllClients(new PacketPlayerInventoryPart(this.player.getServerClient(), var1));
         var1.clean();
      } else {
         for(int var2 = 0; var2 < var1.getSize(); ++var2) {
            if (var1.isDirty(var2)) {
               this.player.getLevel().getServer().network.sendToAllClients(new PacketPlayerInventorySlot(this.player.getServerClient(), new PlayerInventorySlot(var1, var2)));
               var1.clean(var2);
            }
         }
      }

   }

   public void giveLookArmor(boolean var1) {
      Item var2 = this.cosmetic.getItemSlot(1);
      if (var1 || var2 != null && var2.getStringID().equals("shirt")) {
         this.cosmetic.setItem(1, ShirtArmorItem.addColorData(new InventoryItem("shirt"), this.player.look.getShirtColor()));
      }

      Item var3 = this.cosmetic.getItemSlot(2);
      if (var1 || var3 != null && var3.getStringID().equals("shoes")) {
         this.cosmetic.setItem(2, ShoesArmorItem.addColorData(new InventoryItem("shoes"), this.player.look.getShoesColor()));
      }

   }

   public void giveLookArmor() {
      this.giveLookArmor(true);
   }

   public void giveStarterItems() {
      this.giveLookArmor();
      if (this.getAmount(ItemRegistry.getItem("woodaxe"), false, false, false, "startitem") == 0) {
         this.main.addItem(this.player.getLevel(), this.player, new InventoryItem("woodaxe"), "startitem", (InventoryAddConsumer)null);
      }

      if (this.getAmount(ItemRegistry.getItem("woodsword"), false, false, false, "startitem") == 0) {
         this.main.addItem(this.player.getLevel(), this.player, new InventoryItem("woodsword"), "startitem", (InventoryAddConsumer)null);
      }

      if (this.getAmount(ItemRegistry.getItem("craftingguide"), false, false, false, "startitem") == 0) {
         this.main.addItem(this.player.getLevel(), this.player, new InventoryItem("craftingguide"), "startitem", (InventoryAddConsumer)null);
      }

   }

   /** @deprecated */
   @Deprecated
   public boolean addItem(InventoryItem var1, boolean var2, String var3) {
      return this.addItem(var1, var2, var3, (InventoryAddConsumer)null);
   }

   public boolean addItem(InventoryItem var1, boolean var2, String var3, InventoryAddConsumer var4) {
      return this.addItem(var1, false, var2, var3, var4);
   }

   /** @deprecated */
   @Deprecated
   public boolean addItem(InventoryItem var1, boolean var2, boolean var3, String var4) {
      return this.addItem(var1, var2, var3, var4, (InventoryAddConsumer)null);
   }

   public boolean addItem(InventoryItem var1, boolean var2, boolean var3, String var4, InventoryAddConsumer var5) {
      if (this.player.hotbarLocked && !var3) {
         boolean var6 = this.main.addItemOnlyCombine(this.player.getLevel(), this.player, var1, 0, 9, var2, var4, false, false, var5);
         if (var1.getAmount() > 0) {
            var6 = this.main.addItem(this.player.getLevel(), this.player, var1, 10, this.main.getSize() - 1, var2, var4, false, false, var5) || var6;
         }

         return var6;
      } else {
         return this.main.addItem(this.player.getLevel(), this.player, var1, 0, this.main.getSize() - 1, var2, var4, false, false, var5);
      }
   }

   public int canAddItem(InventoryItem var1, boolean var2, String var3) {
      if (this.player.hotbarLocked && !var2) {
         int var4 = this.main.canAddItemOnlyCombine(this.player.getLevel(), this.player, var1, 0, 9, var3, false, false);
         if (var4 > 0) {
            if (var4 >= var1.getAmount()) {
               return var4;
            }

            var1 = var1.copy(var1.getAmount() - var4);
         }

         return this.main.canAddItem(this.player.getLevel(), this.player, var1, 10, this.main.getSize() - 1, var3, false, false) + var4;
      } else {
         return this.main.canAddItem(this.player.getLevel(), this.player, var1, 0, this.main.getSize() - 1, var3, false, false);
      }
   }

   /** @deprecated */
   @Deprecated
   public boolean addItem(InventoryItem var1, PlayerInventorySlot var2, boolean var3, String var4) {
      return this.addItem(var1, var2, var3, var4, (InventoryAddConsumer)null);
   }

   public boolean addItem(InventoryItem var1, PlayerInventorySlot var2, boolean var3, String var4, InventoryAddConsumer var5) {
      PlayerInventory var6 = var2.getInv(this);
      return var6 == null ? this.addItem(var1, false, var4, var5) : var6.addItem(this.player.getLevel(), this.player, var1, var2.slot, var3, var4, var5, this.main);
   }

   public ItemPickupEntity addItemsDropRemaining(InventoryItem var1, String var2, Entity var3, boolean var4, boolean var5) {
      return this.addItemsDropRemaining(var1, var2, var3, var4, var4, var5);
   }

   public ItemPickupEntity addItemsDropRemaining(InventoryItem var1, String var2, Entity var3, boolean var4, boolean var5, boolean var6) {
      return this.addItemsDropRemaining(var1, var2, var3, var4, var5, var6, (InventoryAddConsumer)null);
   }

   public ItemPickupEntity addItemsDropRemaining(InventoryItem var1, String var2, Entity var3, boolean var4, boolean var5, boolean var6, InventoryAddConsumer var7) {
      int var8 = var1.getAmount();
      this.addItem(var1, var6, var2, var7);
      int var9 = var8 - var1.getAmount();
      if (var9 > 0 && this.player != null && this.player.getLevel() != null && var4) {
         if (this.player.isServerClient()) {
            this.player.getServerClient().sendPacket(new PacketShowPickupText(var1.item, var9, var5));
         } else {
            if (Settings.showPickupText) {
               this.player.getLevel().hudManager.addElement(new ItemPickupText(this.player, new InventoryItem(var1.item, var9)));
            }

            if (var5) {
               Screen.playSound(GameResources.pop, SoundEffect.effect(this.player));
            }
         }
      }

      if (var3 != null && var3.getLevel() != null && var3.isServer() && var1.getAmount() > 0) {
         ItemPickupEntity var10;
         var3.getLevel().entityManager.pickups.add(var10 = var1.getPickupEntity(var3.getLevel(), var3.x, var3.y));
         return var10;
      } else {
         return null;
      }
   }

   public int getAmount(Item var1, boolean var2, boolean var3, boolean var4, String var5) {
      int var6 = 0;
      var6 += this.drag.getAmount(this.player.getLevel(), this.player, var1, var5);
      var6 += this.main.getAmount(this.player.getLevel(), this.player, var1, var5);
      var6 += this.armor.getAmount(this.player.getLevel(), this.player, var1, var5);
      var6 += this.cosmetic.getAmount(this.player.getLevel(), this.player, var1, var5);
      var6 += this.equipment.getAmount(this.player.getLevel(), this.player, var1, var5);
      var6 += this.trinkets.getAmount(this.player.getLevel(), this.player, var1, var5);
      if (var2) {
         var6 += this.cloud.getAmount(this.player.getLevel(), this.player, var1, var5);
      }

      if (var3) {
         var6 += this.trash.getAmount(this.player.getLevel(), this.player, var1, var5);
      }

      PlayerTempInventory var8;
      if (var4) {
         for(Iterator var7 = this.tempInvs.iterator(); var7.hasNext(); var6 += var8.getAmount(this.player.getLevel(), this.player, var1, var5)) {
            var8 = (PlayerTempInventory)var7.next();
         }
      }

      return var6;
   }

   public void addSaveData(SaveData var1) {
      var1.addSaveData(InventorySave.getSave(this.drag, "DRAG"));
      var1.addSaveData(InventorySave.getSave(this.main, "MAIN"));
      var1.addSaveData(InventorySave.getSave(this.armor, "ARMOR"));
      var1.addSaveData(InventorySave.getSave(this.cosmetic, "COSMETIC"));
      var1.addSaveData(InventorySave.getSave(this.equipment, "EQUIPMENT"));
      var1.addSaveData(InventorySave.getSave(this.trinkets, "TRINKETS"));
      var1.addSaveData(InventorySave.getSave(this.cloud, "CLOUD"));
      var1.addSaveData(InventorySave.getSave(this.party, "PARTY"));
   }

   public void applyLoadData(LoadData var1) {
      LoadData var2 = var1.getFirstLoadDataByName("DRAG");
      if (var2 != null) {
         this.drag.override(InventorySave.loadSave(var2), false, false);
      }

      LoadData var3 = var1.getFirstLoadDataByName("MAIN");
      if (var3 != null) {
         this.main.override(InventorySave.loadSave(var3), false, false);
      }

      LoadData var4 = var1.getFirstLoadDataByName("ARMOR");
      if (var4 != null) {
         this.armor.override(InventorySave.loadSave(var4), false, false);
      }

      LoadData var5 = var1.getFirstLoadDataByName("COSMETIC");
      if (var5 != null) {
         this.cosmetic.override(InventorySave.loadSave(var5), false, false);
      }

      LoadData var6 = var1.getFirstLoadDataByName("EQUIPMENT");
      if (var6 != null) {
         this.equipment.override(InventorySave.loadSave(var6), false, false);
      }

      LoadData var7 = var1.getFirstLoadDataByName("TRINKETS");
      if (var7 != null) {
         Inventory var8 = InventorySave.loadSave(var7);
         if (var8.getSize() < this.trinkets.getSize()) {
            var8.changeSize(this.trinkets.getSize());
         }

         this.trinkets.override(var8, true, false);
      }

      LoadData var10 = var1.getFirstLoadDataByName("CLOUD");
      if (var10 != null) {
         this.cloud.override(InventorySave.loadSave(var10), true, false);
         this.cloud.adjustSize(0, -1, 0);
      }

      LoadData var9 = var1.getFirstLoadDataByName("PARTY");
      if (var9 != null) {
         this.party.override(InventorySave.loadSave(var9), true, false);
         this.party.compressItems();
         this.adjustPartyInventorySize();
      }

   }

   public boolean adjustPartyInventorySize() {
      return this.party.adjustSize(5, MAX_PARTY_INVENTORY_SIZE, 1);
   }

   public boolean hasPartyItems() {
      return this.hasPartyItems;
   }

   public void setupContentPacket(PacketWriter var1) {
      this.drag.writeContent(var1);
      this.main.writeContent(var1);
      this.armor.writeContent(var1);
      this.cosmetic.writeContent(var1);
      this.equipment.writeContent(var1);
      this.trinkets.writeContent(var1);
      this.trash.writeContent(var1);
      this.cloud.writeContent(var1);
      this.party.writeContent(var1);
   }

   public void applyContentPacket(PacketReader var1) {
      this.drag.override(Inventory.getInventory(var1));
      this.main.override(Inventory.getInventory(var1));
      this.armor.override(Inventory.getInventory(var1));
      this.cosmetic.override(Inventory.getInventory(var1));
      this.equipment.override(Inventory.getInventory(var1));
      this.trinkets.override(Inventory.getInventory(var1));
      this.trash.override(Inventory.getInventory(var1));
      this.cloud.override(Inventory.getInventory(var1));
      this.party.override(Inventory.getInventory(var1));
   }

   public void setupLookContentPacket(PacketWriter var1) {
      this.armor.writeContent(var1);
      this.cosmetic.writeContent(var1);
      this.equipment.writeContent(var1);
      this.trinkets.writeContent(var1);
   }

   public void applyLookContentPacket(PacketReader var1) {
      this.armor.override(Inventory.getInventory(var1));
      this.cosmetic.override(Inventory.getInventory(var1));
      this.equipment.override(Inventory.getInventory(var1));
      this.trinkets.override(Inventory.getInventory(var1));
   }

   public void applyInventoryPartPacket(PacketPlayerInventoryPart var1) {
      PlayerInventory var2 = this.getInventoryByID(var1.inventoryID);
      if (var2 != null) {
         var2.override(Inventory.getInventory(var1.inventoryContent));
      }

   }

   public void clean() {
      this.inventories.forEach(Inventory::clean);
   }

   public void markFullDirty() {
      this.inventories.forEach(Inventory::markFullDirty);
   }

   public void clearInventories() {
      this.inventories.forEach(Inventory::clearInventory);
   }

   private void dropInventory(PlayerInventory var1, int var2, int var3) {
      for(int var4 = var2; var4 <= var3; ++var4) {
         if (!var1.isSlotClear(var4)) {
            PlayerInventorySlot var5 = new PlayerInventorySlot(var1, var4);
            boolean var6 = var5.isItemLocked(this);
            ItemPickupEntity var7 = var1.getItem(var4).getPickupEntity(this.player.getLevel(), this.player.x, this.player.y).setPlayerDeathAuth(this.player.getNetworkClient(), var5, var6);
            this.player.getLevel().entityManager.pickups.add(var7);
            var1.clearSlot(var4);
         }
      }

   }

   private void dropInventory(PlayerInventory var1) {
      this.dropInventory(var1, 0, var1.getSize() - 1);
   }

   public void dropInventory() {
      this.dropInventory(this.drag);
      this.dropInventory(this.main);
      this.dropInventory(this.armor);
      this.dropInventory(this.cosmetic);
      this.dropInventory(this.equipment);
      this.dropInventory(this.trinkets);
      this.trash.clearInventory();
      this.tempInvs.forEach(this::dropInventory);
   }

   public void dropMainInventory() {
      this.dropInventory(this.main, 10, this.main.getSize() - 1);
   }

   public Stream<PlayerInventory> streamInventories(boolean var1, boolean var2, boolean var3) {
      Stream var4 = Stream.of(this.drag, this.main, this.armor, this.cosmetic, this.equipment, this.trinkets);
      if (var1) {
         var4 = Stream.concat(var4, Stream.of(this.cloud));
      }

      if (var2) {
         var4 = Stream.concat(var4, Stream.of(this.trash));
      }

      if (var3) {
         var4 = Stream.concat(var4, this.tempInvs.stream());
      }

      return var4;
   }

   public Stream<PlayerInventorySlot> streamSlots(boolean var1, boolean var2, boolean var3) {
      return this.streamInventories(var1, var2, var3).flatMap((var0) -> {
         Stream.Builder var1 = Stream.builder();

         for(int var2 = 0; var2 < var0.getSize(); ++var2) {
            var1.add(new PlayerInventorySlot(var0, var2));
         }

         return var1.build();
      });
   }

   public void dropItem(PlayerInventory var1, int var2, int var3) {
      if (!var1.isSlotClear(var2)) {
         if (var3 > var1.getAmount(var2)) {
            var3 = var1.getAmount(var2);
         }

         this.player.dropItem(var1.getItem(var2).copy(var3));
         var1.getItem(var2).setAmount(var1.getAmount(var2) - var3);
         if (var1.getAmount(var2) <= 0) {
            var1.setItem(var2, (InventoryItem)null);
         }

         var1.markDirty(var2);
      }

   }

   public void dropItem(PlayerInventory var1, int var2) {
      this.dropItem(var1, var2, var1.getAmount(var2));
   }

   public void dropItem(int var1, int var2, int var3) {
      PlayerInventory var4 = this.getInventoryByID(var1);
      if (var4 != null) {
         this.dropItem(var4, var2, var3);
      } else {
         GameLog.warn.println(this.player.getDisplayName() + " tried to drop item from invalid inventory id " + var1);
      }

   }

   public void setItem(PlayerInventorySlot var1, InventoryItem var2) {
      var1.setItem(this, var2);
   }

   public void setItem(PlayerInventorySlot var1, InventoryItem var2, boolean var3) {
      var1.setItem(this, var2, var3);
   }

   public InventoryItem getItem(PlayerInventorySlot var1) {
      return var1.getItem(this);
   }

   public boolean isSlotClear(PlayerInventorySlot var1) {
      return var1.isSlotClear(this);
   }

   public int removeItems(Item var1, int var2, boolean var3, boolean var4, boolean var5, String var6) {
      int var7 = var2;
      var2 -= this.drag.removeItems(this.player.getLevel(), this.player, var1, var2, var6);
      var2 -= this.main.removeItems(this.player.getLevel(), this.player, var1, var2, var6);
      var2 -= this.armor.removeItems(this.player.getLevel(), this.player, var1, var2, var6);
      var2 -= this.cosmetic.removeItems(this.player.getLevel(), this.player, var1, var2, var6);
      var2 -= this.equipment.removeItems(this.player.getLevel(), this.player, var1, var2, var6);
      var2 -= this.trinkets.removeItems(this.player.getLevel(), this.player, var1, var2, var6);
      if (var3) {
         var2 -= this.cloud.removeItems(this.player.getLevel(), this.player, var1, var2, var6);
      }

      if (var4) {
         var2 -= this.trash.removeItems(this.player.getLevel(), this.player, var1, var2, var6);
      }

      PlayerTempInventory var9;
      if (var5) {
         for(Iterator var8 = this.tempInvs.iterator(); var8.hasNext(); var2 -= var9.removeItems(this.player.getLevel(), this.player, var1, var2, var6)) {
            var9 = (PlayerTempInventory)var8.next();
         }
      }

      return var7 - var2;
   }

   public Packet getTempInventoryPacket(int var1) {
      int var2 = this.findNextInvID(this.inventories.size());
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextByteUnsigned(var2);
      var4.putNextShortUnsigned(var1);
      return var3;
   }

   public PlayerTempInventory applyTempInventoryPacket(Packet var1, Function<PlayerInventoryManager, Boolean> var2) {
      return this.applyTempInventoryPacket(var1, (var2x, var3, var4) -> {
         return new PlayerTempInventory(var2x, var3, var4) {
            public boolean shouldDispose() {
               return (Boolean)var1.apply(this.player.getInv());
            }
         };
      });
   }

   public PlayerTempInventory addTempInventory(int var1, Function<PlayerInventoryManager, Boolean> var2) {
      return this.addTempInventory(var1, (var2x, var3, var4) -> {
         return new PlayerTempInventory(var2x, var3, var4) {
            public boolean shouldDispose() {
               return (Boolean)var1.apply(this.player.getInv());
            }
         };
      });
   }

   public PlayerTempInventory applyTempInventoryPacket(Packet var1, PlayerTempInventoryConstructor var2) {
      PacketReader var3 = new PacketReader(var1);
      int var4 = var3.getNextByteUnsigned();
      int var5 = var3.getNextShortUnsigned();
      return this.addTempInventory(var4, var5, var2);
   }

   public PlayerTempInventory addTempInventory(int var1, PlayerTempInventoryConstructor var2) {
      int var3 = this.findNextInvID(this.inventories.size());
      return this.addTempInventory(var3, var1, var2);
   }

   private PlayerTempInventory addTempInventory(int var1, int var2, PlayerTempInventoryConstructor var3) {
      if (this.tempInvs.size() > 200) {
         throw new NullPointerException("Too many work inventories on " + this.player.getDisplayName());
      } else {
         for(int var4 = 0; var4 < this.tempInvs.size(); ++var4) {
            PlayerTempInventory var5 = (PlayerTempInventory)this.tempInvs.get(var4);
            if (var5.getInventoryID() == var1) {
               System.out.println("Overrode " + this.player.getDisplayName() + " temp inventory with id " + var5.getInventoryID());
               var5.dispose();
               this.tempInvs.remove(var4);
               break;
            }
         }

         PlayerTempInventory var6;
         this.tempInvs.add(var6 = var3.create(this.player, var2, var1));
         return var6;
      }
   }

   private int findNextInvID(int var1) {
      Iterator var2 = this.tempInvs.iterator();

      PlayerInventory var3;
      do {
         if (!var2.hasNext()) {
            return var1;
         }

         var3 = (PlayerInventory)var2.next();
      } while(var3.getInventoryID() != var1);

      ++var1;
      return this.findNextInvID(var1);
   }

   public void dispose() {
      for(int var1 = 0; var1 < this.tempInvs.size(); ++var1) {
         PlayerTempInventory var2 = (PlayerTempInventory)this.tempInvs.get(var1);
         if (var2.shouldDispose()) {
            var2.dispose();
            this.tempInvs.remove(var1);
            --var1;
         }
      }

   }
}
