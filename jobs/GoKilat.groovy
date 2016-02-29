import pipeline.Pipeline
import pipeline.Packer
import pipeline.Deploy

String app = 'go_kilat'

gokilatPackage = new Packer(
  app: app,
  scm: "git@bitbucket.org:gojek/go-kilat.git",
  location: "GoKilat/GoKilat",
  downstream: "GoKilat/GoKilat/StagingDeploy"
)

gokilatDeployConfig = [
  name: "GoKilat",
  haproxyBackend: "nil",
  maxFailPercentage: "50",
  query: "roles:stan_marsh AND chef_environment:production AND name:p-jetty-bid*",
  haproxyQuery: "nil",
  recipe: "app"
]
gokilatDeploy = new Deploy(
  app: app,
  jobLocation: "GoKilat/GoKilat/StagingDeploy",
  environment: "internal",
  LinkedHashMap: gokilatDeployConfig
)

gokilatPipeline = new Pipeline(
  packer: gokilatPackage,
  deploy: gokilatDeploy
)

gokilatPipeline.packRuby(this)
gokilatPipeline.deployStaging(this)
