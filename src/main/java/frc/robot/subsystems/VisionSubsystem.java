// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.AprilVisionSubsystem.Coordinate;

public class VisionSubsystem extends SubsystemBase {
  private PhotonCamera[] cameras;
  private int numCameras;
  class FiducialID {
    public int id;
    //public double 
  }
  public class Coordinate {
    public double x;
    public double y;
    public double z;
    public double rx;
    public double ry;
    public double rz;
    public boolean aprilTagVisible;
}
  
  /** Creates a new VisionSubsystem. */
  public VisionSubsystem(String[] cameraNames) {
    numCameras = cameraNames.length;
    cameras = new PhotonCamera[numCameras];
    for(int i = 0; i < numCameras; i++){
      cameras[i] = new PhotonCamera(cameraNames[i]);
    }
  }

  public List<PhotonTrackedTarget> allTargets(){
    List<PhotonTrackedTarget> seenTargets = new ArrayList<>();
    //PhotonPipelineResult test = new PhotonPipelineResult();

    for(int i = 0; i < numCameras; i++){
      seenTargets.addAll(cameras[i].getAllUnreadResults().stream().map((e) -> e.hasTargets() ? e.getTargets() : null).flatMap(List::stream).toList());
      /* We have an array of cameras. We iterate over every camera, and add seen targets to the list
       * getAllUnreadResults() returns a list of PhotonPipelineResults. stream() converts it into
       * a Stream, which one can use map() on. map() applies the lambda expression to every element
       * in the Stream, and returns a new Stream. flatMap(List::stream) flattens the Stream, although
       * I don't know why or how it works. toList() is obvious
       */
    }
    return seenTargets;
  }

  public List<PhotonPipelineResult> allUnreadResults(){
    List<PhotonPipelineResult> seenTargets = new ArrayList<>();
    
    for(int i = 0; i < numCameras; i++){
      seenTargets.addAll(cameras[i].getAllUnreadResults());
    }
    return seenTargets;
  }

  public enum ReturnTarget{
    TARGET,
    ROBOT,
    FIELD
  }
  
  public Coordinate getCoordinates(int[] ids, ReturnTarget rt){
    Coordinate coordinate = new Coordinate();
    for(int i = 0; i < ids.length; i++){
      coordinate = getCoordinates(ids[i], rt, coordinate); //TODO: Implement versiom with given id
    }
  }

  public List<PhotonTrackedTarget> allTargetsMultipleLines(){
    List<PhotonTrackedTarget> seenTargets = new ArrayList<>();

    for(int i = 0; i < numCameras; i++){
      List<PhotonPipelineResult> results = cameras[i].getAllUnreadResults();
      for(int j = 0; j < results.size(); j++){
        if(results.get(j).hasTargets()){
          seenTargets.addAll(results.get(j).getTargets());
        }
      }
    }
    return seenTargets;
  }

  /**DO NOT USE
   * @return null
   */
  public PhotonTrackedTarget getBestTarget(){
    List<PhotonTrackedTarget> seenTargets = new ArrayList<>();
    for(int i = 0; i < numCameras; i++){
      List<PhotonPipelineResult> results = cameras[i].getAllUnreadResults();
      for(int j = 0; j < results.size(); j++){
        seenTargets.add(results.get(j).getBestTarget());
      }
    }
    return null;
  }
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

}

