/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.IterativeRobot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends IterativeRobot {

    Jaguar jagLeft1, jagLeft2, jagRight1, jagRight2;
    Victor victor;
    Solenoid sol1, sol2, sol4, sol5, sol7, sol8;
    Relay relay;
    AnalogChannel ultrasonic, encoder;
    double conf;
    boolean atShoot, afterShoot;
    int endTimer, noWait;
    NetworkTable server = NetworkTable.getTable("SmartDashboard");

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {

        jagLeft1 = new Jaguar(1);
        jagLeft2 = new Jaguar(2);
        jagRight1 = new Jaguar(3);
        jagRight2 = new Jaguar(4);
        victor = new Victor(5);

        sol1 = new Solenoid(1);
        sol2 = new Solenoid(2);

        sol4 = new Solenoid(4);
        sol5 = new Solenoid(5);

        sol7 = new Solenoid(7);
        sol8 = new Solenoid(8);

        relay = new Relay(1);

        encoder = new AnalogChannel(2);
        ultrasonic = new AnalogChannel(3);

        conf = 0;
        noWait = 0;
        endTimer = 0;

        afterShoot = false;
        atShoot = false;
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousInit() {

        conf = 0;
        noWait = 0;
        endTimer = 0;

        atShoot = false;
        afterShoot = false;

        relay.set(Relay.Value.kOn);

        sol1.set(true);
        sol2.set(false);
        /*sol4.set(true);
        sol5.set(false);*/
        sol7.set(true);
        sol8.set(false);
    }

    public void autonomousPeriodic() {
        System.out.println("Confidence: " + conf);
        if (!atShoot) {
            if (ultrasonic.getVoltage() > 0.86) {
                conf = conf + SmartDashboard.getNumber("Confidence") - 70;
                jagLeft1.set(-0.648);
                jagLeft2.set(-0.648);
                jagRight1.set(0.6);
                jagRight2.set(0.6);
                System.out.println("Driving forward.");
                //420 blaze it
            } else {
                jagLeft1.set(0);
                jagLeft2.set(0);
                jagRight1.set(0);
                jagRight2.set(0);
                atShoot = true;
                System.out.println("Done 420 blazin'.");
            }
        }
        if (atShoot && !afterShoot) {
            if (conf >= 40) {
                System.out.println("Saw Target.");
                sol7.set(false);
                sol8.set(true);
                afterShoot = true;
                System.out.println("Launching.");
            } else {
                if (noWait == 0) {
                    System.out.println("Did not see target.");
                }
                noWait++;
                if (ultrasonic.getVoltage() > 0.85) {
                    jagLeft1.set(-0.216);
                    jagLeft2.set(-0.216);
                    jagRight1.set(0.2);
                    jagRight2.set(0.2);
                }
                if (ultrasonic.getVoltage() < 0.81) {
                    jagLeft1.set(0.216);
                    jagLeft2.set(0.216);
                    jagRight1.set(-0.2);
                    jagRight2.set(-0.2);
                }
                if (noWait == 200) {
                    sol7.set(false);
                    sol8.set(true);
                    afterShoot = true;
                    System.out.println("Launching.");
                }
            }
        }
        if (afterShoot) {
            if (endTimer < 100) {
                endTimer++;
                jagLeft1.set(0);
                jagLeft2.set(0);
                jagRight1.set(0);
                jagRight2.set(0);
                if (endTimer == 99) {
                    System.out.println("Retracting Launcher.");
                }
            } else {
                relay.set(Relay.Value.kOff);
                sol7.set(true);
                sol8.set(false);
                System.out.println("Autonomous Complete.");
            }
        }
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    }
}
