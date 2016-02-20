package org.usfirst.frc.team3397.robot;

public interface BasicDriveTrainInterface {
	
	final double nonTurboSpeed = .7;
	
	void DegreeTurn(double degree, double powerLevel);
	
	void Drive(double length);
	
}
