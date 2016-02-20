
package org.usfirst.frc.team3397.robot;


import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a demo program showing the use of the RobotDrive class.
 * The SampleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're inexperienced,
 * don't. Unless you know what you are doing, complex code will be much more difficult under
 * this system. Use IterativeRobot or Command-Based instead if you're new.
 */
public class Robot extends SampleRobot {
    DriveTrain myRobot;
    Joystick stick, oldStick;
    SendableChooser chooser;
    Victor leftShot, winchMotor, rightShot, bR;
    CameraServer server;
    Compressor compress;
    Solenoid s1, s2;
    
    final String defaultAuto = "Default";
    final String customAuto = "My Auto";

    public Robot() {
        myRobot = new DriveTrain(1, 0, 2, 3); //Left front, left rear, right front, right rear
        myRobot.setExpiration(0.1);
        stick = new Joystick(0);
        oldStick = new Joystick(1);
        compress = new Compressor();
        compress.start(); //we can also stop it during important operations
        s1 = new Solenoid(0);
        s2 = new Solenoid(1);
       
        //USB Camera
        server = CameraServer.getInstance();
        server.setQuality(50);
        server.startAutomaticCapture("cam1");
        //AutoCapture handles all feeding into the DriverStation,TODO maybe try doing it manually
        
        leftShot = new Victor(6);
		winchMotor = new Victor(5);
		rightShot = new Victor(4);
		//bR = new Victor(8);
    }
    
    public void robotInit() {
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto modes", chooser);
        
        s1.set(true); s2.set(false);
    }

	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the if-else structure below with additional strings.
	 * If using the SendableChooser make sure to add them to the chooser code above as well.
	 */
    public void autonomous() {
    	
    	String autoSelected = (String) chooser.getSelected();
//		String autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
    }
    	
    public void operatorControl() {
        myRobot.setSafetyEnabled(true);
        
        final int winchInvert= 1;
        
        double winchSpeed = 0;
        boolean shootersShooting = false;
        boolean shootersPulling = false;

        while (isOperatorControl() && isEnabled()) {
            
        	myRobot.XDrive(stick); // Drive with XBox Joystick!!!
        	
        	if(oldStick.getRawButton(3))
        	{
        		leftShot.set(-1); //Spin shooter wheels out
        		rightShot.set(1);
        		shootersShooting = true;
        		shootersPulling = false;
        	}
        	else if(oldStick.getRawButton(2))
        	{
        		leftShot.set(.3);//Spin shooter wheels in
        		rightShot.set(-.3);
        		shootersShooting = false;
        		shootersPulling = true;
        	}
        	else
        	{
        		leftShot.set(0);
        		rightShot.set(0);
        		shootersShooting = false;
        		shootersPulling = false;
        	}
        	
        	if(oldStick.getRawButton(1) && shootersPulling == false)
        	{
        		s1.set(false); s2.set(true); //extend piston only while pressed and not picking up ball
        	}
        	else
        	{
        		s1.set(true); s2.set(false);
        	}
        	
        	winchSpeed = oldStick.getAxis(Joystick.AxisType.kY);
        					//The front/back axis on the arcade joystick
        	
        	if(Math.abs(winchSpeed) < .1) //buffer
        	{
        		winchSpeed = 0;
        	}
        	
        	if(shootersShooting == false) //No pitch while getting ready to shoot
        	{
        		winchMotor.set(winchInvert * winchSpeed);
        	}
        	else
        	{
        		winchMotor.set(0);
        	}
        	

      //  	SmartDashboard.putNumber("fL(v0)", fL.get());
       //    	SmartDashboard.putNumber("bL(v1)", bL.get());
        //   	SmartDashboard.putNumber("fR(v2)", fR.get());
         //  	SmartDashboard.putNumber("bR(v3)", bR.get());
                   	
        	
            Timer.delay(0.005);		// wait for a motor update time
        }
    }

    /**
     * Runs during test mode
     */
    public void test() {
    }
}
