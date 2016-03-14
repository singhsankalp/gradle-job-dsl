package pipelineBuilders
import packages.Ruby
import deploy.DebDeploy
import pipeline.Pipeline
import utils.BuildPipelineViewWrapper

class RubyPipeline {

  static Pipeline build(){

    String app = 'go_kilat'

    Ruby gokilatPackage = new Ruby(
      app: app,
      scm: "git@bitbucket.org:gojek/go-kilat.git",
      location: "GoKilat/GoKilat",
      downstream: "GoKilat/GoKilat/StagingDeploy"
    )

    LinkedHashMap gokilatDeployConfig = [
      name: "GoKilat",
      haproxyBackend: "nil",
      maxFailPercentage: "50",
      haproxyQuery: "nil",
      recipe: "app"
    ]
    DebDeploy gokilatDeploy = new DebDeploy(
      app: app,
      jobLocation: "GoKilat/GoKilat/StagingDeploy",
      environment: "staging",
      appDeployConfig: gokilatDeployConfig
    )

    BuildPipelineViewWrapper pipelineView = new BuildPipelineViewWrapper(
      appPath: "GoKilat/GoKilat",
      selectJob: "GoKilat/GoKilat/Specs",
      app: "GoKilat"
    )

    Pipeline gokilatPipeline = new Pipeline(
      packer: gokilatPackage,
      deploy: gokilatDeploy,
      pipelineView: pipelineView
    )
    return gokilatPipeline
  }
}
