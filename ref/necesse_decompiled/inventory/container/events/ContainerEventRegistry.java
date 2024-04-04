package necesse.inventory.container.events;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.ListIterator;
import necesse.engine.network.PacketReader;
import necesse.inventory.container.mob.ShopContainerPartyResponseEvent;
import necesse.inventory.container.mob.ShopContainerPartyUpdateEvent;
import necesse.inventory.container.object.HomestoneUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementDefendZoneAutoExpandEvent;
import necesse.inventory.container.settlement.events.SettlementDefendZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementForestryZoneUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementHusbandryZoneUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementMoveErrorEvent;
import necesse.inventory.container.settlement.events.SettlementNewSettlerDietChangedEvent;
import necesse.inventory.container.settlement.events.SettlementNewSettlerEquipmentFilterChangedEvent;
import necesse.inventory.container.settlement.events.SettlementNewSettlerRestrictZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementOpenSettlementListEvent;
import necesse.inventory.container.settlement.events.SettlementOpenStorageConfigEvent;
import necesse.inventory.container.settlement.events.SettlementOpenWorkZoneConfigEvent;
import necesse.inventory.container.settlement.events.SettlementOpenWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneRecolorEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneRenameEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZonesFullEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerDietChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerDietsEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFilterChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFiltersEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerPrioritiesChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerPrioritiesEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerRestrictZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementSingleWorkstationsEvent;
import necesse.inventory.container.settlement.events.SettlementStorageChangeAllowedEvent;
import necesse.inventory.container.settlement.events.SettlementStorageEvent;
import necesse.inventory.container.settlement.events.SettlementStorageFullUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementStorageLimitsEvent;
import necesse.inventory.container.settlement.events.SettlementStoragePriorityLimitEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneNameEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneRemovedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZonesEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeRemoveEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationsEvent;
import necesse.inventory.container.teams.PvPAllTeamsUpdateEvent;
import necesse.inventory.container.teams.PvPCurrentTeamUpdateEvent;
import necesse.inventory.container.teams.PvPJoinRequestUpdateEvent;
import necesse.inventory.container.teams.PvPMemberUpdateEvent;
import necesse.inventory.container.teams.PvPOwnerUpdateEvent;
import necesse.inventory.container.teams.PvPPublicUpdateEvent;
import necesse.inventory.container.travel.IslandsResponseEvent;

public class ContainerEventRegistry {
   public static ArrayList<RegistryElement> events = new ArrayList();

   public ContainerEventRegistry() {
   }

   public static int registerUpdate(Class<? extends ContainerEvent> var0) {
      try {
         if (events.stream().anyMatch((var1x) -> {
            return var1x.eventClass == var0;
         })) {
            throw new IllegalArgumentException("Cannot register the same update class twice");
         } else {
            int var1 = events.size();
            events.add(new RegistryElement(var0));
            return var1;
         }
      } catch (NoSuchMethodException var2) {
         throw new IllegalArgumentException("ContainerEvent class must have a (PacketReader) constructor");
      }
   }

   public static boolean replaceUpdate(Class<? extends ContainerEvent> var0, Class<? extends ContainerEvent> var1) {
      ListIterator var2 = events.listIterator();

      RegistryElement var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (RegistryElement)var2.next();
      } while(var3.eventClass != var0);

