package book;

import static com.jogamp.opengl.GL2ES2.*;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.Vector;

import javax.imageio.ImageIO;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

public class Utils {
	public static String[] readShaderSource(String filename) {
		Vector<String> lines = new Vector<String>();
		String[] program;
		InputStream inputStream = Utils.class.getClassLoader().getResourceAsStream("shaders/" + filename);
		if (inputStream == null) {
			System.err.println("File not found: " + filename);
			return null;
		}
		try (Scanner sc = new Scanner(inputStream)) {
			while (sc.hasNext()) {
				lines.addElement(sc.nextLine());
			}
			program = new String[lines.size()];
			for (int i = 0; i < lines.size(); i++) {
				program[i] = (String) lines.elementAt(i) + "\n";
			}
		}
		return program;
	}

	public static int createShaderProgram(String vShaderSource, String fShaderSource) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		String vshaderSource[] = readShaderSource(vShaderSource);
		String fshaderSource[] = readShaderSource(fShaderSource);
		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0); // 3 is the count of lines of source
																					// code
		gl.glCompileShader(vShader);
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0); // 4 is the count of lines of source
																					// code
		gl.glCompileShader(fShader);
		int vfProgram = gl.glCreateProgram();
		gl.glAttachShader(vfProgram, vShader);
		gl.glAttachShader(vfProgram, fShader);
		gl.glLinkProgram(vfProgram);
		gl.glDeleteShader(vShader);
		gl.glDeleteShader(fShader);
		return vfProgram;
	}

	public static void printShaderLog(int shader) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;
		// determine the length of the shader compilation log
		gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0) {
			log = new byte[len[0]];
			gl.glGetShaderInfoLog(shader, len[0], chWrittn, 0, log, 0);
			System.out.println("Shader Info Log: ");
			for (int i = 0; i < log.length; i++) {
				System.out.print((char) log[i]);
			}
		}
	}

	public static void printProgramLog(int prog) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;
		// determine the length of the program linking log
		gl.glGetProgramiv(prog, GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0) {
			log = new byte[len[0]];
			gl.glGetProgramInfoLog(prog, len[0], chWrittn, 0, log, 0);
			System.out.println("Program Info Log: ");
			for (int i = 0; i < log.length; i++) {
				System.out.print((char) log[i]);
			}
		}
	}

	public static boolean checkOpenGLError() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		boolean foundError = false;
		GLU glu = new GLU();
		int glErr = gl.glGetError();
		while (glErr != GL_NO_ERROR) {
			System.err.println("glError: " + glu.gluErrorString(glErr));
			foundError = true;
			glErr = gl.glGetError();
		}
		return foundError;
	}

	public static int loadTexture(String textureFileName) {
		Texture tex = null;
		try {
			InputStream stream = Utils.class.getClassLoader().getResourceAsStream("assets/" + textureFileName);
			if (stream == null) {
				throw new RuntimeException("File not found " + textureFileName);
			}
			tex = TextureIO.newTexture(stream, false, TextureIO.PNG); // Thay TextureIO.PNG bằng định dạng file phù hợp
																		// nếu cần
		} catch (Exception e) {
			e.printStackTrace();
		}
		int textureID = tex.getTextureObject();
		return textureID;
	}

	public static int loadTextureAWT(String textureFileName) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		BufferedImage textureImage = getBufferedImage(textureFileName);
		byte[] imgRGBA = getRGBAPixelData(textureImage, true);
		ByteBuffer rgbaBuffer = Buffers.newDirectByteBuffer(imgRGBA);
		int[] textureIDs = new int[1];
		gl.glGenTextures(1, textureIDs, 0);
		int textureID = textureIDs[0];
		gl.glBindTexture(GL_TEXTURE_2D, textureID);
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, textureImage.getWidth(), textureImage.getHeight(), 0, GL_RGBA,
				GL_UNSIGNED_BYTE, rgbaBuffer);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		return textureID;
	}

	private static BufferedImage getBufferedImage(String fileName) {
		BufferedImage img;
		try {
			img = ImageIO.read(new File(fileName));
		} catch (IOException e) {
			System.err.println("Error reading '" + fileName + '"');
			throw new RuntimeException(e);
		}
		return img;
	}

	private static byte[] getRGBAPixelData(BufferedImage img, boolean flip) {
		byte[] imgRGBA;
		int height = img.getHeight(null);
		int width = img.getWidth(null);
		WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, 4, null);
		ComponentColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
				new int[] { 8, 8, 8, 8 }, true, false, ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);
		// bits, has Alpha, isAlphaPreMultiplied
		// transparency
		// data transfer type
		BufferedImage newImage = new BufferedImage(colorModel, raster, false, null);
		Graphics2D g = newImage.createGraphics();
		if (flip) {
			AffineTransform gt = new AffineTransform();
			gt.translate(0, height);
			gt.scale(1, -1d);
			g.transform(gt);
		}
		g.drawImage(img, null, null);
		g.dispose();
		DataBufferByte dataBuf = (DataBufferByte) raster.getDataBuffer();
		imgRGBA = dataBuf.getData();
		return imgRGBA;
	}

	public static void main(String[] args) {
		System.out.println(Utils.readShaderSource("fragShader.glsl").toString());
//		System.out.println(Utils.loadTexture("brick1.png"));
	}
}
