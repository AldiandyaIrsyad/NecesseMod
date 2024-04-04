package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.ObjectEntity;

public class PacketOpenContainer extends Packet {
   public final int containerID;
   public final int uniqueSeed;
   public final Packet content;
   public final Object serverObject;

   public PacketOpenContainer(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.containerID = var2.getNextShortUnsigned();
      this.uniqueSeed = var2.getNextInt();
      this.content = var2.getNextContentPacket();
      this.serverObject = null;
   }

   public PacketOpenContainer(int var1, Packet var2, Object var3) {
      this.containerID = var1;
      this.uniqueSeed = GameRandom.globalRandom.nextInt();
      this.content = var2;
      this.serverObject = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextShortUnsigned(var1);
      var4.putNextInt(this.uniqueSeed);
      var4.putNextContentPacket(var2);
   }

   public PacketOpenContainer(int var1, Packet var2) {
      this(var1, var2, (Object)null);
   }

   public PacketOpenContainer(int var1) {
      this(var1, new Packet());
   }

   public static PacketOpenContainer ObjectEntity(int var0, ObjectEntity var1, Packet var2, Object var3) {
      if (var2 == null) {
         var2 = new Packet();
      }

      Packet var4 = new Packet();
      PacketWriter var5 = new PacketWriter(var4);
      var5.putNextShortUnsigned(var1.getX());
      var5.putNextShortUnsigned(var1.getY());
      var5.putNextContentPacket(var2);
      return new PacketOpenContainer(var0, var4, var3);
   }

   public static PacketOpenContainer ObjectEntity(int var0, ObjectEntity var1, Packet var2) {
      return ObjectEntity(var0, var1, var2, (Object)null);
   }

   public static PacketOpenContainer ObjectEntity(int var0, ObjectEntity var1) {
      return ObjectEntity(var0, var1, (Packet)null);
   }

   public static PacketOpenContainer Mob(int var0, Mob var1, Packet var2, Object var3) {
      if (var2 == null) {
         var2 = new Packet();
      }

      Packet var4 = new Packet();
      PacketWriter var5 = new PacketWriter(var4);
      var5.putNextInt(var1.getUniqueID());
      var5.putNextContentPacket(var2);
      return new PacketOpenContainer(var0, var4, var3);
   }

   public static PacketOpenContainer Mob(int var0, Mob var1, Packet var2) {
      return Mob(var0, var1, var2, (Object)null);
   }

   public static PacketOpenContainer Mob(int var0, Mob var1) {
      return Mob(var0, var1, (Packet)null);
   }

   public static PacketOpenContainer LevelObject(int var0, int var1, int var2, Packet var3, Object var4) {
      if (var3 == null) {
         var3 = new Packet();
      }

      Packet var5 = new Packet();
      PacketWriter var6 = new PacketWriter(var5);
      var6.putNextShortUnsigned(var1);
      var6.putNextShortUnsigned(var2);
      var6.putNextContentPacket(var3);
      return new PacketOpenContainer(var0, var5, var4);
   }

   public static PacketOpenContainer LevelObject(int var0, int var1, int var2, Packet var3) {
      return LevelObject(var0, var1, var2, var3, (Object)null);
   }

   public static PacketOpenContainer LevelObject(int var0, int var1, int var2) {
      return LevelObject(var0, var1, var2, (Packet)null);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      ContainerRegistry.openContainer(this.containerID, var2, this.uniqueSeed, this.content);
   }
}
