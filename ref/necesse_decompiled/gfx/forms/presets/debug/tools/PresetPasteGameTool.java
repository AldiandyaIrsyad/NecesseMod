package necesse.gfx.forms.presets.debug.tools;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.control.MouseWheelBuffer;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.hudManager.floatText.UniqueFloatText;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetMirrorException;
import necesse.level.maps.presets.PresetRotateException;
import necesse.level.maps.presets.PresetRotation;

public class PresetPasteGameTool extends MouseDebugGameTool {
   private LinkedList<UndoPreset> undoPresets = new LinkedList();
   private ArrayList<Preset> presets;
   private int selected;
   private HudDrawElement hudElement;
   private MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);

   public PresetPasteGameTool(DebugForm var1) {
      super(var1, "Paste preset");
      this.onLeftClick((var2) -> {
         try {
            Level var3 = this.getLevel();
            if (var3 != null) {
               Preset var4 = (Preset)this.presets.get(this.selected);
               Point var5 = this.getPlaceTile(var4);
               Preset var6 = new Preset(var4.width, var4.height);
               var6.copyFromLevel(this.getLevel(), var5.x, var5.y);
               UndoPreset var7 = new UndoPreset(var6, var5.x, var5.y);
               this.undoPresets.addLast(var7);
               var7.clientUndos = var4.applyToLevel(var3, var5.x, var5.y);
               Level var8 = this.getServerLevel();
               if (var8 != null) {
                  Preset var9 = new Preset(var4.width, var4.height);
                  var9.copyFromLevel(var8, var5.x, var5.y);
                  var7.serverPreset = var9;
                  var7.serverUndos = var4.applyToLevel(var8, var5.x, var5.y);
               }
            }
         } catch (Exception var10) {
            var1.client.setMessage("Error applying preset: " + var10.getMessage(), Color.WHITE);
            var10.printStackTrace();
         }

         return true;
      }, "Place preset");
      this.onRightClick((var2) -> {
         if (this.undoPresets.isEmpty()) {
            var1.client.setMessage("No more presets to undo", Color.WHITE);
         } else {
            UndoPreset var3 = (UndoPreset)this.undoPresets.removeLast();
            Level var4 = this.getLevel();
            if (var4 != null) {
               var3.applyClient(var4);
               Level var5 = this.getServerLevel();
               if (var5 != null) {
                  var3.applyServer(var5);
               }
            }
         }

         return true;
      }, "Undo preset");
      this.onScroll((var1x) -> {
         this.wheelBuffer.add(var1x);
         this.wheelBuffer.useScrollY((var1) -> {
            int var2 = var1 ? 1 : -1;
            this.selected = Math.floorMod(this.selected + var2, this.presets.size());
            this.updateScrollUsage();
         });
         return true;
      }, (String)null);
      this.onKeyClick(82, (var1x) -> {
         try {
            if (Screen.isKeyDown(340)) {
               this.presets.set(this.selected, ((Preset)this.presets.get(this.selected)).rotate(PresetRotation.ANTI_CLOCKWISE));
            } else {
               this.presets.set(this.selected, ((Preset)this.presets.get(this.selected)).rotate(PresetRotation.CLOCKWISE));
            }
         } catch (PresetRotateException var3) {
            this.getLevel().hudManager.addElement(new UniqueFloatText(this.getMouseX(), this.getMouseY() - 4, var3.getMessage(), (new FontOptions(16)).outline()));
         }

         return true;
      }, "Rotate");
      this.onKeyClick(84, (var1x) -> {
         try {
            this.presets.set(this.selected, ((Preset)this.presets.get(this.selected)).rotate(PresetRotation.HALF_180));
         } catch (PresetRotateException var3) {
            this.getLevel().hudManager.addElement(new UniqueFloatText(this.getMouseX(), this.getMouseY() - 4, var3.getMessage(), (new FontOptions(16)).outline()));
         }

         return true;
      }, "Rotate 180");
      this.onKeyClick(86, (var1x) -> {
         try {
            this.presets.set(this.selected, ((Preset)this.presets.get(this.selected)).mirrorY());
         } catch (PresetMirrorException var3) {
            this.getLevel().hudManager.addElement(new UniqueFloatText(this.getMouseX(), this.getMouseY() - 4, var3.getMessage(), (new FontOptions(16)).outline()));
         }

         return true;
      }, "Change Y Mirror");
      this.onKeyClick(72, (var1x) -> {
         try {
            this.presets.set(this.selected, ((Preset)this.presets.get(this.selected)).mirrorX());
         } catch (PresetMirrorException var3) {
            this.getLevel().hudManager.addElement(new UniqueFloatText(this.getMouseX(), this.getMouseY() - 4, var3.getMessage(), (new FontOptions(16)).outline()));
         }

         return true;
      }, "Change X Mirror");
   }

   public void init() {
      String var1 = Screen.getClipboard();
      if (var1 == null) {
         var1 = "";
      }

      this.presets = new ArrayList();

      try {
         this.presets.add(new Preset(var1));
      } catch (Exception var3) {
         this.parent.client.setMessage("Clipboard does not contain a preset", Color.WHITE);
      }

      if (this.presets.isEmpty()) {
         Screen.clearGameTool(this);
      } else {
         this.selected = Math.min(this.selected, this.presets.size() - 1);
         this.updateScrollUsage();
         this.parent.client.setMessage("Will only paste client side if not hosting server", Color.WHITE);
         this.getLevel().hudManager.addElement(this.hudElement = new HudDrawElement() {
            public void addDrawables(List<SortedDrawable> var1, final GameCamera var2, final PlayerMob var3) {
               final Preset var4 = (Preset)PresetPasteGameTool.this.presets.get(PresetPasteGameTool.this.selected);
               final Point var5 = PresetPasteGameTool.this.getPlaceTile(var4);
               final TextureDrawOptionsEnd var6;
               if (var4.canApplyToLevel(this.getLevel(), var5.x, var5.y)) {
                  var6 = null;
               } else {
                  var6 = Screen.initQuadDraw(var4.width * 32, var4.height * 32).color(1.0F, 0.0F, 0.0F, 0.5F).pos(var2.getTileDrawX(var5.x), var2.getTileDrawY(var5.y));
               }

               var1.add(new SortedDrawable() {
                  public int getPriority() {
                     return Integer.MIN_VALUE;
                  }

                  public void draw(TickManager var1) {
                     var4.drawPlacePreview(getLevel(), var5.x, var5.y, var3, var2);
                     if (var6 != null) {
                        var6.draw();
                     }

                  }
               });
            }
         });
      }

   }

   protected void updateScrollUsage() {
      this.scrollUsage = "Select preset - " + ((Preset)this.presets.get(this.selected)).getClass().getSimpleName();
   }

   public void isCancelled() {
      super.isCancelled();
      if (this.hudElement != null) {
         this.hudElement.remove();
      }

   }

   public void isCleared() {
      super.isCleared();
      if (this.hudElement != null) {
         this.hudElement.remove();
      }

   }

   public Point getPlaceTile(Preset var1) {
      return new Point((this.getMouseX() - var1.width * 32 / 2 + 16) / 32, (this.getMouseY() - var1.height * 32 / 2 + 16) / 32);
   }

   private static class UndoPreset {
      public final int x;
      public final int y;
      public final Preset clientPreset;
      public Preset serverPreset;
      public LinkedList<Preset.UndoLogic> serverUndos = new LinkedList();
      public LinkedList<Preset.UndoLogic> clientUndos = new LinkedList();

      public UndoPreset(Preset var1, int var2, int var3) {
         this.clientPreset = var1;
         this.x = var2;
         this.y = var3;
      }

      public void applyClient(Level var1) {
         this.clientPreset.applyToLevel(var1, this.x, this.y);
         this.clientUndos.forEach((var2) -> {
            var2.applyUndo(var1, this.x, this.y);
         });
      }

      public void applyServer(Level var1) {
         if (this.serverPreset != null) {
            this.serverPreset.applyToLevel(var1, this.x, this.y);
            this.serverUndos.forEach((var2) -> {
               var2.applyUndo(var1, this.x, this.y);
            });
         }

      }
   }
}
