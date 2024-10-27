package book;

import static com.jogamp.opengl.GL2ES2.*;
import com.jogamp.opengl.glu.GLU;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

public class Utils {
	public static String[] readShaderSource(String filename) {
		Vector<String> lines = new Vector<String>();
		Scanner sc;
		String[] program;
		try {
			sc = new Scanner(new File(filename));
			while (sc.hasNext()) {
				lines.addElement(sc.nextLine());
			}
			program = new String[lines.size()];
			for (int i = 0; i < lines.size(); i++) {
				program[i] = (String) lines.elementAt(i) + "\n";
			}
		} catch (IOException e) {
			System.err.println("IOException reading file: " + e);
			return null;
		}
		return program;
	}

	public static int createShaderProgram(String vShaderSource, String fShaderSource) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
//		String vshaderSource[] = { "#version 430      \n", "layout (location=0) in vec3 position;  \n",
//				"uniform mat4 mv_matrix;   \n", "uniform mat4 p_matrix;   \n", "void main(void)  \n",
//				"{  gl_Position = p_matrix * mv_matrix * vec4(position, 1.0);  }  \n"};
//		String fshaderSource[] = { "#version 430      \n", "out vec4 color;  \n",
//				"uniform mat4 mv_matrix;   \n", "uniform mat4 p_matrix;   \n", "void main(void)  \n",
//				"{  color = vec4(0.0, 0.0, 1.0, 1.0);  }  \n"};
		String vshaderSource[] = readShaderSource(vShaderSource);
		String fshaderSource[] = readShaderSource(fShaderSource);		
		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0); // 3 is the count of lines of source code
		gl.glCompileShader(vShader);
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0); // 4 is the count of lines of source code
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
	public static void main(String[] args) {
		System.out.println(Utils.readShaderSource("E:\\Workspace\\LEARN\\NLU\\ComputerGraphics\\JOGLTemplate\\src\\book\\vertShader.glsl").toString());
		System.out.println(Utils.createShaderProgram("E:\\Workspace\\LEARN\\NLU\\ComputerGraphics\\JOGLTemplate\\src\\book\\vertShader.glsl", "E:\\Workspace\\LEARN\\NLU\\ComputerGraphics\\JOGLTemplate\\src\\book\\fragShader.glsl"));
	}
}
