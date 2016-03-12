package pipeline
import deploy.Deploy
import packages.Packer
import utils.BuildPipelineViewWrapper
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class Pipeline {

  Packer packer
  Deploy deploy
  BuildPipelineViewWrapper pipelineView

  Pipeline createPackage(DslFactory dslFactory){
    packer.Package(dslFactory)
    return this
  }

  Pipeline deployApp(DslFactory dslFactory){
    deploy.Setup(dslFactory)
    return this
  }

  Pipeline buildView(DslFactory dslFactory){
    pipelineView.buildPipelineView(dslFactory)
    return this
  }
}
