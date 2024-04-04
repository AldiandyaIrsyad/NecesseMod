package necesse.inventory.container.settlement;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementObjectStatusFormManager;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.settlement.actions.SubscribeStorageCustomAction;
import necesse.inventory.container.settlement.actions.SubscribeWorkstationCustomAction;
import necesse.inventory.container.settlement.actions.storage.ChangeAllowedSettlementStorageAction;
import necesse.inventory.container.settlement.actions.storage.ChangeLimitsSettlementStorageAction;
import necesse.inventory.container.settlement.actions.storage.FullUpdateSettlementStorageAction;
import necesse.inventory.container.settlement.actions.storage.PriorityLimitSettlementStorageAction;
import necesse.inventory.container.settlement.actions.storage.RemoveSettlementStorageAction;
import necesse.inventory.container.settlement.actions.workstation.RemoveWorkstationAction;
import necesse.inventory.container.settlement.actions.workstation.RemoveWorkstationRecipeAction;
import necesse.inventory.container.settlement.actions.workstation.UpdateWorkstationRecipeAction;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementOpenStorageConfigEvent;
import necesse.inventory.container.settlement.events.SettlementOpenWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementSingleWorkstationsEvent;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;

public class SettlementContainerObjectStatusManager {
   private final SettlementDependantContainer container;
   public final Level level;
   public final int tileX;
   public final int tileY;
   public final int masterTileX;
   public final int masterTileY;
   public boolean foundSettlement;
   public boolean hasSettlementAccess;
   public final boolean canSettlementStorageConfigure;
   public boolean isSettlementStorage;
   public final boolean canSettlementWorkstationConfigure;
   public boolean isSettlementWorkstation;
   public EmptyCustomAction openSettlementStorageConfig;
   public SubscribeStorageCustomAction subscribeStorage;
   public RemoveSettlementStorageAction removeStorage;
   public ChangeAllowedSettlementStorageAction changeAllowedStorage;
   public ChangeLimitsSettlementStorageAction changeLimitsStorage;
   public PriorityLimitSettlementStorageAction priorityLimitStorage;
   public FullUpdateSettlementStorageAction fullUpdateSettlementStorage;
   public EmptyCustomAction openWorkstationConfig;
   public SubscribeWorkstationCustomAction subscribeWorkstation;
   public RemoveWorkstationAction removeWorkstation;
   public UpdateWorkstationRecipeAction updateWorkstationRecipe;
   public RemoveWorkstationRecipeAction removeWorkstationRecipe;

