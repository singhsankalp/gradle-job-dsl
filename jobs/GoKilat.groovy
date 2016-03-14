import packages.Ruby
import deploy.DebDeploy
import pipeline.Pipeline
import utils.BuildPipelineViewWrapper
import pipelineBuilders.RubyPipeline


def gokilatPipelineConfig = [

]

gokilatPipeline = RubyPipeline.build()
gokilatPipeline.createPackage(this).deployApp(this).buildView(this)
