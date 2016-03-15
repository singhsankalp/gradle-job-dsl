package helpers

class LogRotate {
  static void integrate(context) {
    context.with {
      logRotator {
        numToKeep(10)
        artifactNumToKeep(10)
      }
    }
  }
}
