package book;

import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GLContext;
import org.joml.*;
import java.nio.*;
import javax.swing.*;
import java.lang.Math;

public class Cube24 extends JFrame implements GLEventListener {
	// Setup OpenGL Graphics Renderer
	// for the GL Utility
	private GLCanvas myCanvas;
	private int renderingProgram;
	private int vao[] = new int[1];
	private int vbo[] = new int[2];
	private float cameraX, cameraY, cameraZ;
	private float cubeLocX, cubeLocY, cubeLocZ;
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	private Matrix4f pMat = new Matrix4f();
	private Matrix4f vMat = new Matrix4f();
	private Matrix4f mMat = new Matrix4f();
	private Matrix4f mvMat = new Matrix4f();
	private int vLoc, pLoc;
	private float aspect;
	private String vShaderSource = "E:\\Workspace\\LEARN\\NLU\\ComputerGraphics\\JOGLTemplate\\src\\book\\vertShader24.glsl";
	private String fShaderSource = "E:\\Workspace\\LEARN\\NLU\\ComputerGraphics\\JOGLTemplate\\src\\book\\fragShader24.glsl";
	private double elapsedTime, startTime, tf, timeFactor;

	/** Constructor to setup the GUI for this Component */
	public Cube24() {
		setTitle("Chapter4 - program1a");
		setSize(600, 600);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		Animator animtr = new Animator(myCanvas);
		animtr.start();
		this.setVisible(true);
	}
	// ------ Implement methods declared in GLEventListener ------

	/**
	 * Called back immediately after the OpenGL context is initialized. Can be used
	 * to perform one-time initialization. Run only once.
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		renderingProgram = Utils.createShaderProgram(vShaderSource, fShaderSource);
		setupVertices();
		cameraX = 0.0f;
		cameraY = 0.0f;
		cameraZ = 8.0f;
		cubeLocX = 0.0f;
		cubeLocY = -2.0f;
		cubeLocZ = 0.0f;
	}

	/**
	 * Call-back handler for window re-size event. Also called when the drawable is
	 * first set to visible.
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	/**
	 * Called back by the animator to perform rendering.
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glUseProgram(renderingProgram);

		elapsedTime = System.currentTimeMillis() - startTime;
		tf = elapsedTime / 1000.0;
		vLoc = gl.glGetUniformLocation(renderingProgram, "v_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");
		int tfLoc = gl.glGetUniformLocation(renderingProgram, "tf"); // uniform for the time factor
		gl.glUniform1f(tfLoc, (float) timeFactor);
		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);
		vMat.translation(-cameraX, -cameraY, -cameraZ);
		mMat.translation(cubeLocX, cubeLocY, cubeLocZ);

		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);
		mMat.identity();
		mMat.translate((float) Math.sin(.35f * tf) * 2.0f, (float) Math.sin(.52f * tf) * 2.0f,
				(float) Math.sin(.7f * tf) * 2.0f);
		mMat.rotateXYZ(1.75f * (float) tf, 1.75f * (float) tf, 1.75f * (float) tf);
		gl.glUniformMatrix4fv(vLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glDrawArraysInstanced(GL_TRIANGLES, 0, 36, 24);

	}

	/**
	 * Called back before the OpenGL context is destroyed. Release resource such as
	 * buffers.
	 */
	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	private void setupVertices() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		float[] vertexPositions = { -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
				1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f,
				-1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f,
				-1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
				-1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
				1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f,
				-1.0f };
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(vertexPositions);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * 4, vertBuf, GL_STATIC_DRAW);
	}

	/** The entry main() method to setup the top-level container and animator */
	public static void main(String[] args) {
//		// Run the GUI codes in the event-dispatching thread for thread safety
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Cube24();
			}
		});
//				// Create the OpenGL rendering canvas
//				GLJPanel canvas = new First3D();
//				canvas.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
//
//				// Create a animator that drives canvas' display() at the specified FPS.
//				final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);
//
//				// Create the top-level container
//				final JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame
//				frame.getContentPane().add(canvas);
//				frame.addWindowListener(new WindowAdapter() {
//					@Override
//					public void windowClosing(WindowEvent e) {
//						// Use a dedicate thread to run the stop() to ensure that the
//						// animator stops before program exits.
//						new Thread() {
//							@Override
//							public void run() {
//								if (animator.isStarted())
//									animator.stop();
//								System.exit(0);
//							}
//						}.start();
//					}
//				});
//				frame.setTitle(TITLE);
//				frame.pack();
//				frame.setVisible(true);
//				animator.start(); // start the animation loop
//			}
//		});

	}

}