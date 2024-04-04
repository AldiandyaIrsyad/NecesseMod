package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.regionSystem.RegionPosition;

public abstract class GroundEffectEvent extends MobAbilityLevelEvent {
   protected int x;
   protected int y;
   protected HudDrawElement hudDrawElement;

   public GroundEffectEvent() {
   }

   public GroundEffectEvent(Mob var1, int var2, int var3, GameRandom var4) {
      super(var1, var4);
      this.x = var2;
      this.y = var3;
   }

   public void init() {
      super.init();
      if (this.isClient() && !this.isOver()) {
         this.hudDrawElement = this.level.hudManager.addElement(new HudDrawElement() {
            public void addDrawables(List<SortedDrawable> var1, final GameCamera var2, PlayerMob var3) {
               var1.add(new SortedDrawable() {
                  public int getPriority() {
                     return 0;
                  }

                  public void draw(TickManager var1) {
                     if (GlobalData.debugActive()) {
                        Shape var2x = GroundEffectEvent.this.getHitBox();
                        if (var2x != null) {
                           Screen.drawShape(var2x, var2, false, 1.0F, 0.0F, 0.0F, 1.0F);
                        }
                     }

                  }
               });
            }
         });
      }

   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.x);
      var1.putNextInt(this.y);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.x = var1.getNextInt();
      this.y = var1.getNextInt();
   }

   public abstract Shape getHitBox();

   public abstract void clientHit(Mob var1);

   public abstract void serverHit(Mob var1, boolean var2);

   public abstract void hitObject(LevelObjectHit var1);

   public boolean canHit(Mob var1) {
      return var1.canBeHit(this);
   }

   public void clientTick() {
      super.clientTick();
      Shape var1 = this.getHitBox();
      if (var1 != null) {
         this.handleHits(var1, this::canHit, (Function)null);
      }

   }

   public void serverTick() {
      super.serverTick();
      Shape var1 = this.getHitBox();
      if (var1 != null) {
         this.handleHits(var1, this::canHit, (Function)null);
      }

   }

   public final void hit(LevelObjectHit var1) {
      super.hit(var1);
      this.hitObject(var1);
   }

   public final void clientHit(Mob var1, Packet var2) {
      super.clientHit(var1, var2);
      this.clientHit(var1);
   }

   public final void serverHit(Mob var1, Packet var2, boolean var3) {
      super.serverHit(var1, var2, var3);
      this.serverHit(var1, var3);
   }

   public void over() {
      super.over();
      if (this.hudDrawElement != null) {
         this.hudDrawElement.remove();
      }

   }

   public Collection<RegionPosition> getRegionPositions() {
      Collection var1 = super.getRegionPositions();
      ArrayList var2 = new ArrayList(var1.size() + 1);
      var2.addAll(var1);
      var2.add(this.level.regionManager.getRegionPosByTile(this.x / 32, this.y / 32));
      return var2;
   }
}
