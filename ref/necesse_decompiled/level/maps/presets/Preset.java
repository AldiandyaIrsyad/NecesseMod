package necesse.level.maps.presets;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.LogicGateRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SignObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.multiTile.MultiTile;

public class Preset {
   public int width;
   public int height;
   public int[] tiles;
   public int[] objects;
   public byte[] objectRotations;
   public byte[] wires;
   public HashMap<Point, PresetLogicGate> logicGates;
   public ArrayList<ApplyPredicate> applyPredicates;
   public ArrayList<CustomApply> customPreApplies;
   public ArrayList<CustomApply> customApplies;
   public ArrayList<TileApplyListener> tileApplyListeners;
   public ArrayList<ObjectApplyListener> objectApplyListeners;
   public ArrayList<WireApplyListener> wireApplyListeners;

   public Preset(int var1, int var2) {
      this.logicGates = new HashMap();
      this.applyPredicates = new ArrayList();
      this.customPreApplies = new ArrayList();
      this.customApplies = new ArrayList();
      this.tileApplyListeners = new ArrayList();
      this.objectApplyListeners = new ArrayList();
      this.wireApplyListeners = new ArrayList();
      this.width = var1;
      this.height = var2;
      this.clearPreset();
   }

   public Preset(String var1) {
      this(new LoadData(var1));
   }

   public Preset(LoadData var1) {
      this(var1.getInt("width"), var1.getInt("height"));
      this.applySave(0, 0, var1);
   }

   public static Preset copyFromLevel(Level var0, int var1, int var2, int var3, int var4) {
      Preset var5 = new Preset(var3, var4);
      var5.copyFromLevel(var0, var1, var2);
      return var5;
   }

   public void clearPreset() {
      this.tiles = new int[this.width * this.height];
      this.objects = new int[this.width * this.height];
      this.objectRotations = new byte[this.width * this.height];
      this.wires = new byte[this.width * this.height];

      for(int var1 = 0; var1 < this.width; ++var1) {
         for(int var2 = 0; var2 < this.height; ++var2) {
            this.setTile(var1, var2, -1);
            this.setObject(var1, var2, -1);
         }
      }

   }

   protected Preset newObject(int var1, int var2) {
      return new Preset(var1, var2);
   }

   public Preset copy() {
      Preset var1 = this.newObject(this.width, this.height);
      var1.copyData(this);
      return var1;
   }

   private void copyData(Preset var1) {
      this.tiles = (int[])var1.tiles.clone();
      this.objects = (int[])var1.objects.clone();
      this.objectRotations = (byte[])var1.objectRotations.clone();
      this.wires = (byte[])var1.wires.clone();
      this.logicGates.putAll(var1.logicGates);
      this.applyPredicates.addAll(var1.applyPredicates);
      this.customPreApplies.addAll(var1.customPreApplies);
      this.customApplies.addAll(var1.customApplies);
      this.tileApplyListeners.addAll(var1.tileApplyListeners);
      this.objectApplyListeners.addAll(var1.objectApplyListeners);
      this.wireApplyListeners.addAll(var1.wireApplyListeners);
   }

   public final Preset tryMirrorX() {
      try {
         return this.mirrorX();
      } catch (PresetMirrorException var2) {
         return this.copy();
      }
   }

   public Preset mirrorX() throws PresetMirrorException {
      Preset var1 = this.newObject(this.width, this.height);
      var1.mirrorXData(this);
      return var1;
   }

   private void mirrorXData(Preset var1) throws PresetMirrorException {
      boolean[] var2 = new boolean[this.width * this.height];

      for(int var3 = 0; var3 < this.width; ++var3) {
         int var4 = this.getMirroredX(var3);

         for(int var5 = 0; var5 < this.height; ++var5) {
            setInt(this.tiles, this.width, var4, var5, getInt(var1.tiles, this.width, var3, var5));
            int var6 = getInt(var1.objects, this.width, var3, var5);
            if (var6 != -1) {
               GameObject var7 = ObjectRegistry.getObject(var6);
               if (var7 != null) {
                  byte var8 = getByte(var1.objectRotations, this.width, var3, var5);
                  MultiTile var9 = var7.getMultiTile(var8);
                  Point var10 = var9.getMirrorXPosOffset();
                  if (var10 == null) {
                     throw new PresetMirrorException(var7.getDisplayName() + " could not be mirrored");
                  }

                  setInt(this.objects, this.width, var4 + var10.x, var5 + var10.y, var6);
                  setByte(this.objectRotations, this.width, var4, var5, (byte)var9.getXMirrorRotation());
                  setBoolean(var2, this.width, var4, var5, true);
               } else {
                  setInt(this.objects, this.width, var4, var5, var6);
               }
            } else if (!getBoolean(var2, this.width, var4, var5)) {
               setInt(this.objects, this.width, var4, var5, var6);
            }

            setByte(this.wires, this.width, var4, var5, getByte(var1.wires, this.width, var3, var5));
         }
      }

      var1.logicGates.forEach((var1x, var2x) -> {
         this.logicGates.put(new Point(this.getMirroredX(var1x.x), var1x.y), new PresetLogicGate(var2x.logicGateID, var2x.data, !var2x.mirrorX, var2x.mirrorY, var2x.rotation));
      });
      Iterator var11 = var1.applyPredicates.iterator();

      while(var11.hasNext()) {
         ApplyPredicate var12 = (ApplyPredicate)var11.next();
         this.applyPredicates.add(var12.mirrorX(this.width));
      }

      var11 = var1.customPreApplies.iterator();

      CustomApply var13;
      while(var11.hasNext()) {
         var13 = (CustomApply)var11.next();
         this.customPreApplies.add(var13.mirrorX(this.width));
      }

      var11 = var1.customApplies.iterator();

      while(var11.hasNext()) {
         var13 = (CustomApply)var11.next();
         this.customApplies.add(var13.mirrorX(this.width));
      }

      this.tileApplyListeners.addAll(var1.tileApplyListeners);
      this.objectApplyListeners.addAll(var1.objectApplyListeners);
      this.wireApplyListeners.addAll(var1.wireApplyListeners);
   }

   public final Preset tryMirrorY() {
      try {
         return this.mirrorY();
      } catch (PresetMirrorException var2) {
         return this.copy();
      }
   }

   public Preset mirrorY() throws PresetMirrorException {
      Preset var1 = this.newObject(this.width, this.height);
      var1.mirrorYData(this);
      return var1;
   }

