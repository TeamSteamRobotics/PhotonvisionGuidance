// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
//import frc.robot.subsystems.AprilVisionSubsystem.Coordinate;

public class VisionSubsystem extends SubsystemBase {
  private PhotonCamera[] cameras;
  private int numCameras;
  private List<PhotonPipelineResult> results;
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
    public Coordinate(){
      aprilTagVisible = false;
    }
    public Coordinate(double x, double y, double z, double rx, double ry, double rz, boolean aprilTagVisible){
      this.x = x;
      this.y = y;
      this.z = z;
      this.rx = rx;
      this.ry = ry;
      this.rz = rz;
      this.aprilTagVisible = aprilTagVisible;
    }
}
  
  /** Creates a new VisionSubsystem. */
  public VisionSubsystem(String[] cameraNames) {
    numCameras = cameraNames.length;
    cameras = new PhotonCamera[numCameras];
    for(int i = 0; i < numCameras; i++){
      cameras[i] = new PhotonCamera(cameraNames[i]);
    }
  }
  public Optional<PhotonPipelineResult> latestResult(){
    if(results.size() != 0){
      return Optional.of(results.get(results.size() - 1));
    }
    return Optional.empty();
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
  public List<PhotonTrackedTarget> allTargets(int cameraId){
    List<PhotonTrackedTarget> seenTargets = new ArrayList<>();
    //PhotonPipelineResult test = new PhotonPipelineResult();

    
    seenTargets.addAll(cameras[cameraId].getAllUnreadResults().stream().map((e) -> e.hasTargets() ? e.getTargets() : null).flatMap(List::stream).toList());
    /* We have an array of cameras. We iterate over every camera, and add seen targets to the list
      * getAllUnreadResults() returns a list of PhotonPipelineResults. stream() converts it into
      * a Stream, which one can use map() on. map() applies the lambda expression to every element
      * in the Stream, and returns a new Stream. flatMap(List::stream) flattens the Stream, although
      * I don't know why or how it works. toList() is obvious
      */
    
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
  /** 
   * Recommended to not use, mostly for backwards compatibility with Kessler's code
   * @deprecated
   * @param ids - Array of fiducial IDs
   * @param rt - Enum, only TARGET is supported
   * @return A <b>Coordinate</b>, with only the <b>z</b> field set
  */
  public Coordinate getCoordinates(int[] ids, ReturnTarget rt){
    Coordinate coordinate = new Coordinate();
    for(int i = 0; i < ids.length; i++){
      coordinate = getCoordinates(ids[i], rt); //DONE: Implement versiom with given id
      if(coordinate.aprilTagVisible){
        return coordinate;
      }
    }
    return new Coordinate(); //TODO: Uh Oh :3
  }

  public Coordinate getCoordinates(int id, ReturnTarget rt){
    switch(rt){
      case TARGET:
        return depTargetPositionRelativeToCamera(id);
      default:
        System.out.println("Attempting to find coordinates in different space. Check VisionSubsystem.java");
        return new Coordinate();
    }
  }

  public Coordinate depTargetPositionRelativeToCamera(int id){
    //return new Coordinate();
    List<PhotonTrackedTarget> allApriltagTargets = allTargets();
    boolean aprilTagVisible = false;
    PhotonTrackedTarget target = new PhotonTrackedTarget();
    //int fiducialId;
    Coordinate coordinate = new Coordinate();
    for(int i = 0; i < allApriltagTargets.size(); i++){
      if(allApriltagTargets.get(i).fiducialId == id){
        aprilTagVisible = true;
        target = allApriltagTargets.get(i);
        //fiducialId = i;
        break;
      }
    }
    if(!aprilTagVisible){
      return new Coordinate();
    }
    coordinate.z = target.getBestCameraToTarget().getZ();
    coordinate.aprilTagVisible = aprilTagVisible;
    return coordinate;
  }

  public PhotonTrackedTarget getSelectFiducial(int id){
    PhotonTrackedTarget targetFiducial;
    List<PhotonTrackedTarget> seenFiducials;
    //List<PhotonPipelineResult> results = allUnreadResults();

    // Big block just gets fiducial of given ID 
    for(int i = 0; i < results.size(); i++){
      System.out.println(id);
      if(results.get(i).hasTargets()){
        System.out.println(id);
        seenFiducials = results.get(i).getTargets();
        for(int j = 0; j < seenFiducials.size(); j++){
          targetFiducial = seenFiducials.get(j);
          //System.out.println(targetFiducial.getFiducialId());
          //System.out.println(id);
          if(targetFiducial.getFiducialId() == id){
            System.out.println("got to proper fiducial");
            return targetFiducial;
          }
        }
      }
    }
    targetFiducial = new PhotonTrackedTarget();
    return targetFiducial;
  }

  /**
   * Logic error somewhere in here
   * @param ids
   * @return
   */
  public PhotonTrackedTarget getSelectFiducial(int[] ids){
    PhotonTrackedTarget targetFiducial;
    for(int i = 0; i < ids.length; i++){
      targetFiducial = getSelectFiducial(ids[i]);
      
      if(!(targetFiducial.getBestCameraToTarget() == null)){
        System.out.println("getting actual fid");
        return targetFiducial;
      }
      
    }
    return new PhotonTrackedTarget();
  }

  public List<PhotonTrackedTarget> allTargetsMultipleLines(){
    List<PhotonTrackedTarget> seenTargets = new ArrayList<>();

    for(int i = 0; i < numCameras; i++){
      //List<PhotonPipelineResult> results = cameras[i].getAllUnreadResults();
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
      //List<PhotonPipelineResult> results = cameras[i].getAllUnreadResults();
      for(int j = 0; j < results.size(); j++){
        seenTargets.add(results.get(j).getBestTarget());
      }
    }
    return null;
  }
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    results = allUnreadResults();
  }

}

