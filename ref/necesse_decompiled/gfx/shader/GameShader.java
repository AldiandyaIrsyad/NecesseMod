package necesse.gfx.shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import necesse.engine.Screen;
import necesse.gfx.shader.shaderVariable.ShaderVariable;
import org.lwjgl.opengl.GL20;

public class GameShader {
   protected int program = GL20.glCreateProgram();
   protected int vertex;
   protected int fragment;
   private ArrayList<ShaderVariable<?>> variables = new ArrayList();

   public GameShader(String var1, String var2) {
      this.vertex = ShaderLoader.loadVertexShader(var1);
      this.fragment = ShaderLoader.loadFragmentShader(var2);
      GL20.glAttachShader(this.program, this.vertex);
      GL20.glAttachShader(this.program, this.fragment);
      GL20.glLinkProgram(this.program);
      GL20.glValidateProgram(this.program);
   }

   public int getUniform(String var1) {
      return GL20.glGetUniformLocation(this.program, var1);
   }

   public int getProgram() {
      return this.program;
   }

   public final void _ScreenUse() {
      GL20.glUseProgram(this.program);
   }

   public void use() {
      Screen.useShader(this);
      this.pass1i("texture", 0);
   }

   public void stop() {
      Screen.stopShader(this);
   }

   public void delete() {
      GL20.glDeleteProgram(this.program);
      GL20.glDeleteShader(this.vertex);
      GL20.glDeleteShader(this.fragment);
   }

   public ArrayList<ShaderVariable<?>> getVariables() {
      return this.variables;
   }

   protected void addVariable(ShaderVariable<?> var1) {
      this.variables.add(var1);
   }

   public void pass1f(String var1, float var2) {
      GL20.glUniform1f(this.getUniform(var1), var2);
   }

   public float get1f(String var1) {
      return GL20.glGetUniformf(this.program, this.getUniform(var1));
   }

   public void pass2f(String var1, float var2, float var3) {
      GL20.glUniform2f(this.getUniform(var1), var2, var3);
   }

   public float[] get2f(String var1) {
      float[] var2 = new float[2];
      GL20.glGetUniformfv(this.program, this.getUniform(var1), var2);
      return var2;
   }

   public void pass3f(String var1, float var2, float var3, float var4) {
      GL20.glUniform3f(this.getUniform(var1), var2, var3, var4);
   }

   public float[] get3f(String var1) {
      float[] var2 = new float[3];
      GL20.glGetUniformfv(this.program, this.getUniform(var1), var2);
      return var2;
   }

   public void pass4f(String var1, float var2, float var3, float var4, float var5) {
      GL20.glUniform4f(this.getUniform(var1), var2, var3, var4, var5);
   }

   public float[] get4f(String var1) {
      float[] var2 = new float[4];
      GL20.glGetUniformfv(this.program, this.getUniform(var1), var2);
      return var2;
   }

   public void pass1i(String var1, int var2) {
      GL20.glUniform1i(this.getUniform(var1), var2);
   }

   public int get1i(String var1) {
      return GL20.glGetUniformi(this.program, this.getUniform(var1));
   }

   public void pass2i(String var1, int var2, int var3) {
      GL20.glUniform2i(this.getUniform(var1), var2, var3);
   }

   public int[] get2i(String var1) {
      int[] var2 = new int[2];
      GL20.glGetUniformiv(this.program, this.getUniform(var1), var2);
      return var2;
   }

   public void pass3i(String var1, int var2, int var3, int var4) {
      GL20.glUniform3i(this.getUniform(var1), var2, var3, var4);
   }

   public int[] get3i(String var1) {
      int[] var2 = new int[3];
      GL20.glGetUniformiv(this.program, this.getUniform(var1), var2);
      return var2;
   }

   public void pass4i(String var1, int var2, int var3, int var4, int var5) {
      GL20.glUniform4i(this.getUniform(var1), var2, var3, var4, var5);
   }

   public int[] get4i(String var1) {
      int[] var2 = new int[4];
      GL20.glGetUniformiv(this.program, this.getUniform(var1), var2);
      return var2;
   }

   public void pass1(String var1, FloatBuffer var2) {
      GL20.glUniform1fv(this.getUniform(var1), var2);
   }

   public void pass1(String var1, IntBuffer var2) {
      GL20.glUniform1iv(this.getUniform(var1), var2);
   }

   public void pass2(String var1, FloatBuffer var2) {
      GL20.glUniform2fv(this.getUniform(var1), var2);
   }

   public void pass2(String var1, IntBuffer var2) {
      GL20.glUniform1iv(this.getUniform(var1), var2);
   }

   public void pass3(String var1, FloatBuffer var2) {
      GL20.glUniform2fv(this.getUniform(var1), var2);
   }

   public void pass3(String var1, IntBuffer var2) {
      GL20.glUniform1iv(this.getUniform(var1), var2);
   }

   public void pass4(String var1, FloatBuffer var2) {
      GL20.glUniform2fv(this.getUniform(var1), var2);
   }

   public void pass4(String var1, IntBuffer var2) {
      GL20.glUniform1iv(this.getUniform(var1), var2);
   }
}
