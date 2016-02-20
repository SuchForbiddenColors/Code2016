package org.usfirst.frc.team3397.robot;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Joystick;

public class DriveTrain implements BasicDriveTrainInterface {
	
	RobotDrive chassis;
	
	final int XDriveTurningAxis = 4; //4 is the right trigger's x axis (Josh's preference), 0 is the left trigger's x axis
	
	public DriveTrain(int frontLeft, int backLeft, int frontRight, int backRight)
	{
			chassis = new RobotDrive(frontLeft, backLeft, frontRight, backRight);
	}
	
	public void setExpiration(double timeout)
	{
		chassis.setExpiration(timeout);
		
	}
	
	public void setSafetyEnabled(boolean enabled)
	{
		chassis.setSafetyEnabled(enabled);
	}
	
	public void XDrive(Joystick XStick)
	{
		double forward = XStick.getRawAxis(1);
		double turn = XStick.getRawAxis(XDriveTurningAxis);
		
		turn = turn * -1;//invert turning direction		
		
		if(forward < 0)
		{
			turn *= -1;
		}
		
     // why doesn't my deadband work?
        
		if (Math.abs(forward) < 0.05)
		{
			forward = 0.0;
		}
		
		if (Math.abs(turn) < 0.05)
		{
			turn = 0.0;
		}
		
		double leftSpeed;
		double rightSpeed;
		
		float m_sensitivity = .5f;

		if (turn < 0)
		{
			double value = Math.log(-turn);
			double ratio = (value - m_sensitivity)/(value + m_sensitivity); //This is pre-existing library code
			if (ratio == 0) ratio =.0000000001;						 // I'm not familiar with its use, so just roll with it
				leftSpeed = forward / ratio;							 //  It's here in case we need to modify it
				rightSpeed = forward;
		}
		else if (turn > 0)
		{
			double value = Math.log(turn);
			double ratio = (value - m_sensitivity)/(value + m_sensitivity);
			if (ratio == 0) ratio =.0000000001;
				leftSpeed = forward;
				rightSpeed = forward / ratio;
		}
		else
		{
			leftSpeed = forward;
			rightSpeed = forward;
		}
		
		double speedMultiplier = nonTurboSpeed + (1 - nonTurboSpeed) * XStick.getRawAxis(2);
													//The more you pull on Axis2, left trigger, the larger the multiplier will be
		leftSpeed = leftSpeed * speedMultiplier * -1;	//Currently max multiplier without turbo is 70%
		rightSpeed = rightSpeed * speedMultiplier * -1;
		
		
		chassis.setLeftRightMotorOutputs(leftSpeed, rightSpeed);
		
	}
	
	@Override
	public void DegreeTurn(double degree, double powerLevel)
	{
		double turnRadius = 2;
		double wheelRadius = 1;
		double wheelRevTime = 1.5;
		
		double defaultPowerLevel = .7;
		
		double wheelRevsPerTurn = wheelRadius/turnRadius;
		
		double timePerDegree = wheelRevsPerTurn/wheelRevTime*360;
		
		double pL;
		
		if(powerLevel == 0)
		{
			pL = defaultPowerLevel;
		}
		else
		{
			pL = powerLevel;
		}
		
		//timePerDegree = manually set value if easier;
		
		if(degree > 0)
		{
			chassis.setLeftRightMotorOutputs(pL, -pL);
			Timer.delay(timePerDegree * Math.abs(degree));
		}
		else
		{
			chassis.setLeftRightMotorOutputs(-pL, pL);
			Timer.delay(timePerDegree * Math.abs(degree));
		}
		
		chassis.setLeftRightMotorOutputs(0, 0);
	}

	@Override
	public void Drive(double length) {
		double secondsPerFoot = 1;
		
		chassis.setLeftRightMotorOutputs(length/Math.abs(length)*.7,length/Math.abs(length)*.7);
											//Have the same sign as the desired length
		Timer.delay(secondsPerFoot * Math.abs(length));
		
		chassis.setLeftRightMotorOutputs(0, 0);
		
	}

}
