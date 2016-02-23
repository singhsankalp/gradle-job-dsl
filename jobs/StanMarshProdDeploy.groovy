import utils.Scm
import utils.LogRotate
import utils.Slack
import utils.BuildPipelineViewWrapper
import deploy.JettyDeploy
import packages.JettyPackage

String app = 'StanMarsh'

folder(app) {
  description "This folder for ${app}"
}

stanmarshPackage = new JettyPackage(
  app:        app,
  downstream: "StagingDeploy",
  scm:        "git@bitbucket.org:gojek/stan-marsh.git"
)
stanmarshPackage.Package(this)

stanmarshStagingBackend = [
  name: "StagingBackend",
  haproxyBackend: "bid-backend",
  maxFailPercentage: "50",
  query: "roles:stan_marsh AND chef_environment:staging",
  haproxyQuery: "chef_environment:staging AND tags:core_haproxy"
]
stanmarshDeploy = new JettyDeploy(
    jobLocation:    app,
    jettyBackend:   stanmarshStagingBackend,
    app:            app
)
stanmarshDeploy.Setup(this)


folder('${app}/ProductionDeploy') {
  description 'This folder for stan-marsh production deploy'
  primaryView('Pipeline')
}

stanmarshProductionBackends= [
  [
    name: "BidBackend",
    haproxyBackend: "bid-backend",
    maxFailPercentage: "50",
    query: "roles:stan_marsh AND chef_environment:production AND name:p-jetty-bid*",
    haproxyQuery: "chef_environment:production AND tags:core_haproxy"
  ],
  [
    name: "JettyBackend",
    haproxyBackend: "jetty-backend",
    maxFailPercentage: "50",
    query: "roles:stan_marsh AND chef_environment:production AND name:p-jetty-backend-??",
    haproxyQuery: "chef_environment:production AND tags:core_haproxy"
  ],
  [
    name: "LbcacheBackend",
    haproxyBackend: "lbcache-backend",
    maxFailPercentage: "50",
    query: "roles:stan_marsh AND chef_environment:production AND name:p-jetty-lbcache*",
    haproxyQuery: "chef_environment:production AND tags:core_haproxy"
  ],
  [
    name: "PoiBackend",
    haproxyBackend: "poi-backend",
    maxFailPercentage: "50",
    query: "roles:stan_marsh AND chef_environment:production AND name:p-jetty-poi*",
    haproxyQuery: "chef_environment:production AND tags:core_haproxy"
  ],
  [
    name: "OauthBackend",
    haproxyBackend: "oauth-backend",
    maxFailPercentage: "50",
    query: "roles:stan_marsh AND chef_environment:production AND name:p-jetty-oauth*",
    haproxyQuery: "chef_environment:production AND tags:core_haproxy"
  ],
  [
    name: "JettyReadBackend",
    haproxyBackend: "jetty_read_backend",
    maxFailPercentage: "50",
    query: "roles:stan_marsh AND chef_environment:production AND name:p-jetty-backend-read*",
    haproxyQuery: "chef_environment:production AND tags:core_haproxy"
  ],
  [
    name: "CalcBackend",
    haproxyBackend: "calc-backend",
    maxFailPercentage: "50",
    query: "roles:stan_marsh AND chef_environment:production AND name:p-jetty-calc*",
    haproxyQuery: "chef_environment:production AND tags:core_haproxy"
  ],
  [
    name: "DispatchBackend",
    haproxyBackend: "dispatch-backend",
    maxFailPercentage: "50",
    query: "roles:stan_marsh AND chef_environment:production AND name:p-jetty-dispatch*",
    haproxyQuery: "chef_environment:production AND tags:core_haproxy"
  ]
]

stanmarshProductionBackends.each { jettyBackend ->

  stanmarshDeploy = new JettyDeploy(
      jobLocation:    "${app}/ProductionDeploy",
      jettyBackend:   jettyBackend,
      app:            app
  )

  stanmarshDeploy.Setup(this)
}

stanmarshPipelineView = new BuildPipelineViewWrapper(
    appPath:     app,
    selectJob:   "${app}/Package",
    app:         app
)

stanmarshDeploy.Setup(this)
