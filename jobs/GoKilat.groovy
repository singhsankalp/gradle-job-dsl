import packages.Ruby
import packages.Packer
import pipeline.Pipeline

String app = 'go_kilat'

gokilatPackage = new Ruby(
  app: app,
  scm: "git@bitbucket.org:gojek/go-kilat.git",
  location: "GoKilat/GoKilat",
  downstream: "GoKilat/GoKilat/StagingDeploy"
)

gokilatDeployConfig = [
  name: "GoKilat",
  haproxyBackend: "nil",
  maxFailPercentage: "50",
  haproxyQuery: "nil",
  recipe: "app"
]
gokilatDeploy = new Deploy(
  app: app,
  jobLocation: "GoKilat/GoKilat/StagingDeploy",
  environment: "internal",
  appDeployConfig: gokilatDeployConfig
)

gokilatPipeline = new Pipeline(
  packer: gokilatPackage,
  deploy: gokilatDeploy
)

gokilatPipeline.createPackage(this)
gokilatPipeline.deployApp(this)
