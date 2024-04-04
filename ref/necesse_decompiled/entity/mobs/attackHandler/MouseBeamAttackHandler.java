package necesse.entity.mobs.attackHandler;

import java.awt.geom.Point2D;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerStopAttack;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MouseBeamLevelEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.level.maps.Level;

public class MouseBeamAttackHandler extends AttackHandler {
   public float manaCost = 0.0F;
   public int lifeCost = 0;
   public int seed;
   public MouseBeamLevelEvent event;

   public MouseBeamAttackHandler(PlayerMob var1, PlayerInventorySlot var2, int var3, int var4, MouseBeamLevelEvent var5) {
      super(var1, var2, var3);
      this.seed = var4;
      this.event = var5;
   }

   public MouseBeamAttackHandler(PlayerMob var1, PlayerInventorySlot var2, int var3, int var4, MouseBeamLevelEvent var5, float var6) {
      super(var1, var2, var3);
      this.seed = var4;
      this.event = var5;
      this.manaCost = var6;
   }

   public MouseBeamAttackHandler(PlayerMob var1, PlayerInventorySlot var2, int var3, int var4, MouseBeamLevelEvent var5, int var6) {
      super(var1, var2, var3);
      this.seed = var4;
      this.event = var5;
      this.lifeCost = var6;
   }

   public void onUpdate() {
      Point2D.Float var1 = GameMath.getAngleDir(this.event.currentAngle);
      int var2 = this.player.getX() + (int)(var1.x * 100.0F);
      int var3 = this.player.getY() + (int)(var1.y * 100.0F);
      Packet var4 = new Packet();
      this.item.item.setupAttackContentPacket(new PacketWriter(var4), this.player.getLevel(), var2, var3, this.player, this.item);
      this.player.showAttack(this.item, var2, var3, this.seed, var4);
      if (this.player.isServer()) {
         ServerClient var5 = this.player.getServerClient();
         this.player.getLevel().getServer().network.sendToClientsAtExcept(new PacketShowAttack(this.player, this.item, var2, var3, this.seed, var4), (ServerClient)var5, var5);
      }

      if (this.manaCost > 0.0F) {
         this.player.useMana(this.manaCost, this.player.isServerClient() ? this.player.getServerClient() : null);
      }

      if (this.lifeCost > 0) {
         this.player.useLife(this.lifeCost, this.player.isServerClient() ? this.player.getServerClient() : null, this.item.getItemLocalization());
      }

   }

   public void onEndAttack(boolean var1) {
      this.player.stopAttack();
      if (this.player.isServer()) {
         ServerClient var2 = this.player.getServerClient();
         this.player.getLevel().getServer().network.sendToClientsAtExcept(new PacketPlayerStopAttack(var2.slot), (ServerClient)var2, var2);
      }

   }

   public void drawControllerAimPos(GameCamera var1, Level var2, PlayerMob var3, InventoryItem var4) {
   }
}
