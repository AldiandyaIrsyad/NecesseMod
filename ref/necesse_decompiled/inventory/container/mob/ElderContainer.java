package necesse.inventory.container.mob;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.Quest;
import necesse.engine.quest.QuestManager;
import necesse.engine.registries.QuestRegistry;
import necesse.engine.team.PlayerTeam;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.ElderHumanMob;
import necesse.gfx.forms.presets.containerComponent.mob.ElderContainerForm;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerTempInventory;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.events.ElderQuestUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFilterChangedEvent;
import necesse.inventory.container.slots.ArmorContainerSlot;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.container.slots.SettlerWeaponContainerSlot;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementClientQuests;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class ElderContainer extends MobContainer {
   public ElderHumanMob elderMob;
   public final BooleanCustomAction setInCraftingForm;
   public final EmptyCustomAction questTakeButton;
   public final EmptyCustomAction questCompleteButton;
   public final EmptyCustomAction questSkipButton;
   public final EmptyCustomAction tierQuestTakeButton;
   public final EmptyCustomAction tierQuestCompleteButton;
   private boolean inCraftingForm;
   public final int INGREDIENT_SLOT;
   public final PlayerTempInventory ingredientInv;
   public GameMessage introMessage;
   public Quest quest;
   public Quest tierQuest;
   public ElderContainerForm containerForm;
   public GameMessage questSkipErr;
   public GameMessage tierQuestErr;
   public final boolean canEditEquipment;
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
   public AtomicBoolean subscribedEquipmentFilter = new AtomicBoolean();
   public BooleanCustomAction subscribeEquipmentFilter;
   public SetElderEquipmentFilterAction setEquipmentFilter;

   public ElderContainer(final NetworkClient var1, int var2, final ElderHumanMob var3, Packet var4) {
      super(var1, var2, var3);
      this.elderMob = var3;
      PacketReader var5 = new PacketReader(var4);
      this.ingredientInv = var1.playerMob.getInv().applyTempInventoryPacket(var5.getNextContentPacket(), (var1x) -> {
         return this.isClosed();
      });
      this.INGREDIENT_SLOT = this.addSlot(new ContainerSlot(this.ingredientInv, 0));
      this.addInventoryQuickTransfer((var1x) -> {
         return this.inCraftingForm;
      }, this.INGREDIENT_SLOT, this.INGREDIENT_SLOT);
      this.introMessage = GameMessage.fromContentPacket(var5.getNextContentPacket());
      this.inCraftingForm = false;
      short var6 = var5.getNextShort();
      if (var6 != -1) {
         Quest var7 = QuestRegistry.getNewQuest(var6);
         var7.applySpawnPacket(var5);
         this.quest = this.replaceManagerQuest(var7);
      }

      short var9 = var5.getNextShort();
      if (var9 != -1) {
         Quest var8 = QuestRegistry.getNewQuest(var9);
         var8.applySpawnPacket(var5);
         this.tierQuest = this.replaceManagerQuest(var8);
      }

      if (var5.getNextBoolean()) {
         if (var5.getNextBoolean()) {
            this.questSkipErr = GameMessage.fromContentPacket(var5.getNextContentPacket());
         } else {
            this.questSkipErr = null;
         }

         if (var5.getNextBoolean()) {
            this.tierQuestErr = GameMessage.fromContentPacket(var5.getNextContentPacket());
         } else {
            this.tierQuestErr = null;
         }
      }

      this.setInCraftingForm = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
         protected void run(boolean var1) {
            ElderContainer.this.inCraftingForm = var1;
         }
      });
      this.questTakeButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var1.isServer() && ElderContainer.this.quest != null) {
               ElderContainer.this.quest.makeActiveFor(var1.getServerClient().getServer(), var1.getServerClient());
            }

         }
      });
      this.questCompleteButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var1.isServer() && ElderContainer.this.quest != null && ElderContainer.this.quest.canComplete(var1)) {
               Level var1x = var1.playerMob.getLevel();
               SettlementClientQuests var2 = ElderContainer.this.elderMob.getSettlementClientQuests(var1.getServerClient());
               if (var2 != null) {
                  ElderContainer.this.quest.complete(var1.getServerClient());
                  Iterator var3 = var2.completeQuestAndGetReward().iterator();

                  while(var3.hasNext()) {
                     InventoryItem var4 = (InventoryItem)var3.next();
                     var1x.entityManager.pickups.add(var4.getPickupEntity(var1x, var1.playerMob.x, var1.playerMob.y));
                  }

                  ElderContainer.this.updateQuestAndUpdateClient(var1.getServerClient(), var2);
               }
            }

         }
      });
      this.questSkipButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var1.isServer() && ElderContainer.this.quest != null && ElderContainer.this.questSkipErr == null) {
               SettlementClientQuests var1x = ElderContainer.this.elderMob.getSettlementClientQuests(var1.getServerClient());
               if (var1x != null) {
                  var1x.removeCurrentQuest();
                  ElderContainer.this.updateQuestAndUpdateClient(var1.getServerClient(), var1x);
               }
            }

         }
      });
      this.tierQuestTakeButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var1.isServer() && ElderContainer.this.tierQuest != null) {
               ElderContainer.this.tierQuest.makeActiveFor(var1.getServerClient().getServer(), var1.getServerClient());
            }

         }
      });
      this.tierQuestCompleteButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var1.isServer() && ElderContainer.this.tierQuest != null && ElderContainer.this.tierQuest.canComplete(var1)) {
               Level var1x = var1.playerMob.getLevel();
               SettlementClientQuests var2 = ElderContainer.this.elderMob.getSettlementClientQuests(var1.getServerClient());
               if (var2 != null) {
                  ElderContainer.this.tierQuest.complete(var1.getServerClient());
                  Iterator var3 = var2.completeTierQuestAndGetReward().iterator();

                  while(var3.hasNext()) {
                     InventoryItem var4 = (InventoryItem)var3.next();
                     var1x.entityManager.pickups.add(var4.getPickupEntity(var1x, var1.playerMob.x, var1.playerMob.y));
                  }

                  var2.removeCurrentQuest();
                  ElderContainer.this.updateQuestAndUpdateClient(var1.getServerClient(), var2);
                  if (var1.getServerClient().achievementsLoaded()) {
                     var1.getServerClient().achievements().VILLAGE_HELPER.markCompleted(var1.getServerClient());
                     PlayerTeam var5 = var1.getServerClient().getPlayerTeam();
                     if (var5 != null) {
                        var5.streamOnlineMembers(var1.getServerClient().getServer()).filter(ServerClient::achievementsLoaded).forEach((var0) -> {
                           var0.achievements().VILLAGE_HELPER.markCompleted(var0);
                        });
                     }
                  }
               }
            }

         }
      });
      this.subscribeEvent(ElderQuestUpdateEvent.class, (var0) -> {
         return true;
      }, () -> {
         return true;
      });
      this.onEvent(ElderQuestUpdateEvent.class, (var1x) -> {
         this.questSkipErr = var1x.questSkipErr;
         this.quest = this.replaceManagerQuest(var1x.quest);
         this.tierQuestErr = var1x.tierQuestErr;
         this.tierQuest = this.replaceManagerQuest(var1x.tierQuest);
      });
      this.canEditEquipment = var5.getNextBoolean();
      if (this.canEditEquipment) {
         Inventory var10 = var3.getInventory();
         this.EQUIPMENT_HEAD_SLOT = this.addSlot(new ArmorContainerSlot(var10, 0, ArmorItem.ArmorType.HEAD));
         this.EQUIPMENT_CHEST_SLOT = this.addSlot(new ArmorContainerSlot(var10, 1, ArmorItem.ArmorType.CHEST));
         this.EQUIPMENT_FEET_SLOT = this.addSlot(new ArmorContainerSlot(var10, 2, ArmorItem.ArmorType.FEET));
         this.EQUIPMENT_COSM_HEAD_SLOT = this.addSlot(new ArmorContainerSlot(var10, 3, ArmorItem.ArmorType.HEAD));
         this.EQUIPMENT_COSM_CHEST_SLOT = this.addSlot(new ArmorContainerSlot(var10, 4, ArmorItem.ArmorType.CHEST));
         this.EQUIPMENT_COSM_FEET_SLOT = this.addSlot(new ArmorContainerSlot(var10, 5, ArmorItem.ArmorType.FEET));
         this.EQUIPMENT_WEAPON_SLOT = this.addSlot(new SettlerWeaponContainerSlot(var10, 6, var3));
         this.addInventoryQuickTransfer((var1x) -> {
            return this.isInEquipment;
         }, this.EQUIPMENT_HEAD_SLOT, this.EQUIPMENT_WEAPON_SLOT);
      }

      this.setIsInEquipment = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
         protected void run(boolean var1) {
            ElderContainer.this.isInEquipment = var1;
         }
      });
      this.setSelfManageEquipment = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
         protected void run(boolean var1) {
            if (ElderContainer.this.canEditEquipment) {
               var3.selfManageEquipment.set(var1);
            }

         }
      });
      this.subscribeEquipmentFilter = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
         protected void run(boolean var1x) {
            ElderContainer.this.subscribedEquipmentFilter.set(var1x);
            if (var1x && ElderContainer.this.canEditEquipment) {
               ElderContainer var10000 = ElderContainer.this;
               Predicate var10002 = (var1xx) -> {
                  return var1xx.mobUniqueID == var3.getUniqueID();
               };
               AtomicBoolean var10003 = ElderContainer.this.subscribedEquipmentFilter;
               Objects.requireNonNull(var10003);
               var10000.subscribeEvent(SettlementSettlerEquipmentFilterChangedEvent.class, var10002, var10003::get);
               if (var1.isServer()) {
                  LevelSettler var2 = var3.levelSettler;
                  if (var2 != null) {
                     (new SettlementSettlerEquipmentFilterChangedEvent(var3.getUniqueID(), var2.preferArmorSets, ItemCategoriesFilterChange.fullChange(var2.equipmentFilter))).applyAndSendToClient(var1.getServerClient());
                  }
               }
            }

         }
      });
      this.setEquipmentFilter = (SetElderEquipmentFilterAction)this.registerAction(new SetElderEquipmentFilterAction(this));
   }

   public Quest replaceManagerQuest(Quest var1) {
      if (var1 == null) {
         return var1;
      } else {
         QuestManager var2 = null;
         if (this.client.isServer()) {
            var2 = this.client.getServerClient().getServer().world.getQuests();
         } else if (this.client.isClient()) {
            var2 = this.client.getClientClient().getClient().quests;
         }

         if (var2 != null) {
            Quest var3 = var2.getQuest(var1.getUniqueID());
            if (var3 != null) {
               var1 = var3;
            }
         }

         return var1;
      }
   }

   protected void updateQuestAndUpdateClient(ServerClient var1, SettlementClientQuests var2) {
      (new ElderQuestUpdateEvent(var2)).applyAndSendToClient(var1);
   }

   public void init() {
      super.init();
      if (this.client.isServer()) {
         this.elderMob.addInteractClient(this.client.getServerClient());
      }

   }

   public void onClose() {
      super.onClose();
      if (this.client.isServer()) {
         this.elderMob.removeInteractClient(this.client.getServerClient());
      }

   }

   public static Packet getElderContainerContent(ElderHumanMob var0, Server var1, ServerClient var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextContentPacket(var2.playerMob.getInv().getTempInventoryPacket(1));
      var4.putNextContentPacket(var0.getRandomMessage(GameRandom.globalRandom, var2).getContentPacket());
      SettlementClientQuests var5 = var0.getSettlementClientQuests(var2);
      if (var5 != null) {
         Quest var6 = var5.getQuest();
         if (var6 != null) {
            var4.putNextShort((short)var6.getID());
            var6.setupSpawnPacket(var4);
         } else {
            var4.putNextShort((short)-1);
         }

         Quest var7 = var5.getTierQuest();
         if (var7 != null) {
            var4.putNextShort((short)var7.getID());
            var7.setupSpawnPacket(var4);
         } else {
            var4.putNextShort((short)-1);
         }
      } else {
         var4.putNextShort((short)-1);
         var4.putNextShort((short)-1);
      }

      SettlementLevelData var10 = var0.getSettlementLevelData();
      if (var10 != null) {
         var4.putNextBoolean(true);
         SettlementClientQuests var11 = var10.getClientsQuests(var2);
         GameMessage var8 = var11.canSkipQuest();
         if (var8 != null) {
            var4.putNextBoolean(true);
            var4.putNextContentPacket(var8.getContentPacket());
         } else {
            var4.putNextBoolean(false);
         }

         GameMessage var9 = var11.getTierQuestError();
         if (var9 != null) {
            var4.putNextBoolean(true);
            var4.putNextContentPacket(var9.getContentPacket());
         } else {
            var4.putNextBoolean(false);
         }
      } else {
         var4.putNextBoolean(false);
      }

      var4.putNextBoolean(var0.isSettlerOnCurrentLevel() && var0.getLevel().settlementLayer.doesClientHaveAccess(var2));
      return var3;
   }
}
