package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ObjectUserMob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.objectEntity.BedObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.BedContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.gameObject.ObjectUsersObject;
import necesse.level.gameObject.RespawnObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

public class BedObject extends FurnitureObject implements RespawnObject, ObjectUsersObject {
   private String textureName;
   public GameTexture baseTexture;
   public GameTexture overlayTexture;
   public GameTexture[] maskTextures;
   protected int counterID;

   private BedObject(String var1, ToolType var2, Color var3) {
      super(new Rectangle(32, 32));
      this.textureName = var1;
      this.toolType = var2;
      this.mapColor = var3;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.roomProperties.add("bed");
      this.furnitureType = "bed";
   }

   public MultiTile getMultiTile(int var1) {
      return new MultiTile(0, 1, 1, 2, var1, true, new int[]{this.counterID, this.getID()});
   }

   public void loadTextures() {
      super.loadTextures();
      this.baseTexture = GameTexture.fromFile("objects/" + this.textureName);
      this.overlayTexture = GameTexture.fromFile("objects/" + this.textureName + "_overlay");
      this.maskTextures = new GameTexture[4];

      int var2;
      try {
         GameTexture var1 = GameTexture.fromFileRaw("objects/" + this.textureName + "_mask", true);

         for(var2 = 0; var2 < this.maskTextures.length; ++var2) {
            GameTexture var3;
            if (var2 == 0) {
               var3 = new GameTexture(var1, 96, 32, 32, 96);
            } else if (var2 == 1) {
               var3 = (new GameTexture(var1, 0, 64, 64, 64)).rotatedClockwise();
            } else if (var2 == 2) {
               var3 = new GameTexture(var1, 64, 32, 32, 96);
            } else {
               var3 = (new GameTexture(var1, 0, 0, 64, 64)).rotatedAnticlockwise();
            }

            this.maskTextures[var2] = var3;
            this.maskTextures[var2].makeFinal();
         }

         var1.makeFinal();
      } catch (FileNotFoundException var4) {
         for(var2 = 0; var2 < this.maskTextures.length; ++var2) {
            this.maskTextures[var2] = new GameTexture("objects/" + this.textureName + " mask" + var2, 64, 64);
            this.maskTextures[var2].makeFinal();
         }
      }

   }

   public boolean drawsUsers() {
      return false;
   }

   public boolean preventsUsersPushed() {
      return true;
   }

   public Rectangle getUserSelectBox(Level var1, int var2, int var3, Mob var4) {
      byte var5 = var1.getObjectRotation(var2, var3);
      switch (var5) {
         case 0:
            return new Rectangle(var2 * 32 + 2, var3 * 32 - 32, 28, 54);
         case 1:
            return new Rectangle(var2 * 32, var3 * 32 - 10, 64, 36);
         case 2:
            return new Rectangle(var2 * 32 + 2, var3 * 32 - 16, 28, 64);
         case 3:
            return new Rectangle(var2 * 32 - 32, var3 * 32 - 10, 64, 36);
         default:
            return new Rectangle(var2 * 32, var3 * 32, 32, 32);
      }
   }

   public Point getUserAppearancePos(Level var1, int var2, int var3, Mob var4) {
      byte var5 = var1.getObjectRotation(var2, var3);
      switch (var5) {
         case 0:
            return new Point(var2 * 32 + 16, var3 * 32);
         case 1:
            return new Point(var2 * 32 + 16, var3 * 32 + 16);
         case 2:
            return new Point(var2 * 32 + 16, var3 * 32 + 32);
         case 3:
            return new Point(var2 * 32 + 16, var3 * 32 + 16);
         default:
            return new Point(var2 * 32 + 16, var3 * 32 + 16);
      }
   }

   public void tickUser(Level var1, int var2, int var3, Mob var4) {
      if (var1.tickManager().getTick() % 20 == 1) {
         ObjectEntity var5 = var1.entityManager.getObjectEntity(var2, var3);
         if (var5 instanceof BedObjectEntity) {
            ((BedObjectEntity)var5).updateUserPosition(var4);
         }
      }

      if (!var4.buffManager.hasBuff(BuffRegistry.SLEEPING) || var4.buffManager.getBuff(BuffRegistry.SLEEPING).getDurationLeft() <= 1000) {
         var4.buffManager.addBuff(new ActiveBuff(BuffRegistry.SLEEPING, var4, 60000, (Attacker)null), var1.isServer());
      }

   }

   public void stopUsing(Level var1, int var2, int var3, Mob var4) {
      ObjectEntity var5 = var1.entityManager.getObjectEntity(var2, var3);
      if (var5 instanceof OEUsers) {
         ((OEUsers)var5).stopUser(var4);
      }

   }

