package necesse.engine.network.packet;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientLevelLoading;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelDeathLocation;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.WorldDeathLocation;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class PacketLevelData extends Packet {
   public int levelID;
   public final LevelIdentifier levelIdentifier;
   public final int width;
   public final int height;
   public final ArrayList<LevelDeathLocation> deathLocations;
   public final Packet levelContent;
   public HashMap<Point, Packet> regionData;

   public PacketLevelData(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelID = var2.getNextShortUnsigned();
      this.levelIdentifier = new LevelIdentifier(var2);
      this.width = var2.getNextInt();
      this.height = var2.getNextInt();
      int var3 = var2.getNextShortUnsigned();
      this.deathLocations = new ArrayList(var3);

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2.getNextInt();
         int var6 = var2.getNextInt();
         int var7 = var2.getNextInt();
         this.deathLocations.add(new LevelDeathLocation(var5, var6, var7));
      }

      this.levelContent = var2.getNextContentPacket();
      this.regionData = PacketRegionsData.readRegionsDataPacket(var2);
   }

   public PacketLevelData(Level var1, ServerClient var2, Collection<Region> var3) {
      this.levelID = var1.getID();
      this.levelIdentifier = var1.getIdentifier();
      this.width = var1.width;
      this.height = var1.height;
      this.deathLocations = new ArrayList();
      Iterator var4 = var2.getDeathLocations().iterator();

      while(var4.hasNext()) {
         WorldDeathLocation var5 = (WorldDeathLocation)var4.next();
         if (var5.levelIdentifier.equals(this.levelIdentifier)) {
            this.deathLocations.add(new LevelDeathLocation(var5.getSecondsSince(var2.characterStats()), var5.x, var5.y));
         }
      }

      this.levelContent = new Packet();
      var1.writeLevelDataPacket(new PacketWriter(this.levelContent));
      this.regionData = PacketRegionsData.toRegionData(var2, var3);
      PacketWriter var8 = new PacketWriter(this);
      var8.putNextShortUnsigned(this.levelID);
      this.levelIdentifier.writePacket(var8);
      var8.putNextInt(this.width);
      var8.putNextInt(this.height);
      var8.putNextShortUnsigned(this.deathLocations.size());
      Iterator var7 = this.deathLocations.iterator();

      while(var7.hasNext()) {
         LevelDeathLocation var6 = (LevelDeathLocation)var7.next();
         var8.putNextInt(var6.secondsSince);
         var8.putNextInt(var6.x);
         var8.putNextInt(var6.y);
      }

      var8.putNextContentPacket(this.levelContent);
      PacketRegionsData.writeRegionsDataPacket(var8, this.regionData);
   }

   public boolean isSameLevel(Level var1) {
      return var1.getIdentifier().equals(this.levelIdentifier) && var1.width == this.width && var1.height == this.height;
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.worldEntity == null) {
         GameLog.warn.println("Got level data packet before getting world entity packet");
      } else {
         if (var2.levelManager.updateLevel(this)) {
            var2.loading.levelDataPhase.submitLevelDataPacket(this);
            ClientLevelLoading var3 = var2.levelManager.loading();
            if (var3.level.getIdentifier().equals(this.levelIdentifier)) {
               var3.applyRegionData(this.regionData);
            }
         }

      }
   }
}
