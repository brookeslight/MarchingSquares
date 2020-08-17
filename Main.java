package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class Main extends Canvas implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7604959185792845431L;
	private boolean running;
	private Thread thread;
	private int res;
	private int row;
	private int col;
	private float[][] grid;

	public static void main(String[] args) {
		new Main().start();
	}
	
	public synchronized void start() {
		if(this.running == true) {
			return;
		}
		this.thread = new Thread(this);
		this.thread.start();
		this.running = true;
	}
	
	public synchronized void stop() {
		this.running = false;
		//clean up
	}
	
	private void init() {
		JFrame frame = new JFrame("2D Game");
		frame.setSize(800, 800);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setUndecorated(true);
		frame.setVisible(true);
		this.requestFocus();
		//init
		this.res = 20; //Pixel distance between each point
		this.row = (this.getHeight() / this.res) + 1;
		this.col = (this.getWidth() / this.res) + 1;
		this.grid = new float[this.row][this.col];
		//fill with random number
		for(int i = 0; i < this.row; i++) {
			for(int j = 0; j < this.col; j++) {
				this.grid[i][j] = (float) Math.random();
			}
		}
	}
	
	@Override
	public void run() {
		this.init();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		while(this.running == true) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
				this.tick();
				updates++;
				delta--;
			}
			this.render();
			frames++;
			
			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames + " TICKS: " + updates);
				frames = 0;
				updates = 0;
			}
		}
		System.exit(0);
	}
	
	private void tick() {
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform af = g2d.getTransform();
//		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//start draw
			//bg
		g.setColor(Color.gray);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
			//render points
		for(int i = 0; i < this.row; i++) {
			for(int j = 0; j < this.col; j++) {
				g.setColor(new Color(this.grid[i][j], this.grid[i][j], this.grid[i][j]));
				g.fillRect(i*this.res, j*this.res, 1, 1);
			}
		}
		g2d.setColor(Color.DARK_GRAY);
		for(int i = 0; i < this.row - 1; i++) {
			for(int j = 0; j < this.col - 1; j++) {
				float x = i * this.res;
				float y = j * this.res;
				Point2D.Float a = new Point2D.Float(x + (this.res / 2), y);
				Point2D.Float b = new Point2D.Float(x + this.res, y + (this.res / 2));
				Point2D.Float c = new Point2D.Float(x + (this.res / 2), y + this.res);
				Point2D.Float d = new Point2D.Float(x, y + (this.res / 2));
				switch(this.getState(Math.round(this.grid[i][j]), Math.round(this.grid[i + 1][j]), Math.round(this.grid[i + 1][j + 1]), Math.round(this.grid[i][j + 1]))) {
				case 1:
					this.drawLine(c, d, g2d);
					break;
				case 2:
					this.drawLine(b, c, g2d);
					break;
				case 3:
					this.drawLine(b, d, g2d);
					break;
				case 4:
					this.drawLine(a, b, g2d);
					break;
				case 5:
					this.drawLine(a, d, g2d);
					this.drawLine(b, c, g2d);
					break;
				case 6:
					this.drawLine(a, c, g2d);
					break;
				case 7:
					this.drawLine(a, d, g2d);
					break;
				case 8:
					this.drawLine(a, d, g2d);
					break;
				case 9:
					this.drawLine(a, c, g2d);
					break;
				case 10:
					this.drawLine(a, b, g2d);
					this.drawLine(c, d, g2d);
					break;
				case 11:
					this.drawLine(a, b, g2d);
					break;
				case 12:
					this.drawLine(b, d, g2d);
					break;
				case 13:
					this.drawLine(b, c, g2d);
					break;
				case 14:
					this.drawLine(c, d, g2d);
					break;
				}
			}
		}
		//end draw
		g2d.setTransform(af);
		g.dispose();
		bs.show();
	}
	
	private int getState(int a, int b, int c, int d) {
		return (8 * a) + (4 * b) + (2 * c) + (1 * d);
	}
	
	private void drawLine(Point2D.Float a, Point2D.Float b, Graphics2D g2d) {
		g2d.draw(new Line2D.Float(a, b));
	}

}