   private void mirrorYData(Preset var1) throws PresetMirrorException {
      boolean[] var2 = new boolean[this.width * this.height];

      for(int var3 = 0; var3 < this.height; ++var3) {
         int var4 = this.getMirroredY(var3);

         for(int var5 = 0; var5 < this.width; ++var5) {
            setInt(this.tiles, this.width, var5, var4, getInt(var1.tiles, this.width, var5, var3));
            int var6 = getInt(var1.objects, this.width, var5, var3);
            if (var6 != -1) {
               GameObject var7 = ObjectRegistry.getObject(var6);
               if (var7 != null) {
                  byte var8 = getByte(var1.objectRotations, this.width, var5, var3);
                  MultiTile var9 = var7.getMultiTile(var8);
                  Point var10 = var9.getMirrorYPosOffset();
                  if (var10 == null) {
                     throw new PresetMirrorException(var7.getDisplayName() + " could not be mirrored");
                  }

                  setInt(this.objects, this.width, var5 + var10.x, var4 + var10.y, var6);
                  setByte(this.objectRotations, this.width, var5, var4, (byte)var9.getYMirrorRotation());
                  setBoolean(var2, this.width, var5, var4, true);
               } else {
                  setInt(this.objects, this.width, var5, var4, var6);
               }
            } else if (!getBoolean(var2, this.width, var5, var4)) {
               setInt(this.objects, this.width, var5, var4, -1);
            }

            setByte(this.wires, this.width, var5, var4, getByte(var1.wires, this.width, var5, var3));
         }
      }

      var1.logicGates.forEach((var1x, var2x) -> {
         this.logicGates.put(new Point(var1x.x, this.getMirroredY(var1x.y)), new PresetLogicGate(var2x.logicGateID, var2x.data, var2x.mirrorX, !var2x.mirrorY, var2x.rotation));
      });
      Iterator var11 = var1.applyPredicates.iterator();

      while(var11.hasNext()) {
         ApplyPredicate var12 = (ApplyPredicate)var11.next();
         this.applyPredicates.add(var12.mirrorY(this.height));
      }

      var11 = var1.customPreApplies.iterator();

      CustomApply var13;
      while(var11.hasNext()) {
         var13 = (CustomApply)var11.next();
         this.customPreApplies.add(var13.mirrorY(this.height));
      }

      var11 = var1.customApplies.iterator();

      while(var11.hasNext()) {
         var13 = (CustomApply)var11.next();
         this.customApplies.add(var13.mirrorY(this.height));
      }

      this.tileApplyListeners.addAll(var1.tileApplyListeners);
      this.objectApplyListeners.addAll(var1.objectApplyListeners);
      this.wireApplyListeners.addAll(var1.wireApplyListeners);
   }

   public final Preset tryRotate(PresetRotation var1) {
      try {
         return this.rotate(var1);
      } catch (PresetRotateException var3) {
         return this.copy();
      }
   }

   public Preset rotate(PresetRotation var1) throws PresetRotateException {
      Point var2 = PresetUtils.getRotatedPoint(this.width, this.height, 0, 0, var1);
      Preset var3 = this.newObject(Math.abs(var2.x), Math.abs(var2.y));
      var3.rotateData(this, var1);
      return var3;
   }

   private void rotateData(Preset var1, PresetRotation var2) throws PresetRotateException {
      boolean[] var3 = new boolean[this.width * this.height];

      for(int var4 = 0; var4 < var1.width; ++var4) {
         for(int var5 = 0; var5 < var1.height; ++var5) {
            Point var6 = PresetUtils.getRotatedPointInSpace(var4, var5, var1.width, var1.height, var2);
            setInt(this.tiles, this.width, var6.x, var6.y, getInt(var1.tiles, var1.width, var4, var5));
            int var7 = getInt(var1.objects, var1.width, var4, var5);
            if (var7 != -1) {
               GameObject var8 = ObjectRegistry.getObject(var7);
               if (var8 != null) {
                  byte var9 = getByte(var1.objectRotations, var1.width, var4, var5);
                  MultiTile var10 = var8.getMultiTile(var9);
                  Point var11 = var10.getPresetRotationOffset(var2);
                  if (var11 == null) {
                     throw new PresetRotateException(var8.getDisplayName() + " could not be rotated");
                  }

                  setInt(this.objects, this.width, var6.x + var11.x, var6.y + var11.y, var7);
                  setByte(this.objectRotations, this.width, var6.x, var6.y, (byte)var10.getPresetRotation(var2));
                  setBoolean(var3, this.width, var6.x, var6.y, true);
               } else {
                  setInt(this.objects, this.width, var6.x, var6.y, var7);
               }
            } else if (!getBoolean(var3, this.width, var6.x, var6.y)) {
               setInt(this.objects, this.width, var6.x, var6.y, -1);
            }

            setByte(this.wires, this.width, var6.x, var6.y, getByte(var1.wires, var1.width, var4, var5));
         }
      }

      var1.logicGates.forEach((var3x, var4x) -> {
         this.logicGates.put(PresetUtils.getRotatedPointInSpace(var3x.x, var3x.y, var1.width, var1.height, var2), new PresetLogicGate(var4x.logicGateID, var4x.data, false, false, PresetRotation.addRotations(var4x.rotation, var2)));
      });
      Iterator var12 = var1.applyPredicates.iterator();

      while(var12.hasNext()) {
         ApplyPredicate var13 = (ApplyPredicate)var12.next();
         this.applyPredicates.add(var13.rotate(var2, var1.width, var1.height));
      }

      var12 = var1.customPreApplies.iterator();

      CustomApply var14;
      while(var12.hasNext()) {
         var14 = (CustomApply)var12.next();
         this.customPreApplies.add(var14.rotate(var2, var1.width, var1.height));
      }

      var12 = var1.customApplies.iterator();

      while(var12.hasNext()) {
         var14 = (CustomApply)var12.next();
         this.customApplies.add(var14.rotate(var2, var1.width, var1.height));
      }

      this.tileApplyListeners.addAll(var1.tileApplyListeners);
      this.objectApplyListeners.addAll(var1.objectApplyListeners);
      this.wireApplyListeners.addAll(var1.wireApplyListeners);
   }

   public void drawPlacePreview(Level var1, int var2, int var3, PlayerMob var4, GameCamera var5) {
      int var6;
      int var7;
      int var8;
      int var9;
      int var10;
      for(var6 = 0; var6 < this.width; ++var6) {
         var7 = var2 + var6;
         if (var7 >= 0 && var7 < var1.width) {
            for(var8 = 0; var8 < this.height; ++var8) {
               var9 = var3 + var8;
               if (var9 >= 0 && var9 < var1.height) {
                  var10 = getInt(this.tiles, this.width, var6, var8);
                  if (var10 != -1) {
                     TileRegistry.getTile(var10).drawPreview(var1, var7, var9, 0.5F, var4, var5);
                  }
               }
            }
         }
      }

      for(var6 = 0; var6 < this.width; ++var6) {
         var7 = var2 + var6;
         if (var7 >= 0 && var7 < var1.width) {
            for(var8 = 0; var8 < this.height; ++var8) {
               var9 = var3 + var8;
               if (var9 >= 0 && var9 < var1.height) {
                  var10 = getInt(this.objects, this.width, var6, var8);
                  if (var10 != -1) {
                     byte var11 = getByte(this.objectRotations, this.width, var6, var8);
                     ObjectRegistry.getObject(var10).drawPreview(var1, var7, var9, var11, 0.5F, var4, var5);
                  }
               }
            }
         }
      }

      this.logicGates.forEach((var5x, var6x) -> {
         int var7 = var2 + var5x.x;
         int var8 = var3 + var5x.y;
         if (var7 >= 0 && var7 < var1.width && var8 >= 0 && var8 < var1.height) {
            LogicGateRegistry.getLogicGate(var6x.logicGateID).drawPreview(var1, var7, var8, 0.5F, var4, var5);
         }

      });

      for(var6 = 0; var6 < this.width; ++var6) {
         var7 = var2 + var6;
         if (var7 >= 0 && var7 < var1.width) {
            for(var8 = 0; var8 < this.height; ++var8) {
               var9 = var3 + var8;
               if (var9 >= 0 && var9 < var1.height) {
                  for(var10 = 0; var10 < 4; ++var10) {
                     if (this.hasWire(var6, var8, var10)) {
                        var1.wireManager.drawWirePreset(var7, var9, var5, var10);
                     }
                  }
               }
            }
         }
      }

   }

