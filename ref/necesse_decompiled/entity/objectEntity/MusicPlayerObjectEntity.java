package necesse.entity.objectEntity;

import java.awt.Point;
import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.MusicOptions;
import necesse.engine.MusicOptionsOffset;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameMusic;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.miscItem.VinylItem;
import necesse.level.maps.Level;

public class MusicPlayerObjectEntity extends InventoryObjectEntity {
   public static int LISTEN_DISTANCE = 1600;
   protected MusicPlayerManager manager;

   public MusicPlayerObjectEntity(Level var1, int var2, int var3) {
      super(var1, var2, var3, 10);
      this.manager = new MusicPlayerManager(this.slots) {
         public void markDirty() {
            MusicPlayerObjectEntity.this.markDirty();
         }
      };
   }

   public boolean isItemValid(int var1, InventoryItem var2) {
      return var2 != null ? var2.item instanceof VinylItem : true;
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      this.manager.addSaveData(var1);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.manager.applyLoadData(var1);
   }

   public void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      this.manager.writeContentPacket(var1);
   }

   public void applyContentPacket(PacketReader var1) {
      super.applyContentPacket(var1);
      this.manager.readContentPacket(var1);
   }

   public InventoryRange getSettlementStorage() {
      return null;
   }

   public boolean canQuickStackInventory() {
      return false;
   }

   public boolean canRestockInventory() {
      return false;
   }

   public boolean canSortInventory() {
      return false;
   }

   public boolean canUseForNearbyCrafting() {
      return false;
   }

   public boolean canSetInventoryName() {
      return false;
   }

   public void clientTick() {
      super.clientTick();
      MusicList var1 = this.manager.getCurrentMusicList();
      if (var1 != null && !this.isPaused() && this.isClient()) {
         ClientClient var2 = this.getLevel().getClient().getClient();
         if (var2 != null && var2.hasSpawned()) {
            float var3 = var2.playerMob.getDistance((float)(this.getTileX() * 32 + 16), (float)(this.getTileY() * 32 + 16));
            if (var3 <= (float)LISTEN_DISTANCE) {
               Screen.setMusic((AbstractMusicList)var1, (ComparableSequence)Screen.MusicPriority.MUSIC_PLAYER.thenBy(this.getTileX() * this.getTileY()).thenBy((Comparable)this.getTileX()));
            }
         }
      }

   }

   public void serverTick() {
      super.serverTick();
      if (this.isServer() && this.getLevel().tickManager().getTick() == 1 && this.manager.hasAnyVinyls() && !this.isPaused()) {
         Point var1 = new Point(this.getTileX() * 32 + 16, this.getTileY() * 32 + 16);
         float var10001 = (float)var1.x;
         float var10002 = (float)var1.y;
         this.getLevel().entityManager.players.streamArea(var10001, var10002, LISTEN_DISTANCE).filter((var1x) -> {
            return var1x.getDistance((float)var1.x, (float)var1.y) <= (float)LISTEN_DISTANCE;
         }).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).forEach((var0) -> {
            if (var0.achievementsLoaded()) {
               var0.achievements().MY_JAM.markCompleted(var0);
            }

         });
      }

   }

   protected void onInventorySlotUpdated(int var1) {
      this.manager.updatePlaying(this.inventory, var1, this.isClient());
   }

   public void onWireUpdated() {
      this.manager.setIsPaused(this.getLevel().wireManager.isWireActiveAny(this.getTileX(), this.getTileY()));
   }

   public MusicPlayerManager getMusicManager() {
      return this.manager;
   }

   public MusicOptionsOffset getCurrentMusic() {
      return this.manager.getCurrentMusic();
   }

   public MusicOptions getPreviousMusic() {
      return this.manager.getPreviousMusic();
   }

   public boolean isPaused() {
      return this.manager.isPaused();
   }

   public void onMouseHover(PlayerMob var1, boolean var2) {
      super.onMouseHover(var1, var2);
      MusicOptionsOffset var3 = this.getCurrentMusic();
      if (var3 != null) {
         GameMusic var4 = var3.options.music;
         StringTooltips var5 = new StringTooltips(Localization.translate("ui", "musicplaying", "name", var4.trackName.translate() + (var4.fromName == null ? "" : " (" + var4.fromName.translate() + ")")));
         Screen.addTooltip(var5, TooltipLocation.INTERACT_FOCUS);
      }

      if (var2) {
         long var7 = this.manager.getMusicPlayingOffset();
         StringTooltips var6 = new StringTooltips("Offset: " + (var7 < 0L ? "-" : "") + GameUtils.getTimeStringMillis(Math.abs(var7)));
         Screen.addTooltip(var6, TooltipLocation.INTERACT_FOCUS);
      }

   }
}
