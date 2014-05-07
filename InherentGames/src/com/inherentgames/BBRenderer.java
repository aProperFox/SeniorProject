package com.inherentgames;

import java.util.ConcurrentModificationException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.android.texample2.GLText;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.IPaintListener;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.World;

/**
 * @author Tyler
 * Renders the state of the game. This is the second main class for the entire game. It handles rendering everything
 * in the level and on screen. It also calls BBGame.update() since it has to be updated often.
 */
class BBRenderer implements GLSurfaceView.Renderer {
	
	// Library objects
	protected static FrameBuffer fb;
	private BBTextureManager tm;
	private World loadingWorld;
	private BBGame game;
	
	/* Internal parameters */
	
	private GLText textSmall;
	private GLText textMedium;
	private GLText textLarge;
	
	// Represents the "cleared" background color of the frame buffer 
	private RGBColor bg = new RGBColor( 255, 255, 255 );
	
	private int letterWidth;
	
	int soundID = 1;
	
	private float timeHeight;
	
	private int arrowX;
	private int arrowY;
	
	private String arrowState = "ArrowUp";
	
	private int paintCount = 0;
	
	private static final Texture REFERENCE_POINT = new Texture( 8, 8, RGBColor.WHITE );

	private int halfWidth;
	private int halfHeight;
	private int maxLoadingTexWidth;
	private int maxLoadingTexHeight;
	
	private static int charHeight = 16;
	private static int charWidth = 9;
	private static int charPerLine = 32;
	
	private float[] mProjMatrix = new float[16];
	private float[] mVMatrix = new float[16];
	private float[] mVPMatrix = new float[16];
	
	/**
	 * The Constructor for BBRenderer. Instantiates some local parameters.
	 */
	public BBRenderer( ) {
		
		// Initialize variables
		tm = BBTextureManager.getInstance();
		game = BBGame.getInstance();
		
		halfWidth = BB.width / 2;
		halfHeight = BB.height / 2;
		maxLoadingTexWidth = Math.min( 512,  BB.width );
		maxLoadingTexHeight = Math.min( 1024,  BB.height );
		
		letterWidth = BB.width / 85;
		
		arrowX = BB.width / 12;
		arrowY = BB.height / 10 + BB.width / 6;
	}
	
	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
	 */
	public void onSurfaceCreated( GL10 unused, EGLConfig config ) {
		textSmall = new GLText( BB.context.getAssets() );
		textSmall.load( "futura-normal.ttf", BB.width / 35, 2, 2 );
		textMedium = new GLText( BB.context.getAssets() );
		textMedium.load( "futura-normal.ttf", BB.width / 25, 2, 2 );
		textLarge = new GLText( BB.context.getAssets() );
		textLarge.load( "futura-normal.ttf", BB.width / 15, 2, 2 );
	}
	
	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
	 */
	public void onSurfaceChanged( GL10 unused, int w, int h ) {
		// Clear the frame buffer if it's not clean
		if ( fb != null ) {
			fb.dispose();
		}
		// Initialize a new frame buffer with the new frame/screen size
		fb = new FrameBuffer( w, h );
		
		// Create and render a dummy world for the loading screen
		// Note: We need to do this because jPCT-AE won't allow us to blit 
		//       a frame buffer until we've rendered a world to it at least once.
		loadingWorld = new World();
		loadingWorld.renderScene( fb );
		
		// Watch the frame buffer for paint events
		fb.setPaintListener( new IPaintListener() {

			private static final long serialVersionUID = 449987772860108478L;

			@Override
			// When the frame buffer finishes painting the first time, we have our 
			// loading screen painted. Then, we trigger the resource loader runnable
			// to load the actual game resources asynchronously. When the actual game
			// loads, it clears the frame buffer and starts painting the actual game scene.
			public void finishedPainting() {
				++paintCount;
				if ( paintCount == 1 )
					new Thread( game.loader ).start();
				if ( !game.loading ) {
					Log.d( "BBRenderer", "Finished loading game" );
					paintCount = 0;
					fb.setPaintListener( null );
				}
			}

			@Override
			public void startPainting() {}
			
		} );
		
		// Calculate rendering matrices for GLText
		float ratio = (float) BB.width / BB.height;
		
		if ( BB.width > BB.height ) {
			Matrix.frustumM( mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 10 );
		} else {
			Matrix.frustumM( mProjMatrix, 0, -1, 1, -1/ratio, 1/ratio, 1, 10 );
		}
		
		int useForOrtho = Math.min(BB.width, BB.height);

		//TODO: Is this wrong?
		Matrix.orthoM(mVMatrix, 0, -useForOrtho/2, useForOrtho/2, -useForOrtho/2, useForOrtho/2, 0.1f, 100f);
		Matrix.multiplyMM( mVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0 );
		
	}
	
	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
	 */
	public void onDrawFrame( GL10 unused ) {
		
		// Clear the frame buffer
		fb.clear( bg );
		
		// Render loading screen if the game is loading
		if ( game.loading ) {
			
			loadingWorld.draw( fb );
			blitImage("loading_backdrop", halfWidth, halfHeight, 1024, 1024, BB.width, (int) (BB.width / 1.3333f), game.loadingProgress);
			blitImage("loading_splash", halfWidth, halfHeight, 512, 1024, maxLoadingTexWidth, maxLoadingTexHeight, 100);
			
			// Switch to the frame buffer
			fb.display();
			
			/* The following code needs to come after switching to the frame buffer */
			drawText ( textSmall, "LOADING...", (int) (BB.width / 1.239f), (int) (BB.height / 1.054), RGBColor.BLACK, false);
			
		} else {
			
			// Update game elements
			game.update();
			
			
			// Render sky box (if applicable)
			if ( game.world.skybox != null )
				game.world.skybox.render( game.world, fb );
		    
		    // Render the world, and then draw to the frame buffer
			game.world.renderScene( fb );
			game.world.draw( fb );
			
			timeHeight = (int) (((game.timeLeft/100.0f) * (BB.height * 0.76)));
			
			// Render the HUD/sprite elements
			display2DGameInfo();
			
			/*
			 * TODO: add color to WordObjects when camera is aimed at them
			int id = world.getCameraBox().checkForCollision( cam.getDirection(), 80 );
			if ( id != -100 ) {
				WordObject wordObject = ( WordObject )world.getObject( id );
				if ( wordObject.getStaticState() ) {
				Log.i( "BBRenderer", "Viewed object collision!" );
					//wordObject.setAdditionalColor( 255, 255, 0 );
				}
			}
			*/
		}
			
	}
	
