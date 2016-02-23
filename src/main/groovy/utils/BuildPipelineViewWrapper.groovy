package utils

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.views.BuildPipelineView

class BuildPipelineViewWrapper {
  String appPath
  String selectJob
  String app

  BuildPipelineView buildPipelineView(DslFactory dslFactory) {
      dslFactory.buildPipelineView("${appPath}/Pipeline") {
      title("${this.app} Containers Pipeline")
      selectedJob("${this.selectJob}")
      displayedBuilds(5)
      filterBuildQueue()
      filterExecutors()
      alwaysAllowManualTrigger()
      showPipelineParameters()
      refreshFrequency(60)
      customCssUrl('/userContent/style.css')
    }
  }
}
