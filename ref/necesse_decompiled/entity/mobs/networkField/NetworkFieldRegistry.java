package necesse.entity.mobs.networkField;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public abstract class NetworkFieldRegistry {
   private boolean registryOpen = true;
   private final ArrayList<NetworkField<?>> fields = new ArrayList();
   private final HashSet<Integer> dirty = new HashSet();

   public NetworkFieldRegistry() {
   }

   protected void markDirty(int var1) {
      this.dirty.add(var1);
   }

   public void writeSpawnPacket(PacketWriter var1) {
      Iterator var2 = this.fields.iterator();

      while(var2.hasNext()) {
         NetworkField var3 = (NetworkField)var2.next();
         var3.writeUpdatePacket(var1);
      }

   }

   public void readSpawnPacket(PacketReader var1) {
      Iterator var2 = this.fields.iterator();

      while(var2.hasNext()) {
         NetworkField var3 = (NetworkField)var2.next();
         var3.readUpdatePacket(var1);
      }

   }

   public void tickSync() {
      if (!this.dirty.isEmpty()) {
         Packet var1 = new Packet();
         PacketWriter var2 = new PacketWriter(var1);
         var2.putNextShortUnsigned(this.dirty.size());
         Iterator var3 = this.dirty.iterator();

         while(var3.hasNext()) {
            int var4 = (Integer)var3.next();
            NetworkField var5 = (NetworkField)this.fields.get(var4);
            var2.putNextShortUnsigned(var4);
            var5.writeUpdatePacket(var2);
         }

         this.dirty.clear();
         this.sendUpdatePacket(var1);
      }

   }

   public void readUpdatePacket(PacketReader var1) {
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         ((NetworkField)this.fields.get(var4)).readUpdatePacket(var1);
      }

   }

   public void closeRegistry() {
      this.registryOpen = false;
      this.dirty.clear();
   }

   public abstract void sendUpdatePacket(Packet var1);

   public abstract String getDebugIdentifierString();

   protected String getClosedErrorMessage() {
      return "Cannot register network fields after initialization, must be done in constructor";
   }

   public final <T extends NetworkField<?>> T registerField(T var1) {
      if (!this.registryOpen) {
         throw new IllegalStateException(this.getClosedErrorMessage());
      } else if (this.fields.size() >= 32767) {
         throw new IllegalStateException("Cannot register any more network fields for " + this.getDebugIdentifierString());
      } else {
         this.fields.add(var1);
         var1.onRegister(this, this.fields.size() - 1);
         return var1;
      }
   }
}
