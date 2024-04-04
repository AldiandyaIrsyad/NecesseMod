package necesse.entity.mobs.attackHandler;

import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketFireDeathRipper;
import necesse.engine.network.packet.PacketPlayerStopAttack;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.DeathRipperProjectileToolItem;

public class DeathRipperAttackHandler extends MouseAngleAttackHandler {
   private InventoryItem item;
   private DeathRipperProjectileToolItem toolItem;
   private long lastTime;
   private long timeBuffer;
   private int attackSeed;
   private int shots;
   private GameRandom random = new GameRandom();

   public DeathRipperAttackHandler(PlayerMob var1, PlayerInventorySlot var2, InventoryItem var3, DeathRipperProjectileToolItem var4, int var5, int var6, int var7) {
      super(var1, var2, 20, 1000.0F, var6, var7);
      this.item = var3;
      this.toolItem = var4;
      this.attackSeed = var5;
      this.lastTime = var1.getWorldEntity().getLocalTime();
   }

   public void onUpdate() {
      super.onUpdate();
      Point2D.Float var1 = GameMath.getAngleDir(this.currentAngle);
      int var2 = this.player.getX() + (int)(var1.x * 100.0F);
      int var3 = this.player.getY() + (int)(var1.y * 100.0F);
      long var4 = this.player.getLevel().getWorldEntity().getLocalTime();
      if (this.toolItem.canAttack(this.player.getLevel(), var2, var3, this.player, this.item) == null) {
         this.timeBuffer += var4 - this.lastTime;
         int var6 = Item.getRandomAttackSeed(this.random.seeded((long)GameRandom.prime(this.attackSeed * this.shots)));
         Packet var7 = new Packet();
         this.toolItem.setupAttackContentPacket(new PacketWriter(var7), this.player.getLevel(), var2, var3, this.player, this.item);
         this.player.showAttack(this.item, var2, var3, var6, var7);
         if (this.player.isServer()) {
            ServerClient var8 = this.player.getServerClient();
            this.player.getLevel().getServer().network.sendToClientsAtExcept(new PacketShowAttack(this.player, this.item, var2, var3, var6, var7), (ServerClient)var8, var8);
         }

         while(true) {
            int var11 = this.getShootCooldown();
            if (this.timeBuffer < (long)var11) {
               break;
            }

            this.timeBuffer -= (long)var11;
            var6 = Item.getRandomAttackSeed(this.random.nextSeeded(GameRandom.prime(this.attackSeed * this.shots)));
            ++this.shots;
            this.toolItem.superOnAttack(this.player.getLevel(), var2, var3, this.player, this.player.getCurrentAttackHeight(), this.item, this.slot, 0, var6, new PacketReader(var7));
            if (this.player.isClient()) {
               Screen.playSound(GameResources.handgun, SoundEffect.effect(this.player));
            } else if (this.player.isServer()) {
               ServerClient var9 = this.player.getServerClient();
               Server var10 = this.player.getLevel().getServer();
               var10.network.sendToClientsAtExcept(new PacketFireDeathRipper(var9.slot), (ServerClient)var9, var9);
            }
         }
      }

      this.lastTime = var4;
   }

   private int getShootCooldown() {
      float var1 = 1.0F / this.toolItem.getAttackSpeedModifier(this.item, this.player);
      if (this.shots > 9) {
         return (int)(var1 * 150.0F);
      } else if (this.shots > 6) {
         return (int)(var1 * 200.0F);
      } else {
         return this.shots > 3 ? (int)(var1 * 275.0F) : (int)(var1 * 350.0F);
      }
   }

   public void onEndAttack(boolean var1) {
      this.player.stopAttack();
      if (this.player.isServer()) {
         ServerClient var2 = this.player.getServerClient();
         this.player.getLevel().getServer().network.sendToClientsAtExcept(new PacketPlayerStopAttack(var2.slot), (ServerClient)var2, var2);
      }

   }
}
