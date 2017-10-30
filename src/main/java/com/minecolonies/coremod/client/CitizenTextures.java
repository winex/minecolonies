package com.minecolonies.coremod.client;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.coremod.MineColonies;
import com.minecolonies.coremod.client.render.RenderBipedCitizen.Model;
import com.minecolonies.coremod.util.MetricsUtils;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CitizenTextures
{
	private static final Map<String, IntList> TEXTURES = new HashMap<>();
	
	public static boolean bindTexture(Model model, boolean female, long id)
	{
		IntList ids = TEXTURES.get(bakeType(model, female));
		if(ids == null || ids.isEmpty())
			return false;
		int gl = ids.getInt((int) (id % (long) ids.size()));
		GlStateManager.bindTexture(gl);
		return true;
	}
	
	public static String bakeType(Model mod, boolean female)
	{
		return mod.textureBase.toLowerCase() + (female ? "/female" : "/male");
	}
	
	public static void refreshTextures()
	{
		MineColonies.getLogger().info("Refreshing citizen textures...");
		long time = MetricsUtils.getExecutionMilliTime(() ->
		{
			MineColonies.getLogger().info("Performing texture clean-up...");
			/** Cleanup */
			for(IntList list : TEXTURES.values())
				for(int i : list)
					GL11.glDeleteTextures(i);
			TEXTURES.clear();
			
			File textures = new File(Minecraft.getMinecraft().mcDataDir, Constants.MOD_ID + File.separator + "citizen-textures");
			
			if(!textures.isDirectory())
				textures.mkdirs();
			
			File males = new File(textures, "male");
			File females = new File(textures, "female");
			
			if(!males.isDirectory())
				males.mkdirs();
			
			if(!females.isDirectory())
				females.mkdirs();
			
			/** Define Textures */
			Map<String, List<BufferedImage>> IMAGES = new HashMap<>();
			for(File sub : new File[] { males, females })
				for(Model type : Model.values())
				{
					MineColonies.getLogger().debug("Loading all textures " + type.textureBase.toLowerCase() + " for " + sub.getName());
					
					boolean unpack = false;
					File sub2 = new File(sub, type.textureBase.toLowerCase());
					if(unpack = !sub2.isDirectory())
						sub2.mkdirs();
					
					File[] matching = sub2.listFiles(f -> f.isFile() && f.getName().endsWith(".png") && asInt(f.getName().substring(0, f.getName().length() - 4)) > 0);
					unpack |= matching.length == 0;
					
					if(unpack)
					{
						extractAllTextures(sub2, type, sub.getName().equals("female"));
						matching = sub2.listFiles(f -> f.isFile() && f.getName().endsWith(".png") && asInt(f.getName().substring(0, f.getName().length() - 4)) > 0);
					}
					
					String ti = type.textureBase.toLowerCase() + "/" + sub.getName();
					
					for(File m : matching)
						try
						{
							BufferedImage img = ImageIO.read(m);
							if(img == null)
								MineColonies.getLogger().warn("File \"" + Constants.MOD_ID + "/" + sub.getName() + "/" + sub2.getName() + "/" + m.getName() + ".png\" is not an image! Please fix this!");
							else
							{
								List<BufferedImage> imgs = IMAGES.get(ti);
								if(imgs == null)
									IMAGES.put(ti, imgs = new ArrayList<>());
								imgs.add(img);
							}
						} catch(Throwable err)
						{
							err.printStackTrace();
						}
				}
			
			TEXTURES.putAll(uploadToOpenGL(IMAGES));
			
		});
		MineColonies.getLogger().info("Citizen textures reloaded in " + time + " ms.");
	}
	
	public static void extractAllTextures(File folder, Model type, boolean female)
	{
		MineColonies.getLogger().info("Unpacking textures for " + folder.getName() + " (" + (female ? "Female" : "Male") + ")...");
		int i = 1;
		while(true)
		{
			InputStream in = MineColonies.class.getResourceAsStream("/assets/" + Constants.MOD_ID + "/textures/entity/" + type.textureBase.toLowerCase() + (female ? "female" : "male") + i + ".png");
			if(in == null)
				break;
			File target = new File(folder, i + ".png");
			try
			{
				FileOutputStream fos = new FileOutputStream(target);
				int red = 0;
				byte[] buf = new byte[1024 * 4];
				while((red = in.read(buf)) > 0)
					fos.write(buf, 0, red);
				fos.close();
			} catch(Throwable err)
			{
				err.printStackTrace();
			}
			++i;
		}
		MineColonies.getLogger().info(" -Unpacked " + (i - 1) + " textures.");
	}
	
	public static int asInt(String text)
	{
		try
		{
			return Integer.parseInt(text);
		} catch(Throwable e)
		{
			return -1;
		}
	}
	
	public static Map<String, IntList> uploadToOpenGL(Map<String, List<BufferedImage>> imgs)
	{
		Map<String, IntList> map = new HashMap<>();
		
		imgs.keySet().forEach(key ->
		{
			List<BufferedImage> img = imgs.get(key);
			IntList tex = new IntArrayList();
			img.forEach(i ->
			{
				int id = GL11.glGenTextures();
				uploadToOpenGL(i, id, false);
				tex.add(id);
			});
			map.put(key, tex);
		});
		
		return map;
	}
	
	public static void uploadToOpenGL(BufferedImage image, int id, boolean mirrorEffect)
	{
		if(image == null)
			return;
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
		for(int y = 0; y < image.getHeight(); y++)
			for(int x = 0; x < image.getWidth(); x++)
			{
				int pixel = pixels[y * image.getWidth() + (mirrorEffect ? image.getWidth() - x - 1 : x)];
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		buffer.flip();
		glBindTexture(GL_TEXTURE_2D, id);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
	}
}