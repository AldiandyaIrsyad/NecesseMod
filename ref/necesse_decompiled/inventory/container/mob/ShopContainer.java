package necesse.inventory.container.mob;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketShopContainerUpdate;
import necesse.engine.network.server.AdventureParty;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.levelCache.SettlementCache;
import necesse.engine.world.worldData.SettlersWorldData;
import necesse.entity.mobs.friendly.human.ExpeditionList;
import necesse.entity.mobs.friendly.human.HappinessModifier;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.explorerMission.ExpeditionMission;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkContainerHandler;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkSetting;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkSettingRegistry;
import necesse.gfx.GameResources;
import necesse.gfx.fairType.TypeParsers;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.ContentCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.customAction.EnumCustomAction;
import necesse.inventory.container.customAction.PointCustomAction;
import necesse.inventory.container.slots.ArmorContainerSlot;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.container.slots.PartyInventoryContainerSlot;
import necesse.inventory.container.slots.SettlerWeaponContainerSlot;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.villageShops.ShopItem;

public class ShopContainer extends MobContainer {
   public HumanShop humanShop;
   public boolean hasSettlerAccess;
   public boolean isSettlerOutsideSettlement;
   public boolean canJoinAdventureParties;
   public boolean isInAdventureParty;
   public boolean isInYourAdventureParty;
   public boolean isInPartyConfig;
   public final BooleanCustomAction setIsInPartyConfig;
   public int PARTY_SLOTS_START = -1;
   public int PARTY_SLOTS_END = -1;
   public final long priceSeed;
   public GameMessage workInvMessage;
   public GameMessage missionFailedMessage;
   public GameMessage introMessage;
   public boolean hungerStrike;
   public int settlerHappiness = 50;
   public ArrayList<HappinessModifier> happinessModifiers;
   public ArrayList<NetworkShopItem> items;
   public ArrayList<ContainerWorkSetting> workSettings;
   public GameMessage recruitError;
   public ArrayList<InventoryItem> recruitItems;
   public final boolean startInRecruitment;
   public ArrayList<ExpeditionList> expeditionLists;
   public boolean isInEquipment;
   public final BooleanCustomAction setIsInEquipment;
   public final BooleanCustomAction setSelfManageEquipment;
   public int EQUIPMENT_HEAD_SLOT = -1;
   public int EQUIPMENT_CHEST_SLOT = -1;
   public int EQUIPMENT_FEET_SLOT = -1;
   public int EQUIPMENT_COSM_HEAD_SLOT = -1;
   public int EQUIPMENT_COSM_CHEST_SLOT = -1;
   public int EQUIPMENT_COSM_FEET_SLOT = -1;
   public int EQUIPMENT_WEAPON_SLOT = -1;
   public final EmptyCustomAction continueFailedMissionAction;
   public final EnumCustomAction<WorkItemsAction> workItemsAction;
   public final EmptyCustomAction acceptRecruitAction;
   public final EmptyCustomAction joinAdventurePartyAction;
   public final EmptyCustomAction leaveAdventurePartyAction;
   public final EmptyCustomAction returnToSettlementAction;
   public final PointCustomAction buyExpeditionButton;
   private final ContentCustomAction workSettingAction;

