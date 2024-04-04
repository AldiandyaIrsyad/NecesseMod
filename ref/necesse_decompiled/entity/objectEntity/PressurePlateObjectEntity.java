package necesse.entity.objectEntity;

import java.awt.Rectangle;
import necesse.engine.Screen;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.Level;

public class PressurePlateObjectEntity extends ObjectEntity {
   private Rectangle collision;
   private boolean isDown;
   public int resetTime;
   private long hardResetTime;

   public PressurePlateObjectEntity(Level var1, int var2, int var3, Rectangle var4) {
      super(var1, "pressureplate", var2, var3);
      this.collision = var4;
      this.isDown = false;
      this.resetTime = 500;
      this.shouldSave = false;
   }

   public boolean shouldRequestPacket() {
      return false;
   }

   public PressurePlateObjectEntity(Level var1, int var2, int var3, Rectangle var4, int var5) {
      this(var1, var2, var3, var4);
      this.resetTime = var5;
   }

   public Rectangle getCollision() {
      return new Rectangle(this.getX() * 32 + this.collision.x, this.getY() * 32 + this.collision.y, this.collision.width, this.collision.height);
   }

   public void clientTick() {
      this.checkCollision();
   }

   public void serverTick() {
      this.checkCollision();
   }

   private void checkCollision() {
      ServerClient var1 = null;
      boolean var2 = this.getLevel().entityManager.mobs.getInRegionRangeByTile(this.getX(), this.getY(), 1).stream().anyMatch((var1x) -> {
         return var1x.canLevelInteract() && !var1x.isFlying() && var1x.getCollision().intersects(this.getCollision());
      });
      if (!var2) {
         if (this.isServer()) {
            var1 = (ServerClient)GameUtils.streamServerClients(this.getLevel()).filter((var1x) -> {
               return var1x.playerMob.canLevelInteract() && !var1x.playerMob.isFlying() && var1x.playerMob.getCollision().intersects(this.getCollision());
            }).findFirst().orElse((Object)null);
            if (var1 != null) {
               var2 = true;
            }
         } else if (this.isClient()) {
            var2 = GameUtils.streamClientClients(this.getLevel()).anyMatch((var1x) -> {
               return var1x.playerMob.canLevelInteract() && !var1x.playerMob.isFlying() && var1x.playerMob.getCollision().intersects(this.getCollision());
            });
         }
      }

      if (var2) {
         this.hardResetTime = this.getWorldEntity().getTime() + (long)this.resetTime;
      }

      if (var2 && !this.isDown) {
         this.getLevel().wireManager.updateWire(this.getX(), this.getY(), true);
         this.isDown = true;
         this.hardResetTime = this.getWorldEntity().getTime() + (long)this.resetTime;
         if (this.isClient()) {
            Screen.playSound(GameResources.tick, SoundEffect.effect((float)(this.getX() * 32 + 16), (float)(this.getY() * 32 + 16)).pitch(0.8F));
         } else if (var1 != null) {
            var1.newStats.plates_triggered.increment(1);
         }
      } else if (!var2 && this.isDown && this.getWorldEntity().getTime() >= this.hardResetTime) {
         this.isDown = false;
         this.getLevel().wireManager.updateWire(this.getX(), this.getY(), false);
         if (this.isClient()) {
            Screen.playSound(GameResources.tick, SoundEffect.effect((float)(this.getX() * 32 + 16), (float)(this.getY() * 32 + 16)).pitch(0.8F));
         }
      }

   }

   public boolean isDown() {
      return this.isDown;
   }

   public void onMouseHover(PlayerMob var1, boolean var2) {
      super.onMouseHover(var1, var2);
      if (var2) {
         Screen.addTooltip(new StringTooltips(new String[]{"Down: " + this.isDown(), "Reset time: " + this.resetTime}), TooltipLocation.INTERACT_FOCUS);
      }

   }
}
