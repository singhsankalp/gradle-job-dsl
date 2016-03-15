package pipelineBuilders
import specs.RubySpecs
import docs.AppDocs
import packages.Ruby
import deploy.DebDeploy
import pipeline.Pipeline
import views.BuildPipelineViewWrapper

class RubyPipeline {

  static Pipeline build(LinkedHashMap applicationConfig){

    RubySpecs appTest = new RubySpecs(
      scm: applicationConfig.appRepoURL,
      location: applicationConfig.pipelineLocation,
      downstreamJobs: applicationConfig.testDownstream,
      artifactPattern: applicationConfig.testArtifact
    )

    AppDocs appDocs = new AppDocs(
      app: applicationConfig.appName,
      location: applicationConfig.pipelineLocation,
    )

    Ruby appPackage = new Ruby(
      app: applicationConfig.appName,
      scm: applicationConfig.appRepoURL,
      location: applicationConfig.pipelineLocation,
      downstream: "$applicationConfig.pipelineLocation/$applicationConfig.packageDownstream"
    )

    LinkedHashMap appDeployConfig = [
      name: applicationConfig.jobName,
      haproxyBackend: applicationConfig.haproxyBackend,
      maxFailPercentage: applicationConfig.maxFailPercentage,
      haproxyQuery: applicationConfig.haproxyQuery,
      recipe: applicationConfig.recipe
    ]
    DebDeploy appDeploy = new DebDeploy(
      app: applicationConfig.appName,
      jobLocation: "$applicationConfig.pipelineLocation/StagingDeploy",
      environment: "staging",
      appDeployConfig: appDeployConfig
    )

    BuildPipelineViewWrapper pipelineView = new BuildPipelineViewWrapper(
      appPath: applicationConfig.pipelineLocation,
      selectJob: "$applicationConfig.pipelineLocation/$applicationConfig.viewSelectJob",
      app: applicationConfig.jobName
    )

    Pipeline appPipeline = new Pipeline(
      specs: appTest,
      docs: appDocs,
      packer: appPackage,
      deploy: appDeploy,
      pipelineView: pipelineView
    )
    return appPipeline
  }
}
