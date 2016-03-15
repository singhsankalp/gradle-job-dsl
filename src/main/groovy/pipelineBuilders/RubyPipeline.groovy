package pipelineBuilders
import specs.RubySpecs
import packages.Ruby
import deploy.DebDeploy
import pipeline.Pipeline
import utils.BuildPipelineViewWrapper

class RubyPipeline {

  static Pipeline build(LinkedHashMap applicationConfig){

    RubySpecs gokilatTest = new RubySpecs(
      scm: applicationConfig.appRepoURL,
      location: applicationConfig.pipelineLocation,
      downstreamJobs: applicationConfig.testDownstream,
      artifactPattern: applicationConfig.testArtifact
    )

    Ruby gokilatPackage = new Ruby(
      app: applicationConfig.appName,
      scm: applicationConfig.appRepoURL,
      location: applicationConfig.pipelineLocation,
      downstream: "$applicationConfig.pipelineLocation/$applicationConfig.packageDownstream"
    )

    LinkedHashMap gokilatDeployConfig = [
      name: applicationConfig.jobName,
      haproxyBackend: applicationConfig.haproxyBackend,
      maxFailPercentage: applicationConfig.maxFailPercentage,
      haproxyQuery: applicationConfig.haproxyQuery,
      recipe: applicationConfig.recipe
    ]
    DebDeploy gokilatDeploy = new DebDeploy(
      app: applicationConfig.appName,
      jobLocation: "$applicationConfig.pipelineLocation/StagingDeploy",
      environment: "staging",
      appDeployConfig: gokilatDeployConfig
    )

    BuildPipelineViewWrapper pipelineView = new BuildPipelineViewWrapper(
      appPath: applicationConfig.pipelineLocation,
      selectJob: "$applicationConfig.pipelineLocation/$applicationConfig.viewSelectJob",
      app: applicationConfig.jobName
    )

    Pipeline gokilatPipeline = new Pipeline(
      specs: gokilatTest,
      packer: gokilatPackage,
      deploy: gokilatDeploy,
      pipelineView: pipelineView
    )
    return gokilatPipeline
  }
}
