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

// Renders the state of the game
class BBRenderer implements GLSurfaceView.Renderer {
	
	// Library objects
	protected static FrameBuffer fb;
	private BBTextureManager tm;
	private World loadingWorld;
	private BBGame game;
	
	/* Internal parameters */
	
	private GLText textSmall;
	private GLText textLarge;
	
	// Represents the "cleared" background color of the frame buffer 
	private RGBColor bg = new RGBColor( 255, 255, 255 );
	
	private int letterWidth;
	
	int soundID = 1;
	
	private float fuelHeight;
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
	 * @param c
	 * @param w
	 * @param h
	 * @param level
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
		
		fuelHeight = 0;
		
		arrowX = BB.width / 12;
		arrowY = BB.height / 10 + BB.width / 6;
	}
	
	// Triggered when the view port is created
	public void onSurfaceCreated( GL10 unused, EGLConfig config ) {
		textSmall = new GLText( BB.context.getAssets() );
		textSmall.load( "futura-normal.ttf", BB.width / 35, 2, 2 );
		textLarge = new GLText( BB.context.getAssets() );
		textLarge.load( "futura-normal.ttf", BB.width / 15, 2, 2 );
	}
	
	// Triggered when the view port changes (e.g., by size)
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
	
	// Triggers every clock cycle / time the system wants to draw a frame
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
			drawTextSmall( "LOADING...", (int) (BB.width / 1.239f), (int) (BB.height / 1.054), RGBColor.BLACK, false);
			
		} else {
			
			// Update game elements
			game.update();
			
			// Render sky box (if applicable)
			if ( game.world.skybox != null )
				game.world.skybox.render( game.world, fb );
		    
		    // Render the world, and then draw to the frame buffer
			game.world.renderScene( fb );
			game.world.draw( fb );
			
			fuelHeight = (int) (((float) game.captured / game.world.roomObjectWords.size()) * (BB.height * 0.75));
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
	 * @return
	 */
	public static FrameBuffer getFrameBuffer() {
		return fb;
	}
	
	/**
	 * @param fb
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
				drawTextLarge ( game.world.getBubbleArticle(), (int) (BB.width / 2.11f), (int) (BB.height / 1.08f), RGBColor.WHITE, false);
				// Fire Button
				blitImage( game.fireButton.currentImage, w/8, h-( w/8 ), 128, 128, w/8, w/8, 10 );
				// Pause Button
				blitButton( game.pauseButton );
				// Dynamic fuel/time bars
				//blitImageBottomUp( "FuelBar", (int) (w * 0.909), halfHeight, 16, 512, w/38, (int) fuelHeight, (int) (h * 0.76), 100 );
				blitImageBottomUp( "TimeBar", (int) ( BB.width*0.966 ), halfHeight, 16, 512, BB.width/38, (int)timeHeight, (int) ( BB.height*0.76 ), 100 );
				// Score bars 
				blitImage( "ScoreBars", w-( w/16 ), halfHeight, 128, 512, w/8, ( int )( h*0.9 ), 100 );
				
				
			} else {
				// Pause Button
				blitButton( game.pauseButton );

				displayScreenItem( fb, game.wattsonPrivileges );
			}
			if( ( game.wattsonPrivileges & 2) == 0 ) {
				if ( arrowState == "ArrowRight" ) {
					blitImage( arrowState, arrowX + game.arrowMod, arrowY, 128, 128, BB.width / 8, BB.width / 8, 100 );
				} else if (arrowState == "NoArrow" ){
					
				} else {
					blitImage( arrowState, arrowX, arrowY + game.arrowMod, 128, 128, BB.width / 8, BB.width / 8, 100 );
				}
			}
			//Info Bar
			//Has extra 1 px hang if using real size? Decremented to 127x127
			blitImage( "InfoBar", w/10, w/10, 127, 127, w/5, w/5, 100 );

			try {
				// Wattson help text
				int iteration = 0;
				for ( String string : game.wattsonText ) {
					drawTextSmall( string, w/6, h/23 + ( letterWidth*2*iteration ), RGBColor.WHITE, false);
					iteration++;
				}
			} catch ( ConcurrentModificationException e ) {
				Log.e( "BBRenderer", "display2DGameInfo got ConcurrentModificationError: " + e );
			}
			
			
			if ( game.answerTransparency != 0 ) {
				blitImage( game.answer, halfWidth, halfHeight, 64, 64, w, h, game.answerTransparency );
			}
			
		}
		
		// Not tutorial displays
		else {
			
			blitCrosshair( w, h );
			// Bubble image
			blitImage( game.bubbleTex, w/2, h, 256, 256, w/3, w/3, 5 );
			// Bubble text
			drawTextLarge ( game.world.getBubbleArticle(), (int) (BB.width / 2.11f), (int) (BB.height / 1.08f), RGBColor.WHITE, false);
			// Fire Button
			blitButton( game.fireButton );
			// Pause Button
			blitButton( game.pauseButton );
			// score button
			blitButton( game.scoreButton );
			// Info Bar
			// Has extra 1px hang if using real size? Decremented to 255x255
			//blitImage( "InfoBar", w/10, w/10, 127, 127, w/5, w/5, 100 );
			
			// Dynamic time bar
			blitImageBottomUp( "TimeBar", (int) (w * 0.966), h/2, 16, 512, w/38, (int) timeHeight, (int) (h * 0.76), 100 );
			// Score bars
			blitImage( "ScoreBars", w-( w/16 ), h/2, 128, 512, w/8, ( int )( h*0.9 ), 100 );
			//Score
			String score = Integer.toString(game.score);
			drawTextLarge( score, game.scoreButton.posX + BB.width / 10, game.scoreButton.posY - game.scoreButton.height / 3, RGBColor.WHITE, true );
			
			if ( game.answerTransparency != 0 ) {
				blitImage( game.answer, halfWidth, halfHeight, 64, 64, w, h, game.answerTransparency );
			}
		
		}
		
		fb.display();

		
	}
	
	/**
	 * @param fb
	 * @param itemNum
	 */
	// TODO: Figure out what this function actually does, and separate the tasks between BBRenderer & BBGame
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
				break;
			case 2:
				//Fire Button
				blitButton( game.fireButton );
				//Bubble image
				blitImage( game.bubbleTex, halfWidth, h, 256, 256, w/3, w/3, 5 );
				//Bubble text
				//blitText( game.world.getBubbleArticle(), halfWidth-w/25, h-w/10, w/25, h/10, RGBColor.WHITE );
				
				if ( isLastItem ) {
					arrowX = ( int )( w/3.5f );
					arrowY = h - ( w/8 );
					arrowState = "ArrowLeft";
				}
				
				 //Dynamic fuel bar
				 blitImageBottomUp("TimeBar", (int)( BB.width*0.966 ), halfHeight, 16, 512, BB.width/38, (int) timeHeight, (int)( BB.height*0.76 ), 100 );
				 //blitImageBottomUp("FuelBar", (int)( BB.width*0.909 ), BB.height/2, 16, 512, BB.width/38, (int) fuelHeight, (int)( BB.height*0.76 ), 100 );
				 blitImage("ScoreBars", BB.width-( BB.width/16 ), halfHeight, 128, 512, BB.width/8, (int)( BB.height*0.9 ), 100 );
				
				break;
			case 4:
				 if ( game.wattsonPrivileges == itemNum )
					 game.cam.lookAt( new SimpleVector( 0, 0.1, 1 ) );
				 if ( isLastItem )
					 game.cam.lookAt( new SimpleVector( -.358, 0.174, 0.917 ) );
				 
				 //Bubble image
				 blitImage( game.bubbleTex, halfWidth, BB.height, 256, 256, BB.width/3, BB.width/3, 5 );
				 //Bubble text
				 //blitText( game.world.getBubbleArticle(), halfWidth-BB.width/25, BB.height-BB.width/10, BB.width/25, BB.height/10, RGBColor.WHITE );
				 
				 //Dynamic fuel bar
				 blitImageBottomUp("TimeBar", (int)( BB.width*0.966 ), halfHeight, 16, 512, BB.width/38, (int) timeHeight, (int)( BB.height*0.76 ), 100 );
				 //blitImageBottomUp("FuelBar", (int)( BB.width*0.909 ), BB.height/2, 16, 512, BB.width/38, (int) fuelHeight, (int)( BB.height*0.76 ), 100 );
				 blitImage("ScoreBars", BB.width-( BB.width/16 ), halfHeight, 128, 512, BB.width/8, (int)( BB.height*0.9 ), 100 );
				
				 
				 if ( !game.fireButton.isActive() ) {
					 arrowX = game.fireButton.posX;
					  arrowY = game.fireButton.posY - game.fireButton.height;
					 arrowState = "ArrowDown";
					 blitImage( "Filter", halfWidth, halfHeight, 64, 64, w, h, 10 );
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
 				 //blitText( game.world.getBubbleArticle(), halfWidth - BB.width/25, BB.height - BB.width / 10, BB.width / 25, BB.height / 10, RGBColor.WHITE );

				 //Dynamic fuel bar
				 blitImageBottomUp( "TimeBar", (int) ( BB.width*0.966 ), halfHeight, 16, 512, BB.width/38, (int) timeHeight, (int) ( BB.height*0.76 ), 100 );
				 //blitImageBottomUp( "FuelBar", (int) ( BB.width*0.909 ), BB.height/2, 16, 512, BB.width/38, (int) fuelHeight, (int) ( BB.height*0.76 ), 100 );
				 blitImage( "ScoreBars", BB.width-( BB.width/16 ), halfHeight, 128, 512, BB.width/8, (int) ( BB.height*0.9 ), 100 );
				 	
 				 
				 if ( !game.fireButton.isActive() ) {
					  arrowX = game.fireButton.posX;
					  arrowY = game.fireButton.posY - game.fireButton.height;
					  arrowState = "ArrowDown";
					  blitImage( "Filter", halfWidth, halfHeight, 64, 64, w, h, 10 );
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
				break;
			default:
				break;
		}
		
	}

	
	/**
	 * @param vector
	 * @return
	 */
	public Vector3f toVector3f( SimpleVector vector ) {
		return new Vector3f( vector.x, vector.y, vector.z );
	}
	
	/**
	 * @param vector
	 * @return
	 */
	public SimpleVector toSimpleVector( Vector3f vector ) {
		return new SimpleVector( vector.x, vector.y, vector.z );
	}

	public static int getTextWidth( String text ) {
		int length = text.length() * charWidth;
		return length;
	}

	/**
	 * @param text
	 * @param pixelX
	 * @param pixelY
	 * @param color
	 * @param reverse
	 */
	public void drawTextLarge( String text, int pixelX, int pixelY, RGBColor color, boolean reverse ) {
		// Switch frame buffers
		fb.display();
		
		// Enable texture + alpha blending (need to do this every time because JPCT-AE presumably
		// turns it off)
		GLES20.glEnable( GLES20.GL_BLEND );
		GLES20.glBlendFunc( GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA );
		textLarge.begin( color.getRed() / 255f, color.getRed() / 255f, color.getRed() / 255f, 1.0f, mVPMatrix );
		
		if ( reverse ) {
			textLarge.drawC( text, pixelX - ( BB.width / 2 ), ( BB.height / 2 ) - pixelY );
		} else {
			textLarge.draw( text, pixelX - ( BB.width / 2 ), ( BB.height / 2 ) - pixelY );
		}
		
		textLarge.end();
		GLES20.glDisable( GLES20.GL_BLEND );
		
	}
	
	/**
	 * @param text
	 * @param pixelX
	 * @param pixelY
	 * @param color
	 * @param reverse
	 */
	public void drawTextSmall( String text, int pixelX, int pixelY, RGBColor color, boolean reverse ) {
		// Switch frame buffers
		fb.display();
		
		// Enable texture + alpha blending (need to do this every time because JPCT-AE presumably
		// turns it off)
		GLES20.glEnable( GLES20.GL_BLEND );
		GLES20.glBlendFunc( GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA );
		textSmall.begin( color.getRed() / 255f, color.getRed() / 255f, color.getRed() / 255f, 1.0f, mVPMatrix );
		
		if ( reverse ) { 
			textSmall.drawC( text, pixelX - ( BB.width / 2 ), ( BB.height / 2 ) - pixelY );
		} else { 
			textSmall.draw( text, pixelX - ( BB.width / 2 ), ( BB.height / 2 ) - pixelY );
		}
		
		textSmall.end();
		GLES20.glDisable( GLES20.GL_BLEND );
	}
	
	/**
	 * @param textureName
	 * @param x
	 * @param y
	 * @param imageWidth
	 * @param imageHeight
	 * @param width
	 * @param height
	 * @param transparency
	 */
	public void blitImage( String textureName, int x, int y, int imageWidth, int imageHeight, int width, int height, int transparency ) {
		Texture image = tm.getTexture( textureName );
		fb.blit( image, 0, 0, ( x-width/2 ), y-( height/2 ), imageWidth, imageHeight, width, height, transparency, false, null );
	}
	
	/**
	 * @param textureName
	 * @param x
	 * @param y
	 * @param imageWidth
	 * @param imageHeight
	 * @param width
	 * @param height
	 * @param totalHeight
	 * @param transparency
	 */
	public void blitImageBottomUp( String textureName, int x, int y, int imageWidth, int imageHeight, int width, int height, int totalHeight, int transparency ) {
		Texture image = tm.getTexture( textureName );
		fb.blit( image, 0, 0, ( x-width/2 ), y-( height/2 ) + ( totalHeight- height )/2, imageWidth, imageHeight, width, height, transparency, false, null );
	}

	/**
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param transparency
	 * @param color
	 */
	public void blitFilledBox( int x, int y, int w, int h, int transparency, RGBColor color ) {
		fb.blit( REFERENCE_POINT, 0, 0, x, y, 8, 8, w, h, transparency, false, color );
	}
	
	/**
	 * @param fb
	 * @param w
	 * @param h
	 */
	public void blitCrosshair( int w, int h ) {
		fb.blit( REFERENCE_POINT, 0, 0, w/2-w/100, h/2-h/150, 8, 8, w/50, h/75, 10, false, RGBColor.BLACK );
		fb.blit( REFERENCE_POINT, 0, 0, w/2-h/150, h/2-w/100, 8, 8, h/75, w/50, 10, false, RGBColor.BLACK );
	}
	
	/**
	 * @param x
	 * @param y
	 * @param radius
	 * @param transparency
	 * @param color
	 */
	public void blitFilledCircle( int x, int y, int radius, int transparency, RGBColor color ) {
		for ( float i = 180.0f; i > 90.0f; i-=1 ) {
			fb.blit( REFERENCE_POINT, 0, 0, ( int )( x+Math.cos( i*Math.PI/180 )*radius ), ( int )( y-Math.sin( i*Math.PI/180 )*radius ),
					8, 8, ( int )( Math.abs( Math.cos( i*Math.PI/180 )*radius )*2 ), ( int )( Math.floor( (Math.sin( i*Math.PI/180 )*radius )-( Math.sin( (i-1 )*Math.PI/180 )*radius ) ) ), transparency, false, color );
		}
	}
	
	/**
	 * @param button
	 */
	public void blitButton( BBButton button ) {
		Texture image = tm.getTexture( button.currentImage );
		fb.blit( image, 0, 0, ( button.posX - button.width / 2 ), button.posY - ( button.height / 2 ), button.imageWidth, button.imageHeight, button.width, button.height, 20, false, null );

	}
	
}

