package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketAchievementUpdate;
import necesse.engine.network.packet.PacketActiveSetBuffAbility;
import necesse.engine.network.packet.PacketActiveSetBuffAbilityStopped;
import necesse.engine.network.packet.PacketActiveSetBuffAbilityUpdate;
import necesse.engine.network.packet.PacketActiveTrinketBuffAbility;
import necesse.engine.network.packet.PacketActiveTrinketBuffAbilityStopped;
import necesse.engine.network.packet.PacketActiveTrinketBuffAbilityUpdate;
import necesse.engine.network.packet.PacketAddDeathLocation;
import necesse.engine.network.packet.PacketAddSteamInvite;
import necesse.engine.network.packet.PacketAdventurePartyAdd;
import necesse.engine.network.packet.PacketAdventurePartyBuffPolicy;
import necesse.engine.network.packet.PacketAdventurePartyCompressInventory;
import necesse.engine.network.packet.PacketAdventurePartyRemove;
import necesse.engine.network.packet.PacketAdventurePartyRequestUpdate;
import necesse.engine.network.packet.PacketAdventurePartySync;
import necesse.engine.network.packet.PacketAdventurePartyUpdate;
import necesse.engine.network.packet.PacketBlinkScepter;
import necesse.engine.network.packet.PacketBuffAbility;
import necesse.engine.network.packet.PacketChangeObject;
import necesse.engine.network.packet.PacketChangeTile;
import necesse.engine.network.packet.PacketChangeWire;
import necesse.engine.network.packet.PacketChangeWorldTime;
import necesse.engine.network.packet.PacketCharacterSelectError;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketClientStats;
import necesse.engine.network.packet.PacketClientStatsUpdate;
import necesse.engine.network.packet.PacketCloseContainer;
import necesse.engine.network.packet.PacketCmdAutocomplete;
import necesse.engine.network.packet.PacketConnectApproved;
import necesse.engine.network.packet.PacketConnectRequest;
import necesse.engine.network.packet.PacketContainerAction;
import necesse.engine.network.packet.PacketContainerCustomAction;
import necesse.engine.network.packet.PacketContainerEvent;
import necesse.engine.network.packet.PacketCraftAction;
import necesse.engine.network.packet.PacketCraftUseNearbyInventories;
import necesse.engine.network.packet.PacketDeath;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.packet.PacketDownloadCharacter;
import necesse.engine.network.packet.PacketDownloadCharacterResponse;
import necesse.engine.network.packet.PacketFireDeathRipper;
import necesse.engine.network.packet.PacketFishingStatus;
import necesse.engine.network.packet.PacketForceOfWind;
import necesse.engine.network.packet.PacketHitMob;
import necesse.engine.network.packet.PacketHitObject;
import necesse.engine.network.packet.PacketHumanWorkUpdate;
import necesse.engine.network.packet.PacketLevelData;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.packet.PacketLevelEventAction;
import necesse.engine.network.packet.PacketLevelEventOver;
import necesse.engine.network.packet.PacketLevelGNDData;
import necesse.engine.network.packet.PacketLevelLayerData;
import necesse.engine.network.packet.PacketLifelineEvent;
import necesse.engine.network.packet.PacketLogicGateOutputUpdate;
import necesse.engine.network.packet.PacketLogicGateUpdate;
import necesse.engine.network.packet.PacketMapData;
import necesse.engine.network.packet.PacketMobAbility;
import necesse.engine.network.packet.PacketMobAbilityLevelEventHit;
import necesse.engine.network.packet.PacketMobAttack;
import necesse.engine.network.packet.PacketMobBuff;
import necesse.engine.network.packet.PacketMobBuffRemove;
import necesse.engine.network.packet.PacketMobChat;
import necesse.engine.network.packet.PacketMobDebugMove;
import necesse.engine.network.packet.PacketMobFollowUpdate;
import necesse.engine.network.packet.PacketMobHealth;
import necesse.engine.network.packet.PacketMobInventory;
import necesse.engine.network.packet.PacketMobInventoryUpdate;
import necesse.engine.network.packet.PacketMobJump;
import necesse.engine.network.packet.PacketMobMana;
import necesse.engine.network.packet.PacketMobMount;
import necesse.engine.network.packet.PacketMobMovement;
import necesse.engine.network.packet.PacketMobNetworkFields;
import necesse.engine.network.packet.PacketMobPathBreakDownHit;
import necesse.engine.network.packet.PacketMobResilience;
import necesse.engine.network.packet.PacketMobUseLife;
import necesse.engine.network.packet.PacketMobUseMana;
import necesse.engine.network.packet.PacketModsMismatch;
import necesse.engine.network.packet.PacketMountMobJump;
import necesse.engine.network.packet.PacketMouseBeamEventUpdate;
import necesse.engine.network.packet.PacketNeedRequestSelf;
import necesse.engine.network.packet.PacketNeedSpawnedPacket;
import necesse.engine.network.packet.PacketNetworkUpdate;
import necesse.engine.network.packet.PacketOEInventoryNameUpdate;
import necesse.engine.network.packet.PacketOEInventoryUpdate;
import necesse.engine.network.packet.PacketOEProgressUpdate;
import necesse.engine.network.packet.PacketOEUseUpdate;
import necesse.engine.network.packet.PacketOEUseUpdateFull;
import necesse.engine.network.packet.PacketOEUseUpdateFullRequest;
import necesse.engine.network.packet.PacketObjectEntity;
import necesse.engine.network.packet.PacketObjectEntityError;
import necesse.engine.network.packet.PacketObjectInteract;
import necesse.engine.network.packet.PacketObjectSwitched;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.packet.PacketOpenPartyConfig;
import necesse.engine.network.packet.PacketOpenPvPTeams;
import necesse.engine.network.packet.PacketOpenQuests;
import necesse.engine.network.packet.PacketPerformanceResult;
import necesse.engine.network.packet.PacketPerformanceStart;
import necesse.engine.network.packet.PacketPermissionUpdate;
import necesse.engine.network.packet.PacketPickupEntityPickup;
import necesse.engine.network.packet.PacketPickupEntityTarget;
import necesse.engine.network.packet.PacketPing;
import necesse.engine.network.packet.PacketPlaceLogicGate;
import necesse.engine.network.packet.PacketPlaceObject;
import necesse.engine.network.packet.PacketPlaceTile;
import necesse.engine.network.packet.PacketPlayObjectDamageSound;
import necesse.engine.network.packet.PacketPlayerAction;
import necesse.engine.network.packet.PacketPlayerAppearance;
import necesse.engine.network.packet.PacketPlayerAttack;
import necesse.engine.network.packet.PacketPlayerAttackHandler;
import necesse.engine.network.packet.PacketPlayerAutoOpenDoors;
import necesse.engine.network.packet.PacketPlayerBuff;
import necesse.engine.network.packet.PacketPlayerBuffs;
import necesse.engine.network.packet.PacketPlayerCollisionHit;
import necesse.engine.network.packet.PacketPlayerDie;
import necesse.engine.network.packet.PacketPlayerDropItem;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.network.packet.PacketPlayerHotbarLocked;
import necesse.engine.network.packet.PacketPlayerHunger;
import necesse.engine.network.packet.PacketPlayerInventory;
import necesse.engine.network.packet.PacketPlayerInventoryAction;
import necesse.engine.network.packet.PacketPlayerInventoryPart;
import necesse.engine.network.packet.PacketPlayerInventorySlot;
import necesse.engine.network.packet.PacketPlayerItemInteract;
import necesse.engine.network.packet.PacketPlayerItemMobInteract;
import necesse.engine.network.packet.PacketPlayerJoinedTeam;
import necesse.engine.network.packet.PacketPlayerLatency;
import necesse.engine.network.packet.PacketPlayerLeftTeam;
import necesse.engine.network.packet.PacketPlayerLevelChange;
import necesse.engine.network.packet.PacketPlayerMobInteract;
import necesse.engine.network.packet.PacketPlayerMovement;
import necesse.engine.network.packet.PacketPlayerPlaceItem;
import necesse.engine.network.packet.PacketPlayerPvP;
import necesse.engine.network.packet.PacketPlayerRespawn;
import necesse.engine.network.packet.PacketPlayerRespawnRequest;
import necesse.engine.network.packet.PacketPlayerStats;
import necesse.engine.network.packet.PacketPlayerStatsUpdate;
import necesse.engine.network.packet.PacketPlayerStopAttack;
import necesse.engine.network.packet.PacketPlayerSync;
import necesse.engine.network.packet.PacketPlayerTeamInviteReceive;
import necesse.engine.network.packet.PacketPlayerTeamInviteReply;
import necesse.engine.network.packet.PacketPlayerTeamRequestReceive;
import necesse.engine.network.packet.PacketPlayerTeamRequestReply;
import necesse.engine.network.packet.PacketPlayerUseMount;
import necesse.engine.network.packet.PacketProjectileHit;
import necesse.engine.network.packet.PacketProjectilePositionUpdate;
import necesse.engine.network.packet.PacketProjectileTargetUpdate;
import necesse.engine.network.packet.PacketQuartzSetEvent;
import necesse.engine.network.packet.PacketQuest;
import necesse.engine.network.packet.PacketQuestAbandon;
import necesse.engine.network.packet.PacketQuestGiverRequest;
import necesse.engine.network.packet.PacketQuestGiverUpdate;
import necesse.engine.network.packet.PacketQuestRemove;
import necesse.engine.network.packet.PacketQuestRequest;
import necesse.engine.network.packet.PacketQuestShare;
import necesse.engine.network.packet.PacketQuestShareReceive;
import necesse.engine.network.packet.PacketQuestShareReply;
import necesse.engine.network.packet.PacketQuestUpdate;
import necesse.engine.network.packet.PacketQuests;
import necesse.engine.network.packet.PacketRefreshCombat;
import necesse.engine.network.packet.PacketRegionData;
import necesse.engine.network.packet.PacketRegionsData;
import necesse.engine.network.packet.PacketRemoveDeathLocation;
import necesse.engine.network.packet.PacketRemoveDeathLocations;
import necesse.engine.network.packet.PacketRemoveMob;
import necesse.engine.network.packet.PacketRemovePickupEntity;
import necesse.engine.network.packet.PacketRemoveProjectile;
import necesse.engine.network.packet.PacketRequestActiveSetBuffAbility;
import necesse.engine.network.packet.PacketRequestActiveTrinketBuffAbility;
import necesse.engine.network.packet.PacketRequestClientStats;
import necesse.engine.network.packet.PacketRequestLevelEvent;
import necesse.engine.network.packet.PacketRequestLogicGate;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.engine.network.packet.PacketRequestObjectChange;
import necesse.engine.network.packet.PacketRequestObjectEntity;
import necesse.engine.network.packet.PacketRequestPacket;
import necesse.engine.network.packet.PacketRequestPassword;
import necesse.engine.network.packet.PacketRequestPickupEntity;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.packet.PacketRequestProjectile;
import necesse.engine.network.packet.PacketRequestQuests;
import necesse.engine.network.packet.PacketRequestRegionData;
import necesse.engine.network.packet.PacketRequestSession;
import necesse.engine.network.packet.PacketRequestTileChange;
import necesse.engine.network.packet.PacketRequestTravel;
import necesse.engine.network.packet.PacketSelectedCharacter;
import necesse.engine.network.packet.PacketServerLevelStats;
import necesse.engine.network.packet.PacketServerStatus;
import necesse.engine.network.packet.PacketServerStatusRequest;
import necesse.engine.network.packet.PacketServerWorldStats;
import necesse.engine.network.packet.PacketSettings;
import necesse.engine.network.packet.PacketSettlementOpen;
import necesse.engine.network.packet.PacketShopContainerUpdate;
import necesse.engine.network.packet.PacketShopItemAction;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.packet.PacketShowAttackOnlyItem;
import necesse.engine.network.packet.PacketShowDPS;
import necesse.engine.network.packet.PacketShowItemLevelInteract;
import necesse.engine.network.packet.PacketShowItemMobInteract;
import necesse.engine.network.packet.PacketShowPickupText;
import necesse.engine.network.packet.PacketSpawnFirework;
import necesse.engine.network.packet.PacketSpawnItem;
import necesse.engine.network.packet.PacketSpawnMob;
import necesse.engine.network.packet.PacketSpawnPickupEntity;
import necesse.engine.network.packet.PacketSpawnPlayer;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.packet.PacketStatusMessage;
import necesse.engine.network.packet.PacketSummonFocus;
import necesse.engine.network.packet.PacketSwapInventorySlots;
import necesse.engine.network.packet.PacketTileDamage;
import necesse.engine.network.packet.PacketTileDestroyed;
import necesse.engine.network.packet.PacketToolItemEventHit;
import necesse.engine.network.packet.PacketTrapTriggered;
import necesse.engine.network.packet.PacketTroughFeed;
import necesse.engine.network.packet.PacketUniqueFloatText;
import necesse.engine.network.packet.PacketUnloadRegion;
import necesse.engine.network.packet.PacketUnloadRegions;
import necesse.engine.network.packet.PacketUpdateSession;
import necesse.engine.network.packet.PacketUpdateTrinketSlots;
import necesse.engine.network.packet.PacketWireHandlerUpdate;
import necesse.engine.network.packet.PacketWorldData;
import necesse.engine.network.packet.PacketWorldEvent;

