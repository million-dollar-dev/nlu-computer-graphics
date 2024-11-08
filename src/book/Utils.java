package book;

import static com.jogamp.opengl.GL2ES2.*;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.Vector;

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
            tex = TextureIO.newTexture(stream, false, TextureIO.PNG); // Thay TextureIO.PNG bằng định dạng file phù hợp nếu cần
        } catch (Exception e) {
            e.printStackTrace();
        }
		int textureID = tex.getTextureObject();
		return textureID;
	}

	public static void main(String[] args) {
		System.out.println(Utils.readShaderSource("fragShader.glsl").toString());
//		System.out.println(Utils.loadTexture("brick1.png"));
	}
}