	/**
	 * @return - the current FrameBuffer being used by the renderer
	 */
	public static FrameBuffer getFrameBuffer() {
		return fb;
	}
	
	/**
	 * Displays the on screen 2D sprites to be rendered. Has different steps for displaying tutorial sprites.
	 */
	public void display2DGameInfo() {
		int w = BB.width;
		int h = BB.height;
		
		if ( game.isTutorial ) {
			//CrossHair
			blitCrosshair( w, h );
			
			if ( game.hasCompletedSteps ) {
				
				// Bubble image
				blitImage( game.bubbleTex, halfWidth, h, 256, 256, w/3, w/3, 5 );
				// Bubble text
				drawText ( textLarge, game.world.getBubbleArticle(), (int) (BB.width / 2.11f), (int) (BB.height / 1.08f), RGBColor.WHITE, false);
				// Fire Button
				blitButton( game.fireButton );
				// Pause Button
				blitButton( game.pauseButton );
				// Dynamic fuel/time bars
				blitImageBottomUp( "TimeBar", (int) ( BB.width*0.966 ), halfHeight, 16, 512, BB.width/38, (int)timeHeight, (int) ( BB.height*0.76 ), 100 );
				// Score bars 
				blitImage( "ScoreBars", w-( w/16 ), halfHeight, 128, 512, w/8, ( int )( h*0.9 ), 100 );
				
				
			} else {
				
				displayScreenItem( fb, game.wattsonPrivileges );
			}

			//Info Bar
			//Has extra 1 px hang if using real size? Decremented to 127x127
			blitImage( "InfoBar", w/10, w/10, 127, 127, w/5, w/5, 100 );

			try {
				// Wattson help text
				int iteration = 0;
				for ( String string : game.wattsonText ) {
					drawText ( textSmall, string, w/6, h/23 + ( letterWidth*2*iteration ), RGBColor.WHITE, false);
					iteration++;
				}
			} catch ( ConcurrentModificationException e ) {
				Log.e( "BBRenderer", "display2DGameInfo got ConcurrentModificationError: " + e );
			}
			
			
		}
		
		// Not tutorial displays
		else {
			
			if (game.fireButton.isActive() ) {
				blitImage( "Crosshair", halfWidth, halfHeight, 2048, 1024, (int) (h * 16f / 9f), h, 30);
			} else {
				blitCrosshair( w, h );
			}
			// Bubble image
			blitImage( game.bubbleTex, halfWidth, h, 256, 256, w/3, w/3, 5 );
			// Bubble text
			drawText ( textLarge, game.world.getBubbleArticle(), (int) (BB.width / 2.11f), (int) (BB.height / 1.08f), RGBColor.WHITE, false);
			// Fire Button
			blitButton( game.fireButton );
			// Pause Button
			blitButton( game.pauseButton );
			// score buttons
			blitButton( game.scoreButton );
			//Score
			drawText ( textMedium, Integer.toString(game.score), game.scoreButton.posX + game.scoreButton.width / 8, game.scoreButton.posY - game.scoreButton.height / 10, RGBColor.WHITE, true );
			//Multiplier
			String multiplier = game.multiplier + "X";
			drawText ( textSmall,multiplier, game.scoreButton.posX + game.scoreButton.width / 6, game.scoreButton.posY - game.scoreButton.height / 5, RGBColor.WHITE, false);
			// Dynamic time bar
			blitImageBottomUp( "TimeBar", (int) (w * 0.966), h/2, 16, 512, w/38, (int) timeHeight, (int) (h * 0.76), 100 );
			// Score bars
			blitImage( "ScoreBars", w-( w/16 ), h/2, 128, 512, w/8, ( int )( h*0.9 ), 100 );
			// Draw word when correct answer guessed
			drawText( textLarge, game.answer.data, (int) game.answer.location.x, (int) game.answer.location.y, game.answer.color, false );
			
		}
		
		fb.display();

		
	}
	
