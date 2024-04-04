package necesse.entity.levelEvent.toolItemEvent;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.packet.PacketToolItemEventHit;
import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.SortedDrawable;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.regionSystem.RegionPosition;

public class ToolItemEvent extends LevelEvent implements Attacker {
   public AttackAnimMob mob;
   public int seed;
   public NetworkClient handlingClient;
   public boolean canBreakObjects;
   public long endTime;
   public long overTime;
   public int hitCooldown;
   public InventoryItem item;
   public int aimX;
   public int aimY;
   private HashMap<Point, Long> objectHits;
   private HashMap<Integer, Long> mobHits;
   private HudDrawElement debugDrawElement;
   public float lastHitboxProgress;
   public boolean hasResilienceApplied;

   public ToolItemEvent(AttackAnimMob var1, int var2, InventoryItem var3, int var4, int var5, int var6, int var7, HashMap<Integer, Long> var8) {
      super(false);
      if (var1 != null) {
         this.level = var1.getLevel();
      }

      this.resetUniqueID(new GameRandom((long)var2));
      this.mob = var1;
      this.seed = var2;
      if (!(var3.item instanceof ToolItem)) {
         throw new IllegalArgumentException("toolItem parameter must be a ToolItem instance");
      } else {
         this.item = var3;
         this.aimX = var4;
         this.aimY = var5;
         this.hitCooldown = var7;
         this.endTime = var1.getWorldEntity().getTime() + (long)var6;
         this.overTime = this.endTime + (long)Math.min(var7, var6);
         this.mobHits = var8;
      }
   }

   public ToolItemEvent(PlayerMob var1, int var2, InventoryItem var3, int var4, int var5, int var6, int var7) {
      this(var1, var2, var3, var4, var5, var6, var7, var1.toolHits);
   }

