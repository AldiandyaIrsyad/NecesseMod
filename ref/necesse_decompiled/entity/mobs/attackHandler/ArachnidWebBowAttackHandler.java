package necesse.entity.mobs.attackHandler;

import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerStopAttack;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.ArachnidWebBowToolItem;

public class ArachnidWebBowAttackHandler extends MouseAngleAttackHandler {
   private final InventoryItem item;
   private final ArachnidWebBowToolItem toolItem;
   private final int attackSeed;
   private int shotsRemaining = 3;
   private int shots;
   private long timeBuffer;
   private final GameRandom random = new GameRandom();
   private int timeBetweenReloads = 500;
   private int timeBetweenBurstShots = 125;

   public ArachnidWebBowAttackHandler(PlayerMob var1, PlayerInventorySlot var2, InventoryItem var3, ArachnidWebBowToolItem var4, int var5, int var6, int var7) {
      super(var1, var2, 20, 1000.0F, var6, var7);
      this.attackSeed = var5;
      this.timeBuffer = (long)this.timeBetweenReloads;
      this.item = var3;
      this.toolItem = var4;
   }

   public void onUpdate() {
      super.onUpdate();
      Point2D.Float var1 = GameMath.getAngleDir(this.currentAngle);
      int var2 = this.player.getX() + (int)(var1.x * 100.0F);
      int var3 = this.player.getY() + (int)(var1.y * 100.0F);
      if (this.toolItem.canAttack(this.player.getLevel(), var2, var3, this.player, this.item) == null) {
         int var4 = Item.getRandomAttackSeed(this.random.seeded((long)GameRandom.prime(this.attackSeed * this.shots)));
         Packet var5 = new Packet();
         this.player.showAttack(this.item, var2, var3, var4, var5);
         this.toolItem.setupAttackContentPacket(new PacketWriter(var5), this.player.getLevel(), var2, var3, this.player, this.item);
         if (this.player.isServer()) {
            ServerClient var6 = this.player.getServerClient();
            this.player.getLevel().getServer().network.sendToAllClientsExcept(new PacketShowAttack(this.player, this.item, var2, var3, var4, var5), var6);
         }

         this.timeBuffer += (long)this.updateInterval;

         while(true) {
            float var7 = this.getSpeedModifier();
            if ((float)this.timeBuffer < (float)this.timeBetweenReloads * var7) {
               break;
            }

            var4 = Item.getRandomAttackSeed(this.random.nextSeeded(GameRandom.prime(this.attackSeed * this.shots)));
            ++this.shots;
            --this.shotsRemaining;
            this.toolItem.superOnAttack(this.player.getLevel(), var2, var3, this.player, this.player.getCurrentAttackHeight(), this.item, this.slot, 0, var4, new PacketReader(var5));
            if (this.player.isClient()) {
               Screen.playSound(GameResources.bow, SoundEffect.effect(this.player));
            }

            if (this.shotsRemaining <= 0) {
               this.shotsRemaining = 3;
               this.timeBuffer = 0L;
               break;
            }

            this.timeBuffer = (long)((int)((float)(this.timeBetweenReloads - this.timeBetweenBurstShots) * var7));
         }
      }

   }

   private float getSpeedModifier() {
      return 1.0F / this.toolItem.getAttackSpeedModifier(this.item, this.player);
   }

   public void onEndAttack(boolean var1) {
      this.player.startItemCooldown(this.toolItem, (int)((float)this.timeBetweenReloads * this.getSpeedModifier()));
      this.player.stopAttack();
      if (this.player.isServer()) {
         ServerClient var2 = this.player.getServerClient();
         this.player.getLevel().getServer().network.sendToAllClientsExcept(new PacketPlayerStopAttack(var2.slot), var2);
      }

   }
}