	/**
	 * Displays screen items based on wattsonPrivileges as each state of the tutorial needs different displays.
	 * 
	 * @param fb - the current FrameBuffer to display to
	 * @param itemNum - the state of the tutorial that defines what is to be displayed
	 */
	private void displayScreenItem( FrameBuffer fb, int itemNum ) {
		int w = BB.width;
		int h = BB.height;
		
		boolean isLastItem;
		if ( game.wattsonPrivileges == itemNum )
			isLastItem = true;
		else
			isLastItem = false;
		switch( itemNum ) {
			case 0:
				break;
			case 1:
				blitImage( "Filter", halfWidth, halfHeight, 64, 64, w, h, 10 );
				arrowX = BB.width / 12;
				arrowY = BB.height / 10 + BB.width / 6;
				arrowState = "ArrowUp";
				blitImage( arrowState, arrowX, arrowY + game.arrowMod, 128, 128, BB.width / 8, BB.width / 8, 100 );
				break;
			case 2:
				break;
			case 4:
				 if ( game.wattsonPrivileges == itemNum )
					 game.cam.lookAt( new SimpleVector( 0, 0.1, 1 ) );
				 if ( isLastItem )
					 game.cam.lookAt( new SimpleVector( -.358, 0.174, 0.917 ) );
				 
				 //Bubble image
				 blitImage( game.bubbleTex, halfWidth, BB.height, 256, 256, BB.width/3, BB.width/3, 5 );
				 //Bubble text
				 drawText ( textLarge, game.world.getBubbleArticle(), (int) (BB.width / 2.11f), (int) (BB.height / 1.08f), RGBColor.WHITE, false);
				 //Dynamic fuel bar
				 blitImageBottomUp("TimeBar", (int)( BB.width*0.966 ), halfHeight, 16, 512, BB.width/38, (int) timeHeight, (int)( BB.height*0.76 ), 100 );
				 //blitImageBottomUp("FuelBar", (int)( BB.width*0.909 ), BB.height/2, 16, 512, BB.width/38, (int) fuelHeight, (int)( BB.height*0.76 ), 100 );
				 blitImage("ScoreBars", BB.width-( BB.width/16 ), halfHeight, 128, 512, BB.width/8, (int)( BB.height*0.9 ), 100 );
				
				 
				 if ( !game.fireButton.isActive() ) {
					 arrowX = game.fireButton.posX;
					 arrowY = game.fireButton.posY - game.fireButton.height;
					 arrowState = "ArrowDown";
					 blitImage( "Filter", halfWidth, halfHeight, 64, 64, w, h, 10 );
					 blitImage( arrowState, arrowX, arrowY + game.arrowMod, 128, 128, BB.width / 8, BB.width / 8, 100 );
					 }
				 else {
					 arrowState = "NoArrow";
					 game.handDir = -1;
					 if ( game.handTransparency != 0 ) {
						 blitImage( "Hand", 9 * BB.width / 12, halfHeight + game.handMod, 128, 128, BB.width/8, BB.width/8, game.handTransparency );
					 } else {
						 
					 }
				 
				 }
				 //Fire Button
				 blitButton( game.fireButton );

				break;
			case 8:
				
 				 //Bubble image
 				 blitImage( game.bubbleTex, halfWidth, BB.height, 256, 256, BB.width / 3, BB.width / 3, 5 );
 				 //Bubble text
 				 drawText ( textLarge, game.world.getBubbleArticle(), (int) (BB.width / 2.11f), (int) (BB.height / 1.08f), RGBColor.WHITE, false);
				 //Dynamic fuel bar
				 blitImageBottomUp( "TimeBar", (int) ( BB.width*0.966 ), halfHeight, 16, 512, BB.width/38, (int) timeHeight, (int) ( BB.height*0.76 ), 100 );
				 //blitImageBottomUp( "FuelBar", (int) ( BB.width*0.909 ), BB.height/2, 16, 512, BB.width/38, (int) fuelHeight, (int) ( BB.height*0.76 ), 100 );
				 blitImage( "ScoreBars", BB.width-( BB.width/16 ), halfHeight, 128, 512, BB.width/8, (int) ( BB.height*0.9 ), 100 );
				 	
 				 
				 if ( !game.fireButton.isActive() ) {
					  arrowX = game.fireButton.posX;
					  arrowY = game.fireButton.posY - game.fireButton.height;
					  arrowState = "ArrowDown";
					  blitImage( "Filter", halfWidth, halfHeight, 64, 64, w, h, 10 );
					  blitImage( arrowState, arrowX, arrowY + game.arrowMod, 128, 128, BB.width / 8, BB.width / 8, 100 );
				  }
	  			 else {
					 arrowState = "NoArrow";
					 game.handDir = 1;
					 if ( game.handTransparency != 0) {
						 blitImage( "Hand", 9 * BB.width / 12, halfHeight + game.handMod, 128, 128, BB.width / 8, BB.width / 8, game.handTransparency );
					 } else {
						 
					 }
	  			 }
				 
				 //Fire Button
				 blitButton( game.fireButton );
			 
  				 break;
			case 17:
				blitImage( "Filter", halfWidth, halfHeight, 64, 64, w, h, 10 );
				blitImageBottomUp( "TimeBar", (int) ( BB.width*0.966 ), halfHeight, 16, 512, BB.width/38, (int) timeHeight, (int) ( BB.height*0.76 ), 100 );
				blitImage("ScoreBars", BB.width-( BB.width/16 ), halfHeight, 128, 512, BB.width/8, (int)( BB.height*0.9 ), 100 );
				arrowX = BB.width / 12;
				arrowY = BB.height / 10 + BB.width / 6;
				arrowState = "ArrowUp";
				if ( game.timeIcon.hasFinished ) {
					blitImage( arrowState, arrowX, arrowY + game.arrowMod, 128, 128, BB.width / 8, BB.width / 8, 100 );
				}
				else {
					blitImage(game.timeIcon.data, (int) game.timeIcon.location.x, (int) game.timeIcon.location.y, 512, 512, (int) game.timeIcon.width, (int) game.timeIcon.height, 100 );
				}
				break;
			default:
				break;
		}
		
	}

	
	/**
	 * Converts the JPCT-AE SimpleVector to the jBullet Vector3f as the graphics and physics engine use different
	 * vector types.
	 * 
	 * @param vector - JPCT-AE SimpleVector to be converted
	 * @return - jBullet Vector3f created by given vector
	 */
	public Vector3f toVector3f( SimpleVector vector ) {
		return new Vector3f( vector.x, vector.y, vector.z );
	}
	
