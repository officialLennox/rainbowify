/*
 * Copyright (c) 2021-2022 Lennox
 *
 * This file is part of rainbowify.
 *
 * rainbowify is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rainbowify is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with rainbowify.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.lennox.rainbowify.effect.effects;

import com.mojang.blaze3d.systems.RenderSystem;
import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.RainbowifyResourceFactory;
import de.lennox.rainbowify.config.CyclingOptions;
import de.lennox.rainbowify.effect.Effect;
import de.lennox.rainbowify.event.Subscription;
import de.lennox.rainbowify.event.events.GlintShaderEvent;
import de.lennox.rainbowify.event.events.InGameHudDrawEvent;
import de.lennox.rainbowify.mixin.interfaces.RainbowifyShader;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.Program;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import java.io.IOException;

/**
 * The rainbow effect applied on enchantment glint
 *
 * @since 2.0.0
 * @author Lennox
 */
public class RainbowGlintShader extends Effect {
  private Shader glint;
  private Program cachedArmorGlint;
  private GlUniform time, res, screenTextureMat, insanity;
  private long startTime;
  private Matrix4f cachedTextureMatrix;

  @Override
  public void init() {
    // Create the shader instance
    try {
      glint =
          new Shader(
              new RainbowifyResourceFactory(), "rainbowify:cglint", VertexFormats.POSITION_TEXTURE);
    } catch (IOException e) {
      System.err.println(
          "Failed to create rainbow shader. Report this in the discord with the log!");
      e.printStackTrace();
    }
    RainbowifyShader rainbowifyShaderInterface = (RainbowifyShader) glint;
    // Create uniforms
    time = rainbowifyShaderInterface.customUniform("time");
    res = rainbowifyShaderInterface.customUniform("res");
    screenTextureMat = rainbowifyShaderInterface.customUniform("ScreenTextureMat");
    insanity = rainbowifyShaderInterface.customUniform("insanity");
    // Update start time
    startTime = System.currentTimeMillis();
  }

  @Override
  public void draw(MatrixStack stack) {
    cachedTextureMatrix = new Matrix4f(RenderSystem.getTextureMatrix());
    cachedTextureMatrix.multiply(4f);
  }

  @SuppressWarnings("unused")
  private final Subscription<InGameHudDrawEvent> inGameHudDrawSubscription =
      event -> {
        cachedTextureMatrix = new Matrix4f(RenderSystem.getTextureMatrix());
        cachedTextureMatrix.multiply(4f);
      };

  @SuppressWarnings("unused")
  private final Subscription<GlintShaderEvent> glintShaderSubscription =
      event -> {
        boolean enabled =
            (boolean) RainbowifyMod.instance().optionRepository().optionOf("glint").value;
        boolean insaneArmor =
            (boolean) RainbowifyMod.instance().optionRepository().optionOf("insane_armor").value;
        if (!enabled) return;
        //noinspection resource
        Shader shader = event.shader();
        CyclingOptions.RainbowSpeed rainbowSpeed =
            (CyclingOptions.RainbowSpeed)
                RainbowifyMod.instance().optionRepository().optionOf("rainbow_speed").value;
        // Set the uniforms now and override the shader
        time.set((float) (System.currentTimeMillis() - startTime) / rainbowSpeed.time());
        res.set(MC.getWindow().getScaledWidth(), MC.getWindow().getScaledHeight());
        screenTextureMat.set(cachedTextureMatrix);
        // Cache the armor glint class
        if (cachedArmorGlint == null && shader.getName().contains("armor")) {
          cachedArmorGlint = shader.getFragmentShader();
        }
        // Enable insanity mode if wanted
        boolean requiresInsanity = insaneArmor && cachedArmorGlint == shader.getFragmentShader();
        insanity.set(requiresInsanity ? 1 : 0);
        // Overwrite the shader
        event.shader(glint);
      };
}