   public SettlementContainerObjectStatusManager(final SettlementDependantContainer var1, Level var2, int var3, int var4, PacketReader var5) {
      this.container = var1;
      this.level = var2;
      this.tileX = var3;
      this.tileY = var4;
      if (var5.getNextBoolean()) {
         this.foundSettlement = true;
         this.hasSettlementAccess = var5.getNextBoolean();
         var1.onEvent(SettlementBasicsEvent.class, (var2x) -> {
            if (var1.client.isServer()) {
               this.hasSettlementAccess = var2x.hasAccess(var1.client.getServerClient());
            } else {
               this.hasSettlementAccess = var2x.hasAccess(var1.client.getClientClient().getClient());
            }

         });
      } else {
         this.foundSettlement = false;
      }

      if (var5.getNextBoolean()) {
         this.masterTileX = var5.getNextShortUnsigned();
         this.masterTileY = var5.getNextShortUnsigned();
         this.canSettlementStorageConfigure = var5.getNextBoolean();
         if (this.canSettlementStorageConfigure) {
            this.isSettlementStorage = var5.getNextBoolean();
            var1.subscribeEvent(SettlementSingleStorageEvent.class, (var1x) -> {
               return var1x.tileX == this.masterTileX && var1x.tileY == this.masterTileY;
            }, () -> {
               return true;
            });
            var1.onEvent(SettlementSingleStorageEvent.class, (var1x) -> {
               if (var1x.tileX == this.masterTileX && var1x.tileY == this.masterTileY) {
                  this.isSettlementStorage = var1x.exists;
               }

            });
         }

         this.canSettlementWorkstationConfigure = var5.getNextBoolean();
         if (this.canSettlementWorkstationConfigure) {
            this.isSettlementWorkstation = var5.getNextBoolean();
            var1.subscribeEvent(SettlementSingleWorkstationsEvent.class, (var1x) -> {
               return var1x.tileX == this.masterTileX && var1x.tileY == this.masterTileY;
            }, () -> {
               return true;
            });
            var1.onEvent(SettlementSingleWorkstationsEvent.class, (var1x) -> {
               if (var1x.tileX == this.masterTileX && var1x.tileY == this.masterTileY) {
                  this.isSettlementWorkstation = var1x.exists;
               }

            });
         }
      } else {
         this.canSettlementStorageConfigure = false;
         this.canSettlementWorkstationConfigure = false;
         this.masterTileX = -1;
         this.masterTileY = -1;
      }

      this.openSettlementStorageConfig = (EmptyCustomAction)var1.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (SettlementContainerObjectStatusManager.this.canSettlementStorageConfigure && var1.client.isServer()) {
               SettlementLevelData var1x = var1.getLevelData();
               if (var1x != null) {
                  ServerClient var2 = var1.client.getServerClient();
                  if (!var1.getLevelLayer().doesClientHaveAccess(var2)) {
                     (new SettlementBasicsEvent(var1x)).applyAndSendToClient(var2);
                     return;
                  }

                  SettlementInventory var3 = var1x.assignStorage(SettlementContainerObjectStatusManager.this.masterTileX, SettlementContainerObjectStatusManager.this.masterTileY);
                  if (var3 != null) {
                     (new SettlementOpenStorageConfigEvent(var3)).applyAndSendToClient(var2);
                  }
               }
            }

         }
      });
      this.subscribeStorage = (SubscribeStorageCustomAction)var1.registerAction(new SubscribeStorageCustomAction(var1));
      this.removeStorage = (RemoveSettlementStorageAction)var1.registerAction(new RemoveSettlementStorageAction(var1));
      this.changeAllowedStorage = (ChangeAllowedSettlementStorageAction)var1.registerAction(new ChangeAllowedSettlementStorageAction(var1));
      this.changeLimitsStorage = (ChangeLimitsSettlementStorageAction)var1.registerAction(new ChangeLimitsSettlementStorageAction(var1));
      this.priorityLimitStorage = (PriorityLimitSettlementStorageAction)var1.registerAction(new PriorityLimitSettlementStorageAction(var1));
      this.fullUpdateSettlementStorage = (FullUpdateSettlementStorageAction)var1.registerAction(new FullUpdateSettlementStorageAction(var1));
      this.openWorkstationConfig = (EmptyCustomAction)var1.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var1.client.isServer()) {
               SettlementLevelData var1x = var1.getLevelData();
               if (var1x != null) {
                  ServerClient var2 = var1.client.getServerClient();
                  if (!var1.getLevelLayer().doesClientHaveAccess(var2)) {
                     (new SettlementBasicsEvent(var1x)).applyAndSendToClient(var2);
                     return;
                  }

                  SettlementWorkstation var3 = var1x.assignWorkstation(SettlementContainerObjectStatusManager.this.masterTileX, SettlementContainerObjectStatusManager.this.masterTileY);
                  if (var3 != null) {
                     (new SettlementOpenWorkstationEvent(var3)).applyAndSendToClient(var2);
                  }
               }
            }

         }
      });
      this.subscribeWorkstation = (SubscribeWorkstationCustomAction)var1.registerAction(new SubscribeWorkstationCustomAction(var1));
      this.removeWorkstation = (RemoveWorkstationAction)var1.registerAction(new RemoveWorkstationAction(var1));
      this.updateWorkstationRecipe = (UpdateWorkstationRecipeAction)var1.registerAction(new UpdateWorkstationRecipeAction(var1));
      this.removeWorkstationRecipe = (RemoveWorkstationRecipeAction)var1.registerAction(new RemoveWorkstationRecipeAction(var1));
   }

   public static void writeContent(ServerClient var0, Level var1, int var2, int var3, PacketWriter var4) {
      SettlementLevelData var5 = SettlementLevelData.getSettlementData(var1);
      boolean var6 = var5 != null && var5.getObjectEntity() != null;
      var4.putNextBoolean(var6);
      if (var6) {
         var4.putNextBoolean(var1.settlementLayer.doesClientHaveAccess(var0));
      }

      LevelObject var7 = var1.getLevelObject(var2, var3);
      LevelObject var8 = (LevelObject)var7.getMultiTile().getMasterLevelObject(var1, var2, var3).orElse((Object)null);
      if (var8 != null) {
         var4.putNextBoolean(true);
         var4.putNextShortUnsigned(var8.tileX);
         var4.putNextShortUnsigned(var8.tileY);
         ObjectEntity var9 = var1.entityManager.getObjectEntity(var8.tileX, var8.tileY);
         if (var9 instanceof OEInventory) {
            OEInventory var10 = (OEInventory)var9;
            if (var10.getSettlementStorage() == null) {
               var4.putNextBoolean(false);
            } else {
               var4.putNextBoolean(true);
               var4.putNextBoolean(var5 != null && var5.getStorage(var8.tileX, var8.tileY) != null);
            }
         } else {
            var4.putNextBoolean(false);
         }

         if (var8.object instanceof SettlementWorkstationObject) {
            var4.putNextBoolean(true);
            var4.putNextBoolean(var5 != null && var5.getWorkstation(var8.tileX, var8.tileY) != null);
         } else {
            var4.putNextBoolean(false);
         }
      } else {
         var4.putNextBoolean(false);
         var4.putNextBoolean(false);
      }

   }

   public SettlementObjectStatusFormManager getFormManager(FormSwitcher var1, FormComponent var2, Client var3) {
      return new SettlementObjectStatusFormManager(this.container, this, var1, var2, var3);
   }
}
