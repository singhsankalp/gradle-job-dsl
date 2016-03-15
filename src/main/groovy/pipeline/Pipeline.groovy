package pipeline
import specs.Specs
import docs.DeployDocs
import deploy.Deploy
import packages.Packer
import views.PipelineView
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class Pipeline {

  Specs specs
  DeployDocs docs
  Packer packer
  Deploy deploy
  PipelineView pipelineView

  Pipeline buildSpecs(DslFactory dslFactory){
    specs.Build(dslFactory)
    return this
  }

  Pipeline deployDocs(DslFactory dslFactory){
    docs.Setup(dslFactory)
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