   public LinkedList<UndoLogic> applyToLevel(Level var1, int var2, int var3) {
      LinkedList var4 = new LinkedList();
      Iterator var5 = this.customPreApplies.iterator();

      CustomApply var6;
      UndoLogic var7;
      while(var5.hasNext()) {
         var6 = (CustomApply)var5.next();
         var7 = var6.applyToLevel(var1, var2, var3);
         if (var7 != null) {
            var4.add(var7);
         }
      }

      for(int var15 = 0; var15 < this.width; ++var15) {
         int var16 = var2 + var15;
         if (var16 >= 0 && var16 < var1.width) {
            for(int var17 = 0; var17 < this.height; ++var17) {
               int var8 = var3 + var17;
               if (var8 >= 0 && var8 < var1.height) {
                  int var9 = getInt(this.tiles, this.width, var15, var17);
                  if (var9 != -1) {
                     var1.setTile(var16, var8, var9);
                     Iterator var10 = this.tileApplyListeners.iterator();

                     while(var10.hasNext()) {
                        TileApplyListener var11 = (TileApplyListener)var10.next();
                        var11.onTileApply(var1, var16, var8, var9);
                     }
                  }

                  byte var18 = getByte(this.wires, this.width, var15, var17);

                  Iterator var13;
                  int var19;
                  for(var19 = 0; var19 < 4; ++var19) {
                     if (GameMath.getBit((long)var18, var19 * 2)) {
                        boolean var12 = GameMath.getBit((long)var18, var19 * 2 + 1);
                        var1.wireManager.setWire(var16, var8, var19, var12);
                        var13 = this.wireApplyListeners.iterator();

                        while(var13.hasNext()) {
                           WireApplyListener var14 = (WireApplyListener)var13.next();
                           var14.onWireApply(var1, var16, var8, var19, var12);
                        }
                     }
                  }

                  var1.logicLayer.clearLogicGate(var16, var8);
                  var19 = getInt(this.objects, this.width, var15, var17);
                  if (var19 != -1) {
                     byte var20 = getByte(this.objectRotations, this.width, var15, var17);
                     var1.setObject(var16, var8, var19, var20);
                     var13 = this.objectApplyListeners.iterator();

                     while(var13.hasNext()) {
                        ObjectApplyListener var21 = (ObjectApplyListener)var13.next();
                        var21.onObjectApply(var1, var16, var8, var19, var20);
                     }
                  }
               }
            }
         }
      }

      this.logicGates.forEach((var3x, var4x) -> {
         int var5 = var2 + var3x.x;
         int var6 = var3 + var3x.y;
         if (var5 >= 0 && var5 < var1.width && var6 >= 0 && var6 < var1.height) {
            var4x.applyToLevel(var1, var5, var6);
         }

      });
      var5 = this.customApplies.iterator();

      while(var5.hasNext()) {
         var6 = (CustomApply)var5.next();
         var7 = var6.applyToLevel(var1, var2, var3);
         if (var7 != null) {
            var4.add(var7);
         }
      }

      return var4;
   }

   public void applyToLevelCentered(Level var1, int var2, int var3) {
      this.applyToLevel(var1, var2 - this.width / 2, var3 - this.height / 2);
   }

   public boolean canApplyToLevel(Level var1, int var2, int var3) {
      Iterator var4 = this.applyPredicates.iterator();

      ApplyPredicate var5;
      do {
         if (!var4.hasNext()) {
            return true;
         }

         var5 = (ApplyPredicate)var4.next();
      } while(var5.canApplyToLevel(var1, var2, var3));

      return false;
   }

   public boolean canApplyToLevelCentered(Level var1, int var2, int var3) {
      return this.canApplyToLevel(var1, var2 - this.width / 2, var3 - this.height / 2);
   }

   public Preset subPreset(int var1, int var2, int var3, int var4) {
      var3 = Math.min(this.width - var1, var3);
      var4 = Math.min(this.height - var2, var4);
      Preset var5 = new Preset(var3, var4);

      for(int var6 = 0; var6 < var3; ++var6) {
         for(int var7 = 0; var7 < var4; ++var7) {
            setInt(var5.tiles, var3, var6, var7, getInt(this.tiles, this.width, var1 + var6, var2 + var7));
            setInt(var5.objects, var3, var6, var7, getInt(this.objects, this.width, var1 + var6, var2 + var7));
            setByte(var5.objectRotations, var3, var6, var7, getByte(this.objectRotations, this.width, var1 + var6, var2 + var7));
            setByte(var5.wires, var3, var6, var7, getByte(this.wires, this.width, var1 + var6, var2 + var7));
         }
      }

      return var5;
   }

   public void applyScript(String var1) {
      this.applyScript(0, 0, var1);
   }

   public void applyScript(int var1, int var2, String var3) {
      this.applySave(var1, var2, new LoadData(var3));
   }

