package necesse.gfx.forms.presets.debug;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.generalTree.GeneralTree;
import necesse.engine.util.generalTree.GeneralTreeNode;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.shader.FormShader;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.HUD;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class DebugMobAIForm extends Form {
   public final Mob mob;
   public final BehaviourTreeAI<?> treeAI;
   private final HudDrawElement hudElement;
   private int maxLevel;

   public DebugMobAIForm(Level var1, Mob var2, Consumer<DebugMobAIForm> var3) {
      this(var1, var2, 350, 300, var3);
   }

   public DebugMobAIForm(Level var1, final Mob var2, int var3, int var4, Consumer<DebugMobAIForm> var5) {
      super(var3, var4);
      this.mob = var2;
      var1.hudManager.addElement(this.hudElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, GameCamera var2x, PlayerMob var3) {
            final DrawOptions var4 = HUD.levelBoundOptions(var2x, var2.getSelectBox());
            var1.add(new SortedDrawable() {
               public int getPriority() {
                  return 0;
               }

               public void draw(TickManager var1) {
                  var4.draw();
               }
            });
         }
      });
      this.treeAI = var2.ai;
      this.setDraggingBox(new Rectangle(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE));
      this.onDragged((var1x) -> {
         var1x.x = GameMath.limit(var1x.x, -this.getWidth() + 20, Screen.getHudWidth() - 20);
         var1x.y = GameMath.limit(var1x.y, -this.getHeight() + 20, Screen.getHudHeight() - 20);
      });
      FormLabel var6 = (FormLabel)this.addComponent(new FormLabel(var2.getDisplayName(), new FontOptions(16), 0, this.getWidth() / 2, 4));
      FormLabel var7 = (FormLabel)this.addComponent(new FormLabel("" + var2.getUniqueID(), new FontOptions(12), 0, this.getWidth() / 2, 20));
      FormTextButton var8 = (FormTextButton)this.addComponent(new FormTextButton("Close", 4, this.getHeight() - 24, this.getWidth() - 8, FormInputSize.SIZE_20, ButtonColor.BASE));
      var8.onClicked((var2x) -> {
         var5.accept(this);
      });
      this.setMinimumResize(100, 100);
      this.allowResize(true, true, true, true, (var3x) -> {
         var6.setX(var3x.width / 2);
         var7.setX(var3x.width / 2);
         var8.setWidth(var3x.width - 8);
         var8.setY(var3x.height - 24);
      });
      if (this.treeAI != null) {
         Form var9 = (Form)this.addComponent(new Form(this.getWidth(), this.getHeight() - 35 - 28));
         var9.setY(35);
         var9.drawBase = false;
         Form var10 = (Form)var9.addComponent(new Form(var9.getWidth(), var9.getHeight() - 75), 1);
         FormContentBox var11 = (FormContentBox)var10.addComponent(new FormContentBox(0, 0, var10.getWidth(), var10.getHeight()));
         byte var12 = 100;
         byte var13 = 50;
         NodeInfo var14 = new NodeInfo(new HashMap(), (NodeInfo)null, 0, this.treeAI.tree);
         (new GeneralTree(var13 + 10, 10, 20)).calculateNodePositions(var14);
         var14.addNodeComponents(var11, var12, var13);
         var11.fitContentBoxToComponents(20);
         var11.centerContentHorizontal();
         var11.centerContentVertical();
         Form var15 = (Form)var9.addComponent(new Form(var9.getWidth(), var9.getHeight() - var10.getHeight()));
         var15.setY(var10.getHeight());
         FormContentBox var16 = (FormContentBox)var15.addComponent(new FormContentBox(0, 0, var15.getWidth(), var15.getHeight()) {
            public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
               DebugMobAIForm.this.updateBlackboardContent(this);
               super.draw(var1, var2, var3);
            }
         });
         var15.drawBase = false;
         this.onResize((var5x) -> {
            var9.setWidth(var5x.width);
            var9.setHeight(var5x.height - 35 - 28);
            var15.setWidth(var9.getWidth());
            var10.setWidth(var9.getWidth());
            var10.setHeight(var9.getHeight() - var15.getHeight());
            var15.setY(var9.getHeight() - var15.getHeight());
            if (var10.getHeight() < 20) {
               var10.setHeight(20);
               var15.setY(20);
               var15.setHeight(var9.getHeight() - var15.getY());
            }

            var11.setWidth(var10.getWidth());
            var11.setHeight(var10.getHeight());
            var11.fitContentBoxToComponents(20);
            var11.centerContentHorizontal();
            var11.centerContentVertical();
            var16.setWidth(var15.getWidth());
            var16.setHeight(var15.getHeight());
            var16.fitContentBoxToComponents(5);
         });
         var10.setMinimumResize(0, 20);
         var10.allowResize(false, true, false, false, (var4x) -> {
            var4x.height = Math.min(var4x.height, var9.getHeight() - 20);
            var15.setY(var4x.y + var4x.height);
            var15.setHeight(var9.getHeight() - var15.getY());
            var11.setHeight(var4x.height);
            var11.fitContentBoxToComponents(20);
            var11.centerContentHorizontal();
            var11.centerContentVertical();
            var16.setWidth(var15.getWidth());
            var16.setHeight(var15.getHeight());
            var16.fitContentBoxToComponents(5);
         });
      } else {
         FormLabel var17 = (FormLabel)this.addComponent(new FormLabel("Mob does not\nhave BehaviourTreeAI", new FontOptions(16), 0, this.getWidth() / 2, this.getHeight() / 2 - 8));
         this.onResize((var1x) -> {
            var17.setPosition(var1x.width / 2, var1x.height / 2 - 8);
         });
      }

      this.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   private void updateBlackboardContent(FormContentBox var1) {
      var1.clearComponents();
      FormFlow var2 = new FormFlow(0);
      var1.addComponent((FormLabel)var2.nextY(new FormLabel(String.valueOf(this.treeAI.blackboard.mover), new FontOptions(12), -1, 0, 0)));
      this.treeAI.blackboard.forEach((var2x, var3) -> {
         var1.addComponent((FormLabel)var2.nextY(new FormLabel(var2x + ": " + var3, new FontOptions(12), -1, 0, 0)));
      });
      var1.fitContentBoxToComponents(10);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      super.draw(var1, var2, var3);
   }

   public void dispose() {
      super.dispose();
      this.hudElement.remove();
   }

   private static String className(Class<?> var0) {
      String var1 = var0.getName();
      int var2 = var1.lastIndexOf(".");
      if (var2 != -1) {
         var1 = var1.substring(var2 + 1);
      }

      int var3 = var1.lastIndexOf("$");
      if (var3 != -1) {
         Class var4 = var0.getSuperclass();
         if (var4 != null) {
            return className(var4) + "{" + var1.substring(var3 + 1) + "}";
         }
      }

      return var1;
   }

   private class NodeInfo extends GeneralTreeNode {
      public final AINode<?> node;
      public final ArrayList<NodeInfo> children = new ArrayList();
      private AINodeComponent component;

      public NodeInfo(HashMap<Integer, GeneralTreeNode> var2, NodeInfo var3, int var4, AINode<?> var5) {
         super(var2, var3, var4, 100);
         DebugMobAIForm.this.maxLevel = Math.max(DebugMobAIForm.this.maxLevel, this.level);
         this.node = var5;
         int var6 = 0;

         for(Iterator var7 = var5.debugChildren().iterator(); var7.hasNext(); ++var6) {
            AINode var8 = (AINode)var7.next();
            this.children.add(DebugMobAIForm.this.new NodeInfo(var2, this, var6, var8));
         }

      }

      public List<? extends GeneralTreeNode> getChildren() {
         return this.children;
      }

      public int getMaxChildrenLevelSize() {
         int var1 = this.children.size();

         NodeInfo var3;
         for(Iterator var2 = this.children.iterator(); var2.hasNext(); var1 = Math.max(var1, var3.getMaxChildrenLevelSize())) {
            var3 = (NodeInfo)var2.next();
         }

         return var1;
      }

      public void addNodeComponents(FormContentBox var1, int var2, int var3) {
         var1.addComponent(this.component = new AINodeComponent(this, this.x, this.y, var2, var3));
         Iterator var4 = this.children.iterator();

         while(var4.hasNext()) {
            NodeInfo var5 = (NodeInfo)var4.next();
            var5.addNodeComponents(var1, var2, var3);
         }

      }
   }

   public static class AINodeTextComponent extends FormComponent implements FormPositionContainer {
      public NodeInfo info;
      private FormPosition position;
      public FontOptions fontOptions;
      public String name;

      public AINodeTextComponent(NodeInfo var1, int var2, int var3, FontOptions var4) {
         this.info = var1;
         this.position = new FormFixedPosition(var2, var3);
         this.fontOptions = var4;
         this.name = var1.node.getClass().getName();
         int var5 = this.name.lastIndexOf(".");
         if (var5 != -1) {
            this.name = this.name.substring(var5 + 1);
         }

      }

      public AINodeTextComponent(NodeInfo var1, int var2, int var3) {
         this(var1, var2, var3, new FontOptions(16));
      }

      public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      }

      public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      }

      public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      }

      public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
         Color var4 = Color.YELLOW;
         if (this.info.node.lastResult != null) {
            switch (this.info.node.lastResult) {
               case SUCCESS:
                  var4 = Color.GREEN;
                  break;
               case FAILURE:
                  var4 = Color.RED;
                  break;
               case RUNNING:
                  var4 = Color.BLUE;
            }
         }

         FontManager.bit.drawString((float)this.getX(), (float)this.getY(), this.name, this.fontOptions.color(var4));
      }

      public List<Rectangle> getHitboxes() {
         return singleBox(new Rectangle(this.getX(), this.getY(), FontManager.bit.getWidthCeil(this.name, this.fontOptions), FontManager.bit.getHeightCeil(this.name, this.fontOptions)));
      }

      public FormPosition getPosition() {
         return this.position;
      }

      public void setPosition(FormPosition var1) {
         this.position = var1;
      }
   }

   public static class AINodeComponent extends FormComponent implements FormPositionContainer {
      public NodeInfo info;
      private FormPosition position;
      public int width;
      public int height;
      private boolean isMouseOver;

      public AINodeComponent(NodeInfo var1, int var2, int var3, int var4, int var5) {
         this.info = var1;
         this.position = new FormFixedPosition(var2, var3);
         this.width = var4;
         this.height = var5;
      }

      public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
         if (var1.isMouseMoveEvent()) {
            this.isMouseOver = this.isMouseOver(var1);
            if (this.isMouseOver) {
               var1.useMove();
            }
         }

         if (var1.getID() == -100 && this.isMouseOver(var1)) {
            if (!var1.state) {
               SelectionFloatMenu var4 = new SelectionFloatMenu(this);
               this.info.node.addDebugActions(var4);
               if (!var4.isEmpty()) {
                  this.getManager().openFloatMenu(var4);
               }
            }

            var1.use();
         }

      }

      public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      }

      public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      }

      public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
         Color var4 = Color.YELLOW;
         if (this.info.node.lastResult != null) {
            switch (this.info.node.lastResult) {
               case SUCCESS:
                  var4 = Color.GREEN;
                  break;
               case FAILURE:
                  var4 = Color.RED;
                  break;
               case RUNNING:
                  var4 = Color.BLUE;
            }
         }

         Screen.initQuadDraw(this.width, this.height).color(var4).alpha(0.5F).draw(this.getX(), this.getY());
         String var5 = DebugMobAIForm.className(this.info.node.getClass());
         if (this.isMouseOver) {
            ListGameTooltips var6 = new ListGameTooltips(var5);
            var6.add("Last result: " + (this.info.node.lastResult == null ? "INACTIVE" : this.info.node.lastResult));
            this.info.node.addDebugTooltips(var6);
            Screen.addTooltip(var6, TooltipLocation.FORM_FOCUS);
         }

         if (this.info.parent != null && ((NodeInfo)this.info.parent).component != null) {
            AINodeComponent var13 = ((NodeInfo)this.info.parent).component;
            Screen.drawLineRGBA(this.getX() + this.width / 2, this.getY(), var13.getX() + var13.width / 2, var13.getY() + var13.height, 1.0F, 1.0F, 0.0F, 1.0F);
         }

         Iterator var14 = this.info.children.iterator();

         while(var14.hasNext()) {
            NodeInfo var7 = (NodeInfo)var14.next();
            Screen.drawLineRGBA(this.getX() + this.width / 2, this.getY() + this.height, var7.component.getX() + var7.component.width / 2, var7.component.getY(), 1.0F, 1.0F, 0.0F, 1.0F);
         }

         FormShader.FormShaderState var15 = GameResources.formShader.startState(new Point(this.getX(), this.getY()), new Rectangle(this.width, this.height));

         try {
            FairType var16 = (new FairType()).append(new FontOptions(12), var5);
            FairTypeDrawOptions var8 = var16.getDrawOptions(FairType.TextAlign.CENTER, this.width, true);
            int var9 = var8.getBoundingBox().height;
            var8.draw(this.width / 2, Math.max(0, this.height / 2 - var9 / 2), new Color(20, 20, 20));
         } finally {
            var15.end();
         }

      }

      public List<Rectangle> getHitboxes() {
         return singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
      }

      public FormPosition getPosition() {
         return this.position;
      }

      public void setPosition(FormPosition var1) {
         this.position = var1;
      }
   }
}