	/**
	 * Converts the jBullet Vector3f to the JPCT-AE SimpleVector as the graphics and physics engine use different
	 * vector types.
	 * 
	 * @param vector - jBullet Vector3f to be converted
	 * @return - JPCT-AE SimpleVector created by given vector
	 */
	public SimpleVector toSimpleVector( Vector3f vector ) {
		return new SimpleVector( vector.x, vector.y, vector.z );
	}


	/**
	 * Displays text on the screen. it will switch the FrameBuffer so it has priority. It converts the GLText
	 * coordinates to screen coordinates since GLText coords look like:
	 * *******************
	 * *(-,+)       (+,+)*
	 * *      (0,0)      *
	 * *(-,-)       (+,-)*
	 * *******************
	 * and screen coords look like:
	 * (0,0)**************
	 * *                 *
	 * *                 *
	 * *                 *
	 * **********(MAX,MAX)
	 * 
	 * @param size - the GLText to be used, called size because GLText doesn't have dynamic size and we have three sizes
	 * @param text - the text to be displayed
	 * @param pixelX - the leftmost x coordinate of the text in pixels (unless reverse == true).
	 * @param pixelY - the topmost y coordinate of the text in pixels.
	 * @param color - the color of the text
	 * @param reverse - if true, will make pixelX the right most coordinate of text in pixels
	 */
	public void drawText( GLText size, String text, int pixelX, int pixelY, RGBColor color, boolean reverse ) {
		// Switch frame buffers
		fb.display();
		
		// Enable texture + alpha blending (need to do this every time because JPCT-AE presumably
		// turns it off)
		GLES20.glEnable( GLES20.GL_BLEND );
		GLES20.glBlendFunc( GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA );
		size.begin( color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1.0f, mVPMatrix );
		
		if ( reverse ) {
			size.drawC( text, pixelX - ( BB.width / 2 ), ( BB.height / 2 ) - pixelY );
		} else {
			size.draw( text, pixelX - ( BB.width / 2 ), ( BB.height / 2 ) - pixelY );
		}
		
		size.end();
		GLES20.glDisable( GLES20.GL_BLEND );
		
	}
	
