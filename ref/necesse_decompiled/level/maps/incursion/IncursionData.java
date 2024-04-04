package necesse.level.maps.incursion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IncursionDataRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.FallenAltarObjectEntity;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.presets.containerComponent.object.FallenAltarContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.fallenAltar.FallenAltarContainer;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.IncursionLevel;

public abstract class IncursionData {
   public final IDData idData = new IDData();
   public ArrayList<UniqueIncursionModifier> uniqueIncursionModifiers = new ArrayList();
   public ArrayList<ArrayList<Supplier<InventoryItem>>> playerPersonalIncursionCompleteRewards = new ArrayList();
   public ArrayList<ArrayList<Supplier<InventoryItem>>> playerSharedIncursionCompleteRewards = new ArrayList();
   private int uniqueID;

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public IncursionData() {
      IncursionDataRegistry.applyIncursionDataIDData(this);

      while(this.uniqueID == 0 || this.uniqueID == -1) {
         this.uniqueID = GameRandom.getNewUniqueID();
      }

   }

   public void addSaveData(SaveData var1) {
      var1.addUnsafeString("stringID", this.getStringID());
      var1.addInt("uniqueID", this.uniqueID);
   }

   public void applyLoadData(LoadData var1) {
      this.uniqueID = var1.getInt("uniqueID", -1, false);
      if (this.uniqueID == -1) {
         throw new LoadDataException("Could not load " + this.getStringID() + " incursionData uniqueID");
      }
   }

   public void writePacket(PacketWriter var1) {
      var1.putNextInt(this.uniqueID);
   }

   public void applyPacket(PacketReader var1) {
      this.uniqueID = var1.getNextInt();
   }

   public void init() {
   }

   public int getUniqueID() {
      return this.uniqueID;
   }

   public abstract GameMessage getDisplayName();

   public abstract IncursionBiome getIncursionBiome();

   public abstract GameMessage getIncursionMissionTypeName();

   public abstract GameSprite getTabletSprite();

   public abstract void setTabletTier(int var1);

   public abstract int getTabletTier();

   public abstract boolean isSameIncursion(IncursionData var1);

   public abstract Collection<FairType> getObjectives(IncursionData var1, FontOptions var2);

   public abstract void setUpDetails(FallenAltarContainer var1, FallenAltarContainerForm var2, FormContentBox var3, boolean var4);

   public abstract GameTooltips getOpenButtonTooltips(FallenAltarContainer var1);

   public abstract boolean canOpen(FallenAltarContainer var1);

   public abstract void onOpened(FallenAltarContainer var1, ServerClient var2);

   public abstract void onCompleted(FallenAltarObjectEntity var1, ServerClient var2);

   public abstract void onClosed(FallenAltarObjectEntity var1, ServerClient var2);

   public abstract IncursionLevel getNewIncursionLevel(LevelIdentifier var1, Server var2, WorldEntity var3);

   public abstract ArrayList<UniqueIncursionModifier> getUniqueIncursionModifiers();

   public Stream<ModifierValue<?>> getDefaultLevelModifiers() {
      return Stream.empty();
   }

   public Stream<ModifierValue<?>> getMobModifiers(Mob var1) {
      return Stream.empty();
   }

   public LootTable getExtraMobDrops(Mob var1) {
      return new LootTable();
   }

   public LootTable getExtraPrivateMobDrops(Mob var1, ServerClient var2) {
      return new LootTable();
   }

   public static IncursionData fromLoadData(LoadData var0) {
      String var1 = var0.getUnsafeString("stringID", (String)null);
      if (var1 == null) {
         throw new LoadDataException("Could not load IncursionData because of missing stringID");
      } else {
         IncursionData var2 = IncursionDataRegistry.getNewIncursionData(var1);
         if (var2 == null) {
            throw new LoadDataException("Could not load IncursionData with stringID " + var1);
         } else {
            var2.applyLoadData(var0);
            var2.init();
            return var2;
         }
      }
   }

   public static void writePacket(IncursionData var0, PacketWriter var1) {
      var1.putNextShortUnsigned(var0.getID());
      var0.writePacket(var1);
   }

   public static IncursionData fromPacket(PacketReader var0) {
      int var1 = var0.getNextShortUnsigned();
      IncursionData var2 = IncursionDataRegistry.getNewIncursionData(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("Could not find IncursionData with ID " + var1);
      } else {
         var2.applyPacket(var0);
         var2.init();
         return var2;
      }
   }

   public static IncursionData makeCopy(IncursionData var0) {
      Packet var1 = new Packet();
      writePacket(var0, new PacketWriter(var1));
      return fromPacket(new PacketReader(var1));
   }
}
