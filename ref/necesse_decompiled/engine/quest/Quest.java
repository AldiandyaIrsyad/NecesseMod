package necesse.engine.quest;

import java.awt.Color;
import java.util.HashSet;
import java.util.Objects;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketQuest;
import necesse.engine.network.packet.PacketQuestRemove;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.registries.QuestRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;

public abstract class Quest implements IDDataContainer {
   public final IDData idData = new IDData();
   private int uniqueID;
   private HashSet<Long> clients = new HashSet();
   private QuestManager manager;
   private boolean dirty;
   private boolean removed;

   public IDData getIDData() {
      return this.idData;
   }

   public Quest() {
      QuestRegistry.instance.applyIDData(this.getClass(), this.idData);
   }

   public void addSaveData(SaveData var1) {
      var1.addInt("uniqueID", this.getUniqueID());
      var1.addLongArray("clients", this.clients.stream().filter(Objects::nonNull).mapToLong(Long::longValue).toArray());
   }

   public void applyLoadData(LoadData var1) {
      this.uniqueID = var1.getInt("uniqueID", this.uniqueID);
      long[] var2 = var1.getLongArray("clients", new long[0]);
      long[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         long var6 = var3[var5];
         this.clients.add(var6);
      }

   }

   public void setupSpawnPacket(PacketWriter var1) {
      var1.putNextInt(this.getUniqueID());
      this.setupPacket(var1);
   }

   public void applySpawnPacket(PacketReader var1) {
      this.uniqueID = var1.getNextInt();
      this.applyPacket(var1);
   }

   public void setupPacket(PacketWriter var1) {
   }

   public void applyPacket(PacketReader var1) {
   }

   public void init(QuestManager var1) {
      this.manager = var1;
      if (this.isDirty()) {
         var1.markDirty();
      }

   }

   public int getUniqueID() {
      if (this.uniqueID == 0) {
         this.uniqueID = GameRandom.globalRandom.nextInt();
      }

      return this.uniqueID;
   }

   public void makeActiveFor(Server var1, ServerClient var2) {
      this.clients.add(var2.authentication);
      var2.quests.add(this);
      var1.network.sendPacket(new PacketQuest(this, true), (ServerClient)var2);
   }

   public void abandonFor(Server var1, ServerClient var2) {
      this.abandonFor(var2.authentication);
      var2.quests.remove(this);
      var1.network.sendPacket(new PacketQuestRemove(this), (ServerClient)var2);
   }

   public void abandonFor(long var1) {
      this.clients.remove(var1);
   }

   public boolean isActiveFor(long var1) {
      return this.clients.contains(var1);
   }

   public int getTotalActiveClients() {
      return this.clients.size();
   }

   public abstract void tick(ServerClient var1);

   public boolean canShare() {
      return true;
   }

   public boolean canShareWith(ServerClient var1, ServerClient var2) {
      return var1.isSameTeam(var2);
   }

   public void onShared(Server var1, ServerClient var2, ServerClient var3) {
      if (this.isRemoved()) {
         var3.sendChatMessage((GameMessage)(new LocalMessage("misc", "questnolongervalid")));
      } else {
         this.makeActiveFor(var1, var3);
      }

   }

   public void markDirty() {
      this.dirty = true;
      if (this.manager != null) {
         this.manager.markDirty();
      }

   }

   public boolean isDirty() {
      return this.dirty;
   }

   public void clean() {
      this.dirty = false;
   }

   public void remove() {
      this.removed = true;
      this.markDirty();
   }

   public final boolean isRemoved() {
      return this.removed;
   }

   public abstract boolean canComplete(NetworkClient var1);

   public void complete(ServerClient var1) {
      var1.newStats.quests_completed.increment(1);
   }

   public abstract GameMessage getTitle();

   public abstract GameMessage getDescription();

   public abstract void drawProgress(PlayerMob var1, int var2, int var3, int var4, Color var5, boolean var6);

   public abstract int getDrawProgressHeight(int var1, boolean var2);

   public MobSpawnTable getExtraCritterSpawnTable(ServerClient var1, Level var2) {
      return null;
   }

   public MobSpawnTable getExtraMobSpawnTable(ServerClient var1, Level var2) {
      return null;
   }

   public FishingLootTable getExtraFishingLoot(ServerClient var1, FishingSpot var2) {
      return null;
   }

   public LootTable getExtraMobDrops(ServerClient var1, Mob var2) {
      return null;
   }
}
