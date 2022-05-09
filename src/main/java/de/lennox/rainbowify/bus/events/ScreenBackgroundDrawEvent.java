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
package de.lennox.rainbowify.bus.events;

import de.lennox.rainbowify.bus.Event;
import net.minecraft.client.util.math.MatrixStack;

public class ScreenBackgroundDrawEvent extends Event {
  private final MatrixStack matrixStack;

  public ScreenBackgroundDrawEvent(MatrixStack matrixStack) {
    this.matrixStack = matrixStack;
  }

  public MatrixStack matrixStack() {
    return matrixStack;
  }
}
