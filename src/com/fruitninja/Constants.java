package com.fruitninja;
//Constants.java: centralizes all game constants (sizes, paths, timings, scores)

import java.awt.Color;
import java.awt.BasicStroke;

public class Constants {

    // window
    public static final int SCREEN_WIDTH  = 800;
    public static final int SCREEN_HEIGHT = 600;

    // time & refresh rate
    public static final int GAME_TIMER_DELAY_MS    = 16;   
    public static final int COUNTDOWN_TIMER_DELAY_MS = 1000; 
    public static final int INITIAL_TIME_SECONDS   = 60;   
    public static final int EXPLOSION_MAX_AGE_TICKS = 60;  
    public static final int SLOW_MOTION_DURATION_TICKS = 180; 
    public static final int FATAL_BOMB_GAMEOVER_DELAY_MS = 500; 

    // object space
    public static final int FRUIT_SPAWN_INTERVAL     = 75;
    public static final int BOMB_SPAWN_INTERVAL      = 240;
    public static final int BONUS_ITEM_SPAWN_INTERVAL = 450;

    // physics
    public static final double GRAVITY_ACCELERATION = 0.4;  
    public static final double OBJECT_START_Y_OFFSET = 50.0; 
    public static final int MIN_INITIAL_VY      = 15;   
    public static final int MAX_INITIAL_VY_RANGE = 6;  
    public static final int MAX_ABS_VX         = 5;    
    public static final double GLOBAL_SPEED_NORMAL      = 1.0;
    public static final double GLOBAL_SPEED_SLOW_MOTION = 0.3;

    public static final double FRUIT_HALF_VELOCITY_DIVERGENCE = 2.0; 

    // attribute
    public static final double FRUIT_SCALE_FACTOR      = 0.6;
    public static final double FRUIT_HALF_SCALE_FACTOR = 0.55;
    public static final double BOMB_SCALE_FACTOR       = 1.0;  
    public static final double BONUS_ITEM_SCALE_FACTOR = 1.0;
    public static final int BOMB_BASE_RADIUS   = 30;   
    public static final int BONUS_ITEM_RADIUS  = 20;   

    // rule
    public static final int INITIAL_LIVES            = 3;
    public static final int POINTS_PER_FRUIT         = 1;
    public static final int BONUS_POINTS_EXTRA_LIFE  = 5;
    public static final int BONUS_POINTS_SLOW_MOTION = 3;
    public static final int TIME_BOMB_PENALTY_SECONDS = 10;
    public static final int COMBO_BONUS_THRESHOLD    = 3;   
    public static final int COMBO_POINT_MULTIPLIER   = 2;   
    public static final double CRITICAL_HIT_CHANCE   = 0.1; 
    public static final int CRITICAL_HIT_BONUS       = 10;  

    // visual settings
    public static final int MAX_SWIPE_POINTS = 4;                      
    public static final Color SWIPE_TRAIL_COLOR = Color.WHITE;
    public static final BasicStroke SWIPE_TRAIL_STROKE = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    public static final int SCORE_DISPLAY_X = 10;
    public static final int SCORE_DISPLAY_Y = 20;
    public static final int LIVES_DISPLAY_Y = 40;
    public static final int TIME_DISPLAY_Y  = 60;
    public static final Color SCORE_TEXT_COLOR = Color.YELLOW;           
    // resources
    public static final String RESOURCE_BASE_PATH    = "resource/";
    public static final String SOUND_BASE_PATH       = "sound/";
    public static final String IMG_BACKGROUND_PATH   = RESOURCE_BASE_PATH + "background/woodBackground.png";
    public static final String IMG_FRUIT_PATH        = RESOURCE_BASE_PATH + "fruit/";
    public static final String IMG_HALF_FRUIT_PATH   = RESOURCE_BASE_PATH + "half-fruit/";
    public static final String IMG_BOMB_PATH         = RESOURCE_BASE_PATH + "bomb/";
    public static final String IMG_EXPLOSION_PATH    = RESOURCE_BASE_PATH + "explosion/";
    public static final String IMG_BONUS_CLOCK_PATH  = RESOURCE_BASE_PATH + "bonusItem/clock.png";
    public static final String IMG_BONUS_STAR_PATH   = RESOURCE_BASE_PATH + "bonusItem/star.png";
    public static final String IMG_COMBO_PATH  		 = RESOURCE_BASE_PATH + "bonusItem/combo.png";
    public static final String SND_SLICE_PATH        = SOUND_BASE_PATH + "slicesSound/splatterMedium1.wav";
    public static final String SND_BOMB_FATAL_PATH   = SOUND_BASE_PATH + "bombSounds/fatalBombExplode.wav";
    public static final String SND_BOMB_TIME_PATH    = SOUND_BASE_PATH + "bombSounds/timeBombExplode.wav";
    public static final String SND_SWING_PATH        = SOUND_BASE_PATH + "gameRoundSounds/swordSwipe1.wav";
    public static final String SND_GAME_START_PATH   = SOUND_BASE_PATH + "gameRoundSounds/gameStart.wav";
    public static final String SND_GAME_OVER_PATH    = SOUND_BASE_PATH + "gameRoundSounds/gameOver.wav";

    // additional resources
    public static final String SND_EXTRA_LIFE_PATH   = SOUND_BASE_PATH + "gameRoundSounds/extraLife.wav";
    public static final String SND_SLOW_MOTION_PATH  = SOUND_BASE_PATH + "gameRoundSounds/clock.wav";
	
	
}
