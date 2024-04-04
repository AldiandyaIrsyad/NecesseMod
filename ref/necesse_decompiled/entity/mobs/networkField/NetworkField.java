package necesse.entity.mobs.networkField;

import java.util.Objects;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public abstract class NetworkField<T> {
   private NetworkFieldRegistry registry;
   private int id = -1;
   private T value;

   public NetworkField(T var1) {
      this.value = var1;
   }

   protected void onRegister(NetworkFieldRegistry var1, int var2) {
      if (this.registry != null) {
         throw new IllegalStateException("Cannot register same network field twice");
      } else {
         this.registry = var1;
         this.id = var2;
      }
   }

   public void set(T var1) {
      if (!this.isSameValue(this.value, var1)) {
         this.value = var1;
         this.onChanged(var1);
         this.markDirty();
      }

   }

   public void onChanged(T var1) {
   }

   public void markDirty() {
      if (this.registry != null) {
         this.registry.markDirty(this.id);
      }

   }

   protected boolean isSameValue(T var1, T var2) {
      return Objects.equals(var1, var2);
   }

   public T get() {
      return this.value;
   }

   public final void writeUpdatePacket(PacketWriter var1) {
      this.writePacket(this.value, var1);
   }

   public final void readUpdatePacket(PacketReader var1) {
      this.value = this.readPacket(var1);
      this.onChanged(this.value);
   }

   protected abstract void writePacket(T var1, PacketWriter var2);

   protected abstract T readPacket(PacketReader var1);
}