   public void applySave(int var1, int var2, LoadData var3) {
      int var4 = var3.getInt("width");
      int var5 = var3.getInt("height");
      int[] var6 = null;
      if (var3.hasLoadDataByName("tiles")) {
         var6 = var3.getIntArray("tiles");
      }

      String[] var8;
      int var10;
      if (var6 != null) {
         String[] var7;
         if (var3.hasLoadDataByName("tileIDs")) {
            var7 = new String[TileRegistry.getTileStringIDs().length];
            var8 = var3.getStringArray("tileIDs");

            for(int var9 = 0; var9 < var8.length; var9 += 2) {
               try {
                  var10 = Integer.parseInt(var8[var9]);
                  if (var10 > 0) {
                     if (var7.length <= var10) {
                        var7 = (String[])Arrays.copyOf(var7, var10 + 1);
                     }

                     var7[var10] = var8[var9 + 1];
                  }
               } catch (NumberFormatException var23) {
               }
            }

            VersionMigration.convertArray(var6, var7, TileRegistry.getTileStringIDs(), -1, VersionMigration.oldTileStringIDs);
         } else if (var3.hasLoadDataByName("tileData")) {
            var7 = var3.getStringArray("tileData");
            var8 = TileRegistry.getTileStringIDs();
            if (!Arrays.equals(var7, var8)) {
               VersionMigration.convertArray(var6, var7, var8, -1, VersionMigration.oldTileStringIDs);
            }
         }
      }

      int[] var24 = null;
      if (var3.hasLoadDataByName("objects")) {
         var24 = var3.getIntArray("objects");
      }

      int var11;
      if (var24 != null) {
         String[] var25;
         if (var3.hasLoadDataByName("objectIDs")) {
            var8 = new String[TileRegistry.getTileStringIDs().length];
            var25 = var3.getStringArray("objectIDs");

            for(var10 = 0; var10 < var25.length; var10 += 2) {
               try {
                  var11 = Integer.parseInt(var25[var10]);
                  if (var11 > 0) {
                     if (var8.length <= var11) {
                        var8 = (String[])Arrays.copyOf(var8, var11 + 1);
                     }

                     var8[var11] = var25[var10 + 1];
                  }
               } catch (NumberFormatException var22) {
               }
            }

            VersionMigration.convertArray(var24, var8, ObjectRegistry.getObjectStringIDs(), -1, VersionMigration.oldObjectStringIDs);
         } else if (var3.hasLoadDataByName("objectData")) {
            var8 = var3.getStringArray("objectData");
            var25 = ObjectRegistry.getObjectStringIDs();
            if (!Arrays.equals(var8, var25)) {
               VersionMigration.convertArray(var24, var8, var25, -1, VersionMigration.oldObjectStringIDs);
            }
         }
      }

      byte[] var26 = null;
      if (var3.hasLoadDataByName("rotations")) {
         var26 = var3.getByteArray("rotations");
      }

      byte[] var27 = null;
      if (var3.hasLoadDataByName("wire")) {
         var27 = var3.getByteArray("wire");
      }

      for(var10 = var1; var10 < var1 + var4; ++var10) {
         if (var10 <= this.width) {
            for(var11 = var2; var11 < var2 + var5; ++var11) {
               if (var10 <= this.width && var11 <= this.height) {
                  if (var6 != null) {
                     this.setTile(var10, var11, getInt(var6, var4, var10 - var1, var11 - var2));
                  }

                  if (var24 != null) {
                     this.setObject(var10, var11, getInt(var24, var4, var10 - var1, var11 - var2));
                  }

                  if (var26 != null) {
                     this.setRotation(var10, var11, getByte(var26, var4, var10 - var1, var11 - var2));
                  }

                  if (var27 != null) {
                     this.setWireData(var10, var11, getByte(var27, var4, var10 - var1, var11 - var2));
                  }
               }
            }
         }
      }

      LoadData var28 = var3.getFirstLoadDataByName("logicGates");
      if (var28 != null && var28.isArray()) {
         Iterator var29 = var28.getLoadDataByName("gate").iterator();

         while(var29.hasNext()) {
            LoadData var12 = (LoadData)var29.next();

            try {
               int var13 = var12.getInt("tileX");
               int var14 = var12.getInt("tileY");
               String var15 = var12.getUnsafeString("stringID");
               int var16 = LogicGateRegistry.getLogicGateID(var15);
               if (var16 != -1) {
                  LoadData var17 = var12.getFirstLoadDataByName("data");
                  boolean var18 = var12.getBoolean("mirrorX", false, false);
                  boolean var19 = var12.getBoolean("mirrorY", false, false);
                  int var20 = var12.getInt("rotation", 0, false);
                  this.logicGates.put(new Point(var13, var14), new PresetLogicGate(var16, var17 == null ? null : var17.toSaveData(), var18, var19, PresetRotation.toRotationAngle(var20)));
               }
            } catch (Exception var21) {
            }
         }
      }

   }

   public SaveData getSaveData(String var1) {
      return this.getSaveData(var1, new PresetCopyFilter());
   }

   public SaveData getSaveData() {
      return this.getSaveData("PRESET");
   }

   public SaveData getSaveData(String var1, PresetCopyFilter var2) {
      SaveData var3 = new SaveData(var1);
      var3.addInt("width", this.width);
      var3.addInt("height", this.height);
      HashSet var4;
      int[] var5;
      int var6;
      int var7;
      int var8;
      String[] var10;
      Iterator var11;
      if (var2.acceptTiles) {
         var4 = new HashSet();
         var5 = this.tiles;
         var6 = var5.length;

         for(var7 = 0; var7 < var6; ++var7) {
            var8 = var5[var7];
            if (var8 >= 0) {
               var4.add(var8);
            }
         }

         if (!var4.isEmpty()) {
            var10 = new String[var4.size() * 2];
            var6 = 0;

            for(var11 = var4.iterator(); var11.hasNext(); var10[var6++] = TileRegistry.getTile(var8).getStringID()) {
               var8 = (Integer)var11.next();
               var10[var6++] = Integer.toString(var8);
            }

            var3.addStringArray("tileIDs", var10);
         }

         var3.addIntArray("tiles", this.tiles);
      }

      if (var2.acceptObjects) {
         var4 = new HashSet();
         var5 = this.objects;
         var6 = var5.length;

         for(var7 = 0; var7 < var6; ++var7) {
            var8 = var5[var7];
            if (var8 >= 0) {
               var4.add(var8);
            }
         }

         if (!var4.isEmpty()) {
            var10 = new String[var4.size() * 2];
            var6 = 0;

            for(var11 = var4.iterator(); var11.hasNext(); var10[var6++] = ObjectRegistry.getObject(var8).getStringID()) {
               var8 = (Integer)var11.next();
               var10[var6++] = Integer.toString(var8);
            }

            var3.addStringArray("objectIDs", var10);
         }

         var3.addIntArray("objects", this.objects);
         var3.addByteArray("rotations", this.objectRotations);
      }

      if (var2.acceptWires) {
         var3.addByteArray("wire", this.wires);
         if (!this.logicGates.isEmpty()) {
            SaveData var9 = new SaveData("logicGates");
            this.logicGates.forEach((var1x, var2x) -> {
               SaveData var3 = new SaveData("gate");
               var3.addInt("tileX", var1x.x);
               var3.addInt("tileY", var1x.y);
               if (var2x.mirrorX) {
                  var3.addBoolean("mirrorX", true);
               }

               if (var2x.mirrorY) {
                  var3.addBoolean("mirrorY", true);
               }

               if (var2x.rotation != null) {
                  var3.addInt("rotation", var2x.rotation.dirOffset);
               }

               var3.addUnsafeString("stringID", LogicGateRegistry.getLogicGateStringID(var2x.logicGateID));
               if (var2x.data != null) {
                  var3.addSaveData(var2x.data);
               }

               var9.addSaveData(var3);
            });
            var3.addSaveData(var9);
         }
      }

      return var3;
   }

   public SaveData getSaveData(PresetCopyFilter var1) {
      return this.getSaveData("PRESET", var1);
   }

   public String getScript() {
      return this.getSaveData().getScript();
   }

   public void copyFromLevel(Level var1, int var2, int var3) {
      this.copyFromLevel(var1, var2, var3, new PresetCopyFilter());
   }

   public void copyFromLevel(Level var1, int var2, int var3, PresetCopyFilter var4) {
      for(int var5 = 0; var5 < this.width; ++var5) {
         int var6 = var5 + var2;

         for(int var7 = 0; var7 < this.height; ++var7) {
            int var8 = var7 + var3;
            if (var4.acceptTiles) {
               this.setTile(var5, var7, var1.getTileID(var6, var8));
            }

            if (var4.acceptObjects) {
               this.setObject(var5, var7, var1.getObjectID(var6, var8));
               this.setRotation(var5, var7, var1.getObjectRotation(var6, var8));
            }

            if (var4.acceptWires) {
               for(int var9 = 0; var9 < 4; ++var9) {
                  this.putWire(var5, var7, var9, var1.wireManager.hasWire(var6, var8, var9));
               }

               if (var1.logicLayer.hasGate(var6, var8)) {
                  this.setLogicGate(var5, var7, var1.logicLayer.getEntity(var6, var8));
               } else {
                  this.setLogicGate(var5, var7, (LogicGateEntity)null);
               }
            }
         }
      }

   }

