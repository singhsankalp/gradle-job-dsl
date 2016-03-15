package pipeline
import specs.Specs
import deploy.Deploy
import packages.Packer
import utils.BuildPipelineViewWrapper
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class Pipeline {

  Specs specs
  Packer packer
  Deploy deploy
  BuildPipelineViewWrapper pipelineView

  Pipeline buildSpecs(DslFactory dslFactory){
    specs.Build(dslFactory)
    return this
  }

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