   public void init() {
      super.init();
      this.objectHits = new HashMap();
      this.canBreakObjects = this.mob.isPlayer;
      if (this.mob.isPlayer) {
         PlayerMob var1 = (PlayerMob)this.mob;
         if (this.isServer()) {
            if (Settings.giveClientsPower) {
               this.handlingClient = var1.getNetworkClient();
            }
         } else if (this.isClient() && this.level.getClient().allowClientsPower() && var1.getNetworkClient() == this.level.getClient().getClient()) {
            this.handlingClient = var1.getNetworkClient();
         }
      }

      if (GlobalData.debugActive()) {
         this.level.hudManager.addElement(this.debugDrawElement = new HudDrawElement() {
            public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
               final DrawOptionsList var4 = new DrawOptionsList();
               ArrayList var5 = ((ToolItem)ToolItemEvent.this.item.item).getHitboxes(ToolItemEvent.this.item, ToolItemEvent.this.mob, ToolItemEvent.this.aimX, ToolItemEvent.this.aimY, ToolItemEvent.this, true);
               Iterator var6 = var5.iterator();

               while(var6.hasNext()) {
                  Shape var7 = (Shape)var6.next();
                  var4.add(() -> {
                     Screen.drawShape(var7, var2, true, 1.0F, 0.0F, 0.0F, 0.5F);
                  });
               }

               var1.add(new SortedDrawable() {
                  public int getPriority() {
                     return Integer.MAX_VALUE;
                  }

                  public void draw(TickManager var1) {
                     var4.draw();
                  }
               });
            }
         });
      }

   }

   public void clientTick() {
      this.globalTick();
   }

   public void serverTick() {
      this.globalTick();
   }

   private void globalTick() {
      if (!this.isOver()) {
         if (this.overTime <= this.mob.getWorldEntity().getTime()) {
            this.over();
         } else if (this.endTime <= this.mob.getWorldEntity().getTime()) {
            if (this.debugDrawElement != null) {
               this.debugDrawElement.remove();
            }

            this.debugDrawElement = null;
         } else {
            ToolItem var1 = (ToolItem)this.item.item;
            ArrayList var2 = var1.getHitboxes(this.item, this.mob, this.aimX, this.aimY, this, false);
            if (this.isServer() && this.canBreakObjects) {
               Iterator var3 = var2.iterator();

               while(var3.hasNext()) {
                  Shape var4 = (Shape)var3.next();
                  ArrayList var5 = this.level.getCollisions(var4, var1.getAttackThroughFilter());
                  Iterator var6 = var5.iterator();

                  while(var6.hasNext()) {
                     LevelObjectHit var7 = (LevelObjectHit)var6.next();
                     if (!var7.invalidPos() && this.canHit(var7) && !this.level.collides((Line2D)(new Line2D.Float(this.mob.x, this.mob.y, (float)(var7.tileX * 32 + 16), (float)(var7.tileY * 32 + 16))), (CollisionFilter)(new CollisionFilter()).projectileCollision().addFilter((var0) -> {
                        return !var0.object().object.attackThrough;
                     }))) {
                        this.hit(var7);
                     }
                  }
               }
            }

            boolean var8 = this.isServer() && this.handlingClient == null || this.isClient() && this.handlingClient != null;
            if (var8) {
               GameUtils.streamTargetsTileRange(this.mob, var1.getAttackRange(this.item) / 32 + 4).filter((var3x) -> {
                  return var1.canHitMob(var3x, this) && this.canHit(var3x, 0) && var2.stream().anyMatch((var1x) -> {
                     return var1x.intersects(var3x.getHitBox());
                  }) && (var3x.canHitThroughCollision() || !this.level.collides((Line2D)(new Line2D.Float(this.mob.x, this.mob.y, var3x.x, var3x.y)), (CollisionFilter)(new CollisionFilter()).projectileCollision()));
               }).forEach((var1x) -> {
                  this.hit(var1x, (ServerClient)null);
               });
            }

         }
      }
   }

   private boolean canHit(LevelObjectHit var1) {
      if (!this.objectHits.containsKey(var1.getPoint())) {
         return true;
      } else {
         return (Long)this.objectHits.get(var1.getPoint()) + (long)this.hitCooldown < this.mob.getWorldEntity().getTime();
      }
   }

   public void hit(LevelObjectHit var1) {
      this.objectHits.put(var1.getPoint(), this.mob.getWorldEntity().getTime());
      ((ToolItem)this.item.item).hitObject(this.item, var1.getLevelObject(), this.mob);
   }

   public boolean canHit(Mob var1, int var2) {
      if (!this.mobHits.containsKey(var1.getHitCooldownUniqueID())) {
         return true;
      } else {
         return (Long)this.mobHits.get(var1.getHitCooldownUniqueID()) + (long)this.hitCooldown - (long)var2 < var1.getWorldEntity().getTime();
      }
   }

   public void hit(Mob var1, ServerClient var2) {
      this.mobHits.put(var1.getHitCooldownUniqueID(), var1.getWorldEntity().getTime());
      if (this.isServer()) {
         ((ToolItem)this.item.item).hitMob(this.item, this, this.level, var1, this.mob);
      } else if (this.isClient()) {
         this.level.getClient().network.sendPacket(new PacketToolItemEventHit(this, var1));
         var1.startHitCooldown();
      }

   }

   public void over() {
      super.over();
      if (this.debugDrawElement != null) {
         this.debugDrawElement.remove();
      }

   }

   public GameMessage getAttackerName() {
      return (GameMessage)(this.mob != null ? this.mob.getAttackerName() : new StaticMessage("TOOL_EVENT"));
   }

   public DeathMessageTable getDeathMessages() {
      return this.mob != null ? this.mob.getDeathMessages() : DeathMessageTable.fromRange("generic", 8);
   }

   public Mob getFirstAttackOwner() {
      return this.mob;
   }

   public Collection<RegionPosition> getRegionPositions() {
      return this.mob != null ? this.mob.getRegionPositions() : super.getRegionPositions();
   }
}
