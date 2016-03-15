package views

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.views.BuildPipelineView

interface PipelineView {
  BuildPipelineView buildPipelineView(DslFactory dslFactory)
}
