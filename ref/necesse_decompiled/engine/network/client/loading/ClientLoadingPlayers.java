package necesse.engine.network.client.loading;

import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.gfx.forms.FormResizeWrapper;

public class ClientLoadingPlayers extends ClientLoadingAutoPhase {
   private boolean[] loaded;

   public ClientLoadingPlayers(ClientLoading var1) {
      super(var1, true);
   }

   public void submitLoadedPlayer(int var1) {
      if (this.loaded != null) {
         this.loaded[var1] = true;
         this.updateLoadingMessage();
         boolean[] var2 = this.loaded;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            boolean var5 = var2[var4];
            if (!var5) {
               return;
            }
         }

         this.markDone();
      }
   }

   public GameMessage getLoadingMessage() {
      int var1 = 0;
      boolean[] var2 = this.loaded;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         boolean var5 = var2[var4];
         if (var5) {
            ++var1;
         }
      }

      float var6 = (float)var1 / (float)this.client.getSlots();
      return new LocalMessage("loading", "connectplayers", new Object[]{"percent", (int)(var6 * 100.0F)});
   }

   public FormResizeWrapper start() {
      if (this.loaded == null) {
         this.loaded = new boolean[this.client.getSlots()];
         if (Settings.instantLevelChange) {
            for(int var1 = 0; var1 < this.client.getSlots(); ++var1) {
               ClientClient var2 = this.client.getClient(var1);
               this.client.network.sendPacket(new PacketRequestPlayerData(var1));
               if (var2 != null && var2.loadedPlayer) {
                  this.loaded[var1] = true;
               }
            }
         }
      }

      return super.start();
   }

   public void tick() {
      if (!this.isWaiting()) {
         boolean var1 = true;

         for(int var2 = 0; var2 < this.loaded.length; ++var2) {
            if (!this.loaded[var2]) {
               this.client.network.sendPacket(new PacketRequestPlayerData(var2));
               var1 = false;
            }
         }

         if (var1) {
            this.markDone();
         } else {
            this.setWait(250);
         }

      }
   }

   public void end() {
      this.loaded = null;
   }

   public void reset() {
      super.reset();
      if (this.loaded != null && !Settings.instantLevelChange) {
         this.loaded[this.client.getSlot()] = false;
      }

   }
}