	/**
	 * Displays an image on screen using the frame buffer
	 * 
	 * @param textureName - the texture name, grabbed from JPCT-AE's TextureManager
	 * @param x - center x coordinate of the image on screen (in pixels)
	 * @param y - center y coordinate of the image on screen (in pixels)
	 * @param imageWidth - loaded width of the given image (always a power of 2)
	 * @param imageHeight - loaded height of the given image (always a power of 2)
	 * @param width - the on screen width of the image (in pixels)
	 * @param height - the on screen height of the image (in pixels)
	 * @param transparency - the transparency of the image (max is ~20, -1 means no transparency)
	 */
	public void blitImage( String textureName, int x, int y, int imageWidth, int imageHeight, int width, int height, int transparency ) {
		Texture image = tm.getTexture( textureName );
		fb.blit( image, 0, 0, ( x-width/2 ), y-( height/2 ), imageWidth, imageHeight, width, height, transparency, false, null );
	}
	
	/**
	 * A special image display that is used for the time bar. It displays an image from the bottom up rather than from the
	 * center out so that the image can go down like a time bar.
	 * 
	 * @param textureName - the texture name, grabbed from JPCT-AE's TextureManager
	 * @param x - center x coordinate of the image on screen (in pixels)
	 * @param y - center y coordinate of the image on screen (in pixels)
	 * @param imageWidth - loaded width of the given image (always a power of 2)
	 * @param imageHeight - loaded height of the given image (always a power of 2)
	 * @param width - the on screen width of the image (in pixels)
	 * @param height - the on screen height of the image (in pixels)
	 * @param totalHeight - the max height it can be
	 * @param transparency - the transparency of the image (max is ~20, -1 means no transparency)
	 */
	public void blitImageBottomUp( String textureName, int x, int y, int imageWidth, int imageHeight, int width, int height, int totalHeight, int transparency ) {
		Texture image = tm.getTexture( textureName );
		fb.blit( image, 0, 0, ( x-width/2 ), y-( height/2 ) + ( totalHeight- height )/2, imageWidth, imageHeight, width, height, transparency, false, null );
	}

	/**
	 * Displays a simple crosshair in the center of the screen
	 * 
	 * @param w - the width of the crosshair
	 * @param h -  the height of the crosshair
	 */
	public void blitCrosshair( int w, int h ) {
		fb.blit( REFERENCE_POINT, 0, 0, w/2-w/100, h/2-h/150, 8, 8, w/50, h/75, 10, false, RGBColor.BLACK );
		fb.blit( REFERENCE_POINT, 0, 0, w/2-h/150, h/2-w/100, 8, 8, h/75, w/50, 10, false, RGBColor.BLACK );
	}

	
	/**
	 * Displays a BBButton easily, since it already has x, y, width, and height parameters
	 * 
	 * @param button - the BBButton to be displayed
	 */
	public void blitButton( BBButton button ) {
		Texture image = tm.getTexture( button.currentImage );
		fb.blit( image, 0, 0, ( button.posX - button.width / 2 ), button.posY - ( button.height / 2 ), button.imageWidth, button.imageHeight, button.width, button.height, 20, false, null );

	}
	
}

