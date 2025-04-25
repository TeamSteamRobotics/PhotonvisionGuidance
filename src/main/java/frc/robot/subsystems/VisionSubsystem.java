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

public class VisionSubsystem extends SubsystemBase {
  private PhotonCamera[] cameras;
  private int numCameras;
  
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
      seenTargets.addAll(cameras[i].getAllUnreadResults().stream().map((e) -> e.getTargets()).flatMap(List::stream).toList());
      /* We have an array of cameras. We iterate over every camera, and add seen targets to the list
       * getAllUnreadResults() returns a list of PhotonPipelineResults. stream() converts it into
       * a Stream, which one can use map() on. map() applies the lambda expression to every element
       * in the Stream, and returns a new Stream. flatMap(List::stream) flattens the Stream, although
       * I don't know why or how it works. toList() is obvious
       */
    }
    return seenTargets;
  }

  public List<PhotonTrackedTarget> allTargetsMultipleLines(){
    List<PhotonTrackedTarget> seenTargets = new ArrayList<>();

    for(int i = 0; i < numCameras; i++){
      List<PhotonPipelineResult> results = cameras[i].getAllUnreadResults();
      for(int j = 0; j < results.size(); j++){
        seenTargets.addAll(results.get(j).getTargets());
      }
    }
    return seenTargets;
  }

  public PhotonTrackedTarget getBestTarget(){
    List<PhotonTrackedTarget> seenTargets = new ArrayList<>();

  }
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