   public static void setInt(int[] var0, int var1, int var2, int var3, int var4) {
      if (var2 + var3 * var1 < var0.length) {
         var0[var2 + var3 * var1] = var4;
      }
   }

   public static int getInt(int[] var0, int var1, int var2, int var3) {
      return var2 + var3 * var1 >= var0.length ? -1 : var0[var2 + var3 * var1];
   }

   public static void setByte(byte[] var0, int var1, int var2, int var3, byte var4) {
      if (var2 + var3 * var1 < var0.length) {
         var0[var2 + var3 * var1] = var4;
      }
   }

   public static byte getByte(byte[] var0, int var1, int var2, int var3) {
      return var2 + var3 * var1 >= var0.length ? -1 : var0[var2 + var3 * var1];
   }

   public static void setBoolean(boolean[] var0, int var1, int var2, int var3, boolean var4) {
      if (var2 + var3 * var1 < var0.length) {
         var0[var2 + var3 * var1] = var4;
      }
   }

   public static boolean getBoolean(boolean[] var0, int var1, int var2, int var3) {
      return var2 + var3 * var1 >= var0.length ? false : var0[var2 + var3 * var1];
   }

   public void setTile(int var1, int var2, int var3) {
      setInt(this.tiles, this.width, var1, var2, var3);
   }

   public void setObject(int var1, int var2, int var3, int var4) {
      setInt(this.objects, this.width, var1, var2, var3);
      setByte(this.objectRotations, this.width, var1, var2, (byte)var4);
   }

   public void setObject(int var1, int var2, int var3) {
      this.setObject(var1, var2, var3, 0);
   }

   public void setRotation(int var1, int var2, int var3) {
      setByte(this.objectRotations, this.width, var1, var2, (byte)var3);
   }

   public void setWireData(int var1, int var2, byte var3) {
      setByte(this.wires, this.width, var1, var2, var3);
   }

   public void putWire(int var1, int var2, int var3, boolean var4) {
      byte var5 = this.getWireData(var1, var2);
      var5 = GameMath.setBit(var5, var3 * 2, true);
      var5 = GameMath.setBit(var5, var3 * 2 + 1, var4);
      this.setWireData(var1, var2, var5);
   }

   public void clearWire(int var1, int var2, int var3) {
      this.setWireData(var1, var2, GameMath.setBit(this.getWireData(var1, var2), var3 * 2, false));
   }

   public boolean hasWire(int var1, int var2, int var3) {
      byte var4 = this.getWireData(var1, var2);
      return GameMath.getBit((long)var4, var3 * 2) && GameMath.getBit((long)var4, var3 * 2 + 1);
   }

   public boolean doesSetWire(int var1, int var2, int var3) {
      return GameMath.getBit((long)this.getWireData(var1, var2), var3 * 2);
   }

   public int getTile(int var1, int var2) {
      return getInt(this.tiles, this.width, var1, var2);
   }

   public int getObject(int var1, int var2) {
      return getInt(this.objects, this.width, var1, var2);
   }

   public byte getObjectRotation(int var1, int var2) {
      return getByte(this.objectRotations, this.width, var1, var2);
   }

   public byte getWireData(int var1, int var2) {
      return getByte(this.wires, this.width, var1, var2);
   }

   public void fillTile(int var1, int var2, int var3, int var4, int var5) {
      for(int var6 = var1; var6 < var3 + var1; ++var6) {
         for(int var7 = var2; var7 < var4 + var2; ++var7) {
            this.setTile(var6, var7, var5);
         }
      }

   }

   public void fillObject(int var1, int var2, int var3, int var4, int var5, int var6) {
      for(int var7 = var1; var7 < var3 + var1; ++var7) {
         for(int var8 = var2; var8 < var4 + var2; ++var8) {
            this.setObject(var7, var8, var5, var6);
         }
      }

   }

   public void fillObject(int var1, int var2, int var3, int var4, int var5) {
      this.fillObject(var1, var2, var3, var4, var5, 0);
   }

   public void boxObject(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.fillObject(var1, var2, var3, 1, var5, var6);
      this.fillObject(var1, var2, 1, var4, var5, var6);
      this.fillObject(var1, var2 + var4 - 1, var3, 1, var5, var6);
      this.fillObject(var1 + var3 - 1, var2, 1, var4, var5, var6);
   }

   public void boxObject(int var1, int var2, int var3, int var4, int var5) {
      this.boxObject(var1, var2, var3, var4, var5, 0);
   }

   public void boxTile(int var1, int var2, int var3, int var4, int var5) {
      this.fillTile(var1, var2, var3, 1, var5);
      this.fillTile(var1, var2, 1, var4, var5);
      this.fillTile(var1, var2 + var4 - 1, var3, 1, var5);
      this.fillTile(var1 + var3 - 1, var2, 1, var4, var5);
   }

   public void replaceTile(String var1, String var2) {
      this.replaceTile(TileRegistry.getTileID(var1), TileRegistry.getTileID(var2));
   }

   public void replaceTile(int var1, int var2) {
      for(int var3 = 0; var3 < this.tiles.length; ++var3) {
         if (this.tiles[var3] == var1) {
            this.tiles[var3] = var2;
         }
      }

   }

   public void replaceObject(String var1, String var2) {
      this.replaceObject(ObjectRegistry.getObjectID(var1), ObjectRegistry.getObjectID(var2), -1);
   }

   public void replaceObject(int var1, int var2) {
      this.replaceObject(var1, var2, -1);
   }

   public void replaceObject(int var1, int var2, int var3) {
      for(int var4 = 0; var4 < this.objects.length; ++var4) {
         if (this.objects[var4] == var1) {
            this.objects[var4] = var2;
            if (var3 != -1) {
               this.objectRotations[var4] = (byte)var3;
            }
         }
      }

   }

   public void iteratePreset(BiConsumer<Integer, Integer> var1) {
      for(int var2 = 0; var2 < this.width; ++var2) {
         for(int var3 = 0; var3 < this.height; ++var3) {
            var1.accept(var2, var3);
         }
      }

   }

   public void iteratePreset(BiFunction<Integer, Integer, Boolean> var1, BiConsumer<Integer, Integer> var2) {
      this.iteratePreset((var2x, var3) -> {
         if ((Boolean)var1.apply(var2x, var3)) {
            var2.accept(var2x, var3);
         }

      });
   }