public class PacketRegistry extends ClassedGameRegistry<Packet, PacketRegistryElement> {
   public static final PacketRegistry instance = new PacketRegistry();

   private PacketRegistry() {
      super("Packet", 65535);
   }

   public void registerCore() {
      registerPacket(false, false, false, PacketServerStatus.class);
      registerPacket(true, false, false, PacketServerStatusRequest.class);
      registerPacket(true, true, false, PacketPing.class);
      registerPacket(false, false, false, PacketConnectRequest.class);
      registerPacket(PacketRequestPacket.class);
      registerPacket(PacketDisconnect.class);
      registerPacket(PacketConnectApproved.class);
      registerPacket(PacketRequestSession.class);
      registerPacket(false, false, false, PacketUpdateSession.class);
      registerPacket(PacketModsMismatch.class);
      registerPacket(PacketRequestPassword.class);
      registerPacket(PacketCharacterSelectError.class);
      registerPacket(PacketRequestClientStats.class);
      registerPacket(PacketClientStats.class);
      registerPacket(PacketClientStatsUpdate.class);
      registerPacket(PacketPlayerStats.class);
      registerPacket(PacketPlayerStatsUpdate.class);
      registerPacket(PacketServerLevelStats.class);
      registerPacket(PacketServerWorldStats.class);
      registerPacket(PacketSettings.class);
      registerPacket(PacketPermissionUpdate.class);
      registerPacket(PacketLevelData.class);
      registerPacket(PacketRequestRegionData.class);
      registerPacket(PacketUnloadRegion.class);
      registerPacket(PacketUnloadRegions.class);
      registerPacket(PacketRegionData.class);
      registerPacket(PacketRegionsData.class);
      registerPacket(PacketLevelGNDData.class);
      registerPacket(PacketWorldData.class);
      registerPacket(PacketRequestTileChange.class);
      registerPacket(PacketRequestObjectChange.class);
      registerPacket(PacketTileDamage.class);
      registerPacket(PacketTileDestroyed.class);
      registerPacket(PacketHitObject.class);
      registerPacket(PacketChangeObject.class);
      registerPacket(PacketPlaceObject.class);
      registerPacket(PacketChangeTile.class);
      registerPacket(PacketPlaceTile.class);
      registerPacket(PacketPlaceLogicGate.class);
      registerPacket(PacketChangeWorldTime.class);
      registerPacket(PacketRequestLogicGate.class);
      registerPacket(PacketLogicGateUpdate.class);
      registerPacket(PacketLogicGateOutputUpdate.class);
      registerPacket(PacketLevelEvent.class);
      registerPacket(PacketLevelEventAction.class);
      registerPacket(PacketLevelEventOver.class);
      registerPacket(PacketRequestLevelEvent.class);
      registerPacket(PacketWorldEvent.class);
      registerPacket(PacketChangeWire.class);
      registerPacket(PacketObjectSwitched.class);
      registerPacket(PacketPlayObjectDamageSound.class);
      registerPacket(PacketLevelLayerData.class);
      registerPacket(PacketMapData.class);
      registerPacket(PacketRequestPlayerData.class);
      registerPacket(PacketNeedRequestSelf.class);
      registerPacket(PacketAchievementUpdate.class);
      registerPacket(PacketPlayerLatency.class);
      registerPacket(PacketPlayerGeneral.class);
      registerPacket(PacketPlayerAppearance.class);
      registerPacket(PacketSelectedCharacter.class);
      registerPacket(PacketDownloadCharacter.class);
      registerPacket(PacketDownloadCharacterResponse.class);
      registerPacket(PacketPlayerInventory.class);
      registerPacket(PacketPlayerInventoryPart.class);
      registerPacket(PacketPlayerInventorySlot.class);
      registerPacket(true, PacketPlayerMovement.class);
      registerPacket(PacketSpawnPlayer.class);
      registerPacket(PacketNeedSpawnedPacket.class);
      registerPacket(PacketPlayerSync.class);
      registerPacket(PacketShowAttack.class);
      registerPacket(PacketShowItemLevelInteract.class);
      registerPacket(PacketShowItemMobInteract.class);
      registerPacket(PacketPlayerLevelChange.class);
      registerPacket(true, PacketChatMessage.class);
      registerPacket(PacketShowPickupText.class);
      registerPacket(PacketCmdAutocomplete.class);
      registerPacket(PacketPlayerAction.class);
      registerPacket(PacketPlayerAttack.class);
      registerPacket(PacketPlayerAttackHandler.class);
      registerPacket(PacketPlayerStopAttack.class);
      registerPacket(PacketPlayerItemInteract.class);
      registerPacket(PacketPlayerItemMobInteract.class);
      registerPacket(PacketToolItemEventHit.class);
      registerPacket(PacketPlayerCollisionHit.class);
      registerPacket(PacketMouseBeamEventUpdate.class);
      registerPacket(PacketPlayerPlaceItem.class);
      registerPacket(PacketPlayerHunger.class);
      registerPacket(PacketContainerAction.class);
      registerPacket(PacketOpenContainer.class);
      registerPacket(PacketCloseContainer.class);
      registerPacket(PacketPlayerInventoryAction.class);
      registerPacket(PacketPlayerDropItem.class);
      registerPacket(PacketCraftAction.class);
      registerPacket(PacketShopItemAction.class);
      registerPacket(PacketContainerEvent.class);
      registerPacket(PacketStatusMessage.class);
      registerPacket(PacketPlayerRespawn.class);
      registerPacket(PacketPlayerRespawnRequest.class);
      registerPacket(PacketObjectInteract.class);
      registerPacket(PacketContainerCustomAction.class);
      registerPacket(PacketSpawnItem.class);
      registerPacket(PacketPlayerBuff.class);
      registerPacket(PacketPlayerBuffs.class);
      registerPacket(PacketRequestTravel.class);
      registerPacket(PacketPlayerDie.class);
      registerPacket(PacketPlayerPvP.class);
      registerPacket(PacketPlayerJoinedTeam.class);
      registerPacket(PacketPlayerLeftTeam.class);
      registerPacket(PacketUpdateTrinketSlots.class);
      registerPacket(PacketFireDeathRipper.class);
      registerPacket(PacketShowAttackOnlyItem.class);
      registerPacket(PacketMountMobJump.class);
      registerPacket(PacketRequestQuests.class);
      registerPacket(PacketQuests.class);
      registerPacket(PacketQuest.class);
      registerPacket(PacketQuestUpdate.class);
      registerPacket(PacketQuestRequest.class);
      registerPacket(PacketQuestRemove.class);
      registerPacket(PacketQuestAbandon.class);
      registerPacket(PacketQuestShare.class);
      registerPacket(PacketQuestShareReceive.class);
      registerPacket(PacketQuestShareReply.class);
      registerPacket(PacketForceOfWind.class);
      registerPacket(PacketBlinkScepter.class);
      registerPacket(PacketPlayerTeamInviteReceive.class);
      registerPacket(PacketPlayerTeamInviteReply.class);
      registerPacket(PacketPlayerTeamRequestReceive.class);
      registerPacket(PacketPlayerTeamRequestReply.class);
      registerPacket(PacketBuffAbility.class);
      registerPacket(PacketActiveSetBuffAbility.class);
      registerPacket(PacketActiveTrinketBuffAbility.class);
      registerPacket(PacketActiveSetBuffAbilityUpdate.class);
      registerPacket(PacketActiveTrinketBuffAbilityUpdate.class);
      registerPacket(PacketActiveSetBuffAbilityStopped.class);
      registerPacket(PacketActiveTrinketBuffAbilityStopped.class);
      registerPacket(PacketRequestActiveSetBuffAbility.class);
      registerPacket(PacketRequestActiveTrinketBuffAbility.class);
      registerPacket(PacketSummonFocus.class);
      registerPacket(PacketShowDPS.class);
      registerPacket(PacketCraftUseNearbyInventories.class);
      registerPacket(PacketPlayerAutoOpenDoors.class);
      registerPacket(PacketPlayerHotbarLocked.class);
      registerPacket(PacketPlayerUseMount.class);
      registerPacket(PacketAddDeathLocation.class);
      registerPacket(PacketRemoveDeathLocation.class);
      registerPacket(PacketRemoveDeathLocations.class);
      registerPacket(PacketUniqueFloatText.class);
      registerPacket(PacketSwapInventorySlots.class);
      registerPacket(PacketAdventurePartySync.class);
      registerPacket(PacketAdventurePartyAdd.class);
      registerPacket(PacketAdventurePartyRemove.class);
      registerPacket(PacketAdventurePartyBuffPolicy.class);
      registerPacket(PacketAdventurePartyCompressInventory.class);
      registerPacket(PacketAdventurePartyUpdate.class);
      registerPacket(PacketAdventurePartyRequestUpdate.class);
      registerPacket(PacketRequestMobData.class);
      registerPacket(true, PacketSpawnMob.class);
      registerPacket(PacketRemoveMob.class);
      registerPacket(true, PacketMobMovement.class);
      registerPacket(PacketHitMob.class);
      registerPacket(PacketMobHealth.class);
      registerPacket(PacketMobResilience.class);
      registerPacket(PacketMobMana.class);
      registerPacket(PacketMobUseMana.class);
      registerPacket(PacketMobUseLife.class);
      registerPacket(PacketMobAttack.class);
      registerPacket(PacketMobNetworkFields.class);
      registerPacket(PacketMobAbility.class);
      registerPacket(PacketPlayerMobInteract.class);
      registerPacket(PacketMobMount.class);
      registerPacket(PacketMobChat.class);
      registerPacket(PacketMobBuff.class);
      registerPacket(PacketMobBuffRemove.class);
      registerPacket(PacketDeath.class);
      registerPacket(PacketMobFollowUpdate.class);
      registerPacket(PacketRefreshCombat.class);
      registerPacket(PacketTroughFeed.class);
      registerPacket(PacketMobDebugMove.class);
      registerPacket(PacketQuestGiverRequest.class);
      registerPacket(PacketQuestGiverUpdate.class);
      registerPacket(PacketLifelineEvent.class);
      registerPacket(PacketQuartzSetEvent.class);
      registerPacket(PacketMobJump.class);
      registerPacket(PacketMobInventory.class);
      registerPacket(PacketMobInventoryUpdate.class);
      registerPacket(PacketMobAbilityLevelEventHit.class);
      registerPacket(PacketHumanWorkUpdate.class);
      registerPacket(PacketMobPathBreakDownHit.class);
      registerPacket(PacketRequestPickupEntity.class);
      registerPacket(PacketSpawnPickupEntity.class);
      registerPacket(PacketRemovePickupEntity.class);
      registerPacket(PacketPickupEntityPickup.class);
      registerPacket(PacketPickupEntityTarget.class);
      registerPacket(PacketRequestObjectEntity.class);
      registerPacket(PacketObjectEntity.class);
      registerPacket(PacketOEInventoryUpdate.class);
      registerPacket(PacketOEInventoryNameUpdate.class);
      registerPacket(PacketOEProgressUpdate.class);
      registerPacket(PacketSettlementOpen.class);
      registerPacket(PacketObjectEntityError.class);
      registerPacket(PacketWireHandlerUpdate.class);
      registerPacket(PacketOEUseUpdate.class);
      registerPacket(PacketOEUseUpdateFull.class);
      registerPacket(PacketOEUseUpdateFullRequest.class);
      registerPacket(PacketShopContainerUpdate.class);
      registerPacket(PacketSpawnFirework.class);
      registerPacket(PacketTrapTriggered.class);
      registerPacket(PacketSpawnProjectile.class);
      registerPacket(PacketRemoveProjectile.class);
      registerPacket(PacketProjectileHit.class);
      registerPacket(PacketProjectilePositionUpdate.class);
      registerPacket(true, PacketProjectileTargetUpdate.class);
      registerPacket(PacketRequestProjectile.class);
      registerPacket(PacketAddSteamInvite.class);
      registerPacket(PacketFishingStatus.class);
      registerPacket(PacketOpenPartyConfig.class);
      registerPacket(PacketOpenPvPTeams.class);
      registerPacket(PacketOpenQuests.class);
      registerPacket(true, true, false, PacketNetworkUpdate.class);
      registerPacket(PacketPerformanceStart.class);
      registerPacket(PacketPerformanceResult.class);
   }

