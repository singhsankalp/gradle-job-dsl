package pipeline

class Pipeline {

  Packer packer
  Deploy deploy

  void packRuby(){
    packer.ruby(this)
  }

  void deployStaging(){
    deploy.environment = "staging"
    deploy.deb(this)
  }

  void deployStaging(){
    deploy.environment = "production"
    deploy.deb(this)
  }
}