   public void setLogicGate(int var1, int var2, LogicGateEntity var3) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         if (var3 == null) {
            this.logicGates.remove(new Point(var1, var2));
         } else {
            SaveData var4 = new SaveData("data");
            var3.addSaveData(var4);
            this.logicGates.put(new Point(var1, var2), new PresetLogicGate(var3.logicGateID, var4, false, false, (PresetRotation)null));
         }

      } else {
         throw new IllegalArgumentException("Cannot add logic gate outside preset bounds");
      }
   }

   public void onTileApply(TileApplyListener var1) {
      Objects.requireNonNull(var1);
      this.tileApplyListeners.add(var1);
   }

   public void onObjectApply(ObjectApplyListener var1) {
      Objects.requireNonNull(var1);
      this.objectApplyListeners.add(var1);
   }

   public void onWireApply(WireApplyListener var1) {
      Objects.requireNonNull(var1);
      this.wireApplyListeners.add(var1);
   }

   public void addCanApplyPredicate(ApplyPredicate var1) {
      Objects.requireNonNull(var1);
      this.applyPredicates.add(var1);
   }

   public void addCanApplyPredicate(int var1, int var2, int var3, ApplyPredicateFunction var4) {
      Objects.requireNonNull(var4);
      this.addCanApplyPredicate(new ApplyTilePredicate(var1, var2, var3, var4));
   }

   public void addCanApplyAreaPredicate(int var1, int var2, int var3, int var4, int var5, ApplyAreaPredicateFunction var6) {
      Objects.requireNonNull(var6);
      this.addCanApplyPredicate(new ApplyAreaPredicate(var1, var2, var3, var4, var5, var6));
   }

   public void addCanApplyRectPredicate(int var1, int var2, int var3, int var4, int var5, ApplyAreaPredicateFunction var6) {
      this.addCanApplyAreaPredicate(var1, var2, var1 + var3 - 1, var2 + var4 - 1, var5, var6);
   }

   public void addCanApplyAreaEachPredicate(int var1, int var2, int var3, int var4, int var5, ApplyPredicateFunction var6) {
      Objects.requireNonNull(var6);
      this.addCanApplyPredicate(new ApplyAreaPredicate(var1, var2, var3, var4, var5, var6));
   }

   public void addCanApplyRectEachPredicate(int var1, int var2, int var3, int var4, int var5, ApplyPredicateFunction var6) {
      this.addCanApplyAreaEachPredicate(var1, var2, var1 + var3 - 1, var2 + var4 - 1, var5, var6);
   }

   public void addCustomPreApply(CustomApply var1) {
      Objects.requireNonNull(var1);
      this.customPreApplies.add(var1);
   }

   public void addCustomPreApply(int var1, int var2, int var3, CustomApplyFunction var4) {
      Objects.requireNonNull(var4);
      this.addCustomPreApply(new CustomApplyTile(var1, var2, var3, var4));
   }

   public void addCustomPreApplyArea(int var1, int var2, int var3, int var4, int var5, CustomApplyAreaFunction var6) {
      Objects.requireNonNull(var6);
      this.addCustomPreApply(new CustomApplyArea(var1, var2, var3, var4, var5, var6));
   }

   public void addCustomPreApplyRect(int var1, int var2, int var3, int var4, int var5, CustomApplyAreaFunction var6) {
      this.addCustomPreApplyArea(var1, var2, var1 + var3 - 1, var2 + var4 - 1, var5, var6);
   }

   public void addCustomPreApplyAreaEach(int var1, int var2, int var3, int var4, int var5, CustomApplyFunction var6) {
      Objects.requireNonNull(var6);
      this.addCustomPreApply(new CustomApplyArea(var1, var2, var3, var4, var5, var6));
   }

   public void addCustomPreApplyRectEach(int var1, int var2, int var3, int var4, int var5, CustomApplyFunction var6) {
      this.addCustomPreApplyAreaEach(var1, var2, var1 + var3 - 1, var2 + var4 - 1, var5, var6);
   }

   public void addCustomApply(CustomApply var1) {
      Objects.requireNonNull(var1);
      this.customApplies.add(var1);
   }

   public void addCustomApply(int var1, int var2, int var3, CustomApplyFunction var4) {
      Objects.requireNonNull(var4);
      this.addCustomApply(new CustomApplyTile(var1, var2, var3, var4));
   }

   public void addCustomApplyArea(int var1, int var2, int var3, int var4, int var5, CustomApplyAreaFunction var6) {
      Objects.requireNonNull(var6);
      this.addCustomApply(new CustomApplyArea(var1, var2, var3, var4, var5, var6));
   }

   public void addCustomApplyRect(int var1, int var2, int var3, int var4, int var5, CustomApplyAreaFunction var6) {
      this.addCustomApplyArea(var1, var2, var1 + var3 - 1, var2 + var4 - 1, var5, var6);
   }

   public void addCustomApplyAreaEach(int var1, int var2, int var3, int var4, int var5, CustomApplyFunction var6) {
      Objects.requireNonNull(var6);
      this.addCustomApply(new CustomApplyArea(var1, var2, var3, var4, var5, var6));
   }

   public void addCustomApplyRectEach(int var1, int var2, int var3, int var4, int var5, CustomApplyFunction var6) {
      this.addCustomApplyAreaEach(var1, var2, var1 + var3 - 1, var2 + var4 - 1, var5, var6);
   }

   public <T extends Mob> void addMob(Function<Level, T> var1, int var2, int var3, Consumer<T> var4) {
      this.addCustomApply(var2, var3, 0, (var2x, var3x, var4x, var5) -> {
         if (!var2x.isClient()) {
            Mob var6 = (Mob)var1.apply(var2x);
            if (var6 != null) {
               var2x.entityManager.addMob(var6, (float)(var3x * 32 + 16), (float)(var4x * 32 + 16));
               var4.accept(var6);
               return (var1x, var2, var3) -> {
                  var6.remove();
               };
            }
         }

         return null;
      });
   }

   public <T extends Mob> void addMob(String var1, int var2, int var3, Class<T> var4, Consumer<T> var5) {
      this.addMob((var2x) -> {
         return (Mob)var4.cast(MobRegistry.getMob(var1, var2x));
      }, var2, var3, var5);
   }

   public void addMob(String var1, int var2, int var3, Consumer<Mob> var4) {
      this.addMob(var1, var2, var3, Mob.class, var4);
   }

   public void addMob(String var1, int var2, int var3, boolean var4) {
      this.addMob(var1, var2, var3, (var1x) -> {
         var1x.canDespawn = var4;
      });
   }

   public void addInventory(LootTable var1, GameRandom var2, int var3, int var4, Object... var5) {
      this.addCustomApply(var3, var4, 0, (var3x, var4x, var5x, var6) -> {
         var1.applyToLevel(var2, (Float)var3x.buffManager.getModifier(LevelModifiers.LOOT), var3x, var4x, var5x, GameUtils.concat(new Object[]{var3x}, var5));
         return null;
      });
   }

   public void addSign(String var1, int var2, int var3, int var4, int var5) {
      this.setObject(var2, var3, var4, var5);
      this.addCustomApply(var2, var3, 0, (var1x, var2x, var3x, var4x) -> {
         try {
            ObjectEntity var5 = var1x.entityManager.getObjectEntity(var2x, var3x);
            if (var5 instanceof SignObjectEntity) {
               ((SignObjectEntity)var5).setText(var1);
            } else if (var1x.isServer()) {
               throw new NullPointerException("Could not find a sign objectEntity for preset at " + var2x + ", " + var3x);
            }
         } catch (Exception var6) {
            System.err.println(var6.getMessage());
         }

         return null;
      });
   }

   public void addSign(String var1, int var2, int var3, int var4) {
      this.addSign(var1, var2, var3, var4, this.getObjectRotation(var2, var3));
   }

   public Point getMirroredPoint(int var1, int var2, boolean var3, boolean var4) {
      return PresetUtils.getMirroredPoint(var1, var2, var3, var4, this.width, this.height);
   }

   public int getMirroredX(int var1) {
      return PresetUtils.getMirroredValue(var1, this.width);
   }

   public int getMirroredY(int var1) {
      return PresetUtils.getMirroredValue(var1, this.height);
   }

   public interface ApplyPredicate {
      boolean canApplyToLevel(Level var1, int var2, int var3);

      ApplyPredicate mirrorX(int var1) throws PresetMirrorException;

      ApplyPredicate mirrorY(int var1) throws PresetMirrorException;

      ApplyPredicate rotate(PresetRotation var1, int var2, int var3) throws PresetRotateException;
   }

   public interface CustomApply {
      UndoLogic applyToLevel(Level var1, int var2, int var3);

      CustomApply mirrorX(int var1) throws PresetMirrorException;

      CustomApply mirrorY(int var1) throws PresetMirrorException;

      CustomApply rotate(PresetRotation var1, int var2, int var3) throws PresetRotateException;
   }

   @FunctionalInterface
   public interface UndoLogic {
      void applyUndo(Level var1, int var2, int var3);
   }

   @FunctionalInterface
   public interface TileApplyListener {
      void onTileApply(Level var1, int var2, int var3, int var4);
   }

   @FunctionalInterface
   public interface WireApplyListener {
      void onWireApply(Level var1, int var2, int var3, int var4, boolean var5);
   }

   @FunctionalInterface
   public interface ObjectApplyListener {
      void onObjectApply(Level var1, int var2, int var3, int var4, int var5);
   }

   public static class PresetLogicGate {
      public final int logicGateID;
      public final SaveData data;
      public final boolean mirrorX;
      public final boolean mirrorY;
      public final PresetRotation rotation;

      public PresetLogicGate(int var1, SaveData var2, boolean var3, boolean var4, PresetRotation var5) {
         if (var2 != null && !var2.toLoadData().getName().equals("data")) {
            throw new IllegalArgumentException("Data name must be \"data\"");
         } else {
            this.logicGateID = var1;
            this.data = var2;
            this.mirrorX = var3;
            this.mirrorY = var4;
            this.rotation = var5;
         }
      }

      public void applyToLevel(Level var1, int var2, int var3) {
         var1.logicLayer.setLogicGate(var2, var3, this.logicGateID, (PacketReader)null);
         LogicGateEntity var4 = var1.logicLayer.getEntity(var2, var3);
         if (var4 != null) {
            var4.applyLoadData(this.data.toLoadData());
            var4.applyPresetRotation(this.mirrorX, this.mirrorY, this.rotation);
         }

      }

      public boolean canPlace(Level var1, int var2, int var3) {
         return LogicGateRegistry.getLogicGate(this.logicGateID).canPlace(var1, var2, var3) == null;
      }
   }

   public static class ApplyTilePredicate implements ApplyPredicate {
      public final int tileX;
      public final int tileY;
      public final int dir;
      public final ApplyPredicateFunction applyFunction;

      public ApplyTilePredicate(int var1, int var2, int var3, ApplyPredicateFunction var4) {
         Objects.requireNonNull(var4);
         this.tileX = var1;
         this.tileY = var2;
         this.dir = var3;
         this.applyFunction = var4;
      }

      public boolean canApplyToLevel(Level var1, int var2, int var3) {
         return this.applyFunction.canApplyToLevel(var1, var2 + this.tileX, var3 + this.tileY, this.dir);
      }

      public ApplyPredicate mirrorX(int var1) {
         int var2 = this.dir != 1 && this.dir != 3 ? this.dir : (this.dir + 2) % 4;
         return new ApplyTilePredicate(PresetUtils.getMirroredValue(this.tileX, var1), this.tileY, var2, this.applyFunction);
      }

      public ApplyPredicate mirrorY(int var1) {
         int var2 = this.dir != 0 && this.dir != 2 ? this.dir : (this.dir + 2) % 4;
         return new ApplyTilePredicate(this.tileX, PresetUtils.getMirroredValue(this.tileY, var1), var2, this.applyFunction);
      }

      public ApplyPredicate rotate(PresetRotation var1, int var2, int var3) {
         int var4 = (this.dir + (var1 == null ? 0 : var1.dirOffset)) % 4;
         Point var5 = PresetUtils.getRotatedPointInSpace(this.tileX, this.tileY, var2, var3, var1);
         return new ApplyTilePredicate(var5.x, var5.y, var4, this.applyFunction);
      }
   }

   @FunctionalInterface
   public interface ApplyPredicateFunction {
      boolean canApplyToLevel(Level var1, int var2, int var3, int var4);
   }

   public static class ApplyAreaPredicate implements ApplyPredicate {
      public final int tileX1;
      public final int tileY1;
      public final int tileX2;
      public final int tileY2;
      public final int dir;
      public final ApplyAreaPredicateFunction applyFunction;

      public ApplyAreaPredicate(int var1, int var2, int var3, int var4, int var5, ApplyAreaPredicateFunction var6) {
         Objects.requireNonNull(var6);
         this.tileX1 = var1;
         this.tileY1 = var2;
         this.tileX2 = var3;
         this.tileY2 = var4;
         this.dir = var5;
         this.applyFunction = var6;
      }

      public ApplyAreaPredicate(int var1, int var2, int var3, int var4, int var5, ApplyPredicateFunction var6) {
         this(var1, var2, var3, var4, var5, (var1x, var2x, var3x, var4x, var5x, var6x) -> {
            for(int var7 = var2x; var7 <= var4x; ++var7) {
               for(int var8 = var3x; var8 <= var5x; ++var8) {
                  if (!var6.canApplyToLevel(var1x, var7, var8, var6x)) {
                     return false;
                  }
               }
            }

            return true;
         });
         Objects.requireNonNull(var6);
      }

      public boolean canApplyToLevel(Level var1, int var2, int var3) {
         int var4 = var2 + Math.min(this.tileX1, this.tileX2);
         int var5 = var2 + Math.max(this.tileX1, this.tileX2);
         int var6 = var3 + Math.min(this.tileY1, this.tileY2);
         int var7 = var3 + Math.max(this.tileY1, this.tileY2);
         return this.applyFunction.canApplyToLevel(var1, var4, var6, var5, var7, this.dir);
      }

      public ApplyPredicate mirrorX(int var1) {
         int var2 = this.dir != 1 && this.dir != 3 ? this.dir : (this.dir + 2) % 4;
         return new ApplyAreaPredicate(PresetUtils.getMirroredValue(this.tileX1, var1), this.tileY1, PresetUtils.getMirroredValue(this.tileX2, var1), this.tileY2, var2, this.applyFunction);
      }

      public ApplyPredicate mirrorY(int var1) {
         int var2 = this.dir != 0 && this.dir != 2 ? this.dir : (this.dir + 2) % 4;
         return new ApplyAreaPredicate(this.tileX1, PresetUtils.getMirroredValue(this.tileY1, var1), this.tileX2, PresetUtils.getMirroredValue(this.tileY2, var1), var2, this.applyFunction);
      }

      public ApplyPredicate rotate(PresetRotation var1, int var2, int var3) {
         int var4 = (this.dir + (var1 == null ? 0 : var1.dirOffset)) % 4;
         Point var5 = PresetUtils.getRotatedPointInSpace(this.tileX1, this.tileY1, var2, var3, var1);
         Point var6 = PresetUtils.getRotatedPointInSpace(this.tileX2, this.tileY2, var2, var3, var1);
         return new ApplyAreaPredicate(var5.x, var5.y, var6.x, var6.y, var4, this.applyFunction);
      }
   }

   @FunctionalInterface
   public interface ApplyAreaPredicateFunction {
      boolean canApplyToLevel(Level var1, int var2, int var3, int var4, int var5, int var6);
   }

   public static class CustomApplyTile implements CustomApply {
      public final int tileX;
      public final int tileY;
      public final int dir;
      public final CustomApplyFunction applyFunction;

      public CustomApplyTile(int var1, int var2, int var3, CustomApplyFunction var4) {
         Objects.requireNonNull(var4);
         this.tileX = var1;
         this.tileY = var2;
         this.dir = var3;
         this.applyFunction = var4;
      }

      public UndoLogic applyToLevel(Level var1, int var2, int var3) {
         return this.applyFunction.applyToLevel(var1, var2 + this.tileX, var3 + this.tileY, this.dir);
      }

      public CustomApply mirrorX(int var1) {
         int var2 = this.dir != 1 && this.dir != 3 ? this.dir : (this.dir + 2) % 4;
         return new CustomApplyTile(PresetUtils.getMirroredValue(this.tileX, var1), this.tileY, var2, this.applyFunction);
      }

      public CustomApply mirrorY(int var1) {
         int var2 = this.dir != 0 && this.dir != 2 ? this.dir : (this.dir + 2) % 4;
         return new CustomApplyTile(this.tileX, PresetUtils.getMirroredValue(this.tileY, var1), var2, this.applyFunction);
      }

      public CustomApply rotate(PresetRotation var1, int var2, int var3) {
         int var4 = (this.dir + (var1 == null ? 0 : var1.dirOffset)) % 4;
         Point var5 = PresetUtils.getRotatedPointInSpace(this.tileX, this.tileY, var2, var3, var1);
         return new CustomApplyTile(var5.x, var5.y, var4, this.applyFunction);
      }
   }

   @FunctionalInterface
   public interface CustomApplyFunction {
      UndoLogic applyToLevel(Level var1, int var2, int var3, int var4);
   }

   public static class CustomApplyArea implements CustomApply {
      public final int tileX1;
      public final int tileY1;
      public final int tileX2;
      public final int tileY2;
      public final int dir;
      public final CustomApplyAreaFunction applyFunction;

      public CustomApplyArea(int var1, int var2, int var3, int var4, int var5, CustomApplyAreaFunction var6) {
         Objects.requireNonNull(var6);
         this.tileX1 = var1;
         this.tileY1 = var2;
         this.tileX2 = var3;
         this.tileY2 = var4;
         this.dir = var5;
         this.applyFunction = var6;
      }

      public CustomApplyArea(int var1, int var2, int var3, int var4, int var5, CustomApplyFunction var6) {
         this(var1, var2, var3, var4, var5, (var1x, var2x, var3x, var4x, var5x, var6x) -> {
            LinkedList var7 = new LinkedList();

            for(int var8 = var2x; var8 <= var4x; ++var8) {
               for(int var9 = var3x; var9 <= var5x; ++var9) {
                  UndoLogic var10 = var6.applyToLevel(var1x, var8, var9, var6x);
                  if (var10 != null) {
                     var7.add(var10);
                  }
               }
            }

            return (var2, var3, var4) -> {
               var7.forEach((var3x) -> {
                  var3x.applyUndo(var1x, var3, var4);
               });
            };
         });
         Objects.requireNonNull(var6);
      }

      public UndoLogic applyToLevel(Level var1, int var2, int var3) {
         int var4 = var2 + Math.min(this.tileX1, this.tileX2);
         int var5 = var2 + Math.max(this.tileX1, this.tileX2);
         int var6 = var3 + Math.min(this.tileY1, this.tileY2);
         int var7 = var3 + Math.max(this.tileY1, this.tileY2);
         return this.applyFunction.applyToLevel(var1, var4, var6, var5, var7, this.dir);
      }

      public CustomApply mirrorX(int var1) {
         int var2 = this.dir != 1 && this.dir != 3 ? this.dir : (this.dir + 2) % 4;
         return new CustomApplyArea(PresetUtils.getMirroredValue(this.tileX1, var1), this.tileY1, PresetUtils.getMirroredValue(this.tileX2, var1), this.tileY2, var2, this.applyFunction);
      }

      public CustomApply mirrorY(int var1) {
         int var2 = this.dir != 0 && this.dir != 2 ? this.dir : (this.dir + 2) % 4;
         return new CustomApplyArea(this.tileX1, PresetUtils.getMirroredValue(this.tileY1, var1), this.tileX2, PresetUtils.getMirroredValue(this.tileY2, var1), var2, this.applyFunction);
      }

      public CustomApply rotate(PresetRotation var1, int var2, int var3) {
         int var4 = (this.dir + (var1 == null ? 0 : var1.dirOffset)) % 4;
         Point var5 = PresetUtils.getRotatedPointInSpace(this.tileX1, this.tileY1, var2, var3, var1);
         Point var6 = PresetUtils.getRotatedPointInSpace(this.tileX2, this.tileY2, var2, var3, var1);
         return new CustomApplyArea(var5.x, var5.y, var6.x, var6.y, var4, this.applyFunction);
      }
   }

   @FunctionalInterface
   public interface CustomApplyAreaFunction {
      UndoLogic applyToLevel(Level var1, int var2, int var3, int var4, int var5, int var6);
   }

   public static class ORApplyPredicate implements ApplyPredicate {
      public final ArrayList<ApplyPredicate> predicates;

      public ORApplyPredicate(List<ApplyPredicate> var1) {
         this.predicates = new ArrayList(var1);
      }

      public ORApplyPredicate(ApplyPredicate... var1) {
         this(Arrays.asList(var1));
      }

      public boolean canApplyToLevel(Level var1, int var2, int var3) {
         Iterator var4 = this.predicates.iterator();

         ApplyPredicate var5;
         do {
            if (!var4.hasNext()) {
               return false;
            }

            var5 = (ApplyPredicate)var4.next();
         } while(!var5.canApplyToLevel(var1, var2, var3));

         return true;
      }

      public ApplyPredicate mirrorX(int var1) throws PresetMirrorException {
         ArrayList var2 = new ArrayList(this.predicates.size());
         Iterator var3 = this.predicates.iterator();

         while(var3.hasNext()) {
            ApplyPredicate var4 = (ApplyPredicate)var3.next();
            var2.add(var4.mirrorX(var1));
         }

         return new ORApplyPredicate(var2);
      }

      public ApplyPredicate mirrorY(int var1) throws PresetMirrorException {
         ArrayList var2 = new ArrayList(this.predicates.size());
         Iterator var3 = this.predicates.iterator();

         while(var3.hasNext()) {
            ApplyPredicate var4 = (ApplyPredicate)var3.next();
            var2.add(var4.mirrorY(var1));
         }

         return new ORApplyPredicate(var2);
      }

      public ApplyPredicate rotate(PresetRotation var1, int var2, int var3) throws PresetRotateException {
         ArrayList var4 = new ArrayList(this.predicates.size());
         Iterator var5 = this.predicates.iterator();

         while(var5.hasNext()) {
            ApplyPredicate var6 = (ApplyPredicate)var5.next();
            var4.add(var6.rotate(var1, var2, var3));
         }

         return new ORApplyPredicate(var4);
      }
   }
}
