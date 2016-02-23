package utils

class Slack {
  static void integrate(context) {
    context.with {
      slackNotifications {
        notifyAborted()
        notifyFailure()
        notifyNotBuilt()
        notifyUnstable()
        notifyBackToNormal()
        notifyRepeatedFailure()
        notifySuccess()
      }
    }
  }
}
