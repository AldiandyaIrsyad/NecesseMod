package necesse.gfx.forms.presets.debug.tools;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.InverseKinematics;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.sidebar.SidebarForm;
import necesse.level.maps.hudManager.HudDrawElement;

public class InverseKinematicsGameTool extends MouseDebugGameTool {
   public ControlForm controlForm;
   public HudDrawElement hudElement;
   public InverseKinematics current;
   private Point2D.Float startPos;
   private Point2D.Float currentPos;
   private Point2D.Float targetPos;
   GameLinkedList<InverseKinematics.Limb>.Element selectedLimb;
   public boolean selectedLimbInbound = false;
   private boolean createMode = true;

   public InverseKinematicsGameTool(DebugForm var1) {
      super(var1, "InverseKinematics");
   }

   public void init() {
      this.getLevel().hudManager.addElement(this.hudElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, final GameCamera var2, PlayerMob var3) {
            var1.add(new SortedDrawable() {
               public int getPriority() {
                  return -10000;
               }

               public void draw(TickManager var1) {
                  if (InverseKinematicsGameTool.this.current != null) {
                     InverseKinematicsGameTool.this.current.drawDebug(var2, new Color(255, 0, 0), new Color(0, 255, 0));
                  } else if (InverseKinematicsGameTool.this.startPos != null) {
                     Screen.drawCircle(var2.getDrawX(InverseKinematicsGameTool.this.startPos.x), var2.getDrawY(InverseKinematicsGameTool.this.startPos.y), 4, 12, 1.0F, 0.0F, 0.0F, 1.0F, false);
                  }

                  if (InverseKinematicsGameTool.this.targetPos != null) {
                     Screen.drawCircle(var2.getDrawX(InverseKinematicsGameTool.this.targetPos.x), var2.getDrawY(InverseKinematicsGameTool.this.targetPos.y), 4, 12, 0.0F, 1.0F, 0.0F, 1.0F, false);
                  }

                  if (InverseKinematicsGameTool.this.currentPos != null) {
                     Screen.drawCircle(var2.getDrawX(InverseKinematicsGameTool.this.currentPos.x), var2.getDrawY(InverseKinematicsGameTool.this.currentPos.y), 4, 12, 1.0F, 1.0F, 0.0F, 1.0F, false);
                  }

                  if (InverseKinematicsGameTool.this.selectedLimb != null && !InverseKinematicsGameTool.this.selectedLimb.isRemoved()) {
                     Point2D.Float var2x;
                     if (InverseKinematicsGameTool.this.selectedLimbInbound) {
                        var2x = new Point2D.Float(((InverseKinematics.Limb)InverseKinematicsGameTool.this.selectedLimb.object).inboundX, ((InverseKinematics.Limb)InverseKinematicsGameTool.this.selectedLimb.object).inboundY);
                     } else {
                        var2x = new Point2D.Float(((InverseKinematics.Limb)InverseKinematicsGameTool.this.selectedLimb.object).outboundX, ((InverseKinematics.Limb)InverseKinematicsGameTool.this.selectedLimb.object).outboundY);
                     }

                     Screen.drawCircle(var2.getDrawX(var2x.x), var2.getDrawY(var2x.y), 4, 12, 0.0F, 0.0F, 1.0F, 1.0F, false);
                  }

               }
            });
         }
      });
      this.updateInput();
      if (this.controlForm != null) {
         this.controlForm.invalidate();
      }

      if (GlobalData.getCurrentState() instanceof MainGame) {
         this.controlForm = new ControlForm(100, 100);
         ((MainGame)GlobalData.getCurrentState()).formManager.addSidebar(this.controlForm);
      }

   }

   public void updateInput() {
      if (this.createMode) {
         this.onLeftClick((var1) -> {
            if (this.current == null) {
               if (this.startPos == null) {
                  this.startPos = new Point2D.Float((float)this.getMouseX(), (float)this.getMouseY());
               } else {
                  this.current = InverseKinematics.startFromPoints(this.startPos.x, this.startPos.y, (float)this.getMouseX(), (float)this.getMouseY());
                  this.startPos = null;
               }
            } else {
               this.current.addJointPoint((float)this.getMouseX(), (float)this.getMouseY());
            }

            return true;
         }, "Add joint");
         this.onRightClick((var1) -> {
            if (this.current != null) {
               if (this.current.getTotalJoints() > 1) {
                  this.current.removeLastLimb();
               } else {
                  InverseKinematics.Limb var2 = (InverseKinematics.Limb)this.current.limbs.getFirst();
                  this.startPos = new Point2D.Float(var2.inboundX, var2.inboundY);
                  this.current = null;
               }
            } else {
               this.startPos = null;
            }

            return true;
         }, "Remove joint");
      } else {
         this.onLeftClick((var1) -> {
            if (this.current != null) {
               this.targetPos = null;
               this.currentPos = null;
               double var2 = Double.MAX_VALUE;
               GameLinkedList.Element var4 = null;
               Iterator var5 = this.current.limbs.elements().iterator();

               while(var5.hasNext()) {
                  GameLinkedList.Element var6 = (GameLinkedList.Element)var5.next();
                  InverseKinematics.Limb var7 = (InverseKinematics.Limb)var6.object;
                  double var8 = (new Point2D.Float(var7.inboundX, var7.inboundY)).distance((double)this.getMouseX(), (double)this.getMouseY());
                  if (var8 < var2) {
                     var2 = var8;
                     var4 = var6;
                     this.selectedLimbInbound = true;
                  }

                  double var10 = (new Point2D.Float(var7.outboundX, var7.outboundY)).distance((double)this.getMouseX(), (double)this.getMouseY());
                  if (var10 < var2) {
                     var2 = var10;
                     var4 = var6;
                     this.selectedLimbInbound = false;
                  }
               }

               if (var2 < 32.0) {
                  this.selectedLimb = var4;
                  if (this.selectedLimb != null) {
                     if (this.selectedLimbInbound) {
                        this.currentPos = new Point2D.Float(((InverseKinematics.Limb)this.selectedLimb.object).inboundX, ((InverseKinematics.Limb)this.selectedLimb.object).inboundY);
                     } else {
                        this.currentPos = new Point2D.Float(((InverseKinematics.Limb)this.selectedLimb.object).outboundX, ((InverseKinematics.Limb)this.selectedLimb.object).outboundY);
                     }
                  }
               } else {
                  this.selectedLimb = null;
               }
            } else {
               this.selectedLimb = null;
            }

            return true;
         }, "Select joint");
         this.onRightClick((var1) -> {
            this.targetPos = new Point2D.Float((float)this.getMouseX(), (float)this.getMouseY());
            if (this.selectedLimb == null && this.current != null) {
               System.out.println(this.current.apply(this.targetPos.x, this.targetPos.y, 0.1F, 0.5F, 100000) + " FABRIK iterations");
            }

            return true;
         }, "Set target");
      }

      this.onScroll((var1) -> {
         this.createMode = !this.createMode;
         this.updateInput();
         return true;
      }, "Change mode");
   }

   public void tick() {
      float var1 = 50.0F;
      float var2 = 50.0F;
      if (this.selectedLimb != null && this.current != null && this.currentPos != null && this.targetPos != null) {
         double var3 = this.currentPos.distance(this.targetPos);
         if (var3 != 0.0) {
            float var5 = var1 * var2 / 250.0F;
            if (var3 <= (double)var5) {
               this.currentPos = new Point2D.Float(this.targetPos.x, this.targetPos.y);
               InverseKinematics.apply(this.selectedLimb, this.selectedLimbInbound, this.currentPos.x, this.currentPos.y, true);
            } else {
               Point2D.Float var6 = GameMath.normalize(this.targetPos.x - this.currentPos.x, this.targetPos.y - this.currentPos.y);
               Point2D.Float var10000 = this.currentPos;
               var10000.x += var6.x * var5;
               var10000 = this.currentPos;
               var10000.y += var6.y * var5;
               InverseKinematics.apply(this.selectedLimb, this.selectedLimbInbound, this.currentPos.x, this.currentPos.y, true);
            }
         }
      }

   }

   public void isCancelled() {
      super.isCancelled();
      if (this.hudElement != null) {
         this.hudElement.remove();
      }

      if (this.controlForm != null) {
         this.controlForm.invalidate();
      }

   }

   public void isCleared() {
      super.isCleared();
      if (this.hudElement != null) {
         this.hudElement.remove();
      }

      if (this.controlForm != null) {
         this.controlForm.invalidate();
      }

   }

   protected static class ControlForm extends SidebarForm {
      private boolean isValid = true;

      public ControlForm(int var1, int var2) {
         super("IKControl", var1, var2);
      }

      public boolean isValid(Client var1) {
         return this.isValid;
      }

      public void invalidate() {
         this.isValid = false;
      }
   }
}
