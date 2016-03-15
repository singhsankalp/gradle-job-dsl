package helpers

class Scm {
  static void git(context, repo, targetDir='') {
    context.with {
      git {
        remote {
          url(repo)
        }
        branch('master')
        relativeTargetDir(targetDir)
      }
    }
  }

  static void gitByBranch(context, repo, branchName) {
    context.with {
      git {
        remote {
          url(repo)
        }
        branch(branchName)
        relativeTargetDir('')
      }
    }
  }
}
