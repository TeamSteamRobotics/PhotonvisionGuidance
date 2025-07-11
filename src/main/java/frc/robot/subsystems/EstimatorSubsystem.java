// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.*;

import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;;

public class EstimatorSubsystem extends SubsystemBase {
  /** Creates a new EstimatorSubsystem. */
  private Function<Pose2d, Function<Double, Consumer<Matrix<N3, N1>>>> addVisionMeasurement;
  private VisionSubsystem m_vision;
  private PhotonPoseEstimator estimator;
  private Optional<EstimatedRobotPose> estimatePose;
  ArrayList<Double> xValues;
  ArrayList<Double> yValues;
  ArrayList<Double> thetaValues;
  Pose2d robotPose;
  public static final Matrix<N3, N1> robotPoseStdDev = new Matrix<N3, N1>(Nat.N3(), Nat.N1());
  AprilTagFieldLayout aprilTagFieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);
  public EstimatorSubsystem(Function<Pose2d, Function<Double, Consumer<Matrix<N3, N1>>>> addMeasurement, VisionSubsystem vision) {
    addVisionMeasurement = addMeasurement;
    estimator = new PhotonPoseEstimator(aprilTagFieldLayout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, Constants.Vision.robotToCam);
    m_vision = vision;
    robotPoseStdDev.set(0, 0, Constants.Vision.robotPoseStdDev[0]);
    robotPoseStdDev.set(0, 1, Constants.Vision.robotPoseStdDev[1]);
    robotPoseStdDev.set(0, 2, Constants.Vision.robotPoseStdDev[2]);
    xValues = new ArrayList<>();
    yValues = new ArrayList<>();
    thetaValues = new ArrayList<>();
  }

  /**
   * Planning on using this just to find stddev for the addVisionMeasurement
   * @param values
   * @return
   */
  public double findStandardDeviation(List<Double> values){
    double average = 0;
    double variance = 0;
    for(int i = 0; i < values.size(); i++){
      average += values.get(i)/values.size();
    }
    for(int i = 0; i < values.size(); i++){
      variance += Math.pow((values.get(i) - average), 2) / (values.size() - 1); // Using Bessel's correction, may be unnecessary
    }
    return Math.sqrt(variance);
  }
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    estimatePose = estimator.update(m_vision.latestResult());
    if(estimatePose.isPresent()){
      robotPose = estimatePose.get().estimatedPose.toPose2d();
      xValues.add(robotPose.getX());
      yValues.add(robotPose.getY());
      thetaValues.add(robotPose.getRotation().getRadians());
      // addVisionMeasurement.apply(estimatePose.get().estimatedPose.toPose2d()).apply(Timer.getFPGATimestamp()).accept(robotPoseStdDev);
    }
    System.out.println("x Standard Deviation: " + Double.toString(findStandardDeviation(xValues)));
    System.out.println("y Standard Deviation: " + Double.toString(findStandardDeviation(yValues)));
    System.out.println("theta Standard Deviation: " + Double.toString(findStandardDeviation(thetaValues)));
  }
}