   protected void onRegistryClose() {
   }

   public static String getPacketSimpleName(int var0) {
      try {
         return ((PacketRegistryElement)instance.getElement(var0)).simpleName;
      } catch (NoSuchElementException var2) {
         return "UnknownPacket0x" + Integer.toHexString(var0);
      }
   }

   public static int registerPacket(Class<? extends Packet> var0) {
      return registerPacket(false, var0);
   }

   public static int registerPacket(boolean var0, Class<? extends Packet> var1) {
      return registerPacket(false, true, var0, var1);
   }

   public static int registerPacket(boolean var0, boolean var1, boolean var2, Class<? extends Packet> var3) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register packets");
      } else {
         try {
            PacketRegistryElement var4 = new PacketRegistryElement(var0, var1, var2, var3);
            return instance.register(var4.simpleName, var4);
         } catch (NoSuchMethodException var5) {
            throw new IllegalArgumentException(var3.getSimpleName() + " does not have a constructor with byte[] parameter");
         }
      }
   }

   public static int getPacketID(Class<? extends Packet> var0) {
      return instance.getElementID(var0);
   }

   public static boolean hasTimestamp(int var0) {
      return ((PacketRegistryElement)instance.getElement(var0)).hasTimestamp;
   }

   public static boolean onlyConnectedClients(int var0) {
      return ((PacketRegistryElement)instance.getElement(var0)).onlyConnectedClients;
   }

   public static boolean processInstantly(int var0) {
      return ((PacketRegistryElement)instance.getElement(var0)).processInstantly;
   }

   public static Packet createPacket(int var0, byte[] var1) throws NoSuchElementException, IllegalAccessException, InstantiationException, InvocationTargetException {
      return (Packet)((PacketRegistryElement)instance.getElement(var0)).newInstance(new Object[]{var1});
   }

   public static int getTotalRegistered() {
      return instance.size();
   }

   protected static class PacketRegistryElement extends ClassIDDataContainer<Packet> {
      public final boolean processInstantly;
      public final boolean onlyConnectedClients;
      public final boolean hasTimestamp;
      public final String simpleName;

      public PacketRegistryElement(boolean var1, boolean var2, boolean var3, Class<? extends Packet> var4) throws NoSuchMethodException {
         super(var4, byte[].class);
         this.processInstantly = var1;
         this.onlyConnectedClients = var2;
         this.hasTimestamp = var3;
         this.simpleName = var4.getSimpleName();
      }
   }
}
