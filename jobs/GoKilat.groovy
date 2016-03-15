import specs.RubySpecs
import docs.AppDocs
import packages.Ruby
import deploy.DebDeploy
import pipeline.Pipeline
import utils.BuildPipelineViewWrapper
import pipelineBuilders.RubyPipeline


gokilatPipelineConfig = [
  appName: "go_kilat",
  appRepoURL: "git@bitbucket.org:gojek/go-kilat.git",
  testDownstream: ["DeployDocs", "Package"],
  testArtifact: "doc/**/*",
  pipelineLocation: "GoKilat/GoKilat",
  packageDownstream: "StagingDeploy",
  jobName: "GoKilat",
  haproxyBackend: "nil",
  maxFailPercentage: "50",
  haproxyQuery: "nil",
  recipe: "app",
  viewSelectJob: "Specs"
]

gokilatPipeline = RubyPipeline.build(gokilatPipelineConfig)
gokilatPipeline.buildSpecs(this).deployDocs(this).createPackage(this).deployApp(this).buildView(this)