   public ShopContainer(final NetworkClient var1, int var2, final HumanShop var3, Packet var4) {
      super(var1, var2, var3);
      this.humanShop = var3;
      var1.playerMob.getInv().party.compressItems();
      PacketReader var5 = new PacketReader(var4);
      this.hasSettlerAccess = var5.getNextBoolean();
      this.isSettlerOutsideSettlement = var5.getNextBoolean();
      int var7;
      if (this.hasSettlerAccess) {
         (new ShopContainerPartyUpdateEvent(var5)).applyUpdate(this);
         AdventureParty var6 = null;
         if (var1.isServer()) {
            var6 = var1.getServerClient().adventureParty;
         } else if (var1.isClient() && var1.getClientClient().isLocalClient()) {
            var6 = var1.getClientClient().getClient().adventureParty;
         }

         for(var7 = 0; var7 < PlayerInventoryManager.MAX_PARTY_INVENTORY_SIZE; ++var7) {
            int var8 = this.addSlot(new PartyInventoryContainerSlot(var1.playerMob.getInv().party, var7, var6));
            if (this.PARTY_SLOTS_START == -1) {
               this.PARTY_SLOTS_START = var8;
            }

            if (this.PARTY_SLOTS_END == -1) {
               this.PARTY_SLOTS_END = var8;
            }

            this.PARTY_SLOTS_START = Math.min(this.PARTY_SLOTS_START, var8);
            this.PARTY_SLOTS_END = Math.max(this.PARTY_SLOTS_END, var8);
         }

         this.addInventoryQuickTransfer((var1x) -> {
            return this.isInPartyConfig;
         }, this.PARTY_SLOTS_START, this.PARTY_SLOTS_END);
      }

      this.priceSeed = var5.getNextLong();
      if (var5.getNextBoolean()) {
         this.workInvMessage = GameMessage.fromPacket(var5);
      }

      if (var5.getNextBoolean()) {
         this.missionFailedMessage = GameMessage.fromPacket(var5);
      }

      this.introMessage = GameMessage.fromPacket(var5);
      int var9;
      if (this.hasSettlerAccess) {
         this.hungerStrike = var5.getNextBoolean();
         var9 = var5.getNextShortUnsigned();
         this.happinessModifiers = new ArrayList(var9);
         this.settlerHappiness = 0;

         for(var7 = 0; var7 < var9; ++var7) {
            HappinessModifier var10 = new HappinessModifier(var5);
            this.happinessModifiers.add(var10);
            this.settlerHappiness += var10.happiness;
         }

         this.happinessModifiers.sort(Comparator.comparingInt((var0) -> {
            return -var0.happiness;
         }));
         this.settlerHappiness = GameMath.limit(this.settlerHappiness, 0, 100);
      }

      if (var5.getNextBoolean()) {
         var9 = var5.getNextShortUnsigned();
         this.items = new ArrayList(var9);

         for(var7 = 0; var7 < var9; ++var7) {
            ShopItem var11 = new ShopItem(var5);
            this.items.add(new NetworkShopItem(var7, var11));
         }
      } else {
         this.items = null;
      }

      if (var5.getNextBoolean()) {
         this.recruitError = GameMessage.fromContentPacket(var5.getNextContentPacket());
      } else if (var5.getNextBoolean()) {
         var9 = var5.getNextShortUnsigned();
         this.recruitItems = new ArrayList(var9);

         for(var7 = 0; var7 < var9; ++var7) {
            this.recruitItems.add(InventoryItem.fromContentPacket(var5));
         }
      }

      this.startInRecruitment = var5.getNextBoolean();
      var9 = var5.getNextShortUnsigned();
      this.expeditionLists = new ArrayList(var9);

      for(var7 = 0; var7 < var9; ++var7) {
         this.expeditionLists.add(new ExpeditionList(var5));
      }

      this.workSettings = new ArrayList();
      Iterator var13 = var3.workSettings.getSettings().iterator();

      while(var13.hasNext()) {
         HumanWorkSetting var12 = (HumanWorkSetting)var13.next();
         this.workSettings.add(new ContainerWorkSetting(var12, var12.readContainer(this, var5)));
      }

      this.continueFailedMissionAction = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var1.isServer()) {
               ShopContainer.this.humanShop.clearMissionResult();
            }

         }
      });
      this.workItemsAction = (EnumCustomAction)this.registerAction(new EnumCustomAction<WorkItemsAction>(WorkItemsAction.class) {
         protected void run(WorkItemsAction var1) {
            ShopContainer.this.handleWorkItemsAction(var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void run(Enum var1) {
            this.run((WorkItemsAction)var1);
         }
      });
      this.subscribeEvent(ShopContainerPartyUpdateEvent.class, (var2x) -> {
         return this.hasSettlerAccess && var2x.mobUniqueID == var3.getUniqueID();
      }, () -> {
         return true;
      });
      this.onEvent(ShopContainerPartyUpdateEvent.class, (var1x) -> {
         var1x.applyUpdate(this);
      });
      this.subscribeEvent(ShopContainerPartyResponseEvent.class, (var0) -> {
         return true;
      }, () -> {
         return true;
      });
      this.joinAdventurePartyAction = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (ShopContainer.this.hasSettlerAccess && ShopContainer.this.canJoinAdventureParties && var1.isServer()) {
               GameMessage var1x = ShopContainer.this.humanShop.willJoinAdventureParty(var1.getServerClient());
               (new ShopContainerPartyResponseEvent(var1x)).applyAndSendToClient(var1.getServerClient());
               if (var1x == null) {
                  var3.commandFollow(var1.getServerClient(), var1.playerMob);
                  var3.adventureParty.set(var1.getServerClient());
               }
            }

         }
      });
      this.leaveAdventurePartyAction = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (ShopContainer.this.hasSettlerAccess && var1.isServer() && var3.adventureParty.getServerClient() == var1) {
               var3.adventureParty.clear(true);
               if (var3.isSettlerOnCurrentLevel()) {
                  var3.clearCommandsOrders(var1.getServerClient());
               } else if (var3.getHealthPercent() <= 0.5F) {
                  SettlersWorldData var1x = SettlersWorldData.getSettlersData(var3.getLevel().getServer());
                  var1x.returnToSettlement(var3, false);
               }

               ShopContainer.this.close();
            }

         }
      });
      this.returnToSettlementAction = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (ShopContainer.this.hasSettlerAccess && var1.isServer() && ShopContainer.this.isSettlerOutsideSettlement) {
               if (var3.adventureParty.isInAdventureParty()) {
                  var3.adventureParty.clear(false);
               }

               SettlersWorldData var1x = SettlersWorldData.getSettlersData(var3.getLevel().getServer());
               var1x.returnToSettlement(var3, false);
               ShopContainer.this.close();
            }

         }
      });
      this.acceptRecruitAction = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var1.isServer()) {
               ServerClient var1x = var1.getServerClient();
               LevelIdentifier var2 = ShopContainer.this.humanShop.getRecruitedToLevel(var1x);
               Server var3x = var1x.getServer();
               if (var2 != null) {
                  PacketShopContainerUpdate.recruitSettler(var3x, var1x, ShopContainer.this, () -> {
                     return var3x.world.getLevel(var2);
                  });
               } else if (ShopContainer.this.canPayForRecruit()) {
                  ArrayList var4 = new ArrayList();
                  Iterator var5 = var3x.levelCache.getSettlements().iterator();

                  while(var5.hasNext()) {
                     SettlementCache var6 = (SettlementCache)var5.next();
                     if (var6.hasAccess(var1x) && var3.isValidRecruitment(var6, var1x) && var6.name != null) {
                        var4.add(var6);
                     }
                  }

                  if (var4.size() == 1) {
                     SettlementCache var7 = (SettlementCache)var4.get(0);
                     PacketShopContainerUpdate.recruitSettler(var3x, var1x, ShopContainer.this, () -> {
                        return var3x.world.getLevel(new LevelIdentifier(var7.islandX, var7.islandY, 0));
                     });
                  } else {
                     var1x.sendPacket(PacketShopContainerUpdate.settlementsList(var3x, var1x, ShopContainer.this.humanShop));
                  }
               }
            }

         }
      });
      this.buyExpeditionButton = (PointCustomAction)this.registerAction(new PointCustomAction() {
         protected void run(int var1x, int var2) {
            if (var1x >= 0 && var1x < ShopContainer.this.expeditionLists.size()) {
               ExpeditionList var3 = (ExpeditionList)ShopContainer.this.expeditionLists.get(var1x);
               if (var2 >= 0 && var2 < var3.expeditions.size()) {
                  ContainerExpedition var4 = (ContainerExpedition)var3.expeditions.get(var2);
                  int var5 = var4.price;
                  if (var5 == 0 || var5 > 0 && var1.playerMob.getInv().getAmount(ItemRegistry.getItem("coin"), true, false, false, "buy") >= var5) {
                     if (var5 > 0) {
                        var1.playerMob.getInv().removeItems(ItemRegistry.getItem("coin"), var5, true, false, false, "buy");
                     }

                     if (var1.isClient()) {
                        Screen.playSound(GameResources.coins, SoundEffect.globalEffect());
                     } else if (var1.isServer()) {
                        System.out.println(var1.getName() + " bought " + var4.expedition.getDisplayName().translate() + " expedition for " + var5 + " coins");
                        var1.getServerClient().closeContainer(true);
                        ShopContainer.this.humanShop.startMission(new ExpeditionMission(var4.expedition, var4.successChance));
                     }
                  }
               }
            }

         }
      });
      this.workSettingAction = (ContentCustomAction)this.registerAction(new ContentCustomAction() {
         protected void run(Packet var1x) {
            if (var1.isServer()) {
               var3.workSettingAction.runAndSend(var1x);
            }

         }
      });
      if (this.hasSettlerAccess) {
         Inventory var14 = var3.getInventory();
         this.EQUIPMENT_HEAD_SLOT = this.addSlot(new ArmorContainerSlot(var14, 0, ArmorItem.ArmorType.HEAD));
         this.EQUIPMENT_CHEST_SLOT = this.addSlot(new ArmorContainerSlot(var14, 1, ArmorItem.ArmorType.CHEST));
         this.EQUIPMENT_FEET_SLOT = this.addSlot(new ArmorContainerSlot(var14, 2, ArmorItem.ArmorType.FEET));
         this.EQUIPMENT_COSM_HEAD_SLOT = this.addSlot(new ArmorContainerSlot(var14, 3, ArmorItem.ArmorType.HEAD));
         this.EQUIPMENT_COSM_CHEST_SLOT = this.addSlot(new ArmorContainerSlot(var14, 4, ArmorItem.ArmorType.CHEST));
         this.EQUIPMENT_COSM_FEET_SLOT = this.addSlot(new ArmorContainerSlot(var14, 5, ArmorItem.ArmorType.FEET));
         this.EQUIPMENT_WEAPON_SLOT = this.addSlot(new SettlerWeaponContainerSlot(var14, 6, var3));
         this.addInventoryQuickTransfer((var1x) -> {
            return this.isInEquipment;
         }, this.EQUIPMENT_HEAD_SLOT, this.EQUIPMENT_WEAPON_SLOT);
      }

      this.setIsInPartyConfig = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
         protected void run(boolean var1) {
            ShopContainer.this.isInPartyConfig = var1;
         }
      });
      this.setIsInEquipment = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
         protected void run(boolean var1) {
            ShopContainer.this.isInEquipment = var1;
         }
      });
      this.setSelfManageEquipment = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
         protected void run(boolean var1) {
            if (ShopContainer.this.hasSettlerAccess) {
               var3.selfManageEquipment.set(var1);
            }

         }
      });
   }

   public void init() {
      super.init();
      if (this.client.isServer()) {
         this.humanShop.addInteractClient(this.client.getServerClient());
      }

   }

   public void handleWorkItemsAction(WorkItemsAction var1) {
      if (var1 == ShopContainer.WorkItemsAction.RECEIVE) {
         Iterator var2 = this.humanShop.workInventory.iterator();

         while(var2.hasNext()) {
            InventoryItem var3 = (InventoryItem)var2.next();
            var3.setNew(true);
            this.client.playerMob.getInv().addItemsDropRemaining(var3, "transfer", this.humanShop, !this.client.isServer(), false);
         }

         this.humanShop.workInventory.clear();
      }

      if (this.client.isServer()) {
         this.humanShop.clearMissionResult();
      }

   }

   public GameMessage getWorkInvMessage() {
      if (this.workInvMessage == null) {
         return this.workInvMessage;
      } else {
         GameMessageBuilder var1 = new GameMessageBuilder();
         var1.append(this.workInvMessage);
         Iterator var2 = this.humanShop.workInventory.iterator();

         while(var2.hasNext()) {
            InventoryItem var3 = (InventoryItem)var2.next();
            var1.append("\n ").append(TypeParsers.getItemParseString(var3) + "x" + var3.getAmount() + " ").append(var3.getItemLocalization());
         }

         return var1;
      }
   }

   public GameMessage getRecruitMessage() {
      if (this.recruitItems == null) {
         return this.recruitError;
      } else if (this.recruitItems.isEmpty()) {
         return new LocalMessage("ui", "settlerrecruitfree");
      } else {
         GameMessageBuilder var1 = new GameMessageBuilder();
         Iterator var2 = this.recruitItems.iterator();

         while(var2.hasNext()) {
            InventoryItem var3 = (InventoryItem)var2.next();
            var1.append("\n ").append(TypeParsers.getItemParseString(var3) + "x" + var3.getAmount() + " ").append(var3.getItemLocalization());
         }

         return new LocalMessage("ui", "settlerrecruitcost", "cost", var1);
      }
   }

   public boolean canPayForRecruit() {
      if (this.recruitItems == null) {
         return false;
      } else {
         Iterator var1 = this.recruitItems.iterator();

         InventoryItem var2;
         int var3;
         do {
            if (!var1.hasNext()) {
               return true;
            }

            var2 = (InventoryItem)var1.next();
            var3 = this.client.playerMob.getInv().main.getAmount(this.client.playerMob.getLevel(), this.client.playerMob, var2.item, "buy");
         } while(var3 >= var2.getAmount());

         return false;
      }
   }

   public void payForRecruit() {
      Iterator var1 = this.recruitItems.iterator();

      while(var1.hasNext()) {
         InventoryItem var2 = (InventoryItem)var1.next();
         this.client.playerMob.getInv().main.removeItems(this.client.playerMob.getLevel(), this.client.playerMob, var2.item, var2.getAmount(), "recruit");
      }

   }

   public int fulfillTrade(int var1, int var2) {
      if (this.items == null) {
         return -1;
      } else {
         NetworkShopItem var3 = (NetworkShopItem)this.items.stream().filter((var1x) -> {
            return var1x.networkID == var1;
         }).findFirst().orElse((Object)null);
         return var3 != null ? this.fulfillTrade(var3, var2) : -1;
      }
   }

   public int fulfillTrade(NetworkShopItem var1, int var2) {
      int var3 = 0;
      ContainerSlot var4 = this.getClientDraggingSlot();
      InventoryItem var5 = var1.shopItem.price < 0 ? new InventoryItem("coin", Math.abs(var1.shopItem.price)) : var1.shopItem.item;
      String var6 = var1.shopItem.price < 0 ? "sell" : "buy";

      for(int var7 = 0; var7 < var2 && var1.shopItem.canTrade(this.client, this.getCraftInventories()) && (var4.isClear() || var4.getItem().canCombine(this.client.playerMob.getLevel(), this.client.playerMob, var5, var6) && var4.getItemAmount() + var5.getAmount() <= var4.getItemStackLimit(var4.getItem())); ++var7) {
         if (var4.isClear()) {
            var4.setItem(var5.copy());
         } else {
            var4.getItem().combine(this.client.playerMob.getLevel(), this.client.playerMob, var4.getInventory(), var4.getInventorySlot(), var5.copy(), var6, (InventoryAddConsumer)null);
         }

         var1.shopItem.consumePrice(this.client, this.getCraftInventories());
         var1.shopItem.onTrade(this.client);
         var4.markDirty();
         var3 += var5.item.getID() * var4.getItemAmount() * 1123;
      }

      return var3;
   }

   public void onClose() {
      super.onClose();
      if (this.client.isServer()) {
         this.humanShop.removeInteractClient(this.client.getServerClient());
      }

   }

   public static Packet getContainerContent(HumanShop var0, ServerClient var1, GameMessage var2, GameMessage var3, GameMessage var4, List<ShopItem> var5, GameMessage var6, List<InventoryItem> var7, boolean var8, List<ExpeditionList> var9, HumanWorkSettingRegistry var10) {
      Packet var11 = new Packet();
      PacketWriter var12 = new PacketWriter(var11);
      SettlementLevelData var13 = var0.isSettler() ? var0.getSettlementLevelData() : null;
      boolean var14 = var13 != null && var13.getLevel().settlementLayer.doesClientHaveAccess(var1);
      var12.putNextBoolean(var14);
      var12.putNextBoolean(var0.isSettler() && !var0.isSettlerOnCurrentLevel());
      if (var14) {
         (new ShopContainerPartyUpdateEvent(var0, var1)).write(var12);
      }

      var12.putNextLong(var0.getShopSeed());
      if (var2 != null) {
         var12.putNextBoolean(true);
         var2.writePacket(var12);
      } else {
         var12.putNextBoolean(false);
      }

      if (var3 != null) {
         var12.putNextBoolean(true);
         var3.writePacket(var12);
      } else {
         var12.putNextBoolean(false);
      }

      var4.writePacket(var12);
      if (var14) {
         var12.putNextBoolean(var0.hungerStrike);
         List var15 = var0.getHappinessModifiers();
         var12.putNextShortUnsigned(var15.size());
         Iterator var16 = var15.iterator();

         while(var16.hasNext()) {
            HappinessModifier var17 = (HappinessModifier)var16.next();
            var17.writePacket(var12);
         }
      }

      var12.putNextBoolean(var5 != null);
      Iterator var18;
      if (var5 != null) {
         var12.putNextShortUnsigned(var5.size());
         var18 = var5.iterator();

         while(var18.hasNext()) {
            ShopItem var19 = (ShopItem)var18.next();
            var19.addPacketContent(var12);
         }
      }

      var12.putNextBoolean(var6 != null);
      if (var6 != null) {
         var12.putNextContentPacket(var6.getContentPacket());
      } else {
         var12.putNextBoolean(var7 != null);
         if (var7 != null) {
            var12.putNextShortUnsigned(var7.size());
            var18 = var7.iterator();

            while(var18.hasNext()) {
               InventoryItem var20 = (InventoryItem)var18.next();
               InventoryItem.addPacketContent(var20, var12);
            }
         }
      }

      var12.putNextBoolean(var8);
      var12.putNextShortUnsigned(var9.size());
      var18 = var9.iterator();

      while(var18.hasNext()) {
         ExpeditionList var21 = (ExpeditionList)var18.next();
         var21.writePacket(var12);
      }

      var18 = var10.getSettings().iterator();

      while(var18.hasNext()) {
         HumanWorkSetting var22 = (HumanWorkSetting)var18.next();
         var22.writeContainerPacket(var1, var12);
      }

      return var11;
   }

   public static class NetworkShopItem {
      public final int networkID;
      public final ShopItem shopItem;

      public NetworkShopItem(int var1, ShopItem var2) {
         this.networkID = var1;
         this.shopItem = var2;
      }
   }

   public class ContainerWorkSetting<T> {
      public final HumanWorkSetting<T> setting;
      public final T data;
      public final HumanWorkContainerHandler handler;

      public ContainerWorkSetting(HumanWorkSetting<T> var2, Object var3) {
         this.setting = var2;
         this.data = var3;
         this.handler = var2.setupHandler(ShopContainer.this, this);
      }

      public void sendAction(Packet var1) {
         Packet var2 = new Packet();
         PacketWriter var3 = new PacketWriter(var2);
         var3.putNextShortUnsigned(this.setting.getID());
         var3.putNextContentPacket(var1);
         ShopContainer.this.workSettingAction.runAndSend(var2);
      }

      public void sendServerOnlyAction(Packet var1) {
         Packet var2 = new Packet();
         PacketWriter var3 = new PacketWriter(var2);
         var3.putNextShortUnsigned(this.setting.getID());
         var3.putNextContentPacket(var1);
         ShopContainer.this.workSettingAction.runAndSend(var2);
      }
   }

   public static enum WorkItemsAction {
      RECEIVE,
      CONTINUE;

      private WorkItemsAction() {
      }

      // $FF: synthetic method
      private static WorkItemsAction[] $values() {
         return new WorkItemsAction[]{RECEIVE, CONTINUE};
      }
   }
}