   public Point getMobPosSleepOffset(Level var1, int var2, int var3) {
      byte var4 = var1.getObjectRotation(var2, var3);
      switch (var4) {
         case 0:
            return new Point(16, 20);
         case 1:
            return new Point(16, 16);
         case 2:
            return new Point(16, 24);
         case 3:
            return new Point(16, 16);
         default:
            return new Point(16, 16);
      }
   }

   public void modifyHumanDrawOptions(Level var1, int var2, int var3, HumanDrawOptions var4) {
      byte var5 = var1.getObjectRotation(var2, var3);
      var4.dir(var5).sprite(0, var5);
      switch (var5) {
         case 0:
            var4.drawOffset(0, 6);
            var4.mask(this.maskTextures[0], -16, 39);
            break;
         case 1:
            var4.rotate(-90.0F, 32, 32).drawOffset(6, 9);
            var4.mask(this.maskTextures[1], -6, -10);
            break;
         case 2:
            var4.drawOffset(0, 7);
            var4.mask(this.maskTextures[2], -16, 12);
            break;
         case 3:
            var4.rotate(90.0F, 32, 32).drawOffset(-6, 9);
            var4.mask(this.maskTextures[3], 6, -10);
      }

   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return new Rectangle(var2 * 32 + 2, var3 * 32, 28, 30);
      } else if (var4 == 1) {
         return new Rectangle(var2 * 32 + 2, var3 * 32 + 6, 30, 24);
      } else {
         return var4 == 2 ? new Rectangle(var2 * 32 + 2, var3 * 32 + 6, 28, 26) : new Rectangle(var2 * 32, var3 * 32 + 6, 30, 24);
      }
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -16, 32, 16));
      return var4;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      List var9 = this.getObjectUsers(var3, var4, var5);
      GameLight var10 = var3.getLightLevel(var4, var5);
      int var11 = var7.getTileDrawX(var4);
      int var12 = var7.getTileDrawY(var5);
      byte var13 = var3.getObjectRotation(var4, var5);
      final DrawOptionsList var14 = new DrawOptionsList();
      if (var13 == 0) {
         var14.add(this.baseTexture.initDraw().sprite(3, 3, 32).light(var10).pos(var11, var12));
      } else if (var13 == 1) {
         var14.add(this.baseTexture.initDraw().sprite(0, 1, 32, 64).light(var10).pos(var11, var12 - 32));
      } else if (var13 == 2) {
         var14.add(this.baseTexture.initDraw().sprite(2, 1, 32).light(var10).pos(var11, var12 - 32));
         var14.add(this.baseTexture.initDraw().sprite(2, 2, 32).light(var10).pos(var11, var12));
      } else {
         var14.add(this.baseTexture.initDraw().sprite(1, 0, 32, 64).light(var10).pos(var11, var12 - 32));
      }

      Iterator var15 = var9.iterator();

      while(var15.hasNext()) {
         ObjectUserMob var16 = (ObjectUserMob)var15.next();
         Point var17 = this.getMobPosSleepOffset(var3, var4, var5);
         var14.add(var16.getUserDrawOptions(var3, var4 * 32 + var17.x, var5 * 32 + var17.y, var6, var7, var8, (var4x) -> {
            if (var4x != null) {
               this.modifyHumanDrawOptions(var3, var4, var5, var4x);
            }

         }));
      }

      if (!var9.isEmpty()) {
         if (var13 == 0) {
            var14.add(this.overlayTexture.initDraw().sprite(3, 1, 32).light(var10).pos(var11, var12 - 64));
            var14.add(this.overlayTexture.initDraw().sprite(3, 2, 32).light(var10).pos(var11, var12 - 32));
            var14.add(this.overlayTexture.initDraw().sprite(3, 3, 32).light(var10).pos(var11, var12));
         } else if (var13 == 1) {
            var14.add(this.overlayTexture.initDraw().sprite(0, 1, 64).light(var10).pos(var11, var12 - 32));
         } else if (var13 == 2) {
            var14.add(this.overlayTexture.initDraw().sprite(2, 1, 32).light(var10).pos(var11, var12 - 32));
            var14.add(this.overlayTexture.initDraw().sprite(2, 2, 32).light(var10).pos(var11, var12));
            var14.add(this.overlayTexture.initDraw().sprite(2, 3, 32).light(var10).pos(var11, var12 + 32));
         } else {
            var14.add(this.overlayTexture.initDraw().sprite(0, 0, 64).light(var10).pos(var11 - 32, var12 - 32));
         }
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 20;
         }

         public void draw(TickManager var1) {
            var14.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      if (var4 == 0) {
         this.baseTexture.initDraw().sprite(3, 1, 32).alpha(var5).draw(var8, var9 - 64);
         this.baseTexture.initDraw().sprite(3, 2, 32).alpha(var5).draw(var8, var9 - 32);
         this.baseTexture.initDraw().sprite(3, 3, 32).alpha(var5).draw(var8, var9);
      } else if (var4 == 1) {
         this.baseTexture.initDraw().sprite(0, 1, 64).alpha(var5).draw(var8, var9 - 32);
      } else if (var4 == 2) {
         this.baseTexture.initDraw().sprite(2, 1, 32).alpha(var5).draw(var8, var9 - 32);
         this.baseTexture.initDraw().sprite(2, 2, 32).alpha(var5).draw(var8, var9);
         this.baseTexture.initDraw().sprite(2, 3, 32).alpha(var5).draw(var8, var9 + 32);
      } else {
         this.baseTexture.initDraw().sprite(0, 0, 64).alpha(var5).draw(var8 - 32, var9 - 32);
      }

   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "usetip");
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      super.interact(var1, var2, var3, var4);
      if (var4.isServerClient()) {
         ServerClient var5 = var4.getServerClient();
         GameMessage var6 = this.getCanSetSpawnError(var1, var2, var3, var5);
         if (var6 != null) {
            var5.sendChatMessage(var6);
            return;
         }

         if (var1.isSleepPrevented() || var1.getWorldEntity().isSleepPrevented()) {
            var5.sendChatMessage((GameMessage)(new LocalMessage("misc", "sleepprevented")));
            return;
         }

         ObjectEntity var7 = var1.entityManager.getObjectEntity(var2, var3);
         if (var7 instanceof BedObjectEntity) {
            BedObjectEntity var8 = (BedObjectEntity)var7;
            if (!var8.canUse(var5.playerMob)) {
               var5.sendChatMessage((GameMessage)(new LocalMessage("misc", "bedinuse")));
            } else if (var1.isServer()) {
               boolean var9 = this.isCurrentSpawn(var1, var2, var3, var5);
               ContainerRegistry.openAndSendContainer(var5, PacketOpenContainer.ObjectEntity(ContainerRegistry.BED_CONTAINER, var8, BedContainer.getContainerContent(var5, var9)));
            }
         }
      }

   }

   public boolean canRespawn(Level var1, int var2, int var3, ServerClient var4) {
      return this.getCanSetSpawnError(var1, var2, var3, var4) == null;
   }

   public GameMessage getCanSetSpawnError(Level var1, int var2, int var3, ServerClient var4) {
      GameMessage var5 = var1.getSetSpawnError(var2, var3, var4);
      if (var5 != null) {
         return var5;
      } else {
         return var1.isOutside(var2, var3) ? new LocalMessage("misc", "spawnhouse") : null;
      }
   }

   public Point getSpawnOffset(Level var1, int var2, int var3, ServerClient var4) {
      byte var5 = var1.getObjectRotation(var2, var3);
      ArrayList var6 = new ArrayList();
      var6.add(new Point(-1, 0));
      var6.add(new Point(1, 0));
      var6.add(new Point(-1, -1));
      var6.add(new Point(1, -1));
      var6.add(new Point(0, 1));
      var6.add(new Point(0, -2));
      var6.add(new Point(-1, -2));
      var6.add(new Point(1, -2));
      var6.add(new Point(-1, 1));
      var6.add(new Point(1, 1));
      Function var7;
      if (var5 == 0) {
         var7 = (var0) -> {
            return var0;
         };
      } else if (var5 == 1) {
         var7 = (var0) -> {
            return new Point(-var0.y, -var0.x);
         };
      } else if (var5 == 2) {
         var7 = (var0) -> {
            return new Point(-var0.x, -var0.y);
         };
      } else {
         var7 = (var0) -> {
            return new Point(var0.y, var0.x);
         };
      }

      Iterator var8 = var6.iterator();

      Point var10;
      do {
         if (!var8.hasNext()) {
            return new Point(16, 16);
         }

         Point var9 = (Point)var8.next();
         var10 = (Point)var7.apply(var9);
      } while(var4.playerMob.collidesWith(var1, (var2 + var10.x) * 32 + 16, (var3 + var10.y) * 32 + 16));

      return new Point(var10.x * 32 + 16, var10.y * 32 + 16);
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "bedtip"));
      return var3;
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new BedObjectEntity(var1, var2, var3);
   }

   public static int[] registerBed(String var0, String var1, ToolType var2, Color var3, float var4) {
      BedObject var5 = new BedObject(var1, var2, var3);
      Bed2Object var6 = new Bed2Object(var1, var2, var3);
      int var7 = ObjectRegistry.registerObject(var0, var5, var4, true);
      int var8 = ObjectRegistry.registerObject(var0 + "2", var6, 0.0F, false);
      var5.counterID = var8;
      var6.counterID = var7;
      return new int[]{var7, var8};
   }

   public static int[] registerBed(String var0, String var1, Color var2, float var3) {
      return registerBed(var0, var1, ToolType.ALL, var2, var3);
   }
}
