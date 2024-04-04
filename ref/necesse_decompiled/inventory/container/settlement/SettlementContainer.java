package necesse.inventory.container.settlement;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.engine.world.levelCache.SettlementCache;
import necesse.entity.objectEntity.SettlementFlagObjectEntity;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.container.customAction.PointCustomAction;
import necesse.inventory.container.settlement.actions.BanishSettlerAction;
import necesse.inventory.container.settlement.actions.ChangeClaimAction;
import necesse.inventory.container.settlement.actions.ChangeDefendZoneAction;
import necesse.inventory.container.settlement.actions.ChangeDefendZoneAutoExpandAction;
import necesse.inventory.container.settlement.actions.ChangeNameAction;
import necesse.inventory.container.settlement.actions.ChangePrivacyAction;
import necesse.inventory.container.settlement.actions.ChangeRestrictZoneAction;
import necesse.inventory.container.settlement.actions.CloneRestrictZoneAction;
import necesse.inventory.container.settlement.actions.CommandSettlersAttackAction;
import necesse.inventory.container.settlement.actions.CommandSettlersClearOrdersAction;
import necesse.inventory.container.settlement.actions.CommandSettlersFollowMeAction;
import necesse.inventory.container.settlement.actions.CommandSettlersGuardAction;
import necesse.inventory.container.settlement.actions.CommandSettlersSetHideOnLowHealthAction;
import necesse.inventory.container.settlement.actions.CreateNewRestrictZoneAction;
import necesse.inventory.container.settlement.actions.DeleteRestrictZoneAction;
import necesse.inventory.container.settlement.actions.DeleteWorkZoneCustomAction;
import necesse.inventory.container.settlement.actions.ForestryZoneConfigCustomAction;
import necesse.inventory.container.settlement.actions.HusbandryZoneConfigCustomAction;
import necesse.inventory.container.settlement.actions.LockNoSettlerRoomAction;
import necesse.inventory.container.settlement.actions.MoveSettlerRoomAction;
import necesse.inventory.container.settlement.actions.MoveSettlerSettlementAction;
import necesse.inventory.container.settlement.actions.RecolorRestrictZoneAction;
import necesse.inventory.container.settlement.actions.RenameRestrictZoneAction;
import necesse.inventory.container.settlement.actions.RenameSettlerNameAction;
import necesse.inventory.container.settlement.actions.RenameWorkZoneCustomAction;
import necesse.inventory.container.settlement.actions.RequestFullRestrictAction;
import necesse.inventory.container.settlement.actions.RequestJoinSettlementAction;
import necesse.inventory.container.settlement.actions.RequestMoveSettlerListCustomAction;
import necesse.inventory.container.settlement.actions.RequestSettlerBasicsAction;
import necesse.inventory.container.settlement.actions.RequestSettlerDietsAction;
import necesse.inventory.container.settlement.actions.RequestSettlerEquipmentFiltersAction;
import necesse.inventory.container.settlement.actions.RequestSettlerPrioritiesAction;
import necesse.inventory.container.settlement.actions.SetNewSettlerDietAction;
import necesse.inventory.container.settlement.actions.SetNewSettlerEquipmentFilterAction;
import necesse.inventory.container.settlement.actions.SetNewSettlerRestrictZoneAction;
import necesse.inventory.container.settlement.actions.SetSettlerDietAction;
import necesse.inventory.container.settlement.actions.SetSettlerEquipmentFilterAction;
import necesse.inventory.container.settlement.actions.SetSettlerPriorityAction;
import necesse.inventory.container.settlement.actions.SetSettlerRestrictZoneAction;
import necesse.inventory.container.settlement.actions.SetSettlerSelfManageEquipmentAction;
import necesse.inventory.container.settlement.actions.SubscribeDefendZoneAction;
import necesse.inventory.container.settlement.actions.SubscribeDietsAction;
import necesse.inventory.container.settlement.actions.SubscribeEquipmentAction;
import necesse.inventory.container.settlement.actions.SubscribePrioritiesAction;
import necesse.inventory.container.settlement.actions.SubscribeRestrictAction;
import necesse.inventory.container.settlement.actions.SubscribeSettlerBasicsAction;
import necesse.inventory.container.settlement.actions.SubscribeStorageCustomAction;
import necesse.inventory.container.settlement.actions.SubscribeWorkContentCustomAction;
import necesse.inventory.container.settlement.actions.SubscribeWorkZoneConfigCustomAction;
import necesse.inventory.container.settlement.actions.SubscribeWorkstationCustomAction;
import necesse.inventory.container.settlement.actions.storage.AssignSettlementStorageAction;
import necesse.inventory.container.settlement.actions.storage.ChangeAllowedSettlementStorageAction;
import necesse.inventory.container.settlement.actions.storage.ChangeLimitsSettlementStorageAction;
import necesse.inventory.container.settlement.actions.storage.FullUpdateSettlementStorageAction;
import necesse.inventory.container.settlement.actions.storage.PriorityLimitSettlementStorageAction;
import necesse.inventory.container.settlement.actions.storage.RemoveSettlementStorageAction;
import necesse.inventory.container.settlement.actions.workstation.AssignWorkstationAction;
import necesse.inventory.container.settlement.actions.workstation.RemoveWorkstationAction;
import necesse.inventory.container.settlement.actions.workstation.RemoveWorkstationRecipeAction;
import necesse.inventory.container.settlement.actions.workstation.UpdateWorkstationRecipeAction;
import necesse.inventory.container.settlement.actions.zones.CreateNewWorkZoneAction;
import necesse.inventory.container.settlement.actions.zones.ExpandWorkZoneAction;
import necesse.inventory.container.settlement.actions.zones.ShrinkWorkZoneAction;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementOpenStorageConfigEvent;
import necesse.inventory.container.settlement.events.SettlementOpenWorkZoneConfigEvent;
import necesse.inventory.container.settlement.events.SettlementOpenWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneRemovedEvent;
import necesse.level.maps.Level;
import necesse.level.maps.layers.SettlementLevelLayer;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class SettlementContainer extends SettlementDependantContainer {
   protected SettlementFlagObjectEntity objectEntity;
   public SettlementBasicsEvent basics;
   public RequestJoinSettlementAction requestJoin;
   public BooleanCustomAction changePrivacy;
   public BooleanCustomAction changeClaim;
   public ChangeNameAction changeName;
   public RequestSettlerBasicsAction requestSettlerBasics;
   public SubscribeSettlerBasicsAction subscribeSettlerBasics;
   public RenameSettlerNameAction renameSettler;
   public MoveSettlerRoomAction moveSettlerRoom;
   public LockNoSettlerRoomAction lockNoSettlerRoom;
   public IntCustomAction banishSettler;
   public RequestMoveSettlerListCustomAction requestMoveSettlerList;
   public MoveSettlerSettlementAction moveSettlerSettlement;
   public CommandSettlersClearOrdersAction commandSettlersClearOrders;
   public CommandSettlersFollowMeAction commandSettlersFollow;
   public CommandSettlersGuardAction commandSettlersGuard;
   public CommandSettlersAttackAction commandSettlersAttack;
   public CommandSettlersSetHideOnLowHealthAction commandSettlersSetHideOnLowHealth;
   public SubscribeWorkContentCustomAction subscribeWorkContent;
   public RequestSettlerPrioritiesAction requestSettlerPriorities;
   public SubscribePrioritiesAction subscribePriorities;
   public SetSettlerPriorityAction setSettlerPriority;
   public RequestSettlerEquipmentFiltersAction requestSettlerEquipmentFilters;
   public SubscribeEquipmentAction subscribeEquipment;
   public SetSettlerSelfManageEquipmentAction setSettlerSelfManageEquipment;
   public SetNewSettlerEquipmentFilterAction setNewSettlerEquipmentFilter;
   public SetSettlerEquipmentFilterAction setSettlerEquipmentFilter;
   public RequestSettlerDietsAction requestSettlerDiets;
   public SubscribeDietsAction subscribeDiets;
   public SetNewSettlerDietAction setNewSettlerDiet;
   public SetSettlerDietAction setSettlerDiet;
   public RequestFullRestrictAction requestFullRestricts;
   public SubscribeRestrictAction subscribeRestrict;
   public SetNewSettlerRestrictZoneAction setNewSettlerRestrictZone;
   public SetSettlerRestrictZoneAction setSettlerRestrictZone;
   public CreateNewRestrictZoneAction createNewRestrictZone;
   public CloneRestrictZoneAction cloneRestrictZone;
   public ChangeRestrictZoneAction changeRestrictZone;
   public RenameRestrictZoneAction renameRestrictZone;
   public RecolorRestrictZoneAction recolorRestrictZone;
   public DeleteRestrictZoneAction deleteRestrictZone;
   public SubscribeDefendZoneAction subscribeDefendZoneAction;
   public ChangeDefendZoneAction changeDefendZoneAction;
   public ChangeDefendZoneAutoExpandAction changeDefendZoneAutoExpandAction;
   public PointCustomAction openStorage;
   public SubscribeStorageCustomAction subscribeStorage;
   public AssignSettlementStorageAction assignStorage;
   public RemoveSettlementStorageAction removeStorage;
   public ChangeAllowedSettlementStorageAction changeAllowedStorage;
   public ChangeLimitsSettlementStorageAction changeLimitsStorage;
   public PriorityLimitSettlementStorageAction priorityLimitStorage;
   public FullUpdateSettlementStorageAction fullUpdateSettlementStorage;
   public PointCustomAction openWorkstation;
   public SubscribeWorkstationCustomAction subscribeWorkstation;
   public AssignWorkstationAction assignWorkstation;
   public RemoveWorkstationAction removeWorkstation;
   public UpdateWorkstationRecipeAction updateWorkstationRecipe;
   public RemoveWorkstationRecipeAction removeWorkstationRecipe;
   public CreateNewWorkZoneAction createWorkZone;
   public ExpandWorkZoneAction expandWorkZone;
   public ShrinkWorkZoneAction shrinkWorkZone;
   public DeleteWorkZoneCustomAction deleteWorkZone;
   public RenameWorkZoneCustomAction renameWorkZone;
   public IntCustomAction openWorkZoneConfig;
   public SubscribeWorkZoneConfigCustomAction subscribeWorkZoneConfig;
   public ForestryZoneConfigCustomAction forestryZoneConfig;
   public HusbandryZoneConfigCustomAction husbandryZoneConfig;

   public SettlementContainer(final NetworkClient var1, int var2, SettlementFlagObjectEntity var3, Packet var4) {
      super(var1, var2);
      this.objectEntity = var3;
      PacketReader var5 = new PacketReader(var4);
      this.basics = new SettlementBasicsEvent(var5);
      this.subscribeEvent(SettlementBasicsEvent.class, (var0) -> {
         return true;
      }, () -> {
         return true;
      });
      this.onEvent(SettlementBasicsEvent.class, (var1x) -> {
         this.basics = var1x;
      });
      this.subscribeEvent(SettlementSettlersChangedEvent.class, (var0) -> {
         return true;
      }, () -> {
         return true;
      });
      this.requestJoin = (RequestJoinSettlementAction)this.registerAction(new RequestJoinSettlementAction(this));
      this.changePrivacy = (BooleanCustomAction)this.registerAction(new ChangePrivacyAction(this));
      this.changeClaim = (BooleanCustomAction)this.registerAction(new ChangeClaimAction(this));
      this.changeName = (ChangeNameAction)this.registerAction(new ChangeNameAction(this));
      this.requestSettlerBasics = (RequestSettlerBasicsAction)this.registerAction(new RequestSettlerBasicsAction(this));
      this.subscribeSettlerBasics = (SubscribeSettlerBasicsAction)this.registerAction(new SubscribeSettlerBasicsAction(this));
      this.renameSettler = (RenameSettlerNameAction)this.registerAction(new RenameSettlerNameAction(this));
      this.moveSettlerRoom = (MoveSettlerRoomAction)this.registerAction(new MoveSettlerRoomAction(this));
      this.lockNoSettlerRoom = (LockNoSettlerRoomAction)this.registerAction(new LockNoSettlerRoomAction(this));
      this.banishSettler = (IntCustomAction)this.registerAction(new BanishSettlerAction(this));
      this.requestMoveSettlerList = (RequestMoveSettlerListCustomAction)this.registerAction(new RequestMoveSettlerListCustomAction(this));
      this.moveSettlerSettlement = (MoveSettlerSettlementAction)this.registerAction(new MoveSettlerSettlementAction(this));
      this.commandSettlersClearOrders = (CommandSettlersClearOrdersAction)this.registerAction(new CommandSettlersClearOrdersAction(this));
      this.commandSettlersFollow = (CommandSettlersFollowMeAction)this.registerAction(new CommandSettlersFollowMeAction(this));
      this.commandSettlersGuard = (CommandSettlersGuardAction)this.registerAction(new CommandSettlersGuardAction(this));
      this.commandSettlersAttack = (CommandSettlersAttackAction)this.registerAction(new CommandSettlersAttackAction(this));
      this.commandSettlersSetHideOnLowHealth = (CommandSettlersSetHideOnLowHealthAction)this.registerAction(new CommandSettlersSetHideOnLowHealthAction(this));
      this.subscribeWorkContent = (SubscribeWorkContentCustomAction)this.registerAction(new SubscribeWorkContentCustomAction(this));
      this.requestSettlerPriorities = (RequestSettlerPrioritiesAction)this.registerAction(new RequestSettlerPrioritiesAction(this));
      this.subscribePriorities = (SubscribePrioritiesAction)this.registerAction(new SubscribePrioritiesAction(this));
      this.setSettlerPriority = (SetSettlerPriorityAction)this.registerAction(new SetSettlerPriorityAction(this));
      this.requestSettlerEquipmentFilters = (RequestSettlerEquipmentFiltersAction)this.registerAction(new RequestSettlerEquipmentFiltersAction(this));
      this.subscribeEquipment = (SubscribeEquipmentAction)this.registerAction(new SubscribeEquipmentAction(this));
      this.setSettlerSelfManageEquipment = (SetSettlerSelfManageEquipmentAction)this.registerAction(new SetSettlerSelfManageEquipmentAction(this));
      this.setNewSettlerEquipmentFilter = (SetNewSettlerEquipmentFilterAction)this.registerAction(new SetNewSettlerEquipmentFilterAction(this));
      this.setSettlerEquipmentFilter = (SetSettlerEquipmentFilterAction)this.registerAction(new SetSettlerEquipmentFilterAction(this));
      this.requestSettlerDiets = (RequestSettlerDietsAction)this.registerAction(new RequestSettlerDietsAction(this));
      this.subscribeDiets = (SubscribeDietsAction)this.registerAction(new SubscribeDietsAction(this));
      this.setNewSettlerDiet = (SetNewSettlerDietAction)this.registerAction(new SetNewSettlerDietAction(this));
      this.setSettlerDiet = (SetSettlerDietAction)this.registerAction(new SetSettlerDietAction(this));
      this.requestFullRestricts = (RequestFullRestrictAction)this.registerAction(new RequestFullRestrictAction(this));
      this.subscribeRestrict = (SubscribeRestrictAction)this.registerAction(new SubscribeRestrictAction(this));
      this.setNewSettlerRestrictZone = (SetNewSettlerRestrictZoneAction)this.registerAction(new SetNewSettlerRestrictZoneAction(this));
      this.setSettlerRestrictZone = (SetSettlerRestrictZoneAction)this.registerAction(new SetSettlerRestrictZoneAction(this));
      this.createNewRestrictZone = (CreateNewRestrictZoneAction)this.registerAction(new CreateNewRestrictZoneAction(this));
      this.cloneRestrictZone = (CloneRestrictZoneAction)this.registerAction(new CloneRestrictZoneAction(this));
      this.changeRestrictZone = (ChangeRestrictZoneAction)this.registerAction(new ChangeRestrictZoneAction(this));
      this.renameRestrictZone = (RenameRestrictZoneAction)this.registerAction(new RenameRestrictZoneAction(this));
      this.recolorRestrictZone = (RecolorRestrictZoneAction)this.registerAction(new RecolorRestrictZoneAction(this));
      this.deleteRestrictZone = (DeleteRestrictZoneAction)this.registerAction(new DeleteRestrictZoneAction(this));
      this.subscribeDefendZoneAction = (SubscribeDefendZoneAction)this.registerAction(new SubscribeDefendZoneAction(this));
      this.changeDefendZoneAction = (ChangeDefendZoneAction)this.registerAction(new ChangeDefendZoneAction(this));
      this.changeDefendZoneAutoExpandAction = (ChangeDefendZoneAutoExpandAction)this.registerAction(new ChangeDefendZoneAutoExpandAction(this));
      this.openStorage = (PointCustomAction)this.registerAction(new PointCustomAction() {
         protected void run(int var1x, int var2) {
            if (var1.isServer()) {
               SettlementLevelData var3 = SettlementContainer.this.getLevelData();
               if (var3 != null) {
                  SettlementInventory var4 = var3.getStorage(var1x, var2);
                  if (var4 != null) {
                     (new SettlementOpenStorageConfigEvent(var4)).applyAndSendToClient(var1.getServerClient());
                  }
               }
            }

         }
      });
      this.subscribeStorage = (SubscribeStorageCustomAction)this.registerAction(new SubscribeStorageCustomAction(this));
      this.assignStorage = (AssignSettlementStorageAction)this.registerAction(new AssignSettlementStorageAction(this));
      this.removeStorage = (RemoveSettlementStorageAction)this.registerAction(new RemoveSettlementStorageAction(this));
      this.changeAllowedStorage = (ChangeAllowedSettlementStorageAction)this.registerAction(new ChangeAllowedSettlementStorageAction(this));
      this.changeLimitsStorage = (ChangeLimitsSettlementStorageAction)this.registerAction(new ChangeLimitsSettlementStorageAction(this));
      this.priorityLimitStorage = (PriorityLimitSettlementStorageAction)this.registerAction(new PriorityLimitSettlementStorageAction(this));
      this.fullUpdateSettlementStorage = (FullUpdateSettlementStorageAction)this.registerAction(new FullUpdateSettlementStorageAction(this));
      this.openWorkstation = (PointCustomAction)this.registerAction(new PointCustomAction() {
         protected void run(int var1x, int var2) {
            if (var1.isServer()) {
               SettlementLevelData var3 = SettlementContainer.this.getLevelData();
               if (var3 != null) {
                  SettlementWorkstation var4 = var3.getWorkstation(var1x, var2);
                  if (var4 != null) {
                     (new SettlementOpenWorkstationEvent(var4)).applyAndSendToClient(var1.getServerClient());
                  }
               }
            }

         }
      });
      this.subscribeWorkstation = (SubscribeWorkstationCustomAction)this.registerAction(new SubscribeWorkstationCustomAction(this));
      this.assignWorkstation = (AssignWorkstationAction)this.registerAction(new AssignWorkstationAction(this));
      this.removeWorkstation = (RemoveWorkstationAction)this.registerAction(new RemoveWorkstationAction(this));
      this.updateWorkstationRecipe = (UpdateWorkstationRecipeAction)this.registerAction(new UpdateWorkstationRecipeAction(this));
      this.removeWorkstationRecipe = (RemoveWorkstationRecipeAction)this.registerAction(new RemoveWorkstationRecipeAction(this));
      this.createWorkZone = (CreateNewWorkZoneAction)this.registerAction(new CreateNewWorkZoneAction(this));
      this.expandWorkZone = (ExpandWorkZoneAction)this.registerAction(new ExpandWorkZoneAction(this));
      this.shrinkWorkZone = (ShrinkWorkZoneAction)this.registerAction(new ShrinkWorkZoneAction(this));
      this.deleteWorkZone = (DeleteWorkZoneCustomAction)this.registerAction(new DeleteWorkZoneCustomAction(this));
      this.renameWorkZone = (RenameWorkZoneCustomAction)this.registerAction(new RenameWorkZoneCustomAction(this));
      this.openWorkZoneConfig = (IntCustomAction)this.registerAction(new IntCustomAction() {
         protected void run(int var1x) {
            if (var1.isServer()) {
               SettlementLevelData var2 = SettlementContainer.this.getLevelData();
               if (var2 != null) {
                  SettlementWorkZone var3 = var2.getWorkZones().getZone(var1x);
                  if (var3 == null) {
                     (new SettlementWorkZoneRemovedEvent(var1x)).applyAndSendToClient(var1.getServerClient());
                  } else {
                     (new SettlementOpenWorkZoneConfigEvent(var3)).applyAndSendToClient(var1.getServerClient());
                  }
               }
            }

         }
      });
      this.subscribeWorkZoneConfig = (SubscribeWorkZoneConfigCustomAction)this.registerAction(new SubscribeWorkZoneConfigCustomAction(this));
      this.forestryZoneConfig = (ForestryZoneConfigCustomAction)this.registerAction(new ForestryZoneConfigCustomAction(this));
      this.husbandryZoneConfig = (HusbandryZoneConfigCustomAction)this.registerAction(new HusbandryZoneConfigCustomAction(this));
   }

   public SettlementFlagObjectEntity getObjectEntity() {
      return this.objectEntity;
   }

   protected Level getLevel() {
      return this.objectEntity.getLevel();
   }

   public boolean isValid(ServerClient var1) {
      if (!super.isValid(var1)) {
         return false;
      } else {
         return !this.objectEntity.removed();
      }
   }

   public static boolean hasAccess(SettlementLevelLayer var0, SettlementCache var1, ServerClient var2) {
      return var1.ownerAuth == var2.authentication || var2.isSameTeam(var1.teamID) || var1.ownerAuth != -1L && var0.getOwnerAuth() == var1.ownerAuth || var1.teamID != -1 && var0.getTeamID() == var1.teamID;
   }
}
