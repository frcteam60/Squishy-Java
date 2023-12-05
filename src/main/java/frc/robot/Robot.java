// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private final VictorSP m_frontLeft = new VictorSP(0);
  private final VictorSP m_rearLeft = new VictorSP(1);
  MotorControllerGroup m_left = new MotorControllerGroup(m_frontLeft, m_rearLeft);

  private final VictorSP m_frontRight = new VictorSP(2);
  private final VictorSP m_rearRight = new VictorSP(3);
  MotorControllerGroup m_right = new MotorControllerGroup(m_frontRight, m_rearRight);

  private final DifferentialDrive m_drive = new DifferentialDrive(m_left, m_right);

  private final VictorSP m_shooter1 = new VictorSP(4);
  private final VictorSP m_shooter2 = new VictorSP(5);
  private final VictorSP m_shooter3 = new VictorSP(6);
  MotorControllerGroup m_shooter = new MotorControllerGroup(m_shooter2, m_shooter3);

  private final XboxController xbox = new XboxController(0);

  double shooterMultiplyer = -0.5;
  double driveMultiplyer = -0.5;
  double deadzone = 0.05;
  int lastPov;

  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private static final String kVision = "Vision";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  public double shutInRange(double upperLimit, double lowerLimit, double x) {
    if (x > lowerLimit && x < upperLimit) {
      return x;
    } else if (x > upperLimit) {
      return upperLimit;
    } else {
      return lowerLimit;
    }
  }

  public double deadZoner(double joystickValue, double deadzone) {
    if ((joystickValue > (deadzone * -1)) && (joystickValue < deadzone)) {
      return 0.0;
    } else {
      return joystickValue;
    }
  }

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // if you want to invert the entire side you can do
    // so here
    m_right.setInverted(true);
    m_shooter2.setInverted(false);
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    m_chooser.addOption("Vision", kVision);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items
   * like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different
   * autonomous modes using the dashboard. The sendable chooser code works with
   * the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the
   * chooser code and
   * uncomment the getString line to get the auto name from the text box below the
   * Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure
   * below with additional strings. If using the SendableChooser make sure to add
   * them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kVision:
        // My code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    // Uses the Joystick inputs for drive control
    m_drive.arcadeDrive((deadZoner(xbox.getLeftY(), deadzone) * driveMultiplyer), (deadZoner(xbox.getRightX(), deadzone) * driveMultiplyer));
    m_shooter1.set(xbox.getLeftTriggerAxis() * 0.5);
    m_shooter.set(xbox.getRightTriggerAxis() * shooterMultiplyer);
    if (xbox.getRightTriggerAxis() >= 0.9) {
      m_shooter2.set(0.5);
      m_shooter3.set(0.8);
    }
    
    if (xbox.getRightBumperPressed()){
      shooterMultiplyer = shutInRange(0.5, 0.0, shooterMultiplyer + 0.1);
    } else if (xbox.getLeftBumperPressed()) {
      shooterMultiplyer = shutInRange(0.5, 0, shooterMultiplyer - 0.1);
    }
    // if (xbox.getPOV() == 0) {
      //driveMultiplyer = shutInRange(1, -1, driveMultiplyer - 0.1);
    //} else if (xbox.getPOV() == 180) {
      //driveMultiplyer = shutInRange(1, -1, driveMultiplyer + 0.1);
    if (xbox.getPOV() == 0 && lastPov != 0){
      driveMultiplyer = shutInRange(1, -1, driveMultiplyer + 0.1);
    } else if (xbox.getPOV() == 180 && lastPov != 180){
      driveMultiplyer = shutInRange(1, -1, driveMultiplyer - 0.1);
    }  

  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
    m_drive.arcadeDrive(0, 0);
    m_left.stopMotor();
    m_right.stopMotor();
    m_shooter.stopMotor();
    m_shooter1.stopMotor();
  }

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
  }

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {
  }

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {
  }
}
