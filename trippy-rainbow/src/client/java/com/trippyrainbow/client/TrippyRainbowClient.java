package com.trippyrainbow.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

/**
 * Trippy Rainbow: a full-screen, animated, psychedelic rainbow wash drawn over the game view.
 *
 * Three layers, all drawn with plain filled rectangles:
 *  1. A warped hue field (the whole screen is a grid of cells whose hue ripples with sine waves).
 *  2. Scrolling rainbow bars along the top and bottom edges.
 *  3. A pulsing hue-cycling border frame.
 */
public class TrippyRainbowClient implements ClientModInitializer {
	public static final String MOD_ID = "trippyrainbow";

	// Tweak these to taste.
	private static final int CELL = 8;              // size of each color cell, in GUI pixels (smaller = smoother, slower)
	private static final int BASE_ALPHA = 46;       // 0-255, how strong the rainbow wash is
	private static final int PULSE_ALPHA = 26;      // extra alpha that pulses in and out
	private static final double SPEED = 0.18;       // how fast the hues cycle
	private static final int BAR = 7;               // thickness of the edge bars, in GUI pixels

	private static final long START = System.nanoTime();

	@Override
	public void onInitializeClient() {
		HudElementRegistry.attachElementBefore(
				VanillaHudElements.CHAT,
				Identifier.fromNamespaceAndPath(MOD_ID, "rainbow_overlay"),
				TrippyRainbowClient::extract);
	}

	private static void extract(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		Minecraft mc = Minecraft.getInstance();
		int w = mc.getWindow().getGuiScaledWidth();
		int h = mc.getWindow().getGuiScaledHeight();
		double t = (System.nanoTime() - START) / 1_000_000_000.0;

		int alpha = BASE_ALPHA + (int) (PULSE_ALPHA * (0.5 + 0.5 * Math.sin(t * 2.1)));

		// Layer 1: warped hue field.
		for (int y = 0; y < h; y += CELL) {
			for (int x = 0; x < w; x += CELL) {
				double warp = Math.sin(x * 0.021 + t * 1.3) + Math.cos(y * 0.027 - t * 0.9)
						+ Math.sin((x + y) * 0.013 + t * 0.7);
				float hue = (float) ((x + y) * 0.0021 + warp * 0.11 + t * SPEED);
				graphics.fill(x, y, Math.min(x + CELL, w), Math.min(y + CELL, h), hsv(hue, 0.9f, 1.0f, alpha));
			}
		}

		// Layer 2: scrolling rainbow bars, top and bottom, running in opposite directions.
		for (int x = 0; x < w; x += 4) {
			float topHue = (float) (x * 0.004 + t * 0.35);
			float botHue = (float) (x * 0.004 - t * 0.35);
			graphics.fill(x, 0, Math.min(x + 4, w), BAR, hsv(topHue, 1.0f, 1.0f, 200));
			graphics.fill(x, h - BAR, Math.min(x + 4, w), h, hsv(botHue, 1.0f, 1.0f, 200));
		}

		// Layer 3: pulsing hue-cycling side borders.
		int side = 3 + (int) (3 * (0.5 + 0.5 * Math.sin(t * 3.0)));
		for (int y = 0; y < h; y += 4) {
			float hue = (float) (y * 0.005 + t * 0.5);
			int c = hsv(hue, 1.0f, 1.0f, 170);
			graphics.fill(0, y, side, Math.min(y + 4, h), c);
			graphics.fill(w - side, y, w, Math.min(y + 4, h), c);
		}
	}

	/** HSV (all 0..1, hue wraps) to packed ARGB. */
	private static int hsv(float hue, float sat, float val, int alpha) {
		hue = hue - (float) Math.floor(hue);
		float scaled = hue * 6.0f;
		int sector = (int) scaled;
		float f = scaled - sector;
		float p = val * (1.0f - sat);
		float q = val * (1.0f - f * sat);
		float tt = val * (1.0f - (1.0f - f) * sat);
		float r;
		float g;
		float b;
		switch (sector % 6) {
			case 0 -> { r = val; g = tt; b = p; }
			case 1 -> { r = q; g = val; b = p; }
			case 2 -> { r = p; g = val; b = tt; }
			case 3 -> { r = p; g = q; b = val; }
			case 4 -> { r = tt; g = p; b = val; }
			default -> { r = val; g = p; b = q; }
		}
		return ((alpha & 0xFF) << 24)
				| (((int) (r * 255.0f) & 0xFF) << 16)
				| (((int) (g * 255.0f) & 0xFF) << 8)
				| ((int) (b * 255.0f) & 0xFF);
	}
}