      try {
         var2.set(new RegistryElement(var1));
         return true;
      } catch (NoSuchMethodException var5) {
         throw new IllegalArgumentException("SettlementUpdate class must have a (PacketReader) constructor and a (SettlementLevelData, ServerClient, PacketReader) constructor");
      }
   }

   public static int getID(ContainerEvent var0) {
      return getID(var0.getClass());
   }

   public static int getID(Class<? extends ContainerEvent> var0) {
      for(int var1 = 0; var1 < events.size(); ++var1) {
         RegistryElement var2 = (RegistryElement)events.get(var1);
         if (var2.eventClass.equals(var0)) {
            return var1;
         }
      }

      return -1;
   }

   public static Constructor<? extends ContainerEvent> getReaderConstructor(int var0) {
      if (var0 >= 0 && var0 < events.size()) {
         return ((RegistryElement)events.get(var0)).readerConstructor;
      } else {
         throw new IllegalStateException("Does not have container event with ID " + var0);
      }
   }

   static {
      registerUpdate(SettlementBasicsEvent.class);
      registerUpdate(SettlementOpenSettlementListEvent.class);
      registerUpdate(SettlementMoveErrorEvent.class);
      registerUpdate(SettlementSettlersChangedEvent.class);
      registerUpdate(SettlementSettlerBasicsEvent.class);
      registerUpdate(SettlementOpenStorageConfigEvent.class);
      registerUpdate(SettlementStorageEvent.class);
      registerUpdate(SettlementSingleStorageEvent.class);
      registerUpdate(SettlementStorageChangeAllowedEvent.class);
      registerUpdate(SettlementStorageLimitsEvent.class);
      registerUpdate(SettlementStoragePriorityLimitEvent.class);
      registerUpdate(SettlementStorageFullUpdateEvent.class);
      registerUpdate(SettlementOpenWorkstationEvent.class);
      registerUpdate(SettlementWorkstationsEvent.class);
      registerUpdate(SettlementSingleWorkstationsEvent.class);
      registerUpdate(SettlementWorkstationEvent.class);
      registerUpdate(SettlementWorkstationRecipeUpdateEvent.class);
      registerUpdate(SettlementWorkstationRecipeRemoveEvent.class);
      registerUpdate(SettlementWorkZonesEvent.class);
      registerUpdate(SettlementWorkZoneRemovedEvent.class);
      registerUpdate(SettlementWorkZoneChangedEvent.class);
      registerUpdate(SettlementWorkZoneNameEvent.class);
      registerUpdate(SettlementOpenWorkZoneConfigEvent.class);
      registerUpdate(SettlementForestryZoneUpdateEvent.class);
      registerUpdate(SettlementHusbandryZoneUpdateEvent.class);
      registerUpdate(SettlementSettlerEquipmentFiltersEvent.class);
      registerUpdate(SettlementNewSettlerEquipmentFilterChangedEvent.class);
      registerUpdate(SettlementSettlerEquipmentFilterChangedEvent.class);
      registerUpdate(SettlementSettlerDietsEvent.class);
      registerUpdate(SettlementNewSettlerDietChangedEvent.class);
      registerUpdate(SettlementSettlerDietChangedEvent.class);
      registerUpdate(SettlementSettlerPrioritiesEvent.class);
      registerUpdate(SettlementSettlerPrioritiesChangedEvent.class);
      registerUpdate(SettlementDefendZoneChangedEvent.class);
      registerUpdate(SettlementDefendZoneAutoExpandEvent.class);
      registerUpdate(SettlementRestrictZonesFullEvent.class);
      registerUpdate(SettlementNewSettlerRestrictZoneChangedEvent.class);
      registerUpdate(SettlementSettlerRestrictZoneChangedEvent.class);
      registerUpdate(SettlementRestrictZoneChangedEvent.class);
      registerUpdate(SettlementRestrictZoneRenameEvent.class);
      registerUpdate(SettlementRestrictZoneRecolorEvent.class);
      registerUpdate(PvPCurrentTeamUpdateEvent.class);
      registerUpdate(PvPAllTeamsUpdateEvent.class);
      registerUpdate(PvPOwnerUpdateEvent.class);
      registerUpdate(PvPPublicUpdateEvent.class);
      registerUpdate(PvPMemberUpdateEvent.class);
      registerUpdate(PvPJoinRequestUpdateEvent.class);
      registerUpdate(HomestoneUpdateEvent.class);
      registerUpdate(SleepUpdateContainerEvent.class);
      registerUpdate(SpawnUpdateContainerEvent.class);
      registerUpdate(IslandsResponseEvent.class);
      registerUpdate(ElderQuestUpdateEvent.class);
      registerUpdate(ShopContainerPartyUpdateEvent.class);
      registerUpdate(ShopContainerPartyResponseEvent.class);
      registerUpdate(AdventurePartyChangedEvent.class);
   }

   public static class RegistryElement {
      public final Class<? extends ContainerEvent> eventClass;
      public final Constructor<? extends ContainerEvent> readerConstructor;

      public RegistryElement(Class<? extends ContainerEvent> var1) throws NoSuchMethodException {
         this.eventClass = var1;
         this.readerConstructor = var1.getConstructor(PacketReader.class);
      }
   }
}
